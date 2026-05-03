package com.slack.slackjarservice.passwordmanager.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordEntryUpdateRequest {

    private String websiteName;

    private String website;

    private String account;

    private String password;

    private String category;

    private String notes;
}
