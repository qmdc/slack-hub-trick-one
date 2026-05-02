package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Budget;
import com.slack.slackjarservice.accounting.service.BudgetService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounting/budget")
public class BudgetController extends BaseController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ApiResponse<List<Budget>> list(
            @RequestParam String periodType,
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month) {
        return success(budgetService.listByPeriod(periodType, year, month));
    }

    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> checkExceed(
            @RequestParam(required = false) Long categoryId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return success(budgetService.checkBudgetExceed(categoryId, amount, year, month));
    }

    @PostMapping
    public ApiResponse<Budget> create(@RequestBody Budget budget) {
        budgetService.save(budget);
        return success(budget);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Budget budget) {
        budget.setId(id);
        budgetService.updateById(budget);
        return success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        budgetService.removeById(id);
        return success();
    }
}