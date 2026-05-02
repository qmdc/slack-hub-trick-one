package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;

/**
 * 好友信息响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitFriendResponse {

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友ID
     */
    private Long friendId;

    /**
     * 好友用户名
     */
    private String friendUsername;

    /**
     * 好友昵称
     */
    private String friendNickname;

    /**
     * 好友头像URL
     */
    private String friendAvatarUrl;

    /**
     * 状态：0-待确认，1-已确认，2-已拒绝，3-已取消
     */
    private Integer status;

    /**
     * 发起申请的用户ID
     */
    private Long applyUserId;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 确认时间
     */
    private Long confirmTime;

    /**
     * 今日打卡数
     */
    private Integer todayCheckinCount;

    /**
     * 最长连续打卡天数
     */
    private Integer longestStreak;

    /**
     * 创建时间
     */
    private Long createTime;
}
