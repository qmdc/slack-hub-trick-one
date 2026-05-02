package com.slack.slackjarservice.habitformed.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitStatisticsResponse;
import com.slack.slackjarservice.habitformed.model.response.HomePageResponse;
import com.slack.slackjarservice.habitformed.service.HabitStatisticsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 统计数据控制器
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/habit/statistics")
@SaCheckLogin
public class HabitStatisticsController extends BaseController {

    @Resource
    private HabitStatisticsService habitStatisticsService;

    /**
     * 获取首页数据
     *
     * @return 首页响应
     */
    @GetMapping("/home")
    public ApiResponse<HomePageResponse> getHomePageData() {
        Long userId = getLoginUserId();
        HomePageResponse response = habitStatisticsService.getHomePageData(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取首页数据成功");
        return success(response);
    }

    /**
     * 获取统计数据
     *
     * @return 统计响应
     */
    @GetMapping("/summary")
    public ApiResponse<HabitStatisticsResponse> getStatistics() {
        Long userId = getLoginUserId();
        HabitStatisticsResponse response = habitStatisticsService.getStatistics(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取统计数据成功");
        return success(response);
    }

    /**
     * 获取本周打卡数据
     *
     * @return 每日打卡数映射
     */
    @GetMapping("/weekly")
    public ApiResponse<Map<String, Integer>> getWeeklyCheckinData() {
        Long userId = getLoginUserId();
        Map<String, Integer> data = habitStatisticsService.getWeeklyCheckinData(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取本周打卡数据成功");
        return success(data);
    }

    /**
     * 获取本月打卡数据
     *
     * @return 每日打卡数映射
     */
    @GetMapping("/monthly")
    public ApiResponse<Map<String, Integer>> getMonthlyCheckinData() {
        Long userId = getLoginUserId();
        Map<String, Integer> data = habitStatisticsService.getMonthlyCheckinData(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取本月打卡数据成功");
        return success(data);
    }

    /**
     * 获取近6个月每月完成率
     *
     * @return 月完成率列表
     */
    @GetMapping("/monthlyRates")
    public ApiResponse<List<HabitStatisticsResponse.MonthlyRate>> getMonthlyRates() {
        Long userId = getLoginUserId();
        List<HabitStatisticsResponse.MonthlyRate> rates = habitStatisticsService.getMonthlyRates(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取近6个月完成率成功");
        return success(rates);
    }

    /**
     * 获取各目标完成率
     *
     * @return 目标完成率列表
     */
    @GetMapping("/goalCompletions")
    public ApiResponse<List<HabitStatisticsResponse.GoalCompletion>> getGoalCompletions() {
        Long userId = getLoginUserId();
        List<HabitStatisticsResponse.GoalCompletion> completions = habitStatisticsService.getGoalCompletions(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取各目标完成率成功");
        return success(completions);
    }

    /**
     * 获取今日完成率
     *
     * @return 完成率（0-1）
     */
    @GetMapping("/todayRate")
    public ApiResponse<Double> getTodayCompletionRate() {
        Long userId = getLoginUserId();
        double rate = habitStatisticsService.getTodayCompletionRate(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取今日完成率成功：" + rate);
        return success(rate);
    }

    /**
     * 获取用户最长连续打卡天数
     *
     * @return 天数
     */
    @GetMapping("/longestStreak")
    public ApiResponse<Integer> getLongestStreak() {
        Long userId = getLoginUserId();
        int streak = habitStatisticsService.getLongestStreak(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取最长连续打卡天数成功：" + streak);
        return success(streak);
    }

    /**
     * 获取用户当前连续打卡天数
     *
     * @return 天数
     */
    @GetMapping("/currentStreak")
    public ApiResponse<Integer> getCurrentStreak() {
        Long userId = getLoginUserId();
        int streak = habitStatisticsService.getCurrentStreak(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取当前连续打卡天数成功：" + streak);
        return success(streak);
    }
}
