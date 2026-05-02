package com.slack.slackjarservice.dialogflow.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对话流程分页查询请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowPageQuery extends BasePagination {

    /**
     * 流程名称（模糊查询）
     */
    private String name;

    /**
     * 流程描述（模糊查询）
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
