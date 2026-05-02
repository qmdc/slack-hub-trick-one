package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;
import java.util.List;

/**
 * 首页响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HomePageResponse {

    /**
     * 今日待打卡目标列表
     */
    private List<HabitGoalResponse> pendingGoals;

    /**
     * 今日已完成目标列表
     */
    private List<HabitGoalResponse> completedGoals;

    /**
     * 今日完成率
     */
    private Double todayCompletionRate;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最长连续打卡天数
     */
    private Integer longestStreak;

    /**
     * 好友动态列表
     */
    private List<HabitCheckinResponse> friendFeed;

    /**
     * 待处理好友申请数
     */
    private Integer pendingFriendRequests;

    /**
     * 最近解锁的成就
     */
    private HabitAchievementResponse recentAchievement;
}
