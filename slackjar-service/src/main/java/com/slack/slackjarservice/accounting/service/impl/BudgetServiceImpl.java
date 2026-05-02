package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.BudgetDao;
import com.slack.slackjarservice.accounting.entity.Budget;
import com.slack.slackjarservice.accounting.service.BudgetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetDao, Budget> implements BudgetService {

    @Override
    public List<Budget> listByPeriod(String periodType, Integer year, Integer month) {
        return baseMapper.selectByPeriod(periodType, year, month);
    }

    @Override
    public Budget getTotalBudget(Integer year, Integer month) {
        LambdaQueryWrapper<Budget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Budget::getPeriodType, "MONTHLY")
                   .eq(Budget::getYear, year)
                   .eq(Budget::getMonth, month)
                   .isNull(Budget::getCategoryId);
        return getOne(queryWrapper);
    }

    @Override
    public void updateSpentAmount(Long budgetId, BigDecimal amount) {
        Budget budget = getById(budgetId);
        if (budget != null) {
            budget.setSpentAmount(budget.getSpentAmount().add(amount));
            updateById(budget);
        }
    }

    @Override
    public Map<String, Object> checkBudgetExceed(Long categoryId, BigDecimal amount, Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();
        result.put("exceeded", false);
        result.put("message", "");
        
        LambdaQueryWrapper<Budget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Budget::getPeriodType, "MONTHLY")
                   .eq(Budget::getYear, year)
                   .eq(Budget::getMonth, month)
                   .eq(Budget::getIsActive, 1);
        
        if (categoryId != null) {
            queryWrapper.eq(Budget::getCategoryId, categoryId);
        } else {
            queryWrapper.isNull(Budget::getCategoryId);
        }
        
        Budget budget = getOne(queryWrapper);
        if (budget != null) {
            BigDecimal totalSpent = budget.getSpentAmount().add(amount);
            if (totalSpent.compareTo(budget.getAmount()) > 0) {
                result.put("exceeded", true);
                result.put("message", "预算超支警告");
                result.put("budgetAmount", budget.getAmount());
                result.put("spentAmount", totalSpent);
            }
        }
        
        return result;
    }
}