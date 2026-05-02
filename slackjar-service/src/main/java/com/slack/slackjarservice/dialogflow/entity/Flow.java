package com.slack.slackjarservice.dialogflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 对话流程表(Flow)表实体类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("df_flow")
public class Flow extends BaseModel {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 流程数据（JSON格式，包含nodes和edges）
     */
    @TableField(value = "flow_data")
    private String flowData;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
