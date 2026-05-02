package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.CategoryDao;
import com.slack.slackjarservice.accounting.entity.Category;
import com.slack.slackjarservice.accounting.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Override
    public List<Category> listByType(String type) {
        return baseMapper.selectByType(type);
    }

    @Override
    public List<Category> listIncomeCategories() {
        return listByType("INCOME");
    }

    @Override
    public List<Category> listExpenseCategories() {
        return listByType("EXPENSE");
    }
}