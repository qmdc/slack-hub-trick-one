package com.slack.slackjarservice.passwordmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.exception.BusinessException;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.passwordmanager.dao.PasswordCategoryDao;
import com.slack.slackjarservice.passwordmanager.dao.PasswordEntryDao;
import com.slack.slackjarservice.passwordmanager.entity.PasswordCategory;
import com.slack.slackjarservice.passwordmanager.entity.PasswordEntry;
import com.slack.slackjarservice.passwordmanager.model.dto.PasswordEntryDTO;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryCreateRequest;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryUpdateRequest;
import com.slack.slackjarservice.passwordmanager.service.PasswordEntryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PasswordEntryServiceImpl extends ServiceImpl<PasswordEntryDao, PasswordEntry> implements PasswordEntryService {

    @Resource
    private PasswordEntryDao passwordEntryDao;

    @Resource
    private PasswordCategoryDao passwordCategoryDao;

    @Override
    public List<PasswordEntryDTO> findByUserId(Long userId) {
        List<PasswordEntry> entries = passwordEntryDao.findByUserId(userId);
        return convertToDTOList(entries);
    }

    @Override
    public List<PasswordEntryDTO> findByUserIdAndCategory(Long userId, String category) {
        List<PasswordEntry> entries = passwordEntryDao.findByUserIdAndCategory(userId, category);
        return convertToDTOList(entries);
    }

    @Override
    public List<PasswordEntryDTO> searchByKeyword(Long userId, String keyword) {
        List<PasswordEntry> entries = passwordEntryDao.searchByKeyword(userId, keyword);
        return convertToDTOList(entries);
    }

    @Override
    public PasswordEntryDTO findById(Long userId, Long id) {
        PasswordEntry entry = passwordEntryDao.selectById(id);
        if (entry == null || !entry.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXISTS);
        }
        return convertToDTO(entry);
    }

    @Override
    @Transactional
    public PasswordEntryDTO create(Long userId, PasswordEntryCreateRequest request) {
        PasswordEntry entry = new PasswordEntry();
        entry.setUserId(userId);
        entry.setWebsiteName(request.getWebsiteName());
        entry.setWebsite(request.getWebsite());
        entry.setAccount(request.getAccount());
        entry.setPassword(encryptPassword(request.getPassword()));
        entry.setCategory(request.getCategory());
        entry.setPasswordStrength(checkPasswordStrength(request.getPassword()));
        entry.setNotes(request.getNotes());

        passwordEntryDao.insert(entry);
        return convertToDTO(entry);
    }

    @Override
    @Transactional
    public PasswordEntryDTO update(Long userId, Long id, PasswordEntryUpdateRequest request) {
        PasswordEntry entry = passwordEntryDao.selectById(id);
        if (entry == null || !entry.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXISTS);
        }

        if (request.getWebsiteName() != null) {
            entry.setWebsiteName(request.getWebsiteName());
        }
        if (request.getWebsite() != null) {
            entry.setWebsite(request.getWebsite());
        }
        if (request.getAccount() != null) {
            entry.setAccount(request.getAccount());
        }
        if (request.getPassword() != null) {
            entry.setPassword(encryptPassword(request.getPassword()));
            entry.setPasswordStrength(checkPasswordStrength(request.getPassword()));
        }
        if (request.getCategory() != null) {
            entry.setCategory(request.getCategory());
        }
        if (request.getNotes() != null) {
            entry.setNotes(request.getNotes());
        }

        passwordEntryDao.updateById(entry);
        return convertToDTO(entry);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        PasswordEntry entry = passwordEntryDao.selectById(id);
        if (entry == null || !entry.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXISTS);
        }
        passwordEntryDao.deleteById(id);
    }

    @Override
    @Transactional
    public void updateLastLoginTime(Long userId, Long id) {
        PasswordEntry entry = passwordEntryDao.selectById(id);
        if (entry == null || !entry.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXISTS);
        }
        entry.setLastLoginTime(System.currentTimeMillis());
        passwordEntryDao.updateById(entry);
    }

    @Override
    public Integer checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        if (score <= 2) return 0;
        if (score <= 4) return 1;
        return 2;
    }

    @Override
    public String generateRandomPassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecialChars) {
        StringBuilder chars = new StringBuilder();
        if (includeUppercase) chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (includeLowercase) chars.append("abcdefghijklmnopqrstuvwxyz");
        if (includeNumbers) chars.append("0123456789");
        if (includeSpecialChars) chars.append("!@#$%^&*()_+-=[]{};':\"\\|,.<>/?");

        if (chars.length() == 0) {
            chars.append("abcdefghijklmnopqrstuvwxyz");
        }

        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    @Override
    public Map<String, Long> getCategoryStatistics(Long userId) {
        List<PasswordEntry> entries = passwordEntryDao.findByUserId(userId);
        return entries.stream()
                .collect(Collectors.groupingBy(PasswordEntry::getCategory, Collectors.counting()));
    }

    private List<PasswordEntryDTO> convertToDTOList(List<PasswordEntry> entries) {
        Map<String, String> categoryNames = getCategoryNames();
        return entries.stream()
                .map(entry -> convertToDTO(entry, categoryNames))
                .collect(Collectors.toList());
    }

    private PasswordEntryDTO convertToDTO(PasswordEntry entry) {
        return convertToDTO(entry, getCategoryNames());
    }

    private PasswordEntryDTO convertToDTO(PasswordEntry entry, Map<String, String> categoryNames) {
        PasswordEntryDTO dto = new PasswordEntryDTO();
        dto.setId(entry.getId());
        dto.setWebsite(entry.getWebsite());
        dto.setWebsiteName(entry.getWebsiteName());
        dto.setAccount(entry.getAccount());
        dto.setPassword(decryptPassword(entry.getPassword()));
        dto.setCategory(entry.getCategory());
        dto.setCategoryName(categoryNames.getOrDefault(entry.getCategory(), entry.getCategory()));
        dto.setPasswordStrength(entry.getPasswordStrength());
        dto.setPasswordStrengthText(getStrengthText(entry.getPasswordStrength()));
        dto.setLastLoginTime(entry.getLastLoginTime());
        dto.setNotes(entry.getNotes());
        dto.setCreateTime(entry.getCreateTime());
        dto.setUpdateTime(entry.getUpdateTime());
        return dto;
    }

    private Map<String, String> getCategoryNames() {
        List<PasswordCategory> categories = passwordCategoryDao.findSystemAndUserCategories(0L);
        Map<String, String> categoryNames = new HashMap<>();
        for (PasswordCategory category : categories) {
            categoryNames.put(category.getCode(), category.getName());
        }
        return categoryNames;
    }

    private String getStrengthText(Integer strength) {
        return switch (strength) {
            case 0 -> "弱";
            case 1 -> "中";
            case 2 -> "强";
            default -> "未知";
        };
    }

    private String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private String decryptPassword(String encryptedPassword) {
        try {
            return new String(Base64.getDecoder().decode(encryptedPassword));
        } catch (Exception e) {
            return encryptedPassword;
        }
    }
}
