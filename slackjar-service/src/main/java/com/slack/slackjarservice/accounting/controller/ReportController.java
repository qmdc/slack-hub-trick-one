package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Report;
import com.slack.slackjarservice.accounting.service.ReportService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounting/report")
public class ReportController extends BaseController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        if (startTime == null) {
            startTime = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        }
        if (endTime == null) {
            endTime = System.currentTimeMillis();
        }
        return success(reportService.getStatistics(startTime, endTime));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ApiResponse<Report> getMonthlyReport(@PathVariable Integer year, @PathVariable Integer month) {
        return success(reportService.getReport("MONTHLY", year, month));
    }

    @PostMapping("/monthly/{year}/{month}")
    public ApiResponse<Report> generateMonthlyReport(@PathVariable Integer year, @PathVariable Integer month) {
        return success(reportService.generateMonthlyReport(year, month));
    }

    @GetMapping("/yearly/{year}")
    public ApiResponse<Report> getYearlyReport(@PathVariable Integer year) {
        return success(reportService.getReport("YEARLY", year, null));
    }

    @PostMapping("/yearly/{year}")
    public ApiResponse<Report> generateYearlyReport(@PathVariable Integer year) {
        return success(reportService.generateYearlyReport(year));
    }
}