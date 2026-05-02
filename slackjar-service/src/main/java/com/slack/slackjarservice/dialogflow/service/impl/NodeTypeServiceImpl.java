package com.slack.slackjarservice.dialogflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.dialogflow.dao.NodeTypeDao;
import com.slack.slackjarservice.dialogflow.entity.NodeType;
import com.slack.slackjarservice.dialogflow.model.response.NodeTypeResponse;
import com.slack.slackjarservice.dialogflow.service.NodeTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 节点类型字典表(NodeType)表服务实现类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Service("nodeTypeService")
public class NodeTypeServiceImpl extends ServiceImpl<NodeTypeDao, NodeType> implements NodeTypeService {

    @Override
    public List<NodeTypeResponse> getAllNodeTypes() {
        LambdaQueryWrapper<NodeType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(NodeType::getSortOrder);

        List<NodeType> nodeTypes = this.list(queryWrapper);

        return nodeTypes.stream().map(nodeType -> {
            NodeTypeResponse response = new NodeTypeResponse();
            response.setId(nodeType.getId());
            response.setTypeCode(nodeType.getTypeCode());
            response.setTypeName(nodeType.getTypeName());
            response.setDescription(nodeType.getDescription());
            response.setIcon(nodeType.getIcon());
            response.setColor(nodeType.getColor());
            response.setSortOrder(nodeType.getSortOrder());
            return response;
        }).toList();
    }
}
