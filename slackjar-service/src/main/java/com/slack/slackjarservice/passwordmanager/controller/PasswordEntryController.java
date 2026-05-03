package com.slack.slackjarservice.passwordmanager.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.passwordmanager.model.dto.PasswordEntryDTO;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryCreateRequest;
import com.slack.slackjarservice.passwordmanager.model.request.PasswordEntryUpdateRequest;
import com.slack.slackjarservice.passwordmanager.service.PasswordEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/password-entry")
@Slf4j
@Validated
public class PasswordEntryController extends BaseController {

    @Resource
    private PasswordEntryService passwordEntryService;

    @GetMapping("/list")
    public ApiResponse<List<PasswordEntryDTO>> list(@RequestParam(required = false) String category) {
        Long userId = getLoginUserId();
        List<PasswordEntryDTO> entries;
        if (category != null && !category.isEmpty()) {
            entries = passwordEntryService.findByUserIdAndCategory(userId, category);
        } else {
            entries = passwordEntryService.findByUserId(userId);
        }
        return success(entries);
    }

    @GetMapping("/search")
    public ApiResponse<List<PasswordEntryDTO>> search(@RequestParam String keyword) {
        Long userId = getLoginUserId();
        List<PasswordEntryDTO> entries = passwordEntryService.searchByKeyword(userId, keyword);
        return success(entries);
    }

    @GetMapping("/{id}")
    public ApiResponse<PasswordEntryDTO> getById(@PathVariable @NotNull @Positive Long id) {
        Long userId = getLoginUserId();
        PasswordEntryDTO entry = passwordEntryService.findById(userId, id);
        return success(entry);
    }

    @PostMapping
    public ApiResponse<PasswordEntryDTO> create(@Valid @RequestBody PasswordEntryCreateRequest request) {
        Long userId = getLoginUserId();
        PasswordEntryDTO entry = passwordEntryService.create(userId, request);
        return success(entry);
    }

    @PutMapping("/{id}")
    public ApiResponse<PasswordEntryDTO> update(@PathVariable @NotNull @Positive Long id, @RequestBody PasswordEntryUpdateRequest request) {
        Long userId = getLoginUserId();
        PasswordEntryDTO entry = passwordEntryService.update(userId, id, request);
        return success(entry);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @NotNull @Positive Long id) {
        Long userId = getLoginUserId();
        passwordEntryService.delete(userId, id);
        return success();
    }

    @PostMapping("/{id}/login")
    public ApiResponse<Void> updateLastLoginTime(@PathVariable @NotNull @Positive Long id) {
        Long userId = getLoginUserId();
        passwordEntryService.updateLastLoginTime(userId, id);
        return success();
    }

    @PostMapping("/check-strength")
    public ApiResponse<Integer> checkPasswordStrength(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        Integer strength = passwordEntryService.checkPasswordStrength(password);
        return success(strength);
    }

    @PostMapping("/generate")
    public ApiResponse<String> generatePassword(@RequestBody Map<String, Object> request) {
        int length = request.containsKey("length") ? (Integer) request.get("length") : 16;
        boolean includeUppercase = request.containsKey("includeUppercase") ? (Boolean) request.get("includeUppercase") : true;
        boolean includeLowercase = request.containsKey("includeLowercase") ? (Boolean) request.get("includeLowercase") : true;
        boolean includeNumbers = request.containsKey("includeNumbers") ? (Boolean) request.get("includeNumbers") : true;
        boolean includeSpecialChars = request.containsKey("includeSpecialChars") ? (Boolean) request.get("includeSpecialChars") : false;

        String password = passwordEntryService.generateRandomPassword(length, includeUppercase, includeLowercase, includeNumbers, includeSpecialChars);
        return success(password);
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Long>> getStatistics() {
        Long userId = getLoginUserId();
        Map<String, Long> statistics = passwordEntryService.getCategoryStatistics(userId);
        return success(statistics);
    }
}
