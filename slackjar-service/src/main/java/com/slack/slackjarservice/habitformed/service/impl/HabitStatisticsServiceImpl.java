package com.slack.slackjarservice.habitformed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.constant.RedisConstants;
import com.slack.slackjarservice.common.util.RedisUtil;
import com.slack.slackjarservice.habitformed.dao.HabitAchievementDao;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinDao;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinLikeDao;
import com.slack.slackjarservice.habitformed.dao.HabitFriendshipDao;
import com.slack.slackjarservice.habitformed.dao.HabitGoalDao;
import com.slack.slackjarservice.habitformed.dao.HabitUserAchievementDao;
import com.slack.slackjarservice.habitformed.entity.HabitAchievement;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitFriendship;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.response.HabitAchievementResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitGoalResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitStatisticsResponse;
import com.slack.slackjarservice.habitformed.model.response.HomePageResponse;
import com.slack.slackjarservice.habitformed.service.HabitAchievementService;
import com.slack.slackjarservice.habitformed.service.HabitCheckinService;
import com.slack.slackjarservice.habitformed.service.HabitFriendshipService;
import com.slack.slackjarservice.habitformed.service.HabitGoalService;
import com.slack.slackjarservice.habitformed.service.HabitStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("habitStatisticsService")
@RequiredArgsConstructor
public class HabitStatisticsServiceImpl implements HabitStatisticsService {

    private final HabitGoalDao habitGoalDao;
    private final HabitCheckinDao habitCheckinDao;
    private final HabitFriendshipDao habitFriendshipDao;
    private final HabitAchievementDao habitAchievementDao;
    private final HabitUserAchievementDao habitUserAchievementDao;
    private final HabitCheckinLikeDao habitCheckinLikeDao;

    private final HabitGoalService habitGoalService;
    private final HabitCheckinService habitCheckinService;
    private final HabitFriendshipService habitFriendshipService;
    private final HabitAchievementService habitAchievementService;

    private final RedisUtil redisUtil;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public HomePageResponse getHomePageData(Long userId) {
        HomePageResponse response = new HomePageResponse();

        List<HabitGoalResponse> pendingGoals = habitGoalService.getTodayPendingGoals(userId);
        response.setPendingGoals(pendingGoals);

        List<HabitGoalResponse> completedGoals = habitGoalService.getTodayCompletedGoals(userId);
        response.setCompletedGoals(completedGoals);

        int total = pendingGoals.size() + completedGoals.size();
        if (total > 0) {
            response.setTodayCompletionRate((double) completedGoals.size() / total);
        } else {
            response.setTodayCompletionRate(0.0);
        }

        response.setCurrentStreak(getCurrentStreak(userId));
        response.setLongestStreak(getLongestStreak(userId));

        response.setPendingFriendRequests(habitFriendshipService.getPendingRequestCount(userId));

        response.setRecentAchievement(habitAchievementService.getRecentAchievement(userId));

        return response;
    }

    @Override
    public HabitStatisticsResponse getStatistics(Long userId) {
        HabitStatisticsResponse response = new HabitStatisticsResponse();

        response.setTotalGoals(countTotalGoals(userId));
        response.setActiveGoals(countActiveGoals(userId));
        response.setTotalCheckinDays(habitCheckinService.getTotalCheckinDays(userId));
        response.setLongestStreak(getLongestStreak(userId));
        response.setCurrentStreak(getCurrentStreak(userId));
        response.setEarnedAchievements(habitAchievementService.getEarnedAchievements(userId));
        response.setTotalAchievements(habitAchievementService.getTotalAchievements());
        response.setTotalFriends(countTotalFriends(userId));
        response.setTotalLikes(habitCheckinService.getTotalLikesReceived(userId));
        response.setTotalComments(habitCheckinService.getTotalCommentsMade(userId));

        response.setWeeklyCheckinData(getWeeklyCheckinData(userId));
        response.setMonthlyCheckinData(getMonthlyCheckinData(userId));
        response.setGoalCompletions(getGoalCompletions(userId));
        response.setMonthlyRates(getMonthlyRates(userId));

        return response;
    }

    @Override
    public Map<String, Integer> getWeeklyCheckinData(Long userId) {
        Map<String, Integer> result = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            long dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long dayEnd = dayStart + 86400000L;

            LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HabitCheckin::getUserId, userId)
                .ge(HabitCheckin::getCheckinDate, dayStart)
                .lt(HabitCheckin::getCheckinDate, dayEnd);

