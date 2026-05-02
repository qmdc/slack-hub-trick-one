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
 * 打卡评论表(HabitCheckinComment)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_checkin_comment")
public class HabitCheckinComment extends BaseModel {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
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
    private String content;
}
