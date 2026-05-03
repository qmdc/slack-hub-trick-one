package com.slack.slackjarservice.quotecollector.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.quotecollector.entity.Quote;

import java.util.List;

public interface QuoteService extends IService<Quote> {

    Quote getById(Long id);

    Quote create(Quote quote);

    Quote update(Quote quote);

    void deleteById(Long id);

    Quote getRandomQuote(Long categoryId);

    IPage<Quote> getQuotesByCategory(Page<Quote> page, Long categoryId);

    IPage<Quote> searchQuotes(Page<Quote> page, String keyword);

    List<Quote> getFavoriteQuotes(Long userId);

    void incrementViewCount(Long id);
}