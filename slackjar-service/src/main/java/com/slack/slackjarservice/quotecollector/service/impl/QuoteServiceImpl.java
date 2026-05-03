package com.slack.slackjarservice.quotecollector.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.quotecollector.dao.QuoteDao;
import com.slack.slackjarservice.quotecollector.entity.Quote;
import com.slack.slackjarservice.quotecollector.service.QuoteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteServiceImpl extends ServiceImpl<QuoteDao, Quote> implements QuoteService {

    @Override
    public Quote getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Quote create(Quote quote) {
        quote.setIsFavorite(0);
        quote.setViewCount(0);
        baseMapper.insert(quote);
        return quote;
    }

    @Override
    public Quote update(Quote quote) {
        baseMapper.updateById(quote);
        return quote;
    }

    @Override
    public void deleteById(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public Quote getRandomQuote(Long categoryId) {
        if (categoryId == null) {
            return baseMapper.selectRandom();
        }
        return baseMapper.selectRandomByCategory(categoryId);
    }

    @Override
    public IPage<Quote> getQuotesByCategory(Page<Quote> page, Long categoryId) {
        if (categoryId == null) {
            return baseMapper.selectPage(page, null);
        }
        return baseMapper.selectByCategory(page, categoryId);
    }

    @Override
    public IPage<Quote> searchQuotes(Page<Quote> page, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return baseMapper.selectPage(page, null);
        }
        return baseMapper.searchQuotes(page, keyword);
    }

    @Override
    public List<Quote> getFavoriteQuotes(Long userId) {
        return baseMapper.selectFavoritesByUserId(userId);
    }

    @Override
    public void incrementViewCount(Long id) {
        Quote quote = baseMapper.selectById(id);
        if (quote != null) {
            quote.setViewCount(quote.getViewCount() + 1);
            baseMapper.updateById(quote);
        }
    }
}