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
 * 好友关系表(HabitFriendship)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("habit_friendship")
public class HabitFriendship extends BaseModel {
    /**
     * 关系ID
     */
    @TableId(type = IdType.AUTO)
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
     * 确认时间（毫秒时间戳）
     */
    private Long confirmTime;
}
