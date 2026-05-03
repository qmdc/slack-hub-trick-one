package com.slack.slackjarservice.quotecollector.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.quotecollector.entity.QuoteFavorite;

public interface QuoteFavoriteService extends IService<QuoteFavorite> {

    boolean isFavorite(Long quoteId, Long userId);

    void addFavorite(Long quoteId, Long userId);

    void removeFavorite(Long quoteId, Long userId);

    Integer getFavoriteCount(Long quoteId);
}