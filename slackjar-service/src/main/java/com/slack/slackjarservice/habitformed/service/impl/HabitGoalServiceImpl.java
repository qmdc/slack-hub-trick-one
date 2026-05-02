package com.slack.slackjarservice.habitformed.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinDao;
import com.slack.slackjarservice.habitformed.dao.HabitGoalDao;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalSaveRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitGoalResponse;
import com.slack.slackjarservice.habitformed.service.HabitAchievementService;
import com.slack.slackjarservice.habitformed.service.HabitGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("habitGoalService")
@RequiredArgsConstructor
public class HabitGoalServiceImpl extends ServiceImpl<HabitGoalDao, HabitGoal> implements HabitGoalService {

    private final HabitCheckinDao habitCheckinDao;
    private final HabitAchievementService habitAchievementService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long saveGoal(HabitGoalSaveRequest request, Long userId) {
        HabitGoal goal;
        if (Objects.nonNull(request.getId())) {
            goal = this.getById(request.getId());
            AssertUtil.notNull(goal, ResponseEnum.NOT_FOUND);
            AssertUtil.isTrue(goal.getUserId().equals(userId), ResponseEnum.FORBIDDEN);
        } else {
            goal = new HabitGoal();
            goal.setUserId(userId);
            goal.setCheckinCount(0);
            goal.setCurrentStreak(0);
            goal.setLongestStreak(0);
            goal.setStatus(1);
            goal.setStartDate(System.currentTimeMillis());
        }

        goal.setGoalName(request.getGoalName());
        goal.setGoalIcon(request.getGoalIcon());
        goal.setGoalColor(request.getGoalColor());
        goal.setDescription(request.getDescription());
        goal.setStatus(Objects.nonNull(request.getStatus()) ? request.getStatus() : 1);

        this.saveOrUpdate(goal);

        if (Objects.isNull(request.getId())) {
            habitAchievementService.checkGoalCreateAchievement(userId, goal);
        }

        return goal.getId();
    }

