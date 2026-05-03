package com.slack.slackjarservice.passwordmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.passwordmanager.entity.PasswordCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PasswordCategoryDao extends BaseMapper<PasswordCategory> {

    @Select("SELECT * FROM password_category WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time ASC")
    List<PasswordCategory> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM password_category WHERE (user_id = #{userId} OR user_id = 0) AND deleted = 0 ORDER BY create_time ASC")
    List<PasswordCategory> findSystemAndUserCategories(@Param("userId") Long userId);

    @Select("SELECT * FROM password_category WHERE code = #{code} AND deleted = 0")
    PasswordCategory findByCode(@Param("code") String code);
}
