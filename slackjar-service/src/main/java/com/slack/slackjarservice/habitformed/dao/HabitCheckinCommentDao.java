package com.slack.slackjarservice.habitformed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.habitformed.entity.HabitCheckinComment;

/**
 * 打卡评论表(HabitCheckinComment)表数据库访问层
 *
 * @author zhn
 * @since 2026-05-02
 */
@Mapper
public interface HabitCheckinCommentDao extends BaseMapper<HabitCheckinComment> {

}