    @Override
    public HabitGoalResponse getGoalById(Long id) {
        HabitGoal goal = this.getById(id);
        AssertUtil.notNull(goal, ResponseEnum.NOT_FOUND);
        return toResponse(goal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteGoalById(Long id, Long userId) {
        HabitGoal goal = this.getById(id);
        AssertUtil.notNull(goal, ResponseEnum.NOT_FOUND);
        AssertUtil.isTrue(goal.getUserId().equals(userId), ResponseEnum.FORBIDDEN);
        this.removeById(id);
    }

    @Override
    public PageResult<HabitGoalResponse> pageQueryGoals(HabitGoalPageQuery query, Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);

        if (StrUtil.isNotBlank(query.getGoalName())) {
            queryWrapper.like(HabitGoal::getGoalName, query.getGoalName());
        }

        if (Objects.nonNull(query.getStatus())) {
            queryWrapper.eq(HabitGoal::getStatus, query.getStatus());
        }

        queryWrapper.orderByDesc(HabitGoal::getCreateTime);

        Page<HabitGoal> page = this.page(
            new Page<>(query.getPageNo(), query.getPageSize()),
            queryWrapper
        );

        List<HabitGoalResponse> items = page.getRecords().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return PageResult.of(items, page.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public List<HabitGoalResponse> getUserGoals(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId)
            .eq(HabitGoal::getStatus, 1)
            .orderByDesc(HabitGoal::getCreateTime);

        return this.list(queryWrapper).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public boolean shouldCheckinToday(HabitGoal goal) {
        if (goal.getStatus() != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasCheckedInToday(Long goalId, Long userId) {
        LocalDate today = LocalDate.now();
        long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckin::getGoalId, goalId)
            .eq(HabitCheckin::getUserId, userId)
            .ge(HabitCheckin::getCheckinDate, startOfDay)
            .lt(HabitCheckin::getCheckinDate, endOfDay);

        return habitCheckinDao.selectCount(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateGoalStats(Long goalId, Long checkinDate) {
        HabitGoal goal = this.getById(goalId);
        if (goal == null) {
            return;
        }

        int streak = calculateStreak(goalId, checkinDate);

        goal.setCheckinCount(goal.getCheckinCount() == null ? 1 : goal.getCheckinCount() + 1);
        goal.setCurrentStreak(streak);
        if (goal.getLongestStreak() == null || streak > goal.getLongestStreak()) {
            goal.setLongestStreak(streak);
        }
        goal.setLastCheckinDate(checkinDate);

        this.updateById(goal);
    }

    @Override
    public int calculateStreak(Long goalId, Long checkinDate) {
        LocalDate currentDate = LocalDate.ofInstant(
            new java.util.Date(checkinDate).toInstant(),
            ZoneId.systemDefault()
        );

        int streak = 1;
        LocalDate checkDate = currentDate.minusDays(1);

        while (true) {
            long startOfDay = checkDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endOfDay = checkDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

            LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HabitCheckin::getGoalId, goalId)
                .ge(HabitCheckin::getCheckinDate, startOfDay)
                .lt(HabitCheckin::getCheckinDate, endOfDay);

            Long count = habitCheckinDao.selectCount(queryWrapper);
            if (count == null || count == 0) {
                break;
            }

            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    @Override
    public List<HabitGoalResponse> getTodayPendingGoals(Long userId) {
        LocalDate today = LocalDate.now();
        long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<HabitGoal> activeGoals = this.list(
            new LambdaQueryWrapper<HabitGoal>()
                .eq(HabitGoal::getUserId, userId)
                .eq(HabitGoal::getStatus, 1)
                .orderByDesc(HabitGoal::getCreateTime)
        );

        return activeGoals.stream()
            .filter(goal -> {
                LambdaQueryWrapper<HabitCheckin> checkinQuery = new LambdaQueryWrapper<>();
                checkinQuery.eq(HabitCheckin::getGoalId, goal.getId())
                    .eq(HabitCheckin::getUserId, userId)
                    .ge(HabitCheckin::getCheckinDate, startOfDay)
                    .lt(HabitCheckin::getCheckinDate, endOfDay);
                return habitCheckinDao.selectCount(checkinQuery) == 0;
            })
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<HabitGoalResponse> getTodayCompletedGoals(Long userId) {
        LocalDate today = LocalDate.now();
        long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<HabitCheckin> todayCheckins = habitCheckinDao.selectList(
            new LambdaQueryWrapper<HabitCheckin>()
                .eq(HabitCheckin::getUserId, userId)
                .ge(HabitCheckin::getCheckinDate, startOfDay)
                .lt(HabitCheckin::getCheckinDate, endOfDay)
        );

        List<Long> completedGoalIds = todayCheckins.stream()
            .map(HabitCheckin::getGoalId)
            .distinct()
            .collect(Collectors.toList());

        if (completedGoalIds.isEmpty()) {
            return List.of();
        }

        return this.listByIds(completedGoalIds).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public HabitGoalResponse toResponse(HabitGoal goal) {
        HabitGoalResponse response = new HabitGoalResponse();
        response.setId(goal.getId());
        response.setUserId(goal.getUserId());
        response.setGoalName(goal.getGoalName());
        response.setGoalIcon(goal.getGoalIcon());
        response.setGoalColor(goal.getGoalColor());
        response.setDescription(goal.getDescription());
        response.setStatus(goal.getStatus());
        response.setCheckinCount(goal.getCheckinCount());
        response.setCurrentStreak(goal.getCurrentStreak());
        response.setLongestStreak(goal.getLongestStreak());
        response.setStartDate(goal.getStartDate());
        response.setLastCheckinDate(goal.getLastCheckinDate());
        response.setCreateTime(goal.getCreateTime());

        if (goal.getTotalDays() != null && goal.getTotalDays() > 0 && goal.getCheckinCount() != null) {
            int rate = (int) ((goal.getCheckinCount() * 100.0) / goal.getTotalDays());
            response.setCompletionRate(Math.min(100, rate));
        } else {
            response.setCompletionRate(0);
        }

        return response;
    }
}
