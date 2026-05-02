package com.slack.slackjarservice.habitformed.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.habitformed.dao.HabitAchievementDao;
import com.slack.slackjarservice.habitformed.dao.HabitCheckinDao;
import com.slack.slackjarservice.habitformed.dao.HabitGoalDao;
import com.slack.slackjarservice.habitformed.dao.HabitUserAchievementDao;
import com.slack.slackjarservice.habitformed.entity.HabitAchievement;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;
import com.slack.slackjarservice.habitformed.entity.HabitUserAchievement;
import com.slack.slackjarservice.habitformed.model.response.HabitAchievementResponse;
import com.slack.slackjarservice.habitformed.service.HabitAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成就徽章表(HabitAchievement)表服务实现类
 *
 * @author zhn
 * @since 2026-05-02
 */
@Service("habitAchievementService")
@RequiredArgsConstructor
public class HabitAchievementServiceImpl extends ServiceImpl<HabitAchievementDao, HabitAchievement> implements HabitAchievementService {

    private final HabitCheckinDao habitCheckinDao;
    private final HabitGoalDao habitGoalDao;
    private final HabitUserAchievementDao habitUserAchievementDao;

    @Override
    public List<HabitAchievementResponse> getAllAchievements(Long userId) {
        LambdaQueryWrapper<HabitAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(HabitAchievement::getSortOrder);
        
        return this.list(queryWrapper).stream()
            .map(achievement -> toResponse(achievement, userId))
            .collect(Collectors.toList());
    }

    @Override
    public List<HabitAchievementResponse> getUserAchievements(Long userId) {
        // 查询用户已解锁的成就
        LambdaQueryWrapper<HabitUserAchievement> userAchievementQuery = new LambdaQueryWrapper<>();
        userAchievementQuery.eq(HabitUserAchievement::getUserId, userId)
            .orderByDesc(HabitUserAchievement::getUnlockTime);
        
        List<HabitUserAchievement> userAchievements = habitUserAchievementDao.selectList(userAchievementQuery);
        
        if (userAchievements.isEmpty()) {
            return List.of();
        }
        
        List<Long> achievementIds = userAchievements.stream()
            .map(HabitUserAchievement::getAchievementId)
            .collect(Collectors.toList());
        
        // 获取成就详情
        List<HabitAchievement> achievements = this.listByIds(achievementIds);
        
        return achievements.stream()
            .map(achievement -> toResponse(achievement, userId))
            .collect(Collectors.toList());
    }

