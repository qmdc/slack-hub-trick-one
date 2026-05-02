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
@TableName("account")
public class Account extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("bank_name")
    private String bankName;

    @TableField("card_number")
    private String cardNumber;

    @TableField("icon")
    private String icon;

    @TableField("color")
    private String color;

    @TableField("is_default")
    private Integer isDefault;

    @TableField("is_active")
    private Integer isActive;

    @TableField("remark")
    private String remark;
}