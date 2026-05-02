package com.slack.slackjarservice.habitformed.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 打卡目标表(HabitGoal)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_goal")
public class HabitGoal extends BaseModel {
    /**
     * 目标ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（创建者）
     */
    private Long userId;

    /**
     * 目标名称（如：早起、跑步、读书）
     */
    private String goalName;

    /**
     * 目标图标（emoji或图标名称）
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
     * 频率类型：0-每天，1-每周，2-自定义
     */
    private Integer frequencyType;

    /**
     * 频率值：每周选哪几天，用逗号分隔如1,3,5表示周一、三、五
     */
    private String frequencyValue;

    /**
     * 提醒时间（HH:mm格式）
     */
    private String remindTime;

    /**
     * 是否开启提醒：0-关闭，1-开启
     */
    private Integer remindEnabled;

    /**
     * 状态：0-停用，1-进行中，2-已完成
     */
    private Integer status;

    /**
     * 开始日期（毫秒时间戳）
     */
    private Long startDate;

    /**
     * 结束日期（毫秒时间戳，可选）
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
     * 最后打卡日期（毫秒时间戳）
     */
    private Long lastCheckinDate;
}