    @Override
    public HabitAchievementResponse getRecentAchievement(Long userId) {
        LambdaQueryWrapper<HabitUserAchievement> userAchievementQuery = new LambdaQueryWrapper<>();
        userAchievementQuery.eq(HabitUserAchievement::getUserId, userId)
            .orderByDesc(HabitUserAchievement::getUnlockTime)
            .last("LIMIT 1");
        
        HabitUserAchievement userAchievement = habitUserAchievementDao.selectOne(userAchievementQuery);
        if (userAchievement == null) {
            return null;
        }
        
        HabitAchievement achievement = this.getById(userAchievement.getAchievementId());
        if (achievement == null) {
            return null;
        }
        
        return toResponse(achievement, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void checkAndUnlockAchievements(Long userId, HabitGoal goal, HabitCheckin checkin) {
        // 这里可以实现具体的成就解锁逻辑
        // 例如：连续打卡7天、30天等成就
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void checkGoalCreateAchievement(Long userId, HabitGoal goal) {
        // 检查是否解锁"首次创建目标"成就
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);
        long goalCount = habitGoalDao.selectCount(queryWrapper);
        
        if (goalCount == 1) {
            // 用户创建了第一个目标，可以解锁相应成就
            unlockAchievement(userId, "first_goal_created", "首次创建目标");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void checkFriendAchievement(Long userId) {
        // 检查好友相关成就
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void checkLikeAchievement(Long userId) {
        // 检查点赞相关成就
    }

    @Override
    public HabitAchievementResponse toResponse(HabitAchievement achievement, Long userId) {
        HabitAchievementResponse response = new HabitAchievementResponse();
        response.setId(achievement.getId());
        response.setAchievementCode(achievement.getAchievementCode());
        response.setAchievementName(achievement.getAchievementName());
        response.setAchievementIcon(achievement.getAchievementIcon());
        response.setAchievementColor(achievement.getAchievementColor());
        response.setDescription(achievement.getDescription());
        response.setAchievementType(achievement.getAchievementType());
        response.setConditionType(achievement.getConditionType());
        response.setConditionValue(achievement.getConditionValue());
        response.setSortOrder(achievement.getSortOrder());
        response.setRarity(achievement.getRarity());
        
        // 检查用户是否已解锁此成就
        if (userId != null) {
            LambdaQueryWrapper<HabitUserAchievement> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(HabitUserAchievement::getUserId, userId)
                     .eq(HabitUserAchievement::getAchievementId, achievement.getId());
            HabitUserAchievement userAchievement = habitUserAchievementDao.selectOne(userQuery);
            response.setUnlocked(userAchievement != null);
            if (userAchievement != null) {
                response.setUnlockTime(userAchievement.getUnlockTime());
            }
        } else {
            response.setUnlocked(false);
        }
        
        return response;
    }

    @Override
    public long calculateCurrentValue(HabitAchievement achievement, Long userId) {
        // 根据成就类型计算当前进度值
        Integer type = achievement.getAchievementType();
        
        if (type == null) {
            return 0;
        }
        
        switch (type) {
            case 0: // 连续打卡天数
                return getCurrentStreak(userId);
            case 1: // 总打卡次数
                return getTotalCheckins(userId);
            case 2: // 创建的目标数量
                return getGoalsCreated(userId);
            default:
                return 0;
        }
    }

    @Override
    public int getTotalAchievements() {
        LambdaQueryWrapper<HabitAchievement> queryWrapper = new LambdaQueryWrapper<>();
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public int getEarnedAchievements(Long userId) {
        LambdaQueryWrapper<HabitUserAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitUserAchievement::getUserId, userId);
        return Math.toIntExact(habitUserAchievementDao.selectCount(queryWrapper));
    }

    /**
     * 解锁成就
     */
    private void unlockAchievement(Long userId, String achievementCode, String achievementName) {
        // 查找对应的成就模板
        LambdaQueryWrapper<HabitAchievement> templateQuery = new LambdaQueryWrapper<>();
        templateQuery.eq(HabitAchievement::getAchievementCode, achievementCode);
        HabitAchievement template = this.getOne(templateQuery);
        
        if (template != null) {
            // 检查用户是否已经解锁过这个成就
            LambdaQueryWrapper<HabitUserAchievement> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(HabitUserAchievement::getUserId, userId)
                     .eq(HabitUserAchievement::getAchievementId, template.getId());
            
            if (habitUserAchievementDao.selectCount(userQuery) == 0) {
                // 创建用户成就记录
                HabitUserAchievement userAchievement = new HabitUserAchievement();
                userAchievement.setUserId(userId);
                userAchievement.setAchievementId(template.getId());
                userAchievement.setUnlockTime(System.currentTimeMillis());
                habitUserAchievementDao.insert(userAchievement);
            }
        }
    }

    /**
     * 获取当前连续打卡天数
     */
    private long getCurrentStreak(Long userId) {
        // 这里需要实现获取用户当前连续打卡天数的逻辑
        // 简化实现，实际应该查询数据库计算
        return 0;
    }

    /**
     * 获取总打卡次数
     */
    private long getTotalCheckins(Long userId) {
        LambdaQueryWrapper<HabitCheckin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitCheckin::getUserId, userId);
        return habitCheckinDao.selectCount(queryWrapper);
    }

    /**
     * 获取创建的目标数量
     */
    private long getGoalsCreated(Long userId) {
        LambdaQueryWrapper<HabitGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HabitGoal::getUserId, userId);
        return habitGoalDao.selectCount(queryWrapper);
    }
}