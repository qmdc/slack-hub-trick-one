package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Report;

import java.util.Map;

public interface ReportService extends IService<Report> {

    Report generateMonthlyReport(Integer year, Integer month);

    Report generateYearlyReport(Integer year);

    Report getReport(String reportType, Integer year, Integer month);

    Map<String, Object> getStatistics(Long startTime, Long endTime);
}