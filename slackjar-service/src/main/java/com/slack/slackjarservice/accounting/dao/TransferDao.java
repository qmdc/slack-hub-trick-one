package com.slack.slackjarservice.accounting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.accounting.entity.Transfer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransferDao extends BaseMapper<Transfer> {
}