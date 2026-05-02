package com.slack.slackjarservice.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.accounting.entity.Transfer;

public interface TransferService extends IService<Transfer> {

    void transfer(Long fromAccountId, Long toAccountId, java.math.BigDecimal amount, String remark);
}