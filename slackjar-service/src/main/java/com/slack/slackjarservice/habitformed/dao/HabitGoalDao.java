package com.slack.slackjarservice.habitformed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.habitformed.entity.HabitGoal;

/**
 * 打卡目标表(HabitGoal)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface HabitGoalDao extends BaseMapper<HabitGoal> {

}
