package com.slack.slackjarservice.habitformed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;

/**
 * 打卡记录表(HabitCheckin)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface HabitCheckinDao extends BaseMapper<HabitCheckin> {

}
