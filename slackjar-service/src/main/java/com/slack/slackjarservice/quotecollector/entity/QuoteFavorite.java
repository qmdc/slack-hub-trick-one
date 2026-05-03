package com.slack.slackjarservice.quotecollector.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quote_favorite")
public class QuoteFavorite extends BaseModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("quote_id")
    private Long quoteId;

    @TableField("user_id")
    private Long userId;
}