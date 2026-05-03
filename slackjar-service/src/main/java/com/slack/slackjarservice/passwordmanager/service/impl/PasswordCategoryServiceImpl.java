package com.slack.slackjarservice.passwordmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.exception.BusinessException;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.passwordmanager.dao.PasswordCategoryDao;
import com.slack.slackjarservice.passwordmanager.entity.PasswordCategory;
import com.slack.slackjarservice.passwordmanager.service.PasswordCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PasswordCategoryServiceImpl extends ServiceImpl<PasswordCategoryDao, PasswordCategory> implements PasswordCategoryService {

    @Resource
    private PasswordCategoryDao passwordCategoryDao;

    @Override
    public List<PasswordCategory> findByUserId(Long userId) {
        return passwordCategoryDao.findByUserId(userId);
    }

    @Override
    public List<PasswordCategory> findSystemAndUserCategories(Long userId) {
        return passwordCategoryDao.findSystemAndUserCategories(userId);
    }

    @Override
    public PasswordCategory findByCode(String code) {
        return passwordCategoryDao.findByCode(code);
    }

    @Override
    @Transactional
    public PasswordCategory create(Long userId, String name, String code, String color) {
        PasswordCategory category = new PasswordCategory();
        category.setUserId(userId);
        category.setName(name);
        category.setCode(code);
        category.setColor(color);
        passwordCategoryDao.insert(category);
        return category;
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        PasswordCategory category = passwordCategoryDao.selectById(id);
        if (category == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXISTS);
        }
        if (category.getUserId().equals(0L)) {
            throw new BusinessException(ResponseEnum.PARAM_ERROR.getCode(), "系统分类不能删除");
        }
        passwordCategoryDao.deleteById(id);
    }
}
