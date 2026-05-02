package com.slack.slackjarservice.habitformed.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 成就徽章表(HabitAchievement)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_achievement")
public class HabitAchievement extends BaseModel {
    /**
     * 成就ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就编码（唯一标识）
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
     * 成就类型：0-打卡连续天数，1-总打卡次数，2-创建目标数，3-互动数
     */
    private Integer achievementType;

    /**
     * 条件类型：0-大于等于，1-等于，2-首次达成
     */
    private Integer conditionType;

    /**
     * 条件值（如连续7天填7）
     */
    private Integer conditionValue;

    /**
     * 关联的目标ID（null表示通用成就）
     */
    private Long relatedGoalId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 稀有度：1-普通，2-稀有，3-史诗，4-传说
     */
    private Integer rarity;
}
