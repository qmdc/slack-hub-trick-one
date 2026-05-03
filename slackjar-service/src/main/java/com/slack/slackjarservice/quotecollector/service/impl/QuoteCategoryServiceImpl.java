package com.slack.slackjarservice.quotecollector.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.quotecollector.dao.QuoteCategoryDao;
import com.slack.slackjarservice.quotecollector.entity.QuoteCategory;
import com.slack.slackjarservice.quotecollector.service.QuoteCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteCategoryServiceImpl extends ServiceImpl<QuoteCategoryDao, QuoteCategory> implements QuoteCategoryService {

    @Override
    public List<QuoteCategory> listAll() {
        LambdaQueryWrapper<QuoteCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(QuoteCategory::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public QuoteCategory getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public QuoteCategory create(QuoteCategory category) {
        baseMapper.insert(category);
        return category;
    }

    @Override
    public QuoteCategory update(QuoteCategory category) {
        baseMapper.updateById(category);
        return category;
    }

    @Override
    public void deleteById(Long id) {
        baseMapper.deleteById(id);
    }
}