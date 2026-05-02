package com.slack.slackjarservice.habitformed.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 好友关系请求（添加好友、确认好友等）
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class HabitFriendshipRequest {

    /**
     * 好友ID
     */
    @NotNull(message = "好友ID不能为空")
    private Long friendId;

    /**
     * 申请理由
     */
    private String applyReason;
}
