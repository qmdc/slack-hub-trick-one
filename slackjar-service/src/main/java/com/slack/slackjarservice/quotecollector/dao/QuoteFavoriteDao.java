package com.slack.slackjarservice.quotecollector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.quotecollector.entity.QuoteFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuoteFavoriteDao extends BaseMapper<QuoteFavorite> {

    @Select("SELECT * FROM quote_favorite WHERE quote_id = #{quoteId} AND user_id = #{userId}")
    QuoteFavorite selectByQuoteIdAndUserId(@Param("quoteId") Long quoteId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM quote_favorite WHERE quote_id = #{quoteId}")
    Integer countByQuoteId(@Param("quoteId") Long quoteId);
}