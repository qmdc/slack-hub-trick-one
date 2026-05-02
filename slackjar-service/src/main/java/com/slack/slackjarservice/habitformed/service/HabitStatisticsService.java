package com.slack.slackjarservice.habitformed.service;

import com.slack.slackjarservice.habitformed.model.response.HabitStatisticsResponse;
import com.slack.slackjarservice.habitformed.model.response.HomePageResponse;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface HabitStatisticsService {

    /**
     * 获取首页数据
     *
     * @param userId 当前用户ID
     * @return 首页响应
     */
    HomePageResponse getHomePageData(Long userId);

    /**
     * 获取统计数据
     *
     * @param userId 当前用户ID
     * @return 统计响应
     */
    HabitStatisticsResponse getStatistics(Long userId);

    /**
     * 获取本周打卡数据
     *
     * @param userId 用户ID
     * @return 每日打卡数映射
     */
    Map<String, Integer> getWeeklyCheckinData(Long userId);

    /**
     * 获取本月打卡数据
     *
     * @param userId 用户ID
     * @return 每日打卡数映射
     */
    Map<String, Integer> getMonthlyCheckinData(Long userId);

    /**
     * 获取近6个月每月完成率
     *
     * @param userId 用户ID
     * @return 月完成率列表
     */
    List<HabitStatisticsResponse.MonthlyRate> getMonthlyRates(Long userId);

    /**
     * 获取各目标完成率
     *
     * @param userId 用户ID
     * @return 目标完成率列表
     */
    List<HabitStatisticsResponse.GoalCompletion> getGoalCompletions(Long userId);

    /**
     * 获取今日完成率
     *
     * @param userId 用户ID
     * @return 完成率（0-1）
     */
    double getTodayCompletionRate(Long userId);

    /**
     * 获取用户最长连续打卡天数
     *
     * @param userId 用户ID
     * @return 天数
     */
    int getLongestStreak(Long userId);

    /**
     * 获取用户当前连续打卡天数
     *
     * @param userId 用户ID
     * @return 天数
     */
    int getCurrentStreak(Long userId);

    /**
     * 统计用户总目标数
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countTotalGoals(Long userId);

    /**
     * 统计用户进行中目标数
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countActiveGoals(Long userId);

    /**
     * 统计用户好友数
     *
     * @param userId 用户ID
     * @return 数量
     */
    int countTotalFriends(Long userId);
}
