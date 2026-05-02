package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Category;
import com.slack.slackjarservice.accounting.service.CategoryService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting/category")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<Category>> list(@RequestParam(required = false) String type) {
        if (type != null) {
            return success(categoryService.listByType(type));
        }
        return success(categoryService.list());
    }

    @GetMapping("/income")
    public ApiResponse<List<Category>> listIncome() {
        return success(categoryService.listIncomeCategories());
    }

    @GetMapping("/expense")
    public ApiResponse<List<Category>> listExpense() {
        return success(categoryService.listExpenseCategories());
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        categoryService.save(category);
        return success(category);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return success();
    }
}