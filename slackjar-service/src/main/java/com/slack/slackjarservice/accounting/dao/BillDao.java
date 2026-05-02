package com.slack.slackjarservice.accounting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.accounting.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface BillDao extends BaseMapper<Bill> {

    BigDecimal sumAmountByTimeRange(@Param("type") String type, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    BigDecimal sumAmountByCategory(@Param("categoryId") Long categoryId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}