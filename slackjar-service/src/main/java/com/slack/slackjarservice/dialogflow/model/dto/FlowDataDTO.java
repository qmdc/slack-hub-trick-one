package com.slack.slackjarservice.dialogflow.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 流程数据DTO
 * 用于存储React Flow的画布数据
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
public class FlowDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点列表
     */
    private List<NodeDTO> nodes;

    /**
     * 边列表
     */
    private List<EdgeDTO> edges;

    /**
     * 视口数据
     */
    private ViewportDTO viewport;

    /**
     * 节点数据DTO
     */
    @Data
    public static class NodeDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 节点ID
         */
        private String id;

        /**
         * 节点类型
         */
        private String type;

        /**
         * 节点位置
         */
        private PositionDTO position;

        /**
         * 节点数据
         */
        private NodeDataDTO data;

        /**
         * 节点宽度
         */
        private Integer width;

        /**
         * 节点高度
         */
        private Integer height;

        /**
         * 是否选中
         */
        private Boolean selected;

        /**
         * 是否拖动
         */
        private Boolean dragging;
    }

    /**
     * 节点数据DTO
     */
    @Data
    public static class NodeDataDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 节点标签
         */
        private String label;

        /**
         * 节点配置
         */
        private NodeConfigDTO config;
    }

    /**
     * 节点配置DTO
     */
    @Data
    public static class NodeConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        // 用户输入节点配置
        /**
         * 提示文本
         */
        private String placeholder;

        /**
         * 输入类型：text-文本, options-选项
         */
        private String inputType;

        /**
         * 选项列表
         */
        private List<OptionItemDTO> options;

        // AI回复节点配置
        /**
         * 回复内容
         */
        private String content;

        /**
         * 消息类型：text-文本, card-卡片
         */
        private String messageType;

        /**
         * 卡片标题
         */
        private String cardTitle;

        /**
         * 卡片描述
         */
        private String cardDescription;

        /**
         * 卡片图片URL
         */
        private String cardImageUrl;

        /**
         * 卡片按钮列表
         */
        private List<CardButtonDTO> cardButtons;

        // 条件分支节点配置
        /**
         * 条件列表
         */
        private List<ConditionItemDTO> conditions;

        /**
         * 默认分支目标节点ID
         */
        private String defaultTarget;
    }

    /**
     * 选项项DTO
     */
    @Data
    public static class OptionItemDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 选项标签
         */
        private String label;

        /**
         * 选项值
         */
        private String value;
    }

    /**
     * 卡片按钮DTO
     */
    @Data
    public static class CardButtonDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 按钮文本
         */
        private String text;

        /**
         * 按钮类型：link-链接, action-动作
         */
        private String type;

        /**
         * 链接URL或动作值
         */
        private String value;
    }

    /**
     * 条件项DTO
     */
    @Data
    public static class ConditionItemDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 条件ID
         */
        private String id;

        /**
         * 条件表达式
         */
        private String expression;

        /**
         * 条件描述
         */
        private String description;

        /**
         * 目标节点ID
         */
        private String targetNodeId;

        /**
         * 条件参数
         */
        private Map<String, Object> params;
    }

    /**
     * 边数据DTO
     */
    @Data
    public static class EdgeDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 边ID
         */
        private String id;

        /**
         * 源节点ID
         */
        private String source;

        /**
         * 目标节点ID
         */
        private String target;

        /**
         * 源节点出口
         */
        private String sourceHandle;

        /**
         * 目标节点入口
         */
        private String targetHandle;

        /**
         * 边类型
         */
        private String type;

        /**
         * 是否动画
         */
        private Boolean animated;

        /**
         * 边标签
         */
        private String label;

        /**
         * 边数据
         */
        private Map<String, Object> data;

        /**
         * 边样式
         */
        private Map<String, Object> style;
    }

    /**
     * 位置DTO
     */
    @Data
    public static class PositionDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * X坐标
         */
        private Double x;

        /**
         * Y坐标
         */
        private Double y;
    }

    /**
     * 视口DTO
     */
    @Data
    public static class ViewportDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * X坐标
         */
        private Double x;

        /**
         * Y坐标
         */
        private Double y;

        /**
         * 缩放比例
         */
        private Double zoom;
    }
}
