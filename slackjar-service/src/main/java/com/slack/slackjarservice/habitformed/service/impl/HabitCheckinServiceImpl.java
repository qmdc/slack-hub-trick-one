package com.slack.slackjarservice.habitformed.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.constant.RedisConstants;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.common.util.RedisUtil;
import com.slack.slackjarservice.foundation.model.dto.SocketMessageDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import com.slack.slackjarservice.foundation.socketio.BackendMessagePush;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinCommentDao;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinDao;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinLikeDao;
import com.slack.slackjarservice.habitformed.dao.HabitFriendshipDao;
import com.slack.slackjarservice.habitformed.dao.HabitGoalDao;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitCheckinComment;
import com.slack.slackjarservice.habitformed.entity.HabitCheckinLike;
import com.slack.slackjarservice.habitformed.entity.HabitFriendship;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinRequest;
import com.slack.slackjarservice.habitformed.model.request.HabitCommentRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinCommentResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinResponse;
import com.slack.slackjarservice.habitformed.service.HabitAchievementService;
import com.slack.slackjarservice.habitformed.service.HabitCheckinService;
import com.slack.slackjarservice.habitformed.service.HabitGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("habitCheckinService")
@RequiredArgsConstructor
public class HabitCheckinServiceImpl extends ServiceImpl<HabitCheckinDao, HabitCheckin> implements HabitCheckinService {

