package com.slack.slackjarservice.passwordmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.passwordmanager.entity.PasswordEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PasswordEntryDao extends BaseMapper<PasswordEntry> {

    @Select("SELECT * FROM password_entry WHERE user_id = #{userId} AND deleted = 0 ORDER BY update_time DESC")
    List<PasswordEntry> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM password_entry WHERE user_id = #{userId} AND category = #{category} AND deleted = 0 ORDER BY update_time DESC")
    List<PasswordEntry> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

    @Select("SELECT * FROM password_entry WHERE user_id = #{userId} AND (website_name LIKE CONCAT('%', #{keyword}, '%') OR account LIKE CONCAT('%', #{keyword}, '%')) AND deleted = 0 ORDER BY update_time DESC")
    List<PasswordEntry> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
