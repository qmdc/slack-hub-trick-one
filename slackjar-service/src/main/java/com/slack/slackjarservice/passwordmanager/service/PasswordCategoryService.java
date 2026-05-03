package com.slack.slackjarservice.passwordmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.passwordmanager.entity.PasswordCategory;

import java.util.List;

public interface PasswordCategoryService extends IService<PasswordCategory> {

    List<PasswordCategory> findByUserId(Long userId);

    List<PasswordCategory> findSystemAndUserCategories(Long userId);

    PasswordCategory findByCode(String code);

    PasswordCategory create(Long userId, String name, String code, String color);

    void delete(Long userId, Long id);
}
