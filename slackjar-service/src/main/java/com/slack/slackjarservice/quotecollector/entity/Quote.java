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
@TableName("quote")
public class Quote extends BaseModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("content")
    private String content;

    @TableField("author")
    private String author;

    @TableField("source")
    private String source;

    @TableField("category_id")
    private Long categoryId;

    @TableField("is_favorite")
    private Integer isFavorite;

    @TableField("view_count")
    private Integer viewCount;
}