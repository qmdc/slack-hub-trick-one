package com.slack.slackjarservice.accounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("budget")
public class Budget extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category_id")
    private Long categoryId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("period_type")
    private String periodType;

    @TableField("year")
    private Integer year;

    @TableField("month")
    private Integer month;

    @TableField("spent_amount")
    private BigDecimal spentAmount;

    @TableField("is_active")
    private Integer isActive;

    @TableField("remark")
    private String remark;
}