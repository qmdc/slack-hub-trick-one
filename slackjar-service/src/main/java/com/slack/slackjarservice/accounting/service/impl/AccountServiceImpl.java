package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.AccountDao;
import com.slack.slackjarservice.accounting.entity.Account;
import com.slack.slackjarservice.accounting.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {

    @Override
    public List<Account> listActive() {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getIsActive, 1);
        return list(queryWrapper);
    }

    @Override
    public Account getDefaultAccount() {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getIsDefault, 1);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBalance(Long accountId, BigDecimal amount, String type) {
        Account account = getById(accountId);
        if (account == null) {
            return;
        }
        BigDecimal newBalance = account.getBalance();
        if ("INCOME".equals(type)) {
            newBalance = newBalance.add(amount);
        } else {
            newBalance = newBalance.subtract(amount);
        }
        account.setBalance(newBalance);
        updateById(account);
    }
}