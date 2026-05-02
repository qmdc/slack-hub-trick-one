package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 统计数据响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitStatisticsResponse {

    /**
     * 总目标数
     */
    private Integer totalGoals;

    /**
     * 进行中目标数
     */
    private Integer activeGoals;

    /**
     * 总打卡天数
     */
    private Integer totalCheckinDays;

    /**
     * 最长连续打卡天数
     */
    private Integer longestStreak;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 获得的成就数
     */
    private Integer earnedAchievements;

    /**
     * 总成就数
     */
    private Integer totalAchievements;

    /**
     * 总好友数
     */
    private Integer totalFriends;

    /**
     * 获得的点赞数
     */
    private Integer totalLikes;

    /**
     * 发表的评论数
     */
    private Integer totalComments;

    /**
     * 本周打卡数据（按天统计）
     * key: 日期字符串，value: 打卡数
     */
    private Map<String, Integer> weeklyCheckinData;

    /**
     * 本月打卡数据（按天统计）
     */
    private Map<String, Integer> monthlyCheckinData;

    /**
     * 各目标完成率
     */
    private List<GoalCompletion> goalCompletions;

    /**
     * 近6个月每月完成率
     */
    private List<MonthlyRate> monthlyRates;

    /**
     * 目标完成率数据结构
     */
    @Data
    public static class GoalCompletion {
        private Long goalId;
        private String goalName;
        private String goalIcon;
        private Integer checkinCount;
        private Integer totalDays;
        private Double completionRate;
    }

    /**
     * 月完成率数据结构
     */
    @Data
    public static class MonthlyRate {
        private String month;
        private Double rate;
        private Integer checkinDays;
    }
}
