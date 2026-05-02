package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Budget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BudgetService extends IService<Budget> {

    List<Budget> listByPeriod(String periodType, Integer year, Integer month);

    Budget getTotalBudget(Integer year, Integer month);

    void updateSpentAmount(Long budgetId, BigDecimal amount);

    Map<String, Object> checkBudgetExceed(Long categoryId, BigDecimal amount, Integer year, Integer month);
}