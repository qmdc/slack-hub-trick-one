package com.slack.slackjarservice.passwordmanager.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordEntryCreateRequest {

    @NotBlank(message = "网站名称不能为空")
    private String websiteName;

    private String website;

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String notes;
}
