package com.slack.slackjarservice.dialogflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.dialogflow.entity.Flow;

/**
 * 对话流程表(Flow)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface FlowDao extends BaseMapper<Flow> {

}