    private final HabitGoalDao habitGoalDao;
    private final HabitCheckinLikeDao habitCheckinLikeDao;
    private final HabitCheckinCommentDao habitCheckinCommentDao;
    private final HabitFriendshipDao habitFriendshipDao;
    private final HabitGoalService habitGoalService;
    private final HabitAchievementService habitAchievementService;
    private final BackendMessagePush backendMessagePush;
    private final RedisUtil redisUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long checkin(HabitCheckinRequest request, Long userId) {
        HabitGoal goal = habitGoalDao.selectById(request.getGoalId());
        AssertUtil.notNull(goal, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(goal.getUserId().equals(userId), ResponseEnum.FORBIDDEN);

        long todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long todayEnd = todayStart + 86400000L;

        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckin::getGoalId, goal.getId())
            .eq(HabitCheckin::getUserId, userId)
            .ge(HabitCheckin::getCheckinDate, todayStart)
            .lt(HabitCheckin::getCheckinDate, todayEnd);

        if (this.count(queryWrapper) > 0) {
            throw new IllegalArgumentException("今日已打卡，请勿重复打卡");
        }

        HabitCheckin checkin = new HabitCheckin();
        checkin.setGoalId(goal.getId());
        checkin.setUserId(userId);
        checkin.setCheckinDate(todayStart);
        checkin.setCheckinTime(System.currentTimeMillis());
        checkin.setContent(request.getContent());
        checkin.setImageIds(request.getImageIds());
        checkin.setMood(request.getMood());
        checkin.setVisibility(1);
        checkin.setLikeCount(0);
        checkin.setCommentCount(0);
        this.save(checkin);

        int newStreak = habitGoalService.calculateStreak(goal.getId(), todayStart);

        LambdaUpdateWrapper<HabitGoal> goalUpdate = new LambdaUpdateWrapper<>();
        goalUpdate.eq(HabitGoal::getId, goal.getId())
            .setSql("checkin_count = checkin_count + 1")
            .set(HabitGoal::getCurrentStreak, newStreak)
            .set(HabitGoal::getLastCheckinDate, todayStart);

        if (newStreak > (goal.getLongestStreak() != null ? goal.getLongestStreak() : 0)) {
            goalUpdate.set(HabitGoal::getLongestStreak, newStreak);
        }

        habitGoalDao.update(null, goalUpdate);

        updateTodayCheckinCache(userId, goal.getId(), true);

        habitAchievementService.checkAndUnlockAchievements(userId, goal, checkin);

        pushToFriendFeed(userId, checkin, goal);

        return checkin.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelCheckin(Long checkinId, Long userId) {
        HabitCheckin checkin = this.getById(checkinId);
        AssertUtil.notNull(checkin, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(checkin.getUserId().equals(userId), ResponseEnum.FORBIDDEN);

        HabitGoal goal = habitGoalDao.selectById(checkin.getGoalId());
        if (goal != null) {
            LambdaUpdateWrapper<HabitGoal> goalUpdate = new LambdaUpdateWrapper<>();
            goalUpdate.eq(HabitGoal::getId, goal.getId())
                .setSql("checkin_count = GREATEST(0, checkin_count - 1)")
                .set(HabitGoal::getCurrentStreak, 0);
            habitGoalDao.update(null, goalUpdate);
        }

        LambdaQueryWrapper<HabitCheckinLike> likeQuery = new LambdaQueryWrapper<>();
        likeQuery.eq(HabitCheckinLike::getCheckinId, checkinId);
        habitCheckinLikeDao.delete(likeQuery);

        LambdaQueryWrapper<HabitCheckinComment> commentQuery = new LambdaQueryWrapper<>();
        commentQuery.eq(HabitCheckinComment::getCheckinId, checkinId);
        habitCheckinCommentDao.delete(commentQuery);

        this.removeById(checkinId);

        if (goal != null) {
            updateTodayCheckinCache(userId, goal.getId(), false);
        }
    }

    @Override
    public HabitCheckinResponse getCheckinById(Long id, Long userId) {
        HabitCheckin checkin = this.getById(id);
        AssertUtil.notNull(checkin, ResponseEnum.NOT_FOUND);

        return toResponse(checkin, userId);
    }

    @Override
    public PageResult<HabitCheckinResponse> pageQueryCheckins(HabitCheckinPageQuery query, Long userId) {
        Page<HabitCheckin> page = new Page<>(query.getPageNo(), query.getPageSize());

        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(query.getUserId() != null, HabitCheckin::getUserId, query.getUserId())
            .eq(query.getGoalId() != null, HabitCheckin::getGoalId, query.getGoalId())
            .ge(query.getStartDate() != null, HabitCheckin::getCheckinDate, query.getStartDate())
            .le(query.getEndDate() != null, HabitCheckin::getCheckinDate, query.getEndDate())
            .orderByDesc(HabitCheckin::getCheckinTime);

        Page<HabitCheckin> resultPage = this.page(page, queryWrapper);

        List<HabitCheckinResponse> responses = resultPage.getRecords().stream()
            .map(checkin -> toResponse(checkin, userId))
            .collect(Collectors.toList());

        return PageResult.of(responses, resultPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public PageResult<HabitCheckinResponse> getFriendFeed(Long userId, Integer pageNo, Integer pageSize) {
        List<Long> friendIds = getFriendIds(userId);

        if (friendIds.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, pageNo, pageSize);
        }

        Page<HabitCheckin> page = new Page<>(pageNo, pageSize);

        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(HabitCheckin::getUserId, friendIds)
            .in(HabitCheckin::getVisibility, 1, 2)
            .orderByDesc(HabitCheckin::getCheckinTime);

        Page<HabitCheckin> resultPage = this.page(page, queryWrapper);

        List<HabitCheckinResponse> responses = resultPage.getRecords().stream()
            .map(checkin -> toResponse(checkin, userId))
            .collect(Collectors.toList());

        return PageResult.of(responses, resultPage.getTotal(), pageNo, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean toggleLike(Long checkinId, Long userId) {
        HabitCheckin checkin = this.getById(checkinId);
        AssertUtil.notNull(checkin, ResponseEnum.NOT_FOUND);

        LambdaQueryWrapper<HabitCheckinLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckinLike::getCheckinId, checkinId)
            .eq(HabitCheckinLike::getUserId, userId);

        HabitCheckinLike existingLike = habitCheckinLikeDao.selectOne(queryWrapper);
        boolean isLiking = false;

        if (existingLike != null) {
            habitCheckinLikeDao.deleteById(existingLike.getId());

            LambdaUpdateWrapper<HabitCheckin> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(HabitCheckin::getId, checkinId)
                .setSql("like_count = GREATEST(0, like_count - 1)");
            this.update(updateWrapper);
        } else {
            isLiking = true;

            HabitCheckinLike like = new HabitCheckinLike();
            like.setCheckinId(checkinId);
            like.setUserId(userId);
            habitCheckinLikeDao.insert(like);

            LambdaUpdateWrapper<HabitCheckin> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(HabitCheckin::getId, checkinId)
                .setSql("like_count = like_count + 1");
            this.update(updateWrapper);

            if (!checkin.getUserId().equals(userId)) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "like");
                notification.put("checkinId", checkinId);
                notification.put("likeUserId", userId);
                pushNotification(checkin.getUserId(), "habit_notification", notification);
            }
        }

        return isLiking;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long addComment(HabitCommentRequest request, Long userId) {
        HabitCheckin checkin = this.getById(request.getCheckinId());
        AssertUtil.notNull(checkin, ResponseEnum.NOT_FOUND);

        HabitCheckinComment comment = new HabitCheckinComment();
        comment.setCheckinId(request.getCheckinId());
        comment.setUserId(userId);
        comment.setReplyCommentId(request.getReplyCommentId());
        comment.setReplyUserId(request.getReplyUserId());
        comment.setContent(request.getContent());
        habitCheckinCommentDao.insert(comment);

        LambdaUpdateWrapper<HabitCheckin> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HabitCheckin::getId, request.getCheckinId())
            .setSql("comment_count = comment_count + 1");
        this.update(updateWrapper);

        if (!checkin.getUserId().equals(userId)) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "comment");
            notification.put("checkinId", request.getCheckinId());
            notification.put("commentUserId", userId);
            notification.put("commentId", comment.getId());
            notification.put("content", request.getContent());
            pushNotification(checkin.getUserId(), "habit_notification", notification);
        }

        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteComment(Long commentId, Long userId) {
        HabitCheckinComment comment = habitCheckinCommentDao.selectById(commentId);
        AssertUtil.notNull(comment, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(comment.getUserId().equals(userId), ResponseEnum.FORBIDDEN);

        Long checkinId = comment.getCheckinId();

        habitCheckinCommentDao.deleteById(commentId);

        LambdaUpdateWrapper<HabitCheckin> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HabitCheckin::getId, checkinId)
            .setSql("comment_count = GREATEST(0, comment_count - 1)");
        this.update(updateWrapper);
    }

    @Override
    public List<HabitCheckinCommentResponse> getComments(Long checkinId, Long userId) {
        LambdaQueryWrapper<HabitCheckinComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckinComment::getCheckinId, checkinId)
            .orderByAsc(HabitCheckinComment::getCreateTime);

        List<HabitCheckinComment> comments = habitCheckinCommentDao.selectList(queryWrapper);

        return comments.stream()
            .map(this::commentToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public int getTotalCheckinDays(Long userId) {
        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckin::getUserId, userId);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public int getTotalLikesReceived(Long userId) {
        String key = RedisConstants.getUserLikesKey(userId);
        Object cached = redisUtil.get(key);
        if (cached != null) {
            return ((Number) cached).intValue();
        }

        LambdaQueryWrapper<HabitCheckin> checkinQuery = new LambdaQueryWrapper<>();
        checkinQuery.eq(HabitCheckin::getUserId, userId);
        List<HabitCheckin> checkins = this.list(checkinQuery);

        int totalLikes = checkins.stream()
            .mapToInt(checkin -> checkin.getLikeCount() != null ? checkin.getLikeCount() : 0)
            .sum();

        redisUtil.set(key, totalLikes, RedisConstants.ONE_HOUR);
        return totalLikes;
    }

    @Override
    public int getTotalCommentsMade(Long userId) {
        LambdaQueryWrapper<HabitCheckinComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckinComment::getUserId, userId);
        return Math.toIntExact(habitCheckinCommentDao.selectCount(queryWrapper));
    }

    private List<Long> getFriendIds(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).or()
            .eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        List<HabitFriendship> friendships = habitFriendshipDao.selectList(queryWrapper);

        return friendships.stream()
            .map(friendship -> {
                if (friendship.getUserId().equals(userId)) {
                    return friendship.getFriendId();
                } else {
                    return friendship.getUserId();
                }
            })
            .collect(Collectors.toList());
    }

