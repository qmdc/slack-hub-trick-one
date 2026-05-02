package com.slack.slackjarservice.dialogflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.dialogflow.entity.Flow;
import com.slack.slackjarservice.dialogflow.model.request.FlowPageQuery;
import com.slack.slackjarservice.dialogflow.model.request.FlowSaveRequest;
import com.slack.slackjarservice.dialogflow.model.response.FlowDetailResponse;
import com.slack.slackjarservice.dialogflow.model.response.FlowItemResponse;

/**
 * 对话流程表(Flow)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface FlowService extends IService<Flow> {

    /**
     * 保存对话流程
     *
     * @param request 保存请求
     * @return 流程ID
     */
    Long saveFlow(FlowSaveRequest request);

    /**
     * 根据ID获取流程详情
     *
     * @param id 流程ID
     * @return 流程详情
     */
    FlowDetailResponse getFlowById(Long id);

    /**
     * 根据ID删除流程
     *
     * @param id 流程ID
     */
    void deleteFlowById(Long id);

    /**
     * 分页查询流程列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<FlowItemResponse> pageQueryFlows(FlowPageQuery query);

    /**
     * 导出流程数据（JSON格式）
     *
     * @param id 流程ID
     * @return JSON字符串
     */
    String exportFlow(Long id);

    /**
     * 导入流程数据
     *
     * @param jsonData JSON数据
     * @return 导入后的流程ID
     */
    Long importFlow(String jsonData);
}
