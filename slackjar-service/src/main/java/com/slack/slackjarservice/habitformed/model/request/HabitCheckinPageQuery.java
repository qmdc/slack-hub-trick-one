package com.slack.slackjarservice.habitformed.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打卡记录分页查询请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HabitCheckinPageQuery extends BasePagination {

    /**
     * 目标ID
     */
    private Long goalId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 开始日期
     */
    private Long startDate;

    /**
     * 结束日期
     */
    private Long endDate;

    /**
     * 可见性：0-私密，1-好友可见，2-公开
     */
    private Integer visibility;
}
