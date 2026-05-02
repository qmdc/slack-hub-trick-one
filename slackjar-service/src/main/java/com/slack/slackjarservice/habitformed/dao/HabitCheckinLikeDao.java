package com.slack.slackjarservice.habitformed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.habitformed.entity.HabitCheckinLike;

/**
 * 打卡点赞表(HabitCheckinLike)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface HabitCheckinLikeDao extends BaseMapper<HabitCheckinLike> {

}
