package com.slack.slackjarservice.habitformed.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitFriendResponse;
import com.slack.slackjarservice.habitformed.service.HabitFriendshipService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友关系控制器
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/habit/friend")
@SaCheckLogin
public class HabitFriendshipController extends BaseController {

    @Resource
    private HabitFriendshipService habitFriendshipService;

    /**
     * 发送好友申请
     *
     * @param request       好友请求
     * @param bindingResult 参数校验结果
     * @return 关系ID
     */
    @PostMapping("/request")
    public ApiResponse<Long> sendFriendRequest(@Valid @RequestBody HabitFriendshipRequest request, BindingResult bindingResult) {
        handleValidationResult(bindingResult);
        Long userId = getLoginUserId();
        Long friendshipId = habitFriendshipService.sendFriendRequest(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "发送好友申请成功，好友ID：" + request.getFriendId());
        return success(friendshipId);
    }

    /**
     * 确认好友申请
     *
     * @param friendshipId 关系ID
     * @return 操作结果
     */
    @PostMapping("/confirm/{friendshipId}")
    public ApiResponse<Boolean> confirmFriendRequest(@PathVariable Long friendshipId) {
        Long userId = getLoginUserId();
        habitFriendshipService.confirmFriendRequest(friendshipId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "确认好友申请成功，关系ID：" + friendshipId);
        return success(true);
    }

    /**
     * 拒绝好友申请
     *
     * @param friendshipId 关系ID
     * @return 操作结果
     */
    @PostMapping("/reject/{friendshipId}")
    public ApiResponse<Boolean> rejectFriendRequest(@PathVariable Long friendshipId) {
        Long userId = getLoginUserId();
        habitFriendshipService.rejectFriendRequest(friendshipId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "拒绝好友申请成功，关系ID：" + friendshipId);
        return success(true);
    }

    /**
     * 取消好友申请
     *
     * @param friendshipId 关系ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{friendshipId}")
    public ApiResponse<Boolean> cancelFriendRequest(@PathVariable Long friendshipId) {
        Long userId = getLoginUserId();
        habitFriendshipService.cancelFriendRequest(friendshipId, userId);
        recordOperateLog(OperationEnum.USER_DELETE, "取消好友申请成功，关系ID：" + friendshipId);
        return success(true);
    }

    /**
     * 删除好友
     *
     * @param friendId 好友ID
     * @return 操作结果
     */
    @DeleteMapping("/remove/{friendId}")
    public ApiResponse<Boolean> removeFriend(@PathVariable Long friendId) {
        Long userId = getLoginUserId();
        habitFriendshipService.removeFriend(friendId, userId);
        recordOperateLog(OperationEnum.USER_DELETE, "删除好友成功，好友ID：" + friendId);
        return success(true);
    }

    /**
     * 获取好友列表
     *
     * @return 好友列表
     */
    @GetMapping("/list")
    public ApiResponse<List<HabitFriendResponse>> getFriendList() {
        Long userId = getLoginUserId();
        List<HabitFriendResponse> friends = habitFriendshipService.getFriendList(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取好友列表成功");
        return success(friends);
    }

    /**
     * 分页查询好友列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @PostMapping("/pageQuery")
    public ApiResponse<PageResult<HabitFriendResponse>> pageQueryFriends(@RequestBody HabitFriendshipPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<HabitFriendResponse> result = habitFriendshipService.pageQueryFriends(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询好友列表成功");
        return success(result);
    }

    /**
     * 获取待确认的好友申请列表
     *
     * @return 好友申请列表
     */
    @GetMapping("/pending")
    public ApiResponse<List<HabitFriendResponse>> getPendingRequests() {
        Long userId = getLoginUserId();
        List<HabitFriendResponse> requests = habitFriendshipService.getPendingRequests(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取待确认好友申请列表成功");
        return success(requests);
    }

    /**
     * 获取我发送的好友申请列表
     *
     * @return 好友申请列表
     */
    @GetMapping("/sent")
    public ApiResponse<List<HabitFriendResponse>> getSentRequests() {
        Long userId = getLoginUserId();
        List<HabitFriendResponse> requests = habitFriendshipService.getSentRequests(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取我发送的好友申请列表成功");
        return success(requests);
    }

    /**
     * 获取待确认申请数量
     *
     * @return 数量
     */
    @GetMapping("/pending/count")
    public ApiResponse<Integer> getPendingRequestCount() {
        Long userId = getLoginUserId();
        int count = habitFriendshipService.getPendingRequestCount(userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取待确认好友申请数量成功：" + count);
        return success(count);
    }

    /**
     * 搜索用户
     *
     * @param keyword 关键词
     * @return 用户列表
     */
    @GetMapping("/search")
    public ApiResponse<List<HabitFriendResponse>> searchUsers(@RequestParam String keyword) {
        Long userId = getLoginUserId();
        List<HabitFriendResponse> users = habitFriendshipService.searchUsers(keyword, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "搜索用户成功，关键词：" + keyword);
        return success(users);
    }

    /**
     * 检查是否是好友
     *
     * @param friendId 好友ID
     * @return true/false
     */
    @GetMapping("/check/{friendId}")
    public ApiResponse<Boolean> isFriend(@PathVariable Long friendId) {
        Long userId = getLoginUserId();
        boolean isFriend = habitFriendshipService.isFriend(userId, friendId);
        recordOperateLog(OperationEnum.USER_QUERY, "检查好友关系，好友ID：" + friendId + "，结果：" + isFriend);
        return success(isFriend);
    }
}
