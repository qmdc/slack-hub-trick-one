package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;
import java.util.List;

/**
 * 打卡评论响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitCheckinCommentResponse {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 打卡记录ID
     */
    private Long checkinId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 评论用户名
     */
    private String username;

    /**
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像URL
     */
    private String avatarUrl;

    /**
     * 回复的评论ID
     */
    private Long replyCommentId;

    /**
     * 回复的用户ID
     */
    private Long replyUserId;

    /**
     * 回复的用户名
     */
    private String replyUsername;

    /**
     * 回复的用户昵称
     */
    private String replyNickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 子评论列表
     */
    private List<HabitCheckinCommentResponse> children;
}
