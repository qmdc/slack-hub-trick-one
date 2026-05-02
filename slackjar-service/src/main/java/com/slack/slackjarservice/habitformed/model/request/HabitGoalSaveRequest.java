package com.slack.slackjarservice.habitformed.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 打卡目标保存请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitGoalSaveRequest {

    /**
     * 目标ID（新增时为空）
     */
    private Long id;

    /**
     * 目标名称
     */
    @NotBlank(message = "目标名称不能为空")
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
     * 频率类型：0-每天，1-每周，2-自定义
     */
    private Integer frequencyType;

    /**
     * 频率值
     */
    private String frequencyValue;

    /**
     * 提醒时间（HH:mm格式）
     */
    private String remindTime;

    /**
     * 是否开启提醒
     */
    private Integer remindEnabled;

    /**
     * 开始日期
     */
    private Long startDate;

    /**
     * 结束日期
     */
    private Long endDate;
}
