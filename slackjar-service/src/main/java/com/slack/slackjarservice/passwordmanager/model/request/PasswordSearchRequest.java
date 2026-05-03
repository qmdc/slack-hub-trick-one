package com.slack.slackjarservice.passwordmanager.model.request;

import lombok.Data;

@Data
public class PasswordSearchRequest {

    private String keyword;

    private String category;
}
