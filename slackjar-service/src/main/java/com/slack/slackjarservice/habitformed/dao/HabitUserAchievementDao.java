package com.slack.slackjarservice.habitformed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.habitformed.entity.HabitUserAchievement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户成就表(HabitUserAchievement)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface HabitUserAchievementDao extends BaseMapper<HabitUserAchievement> {
}