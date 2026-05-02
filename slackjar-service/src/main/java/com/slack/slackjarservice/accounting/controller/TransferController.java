package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Transfer;
import com.slack.slackjarservice.accounting.service.TransferService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/accounting/transfer")
public class TransferController extends BaseController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ApiResponse<Void> transfer(@RequestBody Map<String, Object> params) {
        Long fromAccountId = Long.valueOf(params.get("fromAccountId").toString());
        Long toAccountId = Long.valueOf(params.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        
        transferService.transfer(fromAccountId, toAccountId, amount, remark);
        return success();
    }

    @GetMapping
    public ApiResponse<java.util.List<Transfer>> list() {
        return success(transferService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Transfer> getById(@PathVariable Long id) {
        return success(transferService.getById(id));
    }
}