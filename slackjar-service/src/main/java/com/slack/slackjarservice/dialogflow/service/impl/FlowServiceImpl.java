package com.slack.slackjarservice.dialogflow.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.dialogflow.dao.FlowDao;
import com.slack.slackjarservice.dialogflow.entity.Flow;
import com.slack.slackjarservice.dialogflow.model.dto.FlowDataDTO;
import com.slack.slackjarservice.dialogflow.model.request.FlowPageQuery;
import com.slack.slackjarservice.dialogflow.model.request.FlowSaveRequest;
import com.slack.slackjarservice.dialogflow.model.response.FlowDetailResponse;
import com.slack.slackjarservice.dialogflow.model.response.FlowItemResponse;
import com.slack.slackjarservice.dialogflow.service.FlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 对话流程表(Flow)表服务实现类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Service("flowService")
public class FlowServiceImpl extends ServiceImpl<FlowDao, Flow> implements FlowService {

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long saveFlow(FlowSaveRequest request) {
        Flow flow;
        if (Objects.nonNull(request.getId())) {
            flow = this.getById(request.getId());
            AssertUtil.notNull(flow, ResponseEnum.NOT_FOUND);
        } else {
            flow = new Flow();
        }

        flow.setName(request.getName());
        flow.setDescription(request.getDescription());
        flow.setFlowData(request.getFlowData());
        flow.setStatus(Objects.nonNull(request.getStatus()) ? request.getStatus() : 1);

        this.saveOrUpdate(flow);
        return flow.getId();
    }

    @Override
    public FlowDetailResponse getFlowById(Long id) {
        Flow flow = this.getById(id);
        AssertUtil.notNull(flow, ResponseEnum.NOT_FOUND);

        FlowDetailResponse response = new FlowDetailResponse();
        response.setId(flow.getId());
        response.setName(flow.getName());
        response.setDescription(flow.getDescription());
        response.setStatus(flow.getStatus());
        response.setCreateTime(flow.getCreateTime());
        response.setUpdateTime(flow.getUpdateTime());

        if (StrUtil.isNotBlank(flow.getFlowData())) {
            try {
                FlowDataDTO flowData = JSON.parseObject(flow.getFlowData(), FlowDataDTO.class);
                response.setFlowData(flowData);
            } catch (Exception e) {
                response.setFlowData(null);
            }
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteFlowById(Long id) {
        Flow flow = this.getById(id);
        AssertUtil.notNull(flow, ResponseEnum.NOT_FOUND);
        this.removeById(id);
    }

    @Override
    public PageResult<FlowItemResponse> pageQueryFlows(FlowPageQuery query) {
        LambdaQueryWrapper<Flow> queryWrapper = new LambdaQueryWrapper<>();

        if (Objects.nonNull(query.getName()) && !query.getName().isEmpty()) {
            queryWrapper.like(Flow::getName, query.getName());
        }

        if (Objects.nonNull(query.getDescription()) && !query.getDescription().isEmpty()) {
            queryWrapper.like(Flow::getDescription, query.getDescription());
        }

        if (Objects.nonNull(query.getStatus())) {
            queryWrapper.eq(Flow::getStatus, query.getStatus());
        }

        queryWrapper.orderByDesc(Flow::getCreateTime);

        Page<Flow> flowPage = this.page(new Page<>(query.getPageNo(), query.getPageSize()), queryWrapper);

        List<FlowItemResponse> items = flowPage.getRecords().stream().map(flow -> {
            FlowItemResponse item = new FlowItemResponse();
            item.setId(flow.getId());
            item.setName(flow.getName());
            item.setDescription(flow.getDescription());
            item.setStatus(flow.getStatus());
            item.setCreateTime(flow.getCreateTime());
            item.setUpdateTime(flow.getUpdateTime());
            return item;
        }).toList();

        return PageResult.of(items, flowPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public String exportFlow(Long id) {
        Flow flow = this.getById(id);
        AssertUtil.notNull(flow, ResponseEnum.NOT_FOUND);
        return flow.getFlowData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long importFlow(String jsonData) {
        AssertUtil.notBlank(jsonData, ResponseEnum.PARAM_ERROR);

        try {
            FlowDataDTO flowData = JSON.parseObject(jsonData, FlowDataDTO.class);
            AssertUtil.notNull(flowData, ResponseEnum.PARAM_ERROR);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON格式错误");
        }

        Flow flow = new Flow();
        flow.setName("导入的流程");
        flow.setDescription("通过JSON导入的对话流程");
        flow.setFlowData(jsonData);
        flow.setStatus(1);

        this.save(flow);
        return flow.getId();
    }
}
