package com.slack.slackjarservice.dialogflow.model.response;

import lombok.Data;

/**
 * 节点类型响应
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class NodeTypeResponse {

    private Long id;

    /**
     * 节点类型编码
     */
    private String typeCode;

    /**
     * 节点类型名称
     */
    private String typeName;

    /**
     * 节点类型描述
     */
    private String description;

    /**
     * 节点图标
     */
    private String icon;

    /**
     * 节点颜色
     */
    private String color;

    /**
     * 排序号
     */
    private Integer sortOrder;
}
