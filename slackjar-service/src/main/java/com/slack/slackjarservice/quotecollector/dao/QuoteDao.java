package com.slack.slackjarservice.quotecollector.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slack.slackjarservice.quotecollector.entity.Quote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuoteDao extends BaseMapper<Quote> {

    @Select("SELECT q.* FROM quote q ORDER BY RAND() LIMIT 1")
    Quote selectRandom();

    @Select("SELECT q.* FROM quote q WHERE q.category_id = #{categoryId} ORDER BY RAND() LIMIT 1")
    Quote selectRandomByCategory(@Param("categoryId") Long categoryId);

    @Select("SELECT q.* FROM quote q WHERE q.id IN (SELECT f.quote_id FROM quote_favorite f WHERE f.user_id = #{userId})")
    List<Quote> selectFavoritesByUserId(@Param("userId") Long userId);

    IPage<Quote> selectByCategory(Page<Quote> page, @Param("categoryId") Long categoryId);

    @Select("SELECT q.* FROM quote q WHERE q.content LIKE CONCAT('%', #{keyword}, '%') OR q.author LIKE CONCAT('%', #{keyword}, '%')")
    IPage<Quote> searchQuotes(Page<Quote> page, @Param("keyword") String keyword);
}