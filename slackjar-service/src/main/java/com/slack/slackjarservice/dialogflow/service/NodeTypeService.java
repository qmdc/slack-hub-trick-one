package com.slack.slackjarservice.dialogflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.dialogflow.entity.NodeType;
import com.slack.slackjarservice.dialogflow.model.response.NodeTypeResponse;

import java.util.List;

/**
 * 节点类型字典表(NodeType)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface NodeTypeService extends IService<NodeType> {

    /**
     * 获取所有节点类型列表
     *
     * @return 节点类型列表
     */
    List<NodeTypeResponse> getAllNodeTypes();
}
