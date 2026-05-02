package com.slack.slackjarservice.habitformed.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalSaveRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitGoalResponse;
import com.slack.slackjarservice.habitformed.service.HabitGoalService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡目标控制器
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/habit/goal")
@SaCheckLogin
public class HabitGoalController extends BaseController {

    @Resource
    private HabitGoalService habitGoalService;

    /**
     * 保存打卡目标
     *
     * @param request       保存请求
     * @param bindingResult 参数校验结果
     * @return 目标ID
     */
    @PostMapping("/save")
    public ApiResponse<Long> saveGoal(@Valid @RequestBody HabitGoalSaveRequest request, BindingResult bindingResult) {
        handleValidationResult(bindingResult);
        Long userId = getLoginUserId();
        Long goalId = habitGoalService.saveGoal(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "保存打卡目标成功，ID：" + goalId);
        return success(goalId);
    }

    /**
     * 根据ID获取目标详情
     *
     * @param id 目标ID
     * @return 目标详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<HabitGoalResponse> getGoalById(@PathVariable Long id) {
        HabitGoalResponse response = habitGoalService.getGoalById(id);
        recordOperateLog(OperationEnum.USER_QUERY, "查询打卡目标详情，ID：" + id);
        return success(response);
    }

    /**
     * 根据ID删除目标
     *
     * @param id 目标ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteGoalById(@PathVariable Long id) {
        Long userId = getLoginUserId();
        habitGoalService.deleteGoalById(id, userId);
        recordOperateLog(OperationEnum.USER_DELETE, "删除打卡目标成功，ID：" + id);
        return success(true);
    }

    /**
     * 分页查询目标列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @PostMapping("/pageQuery")
    public ApiResponse<PageResult<HabitGoalResponse>> pageQueryGoals(@RequestBody HabitGoalPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<HabitGoalResponse> result = habitGoalService.pageQueryGoals(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询打卡目标列表成功");
        return success(result);
    }

    /**
     * 获取当前用户的所有目标
     *
     * @return 目标列表
     */
    @GetMapping("/list")
    public ApiResponse<List<HabitGoalResponse>> getUserGoals() {
        Long userId = getLoginUserId();
        List<HabitGoalResponse> goals = habitGoalService.getUserGoals(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取用户打卡目标列表成功");
        return success(goals);
    }

    /**
     * 获取今日待打卡目标
     *
     * @return 目标列表
     */
    @GetMapping("/today/pending")
    public ApiResponse<List<HabitGoalResponse>> getTodayPendingGoals() {
        Long userId = getLoginUserId();
        List<HabitGoalResponse> goals = habitGoalService.getTodayPendingGoals(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取今日待打卡目标成功");
        return success(goals);
    }

    /**
     * 获取今日已完成目标
     *
     * @return 目标列表
     */
    @GetMapping("/today/completed")
    public ApiResponse<List<HabitGoalResponse>> getTodayCompletedGoals() {
        Long userId = getLoginUserId();
        List<HabitGoalResponse> goals = habitGoalService.getTodayCompletedGoals(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取今日已完成目标成功");
        return success(goals);
    }
}
