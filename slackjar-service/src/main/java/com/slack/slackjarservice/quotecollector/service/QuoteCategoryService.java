package com.slack.slackjarservice.quotecollector.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.quotecollector.entity.QuoteCategory;

import java.util.List;

public interface QuoteCategoryService extends IService<QuoteCategory> {

    List<QuoteCategory> listAll();

    QuoteCategory getById(Long id);

    QuoteCategory create(QuoteCategory category);

    QuoteCategory update(QuoteCategory category);

    void deleteById(Long id);
}