    private void updateTodayCheckinCache(Long userId, Long goalId, boolean checked) {
        String todayKey = RedisConstants.getTodayCheckinKey(userId);
        String statusKey = RedisConstants.getCheckinStatusKey(userId, goalId);

        if (checked) {
            redisUtil.sSet(todayKey, goalId.toString());
            redisUtil.expire(todayKey, RedisConstants.ONE_DAY);
            redisUtil.set(statusKey, 1, RedisConstants.ONE_DAY);
        } else {
            redisUtil.setRemove(todayKey, goalId.toString());
            redisUtil.delete(statusKey);
        }
    }

    private void pushToFriendFeed(Long userId, HabitCheckin checkin, HabitGoal goal) {
        List<Long> friendIds = getFriendIds(userId);

        Map<String, Object> feedData = new HashMap<>();
        feedData.put("type", "new_checkin");
        feedData.put("checkinId", checkin.getId());
        feedData.put("userId", userId);
        feedData.put("goalId", checkin.getGoalId());
        feedData.put("goalName", goal.getGoalName());
        feedData.put("goalIcon", goal.getGoalIcon());
        feedData.put("content", checkin.getContent());
        feedData.put("checkinTime", checkin.getCheckinTime());

        for (Long friendId : friendIds) {
            String feedKey = RedisConstants.getFriendFeedKey(friendId);
            redisTemplate.opsForList().leftPush(feedKey, feedData);
            redisTemplate.opsForList().trim(feedKey, 0, 99);
            redisUtil.expire(feedKey, RedisConstants.ONE_DAY * 3);

            pushNotification(friendId, "habit_feed", feedData);
        }
    }

