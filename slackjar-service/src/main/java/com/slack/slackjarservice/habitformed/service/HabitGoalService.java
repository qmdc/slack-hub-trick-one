package com.slack.slackjarservice.habitformed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitGoalSaveRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitGoalResponse;

import java.util.List;

/**
 * 打卡目标表(HabitGoal)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface HabitGoalService extends IService<HabitGoal> {

    /**
     * 保存打卡目标
     *
     * @param request 保存请求
     * @param userId  当前用户ID
     * @return 目标ID
     */
    Long saveGoal(HabitGoalSaveRequest request, Long userId);

    /**
     * 根据ID获取目标详情
     *
     * @param id 目标ID
     * @return 目标详情
     */
    HabitGoalResponse getGoalById(Long id);

    /**
     * 根据ID删除目标
     *
     * @param id     目标ID
     * @param userId 当前用户ID
     */
    void deleteGoalById(Long id, Long userId);

    /**
     * 分页查询目标列表
     *
     * @param query  分页查询条件
     * @param userId 当前用户ID
     * @return 分页结果
     */
    PageResult<HabitGoalResponse> pageQueryGoals(HabitGoalPageQuery query, Long userId);

    /**
     * 获取用户的所有目标
     *
     * @param userId 用户ID
     * @return 目标列表
     */
    List<HabitGoalResponse> getUserGoals(Long userId);

    /**
     * 检查今日是否需要打卡
     *
     * @param goal 目标
     * @return true/false
     */
    boolean shouldCheckinToday(HabitGoal goal);

    /**
     * 检查今日是否已打卡
     *
     * @param goalId 目标ID
     * @param userId 用户ID
     * @return true/false
     */
    boolean hasCheckedInToday(Long goalId, Long userId);

    /**
     * 更新目标的打卡统计信息
     *
     * @param goalId      目标ID
     * @param checkinDate 打卡日期
     */
    void updateGoalStats(Long goalId, Long checkinDate);

    /**
     * 计算连续打卡天数
     *
     * @param goalId      目标ID
     * @param checkinDate 当前打卡日期
     * @return 连续天数
     */
    int calculateStreak(Long goalId, Long checkinDate);

    /**
     * 获取今日待打卡目标
     *
     * @param userId 用户ID
     * @return 目标列表
     */
    List<HabitGoalResponse> getTodayPendingGoals(Long userId);

    /**
     * 获取今日已完成目标
     *
     * @param userId 用户ID
     * @return 目标列表
     */
    List<HabitGoalResponse> getTodayCompletedGoals(Long userId);

    /**
     * 转换为响应对象
     *
     * @param goal 目标实体
     * @return 响应对象
     */
    HabitGoalResponse toResponse(HabitGoal goal);
}
