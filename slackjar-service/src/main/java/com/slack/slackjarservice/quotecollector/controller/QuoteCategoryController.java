package com.slack.slackjarservice.quotecollector.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.quotecollector.entity.QuoteCategory;
import com.slack.slackjarservice.quotecollector.service.QuoteCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quote-categories")
public class QuoteCategoryController extends BaseController {

    private final QuoteCategoryService quoteCategoryService;

    public QuoteCategoryController(QuoteCategoryService quoteCategoryService) {
        this.quoteCategoryService = quoteCategoryService;
    }

    @GetMapping
    public ApiResponse<List<QuoteCategory>> list() {
        List<QuoteCategory> categories = quoteCategoryService.listAll();
        return ApiResponse.success(categories);
    }

    @GetMapping("/{id}")
    public ApiResponse<QuoteCategory> getById(@PathVariable Long id) {
        QuoteCategory category = quoteCategoryService.getById(id);
        return ApiResponse.success(category);
    }

    @PostMapping
    public ApiResponse<QuoteCategory> create(@RequestBody QuoteCategory category) {
        QuoteCategory created = quoteCategoryService.create(category);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<QuoteCategory> update(@PathVariable Long id, @RequestBody QuoteCategory category) {
        category.setId(id);
        QuoteCategory updated = quoteCategoryService.update(category);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        quoteCategoryService.deleteById(id);
        return ApiResponse.success();
    }
}