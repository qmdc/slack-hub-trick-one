package com.slack.slackjarservice.quotecollector.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.quotecollector.dao.QuoteFavoriteDao;
import com.slack.slackjarservice.quotecollector.entity.QuoteFavorite;
import com.slack.slackjarservice.quotecollector.service.QuoteFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class QuoteFavoriteServiceImpl extends ServiceImpl<QuoteFavoriteDao, QuoteFavorite> implements QuoteFavoriteService {

    @Override
    public boolean isFavorite(Long quoteId, Long userId) {
        QuoteFavorite favorite = baseMapper.selectByQuoteIdAndUserId(quoteId, userId);
        return favorite != null;
    }

    @Override
    public void addFavorite(Long quoteId, Long userId) {
        if (!isFavorite(quoteId, userId)) {
            QuoteFavorite favorite = new QuoteFavorite();
            favorite.setQuoteId(quoteId);
            favorite.setUserId(userId);
            baseMapper.insert(favorite);
        }
    }

    @Override
    public void removeFavorite(Long quoteId, Long userId) {
        QuoteFavorite favorite = baseMapper.selectByQuoteIdAndUserId(quoteId, userId);
        if (favorite != null) {
            baseMapper.deleteById(favorite.getId());
        }
    }

    @Override
    public Integer getFavoriteCount(Long quoteId) {
        return baseMapper.countByQuoteId(quoteId);
    }
}