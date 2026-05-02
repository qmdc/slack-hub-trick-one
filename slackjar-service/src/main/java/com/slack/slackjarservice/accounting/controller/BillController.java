package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Bill;
import com.slack.slackjarservice.accounting.service.BillService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounting/bill")
public class BillController extends BaseController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody Bill bill) {
        billService.createBill(bill);
        return success();
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
        return success(billService.getTrendData(startTime, endTime));
    }

    @GetMapping("/{id}")
    public ApiResponse<Bill> getById(@PathVariable Long id) {
        return success(billService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Bill bill) {
        bill.setId(id);
        billService.updateById(bill);
        return success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        billService.removeById(id);
        return success();
    }
}