package com.slack.slackjarservice.accounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.accounting.dao.TransferDao;
import com.slack.slackjarservice.accounting.entity.Transfer;
import com.slack.slackjarservice.accounting.service.AccountService;
import com.slack.slackjarservice.accounting.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl extends ServiceImpl<TransferDao, Transfer> implements TransferService {

    private final AccountService accountService;

    public TransferServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String remark) {
        accountService.updateBalance(fromAccountId, amount, "EXPENSE");
        accountService.updateBalance(toAccountId, amount, "INCOME");
        
        Transfer transfer = new Transfer();
        transfer.setFromAccountId(fromAccountId);
        transfer.setToAccountId(toAccountId);
        transfer.setAmount(amount);
        transfer.setTransferDate(System.currentTimeMillis());
        transfer.setRemark(remark);
        save(transfer);
    }
}