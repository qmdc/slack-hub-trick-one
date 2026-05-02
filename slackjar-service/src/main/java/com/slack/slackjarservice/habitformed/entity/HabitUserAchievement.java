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
 * 用户成就表(HabitUserAchievement)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_user_achievement")
public class HabitUserAchievement extends BaseModel {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 成就ID
     */
    private Long achievementId;

    /**
     * 解锁时间（毫秒时间戳）
     */
    private Long unlockTime;

    /**
     * 关联的打卡记录ID（如果有）
     */
    private Long relatedCheckinId;

    /**
     * 关联的目标ID（如果有）
     */
    private Long relatedGoalId;
}
