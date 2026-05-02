package com.slack.slackjarservice.dialogflow.model.response;

import com.slack.slackjarservice.dialogflow.model.dto.FlowDataDTO;
import lombok.Data;

/**
 * 对话流程详情响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class FlowDetailResponse {

    private Long id;

    private String name;

    private String description;

    /**
     * 流程数据对象
     */
    private FlowDataDTO flowData;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    private Long createTime;

    private Long updateTime;
}
