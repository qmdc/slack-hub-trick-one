package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.ReportDao;
import com.slack.slackjarservice.accounting.entity.Report;
import com.slack.slackjarservice.accounting.service.BillService;
import com.slack.slackjarservice.accounting.service.ReportService;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportDao, Report> implements ReportService {

    private final BillService billService;

    public ReportServiceImpl(BillService billService) {
        this.billService = billService;
    }

    @Override
    public Report generateMonthlyReport(Integer year, Integer month) {
        long startTime = getMonthStart(year, month);
        long endTime = getMonthEnd(year, month);
        
        BigDecimal totalIncome = billService.sumAmount("INCOME", startTime, endTime);
        BigDecimal totalExpense = billService.sumAmount("EXPENSE", startTime, endTime);
        
        BigDecimal savingsRate = BigDecimal.ZERO;
        if (totalIncome != null && totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = totalIncome.subtract(totalExpense).divide(totalIncome, 2, BigDecimal.ROUND_HALF_UP)
                                   .multiply(new BigDecimal("100"));
        }
        
        Map<String, Object> dataJson = new HashMap<>();
        dataJson.put("totalIncome", totalIncome);
        dataJson.put("totalExpense", totalExpense);
        dataJson.put("savingsRate", savingsRate);
        dataJson.put("categoryBreakdown", billService.sumByCategory(startTime, endTime));
        
        Report report = new Report();
        report.setReportType("MONTHLY");
        report.setYear(year);
        report.setMonth(month);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setSavingsRate(savingsRate);
        report.setDataJson(JSON.toJSONString(dataJson));
        
        save(report);
        return report;
    }

    @Override
    public Report generateYearlyReport(Integer year) {
        long startTime = getYearStart(year);
        long endTime = getYearEnd(year);
        
        BigDecimal totalIncome = billService.sumAmount("INCOME", startTime, endTime);
        BigDecimal totalExpense = billService.sumAmount("EXPENSE", startTime, endTime);
        
        BigDecimal savingsRate = BigDecimal.ZERO;
        if (totalIncome != null && totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = totalIncome.subtract(totalExpense).divide(totalIncome, 2, BigDecimal.ROUND_HALF_UP)
                                   .multiply(new BigDecimal("100"));
        }
        
        Map<String, Object> dataJson = new HashMap<>();
        dataJson.put("totalIncome", totalIncome);
        dataJson.put("totalExpense", totalExpense);
        dataJson.put("savingsRate", savingsRate);
        
        Report report = new Report();
        report.setReportType("YEARLY");
        report.setYear(year);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setSavingsRate(savingsRate);
        report.setDataJson(JSON.toJSONString(dataJson));
        
        save(report);
        return report;
    }

    @Override
    public Report getReport(String reportType, Integer year, Integer month) {
        return baseMapper.selectByTypeAndPeriod(reportType, year, month);
    }

    @Override
    public Map<String, Object> getStatistics(Long startTime, Long endTime) {
        Map<String, Object> result = new HashMap<>();
        
        BigDecimal totalIncome = billService.sumAmount("INCOME", startTime, endTime);
        BigDecimal totalExpense = billService.sumAmount("EXPENSE", startTime, endTime);
        
        result.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        result.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        result.put("balance", totalIncome != null && totalExpense != null ? totalIncome.subtract(totalExpense) : BigDecimal.ZERO);
        result.put("categoryBreakdown", billService.sumByCategory(startTime, endTime));
        result.put("trendData", billService.getTrendData(startTime, endTime));
        
        return result;
    }

    private long getMonthStart(int year, int month) {
        return java.time.LocalDate.of(year, month, 1).atStartOfDay().toInstant(java.time.ZoneOffset.ofHours(8)).toEpochMilli();
    }

    private long getMonthEnd(int year, int month) {
        return java.time.LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59).toInstant(java.time.ZoneOffset.ofHours(8)).toEpochMilli();
    }

    private long getYearStart(int year) {
        return java.time.LocalDate.of(year, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.ofHours(8)).toEpochMilli();
    }

    private long getYearEnd(int year) {
        return java.time.LocalDate.of(year, 12, 31).atTime(23, 59, 59).toInstant(java.time.ZoneOffset.ofHours(8)).toEpochMilli();
    }
}