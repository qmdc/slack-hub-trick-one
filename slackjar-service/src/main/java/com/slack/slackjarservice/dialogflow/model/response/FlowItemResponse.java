package com.slack.slackjarservice.dialogflow.model.response;

import lombok.Data;

/**
 * 对话流程分页项响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class FlowItemResponse {

    private Long id;

    private String name;

    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    private Long createTime;

    private Long updateTime;
}
