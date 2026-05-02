package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;

/**
 * 打卡目标响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitGoalResponse {

    /**
     * 目标ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 目标图标
     */
    private String goalIcon;

    /**
     * 目标颜色
     */
    private String goalColor;

    /**
     * 目标描述
     */
    private String description;

    /**
     * 频率类型
     */
    private Integer frequencyType;

    /**
     * 频率值
     */
    private String frequencyValue;

    /**
     * 提醒时间
     */
    private String remindTime;

    /**
     * 是否开启提醒
     */
    private Integer remindEnabled;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始日期
     */
    private Long startDate;

    /**
     * 结束日期
     */
    private Long endDate;

    /**
     * 目标总天数
     */
    private Integer totalDays;

    /**
     * 已打卡天数
     */
    private Integer checkinCount;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最长连续打卡天数
     */
    private Integer longestStreak;

    /**
     * 最后打卡日期
     */
    private Long lastCheckinDate;

    /**
     * 完成率
     */
    private Double completionRate;

    /**
     * 今日是否已打卡
     */
    private Boolean todayChecked;

    /**
     * 创建时间
     */
    private Long createTime;
}
