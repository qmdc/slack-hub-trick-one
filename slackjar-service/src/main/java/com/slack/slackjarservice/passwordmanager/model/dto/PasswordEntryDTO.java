package com.slack.slackjarservice.passwordmanager.model.dto;

import lombok.Data;

@Data
public class PasswordEntryDTO {

    private Long id;

    private String website;

    private String websiteName;

    private String account;

    private String password;

    private String category;

    private String categoryName;

    private Integer passwordStrength;

    private String passwordStrengthText;

    private Long lastLoginTime;

    private String notes;

    private Long createTime;

    private Long updateTime;
}
