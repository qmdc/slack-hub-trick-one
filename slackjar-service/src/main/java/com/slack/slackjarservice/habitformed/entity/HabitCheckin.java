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
 * 打卡记录表(HabitCheckin)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_checkin")
public class HabitCheckin extends BaseModel {
    /**
     * 打卡记录ID
     */
    @TableId(type = IdType.AUTO)
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
     * 打卡日期（毫秒时间戳，取当天0点）
     */
    private Long checkinDate;

    /**
     * 打卡具体时间（毫秒时间戳）
     */
    private Long checkinTime;

    /**
     * 打卡内容/感想
     */
    private String content;

    /**
     * 打卡图片ID列表，多个用逗号分隔
     */
    private String imageIds;

    /**
     * 心情：happy, excited, tired, normal
     */
    private String mood;

    /**
     * 可见性：0-私密，1-好友可见，2-公开
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
}
