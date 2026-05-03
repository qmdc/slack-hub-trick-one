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
@TableName("password_category")
public class PasswordCategory extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String code;

    private String color;
}
