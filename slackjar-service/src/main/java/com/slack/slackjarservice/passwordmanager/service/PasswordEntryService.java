package com.slack.slackjarservice.passwordmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.passwordmanager.entity.PasswordEntry;
import com.slack.slackjarservice.passwordmanager.model.dto.PasswordEntryDTO;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryCreateRequest;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryUpdateRequest;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordSearchRequest;

import java.util.List;
import java.util.Map;

public interface PasswordEntryService extends IService<PasswordEntry> {

    List<PasswordEntryDTO> findByUserId(Long userId);

    List<PasswordEntryDTO> findByUserIdAndCategory(Long userId, String category);

    List<PasswordEntryDTO> searchByKeyword(Long userId, String keyword);

    PasswordEntryDTO findById(Long userId, Long id);

    PasswordEntryDTO create(Long userId, PasswordEntryCreateRequest request);

    PasswordEntryDTO update(Long userId, Long id, PasswordEntryUpdateRequest request);

    void delete(Long userId, Long id);

    void updateLastLoginTime(Long userId, Long id);

    Integer checkPasswordStrength(String password);

    String generateRandomPassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecialChars);

    Map<String, Long> getCategoryStatistics(Long userId);
}
