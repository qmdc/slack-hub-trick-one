package com.slack.slackjarservice.habitformed.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.foundation.dao.SysUserDao;
import com.slack.slackjarservice.foundation.entity.SysUser;
import com.slack.slackjarservice.foundation.model.dto.SocketMessageDTO;
import com.slack.slackjarservice.foundation.socketio.BackendMessagePush;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinDao;
import com.slack.slackjarservice.habitformed.dao.HabitFriendshipDao;
import com.slack.slackjarservice.habitformed.dao.HabitGoalDao;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitFriendship;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitFriendResponse;
import com.slack.slackjarservice.habitformed.service.HabitFriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("habitFriendshipService")
@RequiredArgsConstructor
public class HabitFriendshipServiceImpl extends ServiceImpl<HabitFriendshipDao, HabitFriendship> implements HabitFriendshipService {

    private final SysUserDao sysUserDao;
    private final HabitGoalDao habitGoalDao;
    private final HabitCheckinDao habitCheckinDao;
    private final BackendMessagePush backendMessagePush;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long sendFriendRequest(HabitFriendshipRequest request, Long userId) {
        Long friendId = request.getFriendId();
        AssertUtil.isTrue(!userId.equals(friendId), ResponseEnum.ERROR);

        SysUser targetUser = sysUserDao.selectById(friendId);
        AssertUtil.notNull(targetUser, ResponseEnum.USER_NOT_EXIST);

        LambdaQueryWrapper<HabitFriendship> existingQuery = new LambdaQueryWrapper<>();
        existingQuery.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).eq(HabitFriendship::getFriendId, friendId)
            .or()
            .eq(HabitFriendship::getUserId, friendId).eq(HabitFriendship::getFriendId, userId)
        );
        if (this.count(existingQuery) > 0) {
            throw new IllegalArgumentException("已存在好友关系或申请");
        }

        HabitFriendship friendship = new HabitFriendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(0);
        friendship.setApplyUserId(userId);
        friendship.setApplyReason(request.getApplyReason());
        this.save(friendship);

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "friend_request");
        notification.put("friendshipId", friendship.getId());
        notification.put("fromUserId", userId);
        notification.put("applyReason", request.getApplyReason());
        pushNotification(friendId, "habit_notification", notification);

        return friendship.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void confirmFriendRequest(Long friendshipId, Long userId) {
        HabitFriendship friendship = this.getById(friendshipId);
        AssertUtil.notNull(friendship, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(
            friendship.getFriendId().equals(userId) || friendship.getUserId().equals(userId),
            ResponseEnum.FORBIDDEN
        );
        AssertUtil.isTrue(friendship.getStatus() == 0, ResponseEnum.ERROR);

        LambdaUpdateWrapper<HabitFriendship> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HabitFriendship::getId, friendshipId)
            .set(HabitFriendship::getStatus, 1)
            .set(HabitFriendship::getConfirmTime, System.currentTimeMillis());
        this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void rejectFriendRequest(Long friendshipId, Long userId) {
        HabitFriendship friendship = this.getById(friendshipId);
        AssertUtil.notNull(friendship, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(
            friendship.getFriendId().equals(userId) || friendship.getUserId().equals(userId),
            ResponseEnum.FORBIDDEN
        );

        LambdaUpdateWrapper<HabitFriendship> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HabitFriendship::getId, friendshipId)
            .set(HabitFriendship::getStatus, 2);
        this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelFriendRequest(Long friendshipId, Long userId) {
        HabitFriendship friendship = this.getById(friendshipId);
        AssertUtil.notNull(friendship, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(friendship.getApplyUserId().equals(userId), ResponseEnum.FORBIDDEN);

        LambdaUpdateWrapper<HabitFriendship> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HabitFriendship::getId, friendshipId)
            .set(HabitFriendship::getStatus, 3);
        this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeFriend(Long friendId, Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).eq(HabitFriendship::getFriendId, friendId)
            .or()
            .eq(HabitFriendship::getUserId, friendId).eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        this.remove(queryWrapper);
    }

    @Override
    public List<HabitFriendResponse> getFriendList(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).or()
            .eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        List<HabitFriendship> friendships = this.list(queryWrapper);

        return friendships.stream()
            .map(friendship -> toResponse(friendship, userId))
            .collect(Collectors.toList());
    }

    @Override
    public PageResult<HabitFriendResponse> pageQueryFriends(HabitFriendshipPageQuery query, Long userId) {
        Page<HabitFriendship> page = new Page<>(query.getPageNo(), query.getPageSize());

        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).or()
            .eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1)
            .orderByDesc(HabitFriendship::getConfirmTime);

        Page<HabitFriendship> resultPage = this.page(page, queryWrapper);

        List<HabitFriendResponse> responses = resultPage.getRecords().stream()
            .map(friendship -> toResponse(friendship, userId))
            .collect(Collectors.toList());

        return PageResult.of(responses, resultPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public List<HabitFriendResponse> getPendingRequests(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitFriendship::getFriendId, userId)
            .eq(HabitFriendship::getStatus, 0)
            .orderByDesc(HabitFriendship::getCreateTime);

        List<HabitFriendship> friendships = this.list(queryWrapper);

        return friendships.stream()
            .map(friendship -> toResponse(friendship, userId))
            .collect(Collectors.toList());
    }

    @Override
    public List<HabitFriendResponse> getSentRequests(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitFriendship::getApplyUserId, userId)
            .eq(HabitFriendship::getStatus, 0)
            .orderByDesc(HabitFriendship::getCreateTime);

        List<HabitFriendship> friendships = this.list(queryWrapper);

        return friendships.stream()
            .map(friendship -> toResponse(friendship, userId))
            .collect(Collectors.toList());
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            return false;
        }

        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).eq(HabitFriendship::getFriendId, friendId)
            .or()
            .eq(HabitFriendship::getUserId, friendId).eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<Long> getFriendIdList(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).or()
            .eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        List<HabitFriendship> friendships = this.list(queryWrapper);

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

    @Override
    public int getPendingRequestCount(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitFriendship::getFriendId, userId)
            .eq(HabitFriendship::getStatus, 0);

        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public List<HabitFriendResponse> searchUsers(String keyword, Long userId) {
        if (StrUtil.isBlank(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysUser> userQuery = new LambdaQueryWrapper<>();
        userQuery.like(SysUser::getUsername, keyword)
            .or().like(SysUser::getNickname, keyword)
            .ne(SysUser::getId, userId)
            .last("LIMIT 20");

        List<SysUser> users = sysUserDao.selectList(userQuery);

        return users.stream()
            .map(user -> searchUserToResponse(user, userId))
            .collect(Collectors.toList());
    }

    private void pushNotification(Long userId, String bizType, Object content) {
        SocketMessageDTO message = new SocketMessageDTO(content, bizType);
        backendMessagePush.pushMessageToUser(String.valueOf(userId), message);
    }

    private HabitFriendResponse toResponse(HabitFriendship friendship, Long currentUserId) {
        HabitFriendResponse response = new HabitFriendResponse();
        response.setId(friendship.getId());
        response.setUserId(friendship.getUserId());
        response.setFriendId(friendship.getFriendId());
        response.setStatus(friendship.getStatus());
        response.setApplyUserId(friendship.getApplyUserId());
        response.setApplyReason(friendship.getApplyReason());
        response.setConfirmTime(friendship.getConfirmTime());
        response.setCreateTime(friendship.getCreateTime());

        Long friendUserId;
        if (friendship.getUserId().equals(currentUserId)) {
            friendUserId = friendship.getFriendId();
        } else {
            friendUserId = friendship.getUserId();
        }
        response.setFriendId(friendUserId);

        SysUser friendUser = sysUserDao.selectById(friendUserId);
        if (friendUser != null) {
            response.setFriendUsername(friendUser.getUsername());
            response.setFriendNickname(friendUser.getNickname());
        }

        if (friendship.getStatus() == 1) {
            long todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long todayEnd = todayStart + 86400000L;

            LambdaQueryWrapper<HabitCheckin> checkinQuery = new LambdaQueryWrapper<>();
            checkinQuery.eq(HabitCheckin::getUserId, friendUserId)
                .ge(HabitCheckin::getCheckinDate, todayStart)
                .lt(HabitCheckin::getCheckinDate, todayEnd);
            int todayCount = Math.toIntExact(habitCheckinDao.selectCount(checkinQuery));
            response.setTodayCheckinCount(todayCount);

            LambdaQueryWrapper<HabitGoal> goalQuery = new LambdaQueryWrapper<>();
            goalQuery.eq(HabitGoal::getUserId, friendUserId);
            List<HabitGoal> goals = habitGoalDao.selectList(goalQuery);

            int maxLongestStreak = goals.stream()
                .mapToInt(goal -> goal.getLongestStreak() != null ? goal.getLongestStreak() : 0)
                .max()
                .orElse(0);
            response.setLongestStreak(maxLongestStreak);
        }

        return response;
    }

    private HabitFriendResponse searchUserToResponse(SysUser user, Long currentUserId) {
        HabitFriendResponse response = new HabitFriendResponse();
        response.setFriendId(user.getId());
        response.setFriendUsername(user.getUsername());
        response.setFriendNickname(user.getNickname());

        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, currentUserId).eq(HabitFriendship::getFriendId, user.getId())
            .or()
            .eq(HabitFriendship::getUserId, user.getId()).eq(HabitFriendship::getFriendId, currentUserId)
        );

        HabitFriendship friendship = this.getOne(queryWrapper);
        if (friendship != null) {
            response.setStatus(friendship.getStatus());
            response.setId(friendship.getId());
        } else {
            response.setStatus(-1);
        }

        return response;
    }
}
