package com.slack.slackjarservice.habitformed.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitAchievementResponse;
import com.slack.slackjarservice.habitformed.service.HabitAchievementService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成就徽章控制器
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/habit/achievement")
@SaCheckLogin
public class HabitAchievementController extends BaseController {

    @Resource
    private HabitAchievementService habitAchievementService;

    /**
     * 获取所有成就列表
     *
     * @return 成就列表
     */
    @GetMapping("/list")
    public ApiResponse<List<HabitAchievementResponse>> getAllAchievements() {
        Long userId = getLoginUserId();
        List<HabitAchievementResponse> achievements = habitAchievementService.getAllAchievements(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取所有成就列表成功");
        return success(achievements);
    }

    /**
     * 获取用户已解锁的成就
     *
     * @return 成就列表
     */
    @GetMapping("/my")
    public ApiResponse<List<HabitAchievementResponse>> getUserAchievements() {
        Long userId = getLoginUserId();
        List<HabitAchievementResponse> achievements = habitAchievementService.getUserAchievements(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取用户已解锁成就列表成功");
        return success(achievements);
    }

    /**
     * 获取用户最近解锁的成就
     *
     * @return 成就
     */
    @GetMapping("/recent")
    public ApiResponse<HabitAchievementResponse> getRecentAchievement() {
        Long userId = getLoginUserId();
        HabitAchievementResponse achievement = habitAchievementService.getRecentAchievement(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取最近解锁成就成功");
        return success(achievement);
    }

    /**
     * 获取总成就数
     *
     * @return 总数
     */
    @GetMapping("/total")
    public ApiResponse<Integer> getTotalAchievements() {
        int total = habitAchievementService.getTotalAchievements();
        recordOperateLog(OperationEnum.USER_QUERY, "获取总成就数成功：" + total);
        return success(total);
    }

    /**
     * 获取用户已解锁成就数
     *
     * @return 数量
     */
    @GetMapping("/earned")
    public ApiResponse<Integer> getEarnedAchievements() {
        Long userId = getLoginUserId();
        int earned = habitAchievementService.getEarnedAchievements(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取用户已解锁成就数成功：" + earned);
        return success(earned);
    }
}
