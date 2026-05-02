package com.slack.slackjarservice.dialogflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.dialogflow.entity.NodeType;

/**
 * 节点类型字典表(NodeType)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface NodeTypeDao extends BaseMapper<NodeType> {

}
