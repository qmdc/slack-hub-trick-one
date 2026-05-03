package com.slack.slackjarservice.passwordmanager.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.passwordmanager.entity.PasswordCategory;
import com.slack.slackjarservice.passwordmanager.service.PasswordCategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/password-category")
@Slf4j
@Validated
public class PasswordCategoryController extends BaseController {

    @Resource
    private PasswordCategoryService passwordCategoryService;

    @GetMapping("/list")
    public ApiResponse<List<PasswordCategory>> list() {
        Long userId = getLoginUserId();
        List<PasswordCategory> categories = passwordCategoryService.findSystemAndUserCategories(userId);
        return success(categories);
    }

    @GetMapping("/{code}")
    public ApiResponse<PasswordCategory> getByCode(@PathVariable String code) {
        PasswordCategory category = passwordCategoryService.findByCode(code);
        return success(category);
    }

    @PostMapping
    public ApiResponse<PasswordCategory> create(@RequestBody Map<String, String> request) {
        Long userId = getLoginUserId();
        String name = request.get("name");
        String code = request.get("code");
        String color = request.get("color");
        PasswordCategory category = passwordCategoryService.create(userId, name, code, color);
        return success(category);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @NotNull @Positive Long id) {
        Long userId = getLoginUserId();
        passwordCategoryService.delete(userId, id);
        return success();
    }
}
