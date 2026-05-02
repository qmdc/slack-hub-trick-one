package com.slack.slackjarservice.dialogflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 节点类型字典表(NodeType)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("df_node_type")
public class NodeType extends BaseModel {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
