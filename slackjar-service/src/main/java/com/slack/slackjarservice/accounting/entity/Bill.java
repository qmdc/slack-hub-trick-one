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
@TableName("bill")
public class Bill extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("type")
    private String type;

    @TableField("category_id")
    private Long categoryId;

    @TableField("account_id")
    private Long accountId;

    @TableField("payee")
    private String payee;

    @TableField("remark")
    private String remark;

    @TableField("bill_date")
    private Long billDate;

    @TableField("location")
    private String location;

    @TableField("receipt_image")
    private String receiptImage;
}