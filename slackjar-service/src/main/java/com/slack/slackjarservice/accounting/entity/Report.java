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
@TableName("report")
public class Report extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("report_type")
    private String reportType;

    @TableField("year")
    private Integer year;

    @TableField("month")
    private Integer month;

    @TableField("total_income")
    private BigDecimal totalIncome;

    @TableField("total_expense")
    private BigDecimal totalExpense;

    @TableField("savings_rate")
    private BigDecimal savingsRate;

    @TableField("data_json")
    private String dataJson;

    @TableField("is_pushed")
    private Integer isPushed;

    @TableField("push_time")
    private Long pushTime;
}