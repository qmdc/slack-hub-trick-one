package com.slack.slackjarservice.dialogflow.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.dialogflow.model.response.NodeTypeResponse;
import com.slack.slackjarservice.dialogflow.service.NodeTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 节点类型字典表(NodeType)表控制层
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/dialogflow/node-type")
public class NodeTypeController extends BaseController {

    @Resource
    private NodeTypeService nodeTypeService;

    /**
     * 获取所有节点类型列表
     *
     * @return 节点类型列表
     */
    @GetMapping("/list")
    public ApiResponse<List<NodeTypeResponse>> getAllNodeTypes() {
        List<NodeTypeResponse> result = nodeTypeService.getAllNodeTypes();
        return success(result);
    }
}
