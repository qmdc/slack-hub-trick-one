package com.slack.slackjarservice.accounting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.accounting.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {

    List<Category> selectByType(@Param("type") String type);
}