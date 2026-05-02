package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Bill;

import java.math.BigDecimal;
import java.util.Map;

public interface BillService extends IService<Bill> {

    BigDecimal sumAmount(String type, Long startTime, Long endTime);

    Map<String, BigDecimal> sumByCategory(Long startTime, Long endTime);

    Map<String, Object> getTrendData(Long startTime, Long endTime);

    void createBill(Bill bill);
}