package com.slack.slackjarservice.habitformed.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 打卡请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitCheckinRequest {

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    private Long goalId;

    /**
     * 打卡日期（毫秒时间戳）
     */
    private Long checkinDate;

    /**
     * 打卡内容/感想
     */
    private String content;

    /**
     * 打卡图片ID列表
     */
    private String imageIds;

    /**
     * 心情
     */
    private String mood;

    /**
     * 可见性：0-私密，1-好友可见，2-公开
     */
    private Integer visibility;
}
