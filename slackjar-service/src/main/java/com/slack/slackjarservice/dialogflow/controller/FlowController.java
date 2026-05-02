package com.slack.slackjarservice.dialogflow.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.dialogflow.model.request.FlowPageQuery;
import com.slack.slackjarservice.dialogflow.model.request.FlowSaveRequest;
import com.slack.slackjarservice.dialogflow.model.response.FlowDetailResponse;
import com.slack.slackjarservice.dialogflow.model.response.FlowItemResponse;
import com.slack.slackjarservice.dialogflow.service.FlowService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 对话流程表(Flow)表控制层
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/dialogflow/flow")
public class FlowController extends BaseController {

    @Resource
    private FlowService flowService;

    /**
     * 保存对话流程
     *
     * @param request 保存请求
     * @return 流程ID
     */
    @SaCheckRole(value = {"ROLE_SUPER_ADMIN"}, mode = SaMode.OR)
    @PostMapping("/save")
    public ApiResponse<Long> saveFlow(@Valid @RequestBody FlowSaveRequest request, BindingResult bindingResult) {
        handleValidationResult(bindingResult);
        Long flowId = flowService.saveFlow(request);
        recordOperateLog(OperationEnum.USER_UPSERT, "保存对话流程成功，ID：" + flowId);
        return success(flowId);
    }

    /**
     * 根据ID获取流程详情
     *
     * @param id 流程ID
     * @return 流程详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<FlowDetailResponse> getFlowById(@PathVariable Long id) {
        FlowDetailResponse response = flowService.getFlowById(id);
        recordOperateLog(OperationEnum.USER_QUERY, "查询对话流程详情，ID：" + id);
        return success(response);
    }

    /**
     * 根据ID删除流程
     *
     * @param id 流程ID
     * @return 操作结果
     */
    @SaCheckRole(value = {"ROLE_SUPER_ADMIN"}, mode = SaMode.OR)
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteFlowById(@PathVariable Long id) {
        flowService.deleteFlowById(id);
        recordOperateLog(OperationEnum.USER_DELETE, "删除对话流程成功，ID：" + id);
        return success(true);
    }

    /**
     * 分页查询流程列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @PostMapping("/pageQuery")
    public ApiResponse<PageResult<FlowItemResponse>> pageQueryFlows(@RequestBody FlowPageQuery query) {
        PageResult<FlowItemResponse> result = flowService.pageQueryFlows(query);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询对话流程列表成功");
        return success(result);
    }

    /**
     * 导出流程数据（JSON格式）
     *
     * @param id 流程ID
     * @return JSON字符串
     */
    @GetMapping("/export/{id}")
    public ApiResponse<String> exportFlow(@PathVariable Long id) {
        String jsonData = flowService.exportFlow(id);
        recordOperateLog(OperationEnum.USER_QUERY, "导出对话流程，ID：" + id);
        return success(jsonData);
    }

    /**
     * 导入流程数据
     *
     * @param jsonData JSON数据
     * @return 导入后的流程ID
     */
    @SaCheckRole(value = {"ROLE_SUPER_ADMIN"}, mode = SaMode.OR)
    @PostMapping("/import")
    public ApiResponse<Long> importFlow(@RequestBody String jsonData) {
        Long flowId = flowService.importFlow(jsonData);
        recordOperateLog(OperationEnum.USER_UPSERT, "导入对话流程成功，ID：" + flowId);
        return success(flowId);
    }
}