            int count = Math.toIntExact(habitCheckinDao.selectCount(queryWrapper));
            result.put(date.format(DATE_FORMATTER), count);
        }

        return result;
    }

    @Override
    public Map<String, Integer> getMonthlyCheckinData(Long userId) {
        Map<String, Integer> result = new HashMap<>();

        LocalDate today = LocalDate.now();
        int daysInMonth = today.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = today.withDayOfMonth(i);
            long dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long dayEnd = dayStart + 86400000L;

            LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HabitCheckin::getUserId, userId)
                .ge(HabitCheckin::getCheckinDate, dayStart)
                .lt(HabitCheckin::getCheckinDate, dayEnd);

            int count = Math.toIntExact(habitCheckinDao.selectCount(queryWrapper));
            result.put(date.format(DATE_FORMATTER), count);
        }

        return result;
    }

    @Override
    public List<HabitStatisticsResponse.MonthlyRate> getMonthlyRates(Long userId) {
        List<HabitStatisticsResponse.MonthlyRate> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(today.minusMonths(i));
            String monthStr = yearMonth.format(MONTH_FORMATTER);

            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            int daysInMonth = monthEnd.getDayOfMonth();

            long startMillis = monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endMillis = monthEnd.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant().toEpochMilli();

            LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HabitCheckin::getUserId, userId)
                .ge(HabitCheckin::getCheckinDate, startMillis)
                .lt(HabitCheckin::getCheckinDate, endMillis);

            int checkinDays = Math.toIntExact(habitCheckinDao.selectCount(queryWrapper));

            HabitStatisticsResponse.MonthlyRate rate = new HabitStatisticsResponse.MonthlyRate();
            rate.setMonth(monthStr);
            rate.setCheckinDays(checkinDays);
            rate.setRate((double) checkinDays / daysInMonth);

            result.add(rate);
        }

        return result;
    }

    @Override
    public List<HabitStatisticsResponse.GoalCompletion> getGoalCompletions(Long userId) {
        List<HabitStatisticsResponse.GoalCompletion> result = new ArrayList<>();

        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);

        List<HabitGoal> goals = habitGoalDao.selectList(queryWrapper);

        for (HabitGoal goal : goals) {
            HabitStatisticsResponse.GoalCompletion completion = new HabitStatisticsResponse.GoalCompletion();
            completion.setGoalId(goal.getId());
            completion.setGoalName(goal.getGoalName());
            completion.setGoalIcon(goal.getGoalIcon());
            completion.setCheckinCount(goal.getCheckinCount() != null ? goal.getCheckinCount() : 0);
            completion.setTotalDays(goal.getTotalDays() != null ? goal.getTotalDays() : 0);

            if (goal.getTotalDays() != null && goal.getTotalDays() > 0 && goal.getCheckinCount() != null) {
                completion.setCompletionRate((goal.getCheckinCount() * 100.0) / goal.getTotalDays());
            } else {
                completion.setCompletionRate(0.0);
            }

            result.add(completion);
        }

        return result;
    }

    @Override
    public double getTodayCompletionRate(Long userId) {
        LambdaQueryWrapper<HabitGoal> activeGoalsQuery = new LambdaQueryWrapper<>();
        activeGoalsQuery.eq(HabitGoal::getUserId, userId)
            .eq(HabitGoal::getStatus, 1);

        long totalActiveGoals = habitGoalDao.selectCount(activeGoalsQuery);

        if (totalActiveGoals == 0) {
            return 0.0;
        }

        long todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long todayEnd = todayStart + 86400000L;

        LambdaQueryWrapper<HabitCheckin> checkinQuery = new LambdaQueryWrapper<>();
        checkinQuery.eq(HabitCheckin::getUserId, userId)
            .ge(HabitCheckin::getCheckinDate, todayStart)
            .lt(HabitCheckin::getCheckinDate, todayEnd);

        long todayCheckinCount = habitCheckinDao.selectCount(checkinQuery);

        return (double) todayCheckinCount / totalActiveGoals;
    }

    @Override
    public int getLongestStreak(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);

        List<HabitGoal> goals = habitGoalDao.selectList(queryWrapper);

        if (goals.isEmpty()) {
            return 0;
        }

        return goals.stream()
            .mapToInt(goal -> goal.getLongestStreak() != null ? goal.getLongestStreak() : 0)
            .max()
            .orElse(0);
    }

    @Override
    public int getCurrentStreak(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId)
            .eq(HabitGoal::getStatus, 1);

        List<HabitGoal> goals = habitGoalDao.selectList(queryWrapper);

        if (goals.isEmpty()) {
            return 0;
        }

        return goals.stream()
            .mapToInt(goal -> goal.getCurrentStreak() != null ? goal.getCurrentStreak() : 0)
            .max()
            .orElse(0);
    }

    @Override
    public int countTotalGoals(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);

        return Math.toIntExact(habitGoalDao.selectCount(queryWrapper));
    }

    @Override
    public int countActiveGoals(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId)
            .eq(HabitGoal::getStatus, 1);

        return Math.toIntExact(habitGoalDao.selectCount(queryWrapper));
    }

    @Override
    public int countTotalFriends(Long userId) {
        LambdaQueryWrapper<HabitFriendship> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .eq(HabitFriendship::getUserId, userId).or()
            .eq(HabitFriendship::getFriendId, userId)
        ).eq(HabitFriendship::getStatus, 1);

        return Math.toIntExact(habitFriendshipDao.selectCount(queryWrapper));
    }
}