    private void pushNotification(Long userId, String bizType, Object content) {
        SocketMessageDTO message = new SocketMessageDTO(content, bizType);
        backendMessagePush.pushMessageToUser(String.valueOf(userId), message);
    }

    private HabitCheckinResponse toResponse(HabitCheckin checkin, Long currentUserId) {
        HabitCheckinResponse response = new HabitCheckinResponse();
        response.setId(checkin.getId());
        response.setGoalId(checkin.getGoalId());
        response.setUserId(checkin.getUserId());
        response.setCheckinDate(checkin.getCheckinDate());
        response.setCheckinTime(checkin.getCheckinTime());
        response.setContent(checkin.getContent());
        if (checkin.getImageIds() != null && !checkin.getImageIds().isEmpty()) {
            response.setImageUrls(Arrays.asList(checkin.getImageIds().split(",")));
        } else {
            response.setImageUrls(new ArrayList<>());
        }
        response.setMood(checkin.getMood());
        response.setVisibility(checkin.getVisibility());
        response.setLikeCount(checkin.getLikeCount());
        response.setCommentCount(checkin.getCommentCount());
        response.setCreateTime(checkin.getCreateTime());

        HabitGoal goal = habitGoalDao.selectById(checkin.getGoalId());
        if (goal != null) {
            response.setGoalName(goal.getGoalName());
            response.setGoalIcon(goal.getGoalIcon());
        }

        if (currentUserId != null) {
            LambdaQueryWrapper<HabitCheckinLike> likeQuery = new LambdaQueryWrapper<>();
            likeQuery.eq(HabitCheckinLike::getCheckinId, checkin.getId())
                .eq(HabitCheckinLike::getUserId, currentUserId);
            response.setLiked(habitCheckinLikeDao.selectCount(likeQuery) > 0);
        } else {
            response.setLiked(false);
        }

        return response;
    }

    private HabitCheckinCommentResponse commentToResponse(HabitCheckinComment comment) {
        HabitCheckinCommentResponse response = new HabitCheckinCommentResponse();
        response.setId(comment.getId());
        response.setCheckinId(comment.getCheckinId());
        response.setUserId(comment.getUserId());
        response.setReplyCommentId(comment.getReplyCommentId());
        response.setReplyUserId(comment.getReplyUserId());
        response.setContent(comment.getContent());
        response.setCreateTime(comment.getCreateTime());
        return response;
    }
}
