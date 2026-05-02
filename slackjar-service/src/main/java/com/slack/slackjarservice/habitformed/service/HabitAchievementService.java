package com.slack.slackjarservice.habitformed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.habitformed.entity.HabitAchievement;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.model.response.HabitAchievementResponse;

import java.util.List;

/**
 * 成就徽章表(HabitAchievement)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface HabitAchievementService extends IService<HabitAchievement> {

    /**
     * 获取所有成就列表
     *
     * @param userId 当前用户ID
     * @return 成就列表
     */
    List<HabitAchievementResponse> getAllAchievements(Long userId);

    /**
     * 获取用户已解锁的成就
     *
     * @param userId 用户ID
     * @return 成就列表
     */
    List<HabitAchievementResponse> getUserAchievements(Long userId);

    /**
     * 获取用户最近解锁的成就
     *
     * @param userId 用户ID
     * @return 成就
     */
    HabitAchievementResponse getRecentAchievement(Long userId);

    /**
     * 检查并解锁成就
     *
     * @param userId  用户ID
     * @param goal    目标对象
     * @param checkin 打卡记录
     */
    void checkAndUnlockAchievements(Long userId, HabitGoal goal, HabitCheckin checkin);

    /**
     * 检查创建目标相关的成就
     *
     * @param userId 用户ID
     * @param goal   目标对象
     */
    void checkGoalCreateAchievement(Long userId, HabitGoal goal);

    /**
     * 检查好友相关的成就
     *
     * @param userId 用户ID
     */
    void checkFriendAchievement(Long userId);

    /**
     * 检查点赞相关的成就
     *
     * @param userId 用户ID
     */
    void checkLikeAchievement(Long userId);

    /**
     * 转换为响应对象
     *
     * @param achievement 成就实体
     * @param userId      用户ID（用于检查是否已解锁）
     * @return 响应对象
     */
    HabitAchievementResponse toResponse(HabitAchievement achievement, Long userId);

    /**
     * 计算用户当前值
     *
     * @param achievement 成就
     * @param userId      用户ID
     * @return 当前值
     */
    long calculateCurrentValue(HabitAchievement achievement, Long userId);

    /**
     * 获取总成就数
     *
     * @return 总数
     */
    int getTotalAchievements();

    /**
     * 获取用户已解锁成就数
     *
     * @param userId 用户ID
     * @return 数量
     */
    int getEarnedAchievements(Long userId);
}
