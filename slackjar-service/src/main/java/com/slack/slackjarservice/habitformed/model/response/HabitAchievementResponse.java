package com.slack.slackjarservice.habitformed.model.response;

import lombok.Data;

/**
 * 成就徽章响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitAchievementResponse {

    /**
     * 成就ID
     */
    private Long id;

    /**
     * 成就编码
     */
    private String achievementCode;

    /**
     * 成就名称
     */
    private String achievementName;

    /**
     * 成就图标
     */
    private String achievementIcon;

    /**
     * 成就颜色
     */
    private String achievementColor;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就类型
     */
    private Integer achievementType;

    /**
     * 条件类型
     */
    private Integer conditionType;

    /**
     * 条件值
     */
    private Integer conditionValue;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 稀有度：1-普通，2-稀有，3-史诗，4-传说
     */
    private Integer rarity;

    /**
     * 是否已解锁
     */
    private Boolean unlocked;

    /**
     * 解锁时间
     */
    private Long unlockTime;

    /**
     * 进度百分比
     */
    private Double progress;

    /**
     * 当前值
     */
    private Long currentValue;
}
