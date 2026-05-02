package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService extends IService<Account> {

    List<Account> listActive();

    Account getDefaultAccount();

    void updateBalance(Long accountId, BigDecimal amount, String type);
}