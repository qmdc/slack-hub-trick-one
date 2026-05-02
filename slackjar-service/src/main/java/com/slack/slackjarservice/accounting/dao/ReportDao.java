package com.slack.slackjarservice.accounting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.accounting.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportDao extends BaseMapper<Report> {

    Report selectByTypeAndPeriod(@Param("reportType") String reportType, @Param("year") Integer year, @Param("month") Integer month);
}