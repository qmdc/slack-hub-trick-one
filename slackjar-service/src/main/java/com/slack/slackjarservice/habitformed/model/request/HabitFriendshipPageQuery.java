package com.slack.slackjarservice.habitformed.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 好友关系分页查询请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HabitFriendshipPageQuery extends BasePagination {

    /**
     * 状态：0-待确认，1-已确认，2-已拒绝，3-已取消
     */
    private Integer status;

    /**
     * 搜索关键词（用户名或昵称）
     */
    private String keyword;
}
