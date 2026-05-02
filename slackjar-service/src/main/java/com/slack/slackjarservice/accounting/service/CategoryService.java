package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> listByType(String type);

    List<Category> listIncomeCategories();

    List<Category> listExpenseCategories();
}