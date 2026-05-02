package com.slack.slackjarservice.habitformed.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitCommentRequest {

    /**
     * 打卡记录ID
     */
    @NotNull(message = "打卡记录ID不能为空")
    private Long checkinId;

    /**
     * 回复的评论ID（null表示一级评论）
     */
    private Long replyCommentId;

    /**
     * 回复的用户ID
     */
    private Long replyUserId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
