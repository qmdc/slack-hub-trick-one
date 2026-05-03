package com.slack.slackjarservice.passwordmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("password_entry")
public class PasswordEntry extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String website;

    private String websiteName;

    private String account;

    private String password;

    private String category;

    private Integer passwordStrength;

    private Long lastLoginTime;

    private String notes;
}
