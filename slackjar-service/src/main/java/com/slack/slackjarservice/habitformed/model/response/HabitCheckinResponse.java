package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;
import java.util.List;

/**
 * 打卡记录响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitCheckinResponse {

    /**
     * 打卡记录ID
     */
    private Long id;

    /**
     * 目标ID
     */
    private Long goalId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 目标图标
     */
    private String goalIcon;

    /**
     * 打卡日期
     */
    private Long checkinDate;

    /**
     * 打卡具体时间
     */
    private Long checkinTime;

    /**
     * 打卡内容
     */
    private String content;

    /**
     * 打卡图片URL列表
     */
    private List<String> imageUrls;

    /**
     * 心情
     */
    private String mood;

    /**
     * 可见性
     */
    private Integer visibility;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean liked;

    /**
     * 创建时间
     */
    private Long createTime;
}
