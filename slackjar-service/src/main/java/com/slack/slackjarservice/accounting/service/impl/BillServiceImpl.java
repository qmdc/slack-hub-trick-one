package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.BillDao;
import com.slack.slackjarservice.accounting.entity.Bill;
import com.slack.slackjarservice.accounting.entity.Category;
import com.slack.slackjarservice.accounting.service.AccountService;
import com.slack.slackjarservice.accounting.service.BillService;
import com.slack.slackjarservice.accounting.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillServiceImpl extends ServiceImpl<BillDao, Bill> implements BillService {

    private final AccountService accountService;
    private final CategoryService categoryService;

    public BillServiceImpl(AccountService accountService, CategoryService categoryService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @Override
    public BigDecimal sumAmount(String type, Long startTime, Long endTime) {
        return baseMapper.sumAmountByTimeRange(type, startTime, endTime);
    }

    @Override
    public Map<String, BigDecimal> sumByCategory(Long startTime, Long endTime) {
        Map<String, BigDecimal> result = new HashMap<>();
        List<Category> categories = categoryService.listExpenseCategories();
        
        for (Category category : categories) {
            BigDecimal amount = baseMapper.sumAmountByCategory(category.getId(), startTime, endTime);
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }
            result.put(category.getName(), amount);
        }
        return result;
    }

    @Override
    public Map<String, Object> getTrendData(Long startTime, Long endTime) {
        Map<String, Object> result = new HashMap<>();
        
        List<String> labels = new ArrayList<>();
        List<BigDecimal> incomeData = new ArrayList<>();
        List<BigDecimal> expenseData = new ArrayList<>();
        
        long current = startTime;
        while (current <= endTime) {
            labels.add(String.valueOf(current));
            incomeData.add(BigDecimal.ZERO);
            expenseData.add(BigDecimal.ZERO);
            current += 86400000L;
        }
        
        result.put("labels", labels);
        result.put("income", incomeData);
        result.put("expense", expenseData);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBill(Bill bill) {
        save(bill);
        accountService.updateBalance(bill.getAccountId(), bill.getAmount(), bill.getType());
    }
}