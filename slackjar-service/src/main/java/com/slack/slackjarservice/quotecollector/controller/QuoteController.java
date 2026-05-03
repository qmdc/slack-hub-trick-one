package com.slack.slackjarservice.quotecollector.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.quotecollector.entity.Quote;
import com.slack.slackjarservice.quotecollector.service.QuoteFavoriteService;
import com.slack.slackjarservice.quotecollector.service.QuoteService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quotes")
public class QuoteController extends BaseController {

    private final QuoteService quoteService;
    private final QuoteFavoriteService quoteFavoriteService;

    public QuoteController(QuoteService quoteService, QuoteFavoriteService quoteFavoriteService) {
        this.quoteService = quoteService;
        this.quoteFavoriteService = quoteFavoriteService;
    }

    @GetMapping
    public ApiResponse<IPage<Quote>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Quote> pageParam = new Page<>(page, size);
        IPage<Quote> result;
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = quoteService.searchQuotes(pageParam, keyword);
        } else {
            result = quoteService.getQuotesByCategory(pageParam, categoryId);
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Quote> getById(@PathVariable Long id) {
        quoteService.incrementViewCount(id);
        Quote quote = quoteService.getById(id);
        return ApiResponse.success(quote);
    }

    @PostMapping
    public ApiResponse<Quote> create(@RequestBody Quote quote) {
        Quote created = quoteService.create(quote);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Quote> update(@PathVariable Long id, @RequestBody Quote quote) {
        quote.setId(id);
        Quote updated = quoteService.update(quote);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        quoteService.deleteById(id);
        return ApiResponse.success();
    }

    @GetMapping("/random")
    public ApiResponse<Quote> getRandom(@RequestParam(required = false) Long categoryId) {
        Quote quote = quoteService.getRandomQuote(categoryId);
        return ApiResponse.success(quote);
    }

    @GetMapping("/favorites")
    public ApiResponse<List<Quote>> getFavorites() {
        Long userId = getLoginUserId();
        List<Quote> favorites = quoteService.getFavoriteQuotes(userId);
        return ApiResponse.success(favorites);
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Map<String, Object>> toggleFavorite(@PathVariable Long id) {
        Long userId = getLoginUserId();
        boolean isFavorite = quoteFavoriteService.isFavorite(id, userId);
        
        if (isFavorite) {
            quoteFavoriteService.removeFavorite(id, userId);
        } else {
            quoteFavoriteService.addFavorite(id, userId);
        }
        
        boolean newFavoriteStatus = !isFavorite;
        int favoriteCount = quoteFavoriteService.getFavoriteCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorite", newFavoriteStatus);
        result.put("favoriteCount", favoriteCount);
        
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}/favorite-status")
    public ApiResponse<Map<String, Object>> getFavoriteStatus(@PathVariable Long id) {
        Long userId = getLoginUserId();
        boolean isFavorite = quoteFavoriteService.isFavorite(id, userId);
        int favoriteCount = quoteFavoriteService.getFavoriteCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        result.put("favoriteCount", favoriteCount);
        
        return ApiResponse.success(result);
    }
}