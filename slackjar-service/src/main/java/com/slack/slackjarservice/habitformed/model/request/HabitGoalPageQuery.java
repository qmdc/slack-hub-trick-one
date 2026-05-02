package com.slack.slackjarservice.habitformed.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打卡目标分页查询请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HabitGoalPageQuery extends BasePagination {

    /**
     * 用户ID（查询指定用户的目标）
     */
    private Long userId;

    /**
     * 目标名称（模糊查询）
     */
    private String goalName;

    /**
     * 状态：0-停用，1-进行中，2-已完成
     */
    private Integer status;
}
