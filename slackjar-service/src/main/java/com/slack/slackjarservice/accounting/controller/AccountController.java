package com.slack.slackjarservice.accounting.controller;

import com.slack.slackjarservice.accounting.entity.Account;
import com.slack.slackjarservice.accounting.service.AccountService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ApiResponse<List<Account>> list() {
        return success(accountService.listActive());
    }

    @GetMapping("/default")
    public ApiResponse<Account> getDefault() {
        return success(accountService.getDefaultAccount());
    }

    @PostMapping
    public ApiResponse<Account> create(@RequestBody Account account) {
        accountService.save(account);
        return success(account);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        accountService.updateById(account);
        return success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        accountService.removeById(id);
        return success();
    }
}