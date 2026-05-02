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
@TableName("transfer")
public class Transfer extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("from_account_id")
    private Long fromAccountId;

    @TableField("to_account_id")
    private Long toAccountId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("transfer_date")
    private Long transferDate;

    @TableField("remark")
    private String remark;
}