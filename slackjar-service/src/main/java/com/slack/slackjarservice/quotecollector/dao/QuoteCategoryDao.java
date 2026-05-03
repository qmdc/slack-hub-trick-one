package com.slack.slackjarservice.quotecollector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.quotecollector.entity.QuoteCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuoteCategoryDao extends BaseMapper<QuoteCategory> {
}