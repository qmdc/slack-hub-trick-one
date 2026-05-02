package com.slack.slackjarservice.dialogflow.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 对话流程保存请求
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class FlowSaveRequest {

    /**
     * 流程ID（新增时为空）
     */
    private Long id;

    /**
     * 流程名称
     */
    @NotBlank(message = "流程名称不能为空")
    private String name;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 流程数据（JSON格式）
     */
    @NotBlank(message = "流程数据不能为空")
    private String flowData;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
