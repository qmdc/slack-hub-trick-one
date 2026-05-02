package com.slack.slackjarservice.accounting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.accounting.entity.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetDao extends BaseMapper<Budget> {

    List<Budget> selectByPeriod(@Param("periodType") String periodType, @Param("year") Integer year, @Param("month") Integer month);
}