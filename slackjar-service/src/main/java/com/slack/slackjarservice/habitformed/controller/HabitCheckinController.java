package com.slack.slackjarservice.habitformed.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinRequest;
import com.slack.slackjarservice.habitformed.model.request.HabitCommentRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinCommentResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinResponse;
import com.slack.slackjarservice.habitformed.service.HabitCheckinService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡记录控制器
 *
 * @author zhn
 * @since 2026-05-02
 */
@RestController
@RequestMapping("/habit/checkin")
@SaCheckLogin
public class HabitCheckinController extends BaseController {

    @Resource
    private HabitCheckinService habitCheckinService;

    /**
     * 执行打卡
     *
     * @param request       打卡请求
     * @param bindingResult 参数校验结果
     * @return 打卡记录ID
     */
    @PostMapping("/checkin")
    public ApiResponse<Long> checkin(@Valid @RequestBody HabitCheckinRequest request, BindingResult bindingResult) {
        handleValidationResult(bindingResult);
        Long userId = getLoginUserId();
        Long checkinId = habitCheckinService.checkin(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "打卡成功，记录ID：" + checkinId);
        return success(checkinId);
    }

    /**
     * 取消打卡
     *
     * @param checkinId 打卡记录ID
     * @return 操作结果
     */
    @DeleteMapping("/cancel/{checkinId}")
    public ApiResponse<Boolean> cancelCheckin(@PathVariable Long checkinId) {
        Long userId = getLoginUserId();
        habitCheckinService.cancelCheckin(checkinId, userId);
        recordOperateLog(OperationEnum.USER_DELETE, "取消打卡成功，记录ID：" + checkinId);
        return success(true);
    }

    /**
     * 根据ID获取打卡详情
     *
     * @param id 打卡记录ID
     * @return 打卡详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse<HabitCheckinResponse> getCheckinById(@PathVariable Long id) {
        Long userId = getLoginUserId();
        HabitCheckinResponse response = habitCheckinService.getCheckinById(id, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "查询打卡详情，ID：" + id);
        return success(response);
    }

    /**
     * 分页查询打卡记录
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @PostMapping("/pageQuery")
    public ApiResponse<PageResult<HabitCheckinResponse>> pageQueryCheckins(@RequestBody HabitCheckinPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<HabitCheckinResponse> result = habitCheckinService.pageQueryCheckins(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询打卡记录列表成功");
        return success(result);
    }

    /**
     * 获取好友动态
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/feed")
    public ApiResponse<PageResult<HabitCheckinResponse>> getFriendFeed(
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Long userId = getLoginUserId();
        PageResult<HabitCheckinResponse> result = habitCheckinService.getFriendFeed(userId, pageNo, pageSize);
        recordOperateLog(OperationEnum.USER_QUERY, "获取好友动态成功");
        return success(result);
    }

    /**
     * 点赞/取消点赞
     *
     * @param checkinId 打卡记录ID
     * @return 是否成功点赞
     */
    @PostMapping("/like/{checkinId}")
    public ApiResponse<Boolean> toggleLike(@PathVariable Long checkinId) {
        Long userId = getLoginUserId();
        boolean liked = habitCheckinService.toggleLike(checkinId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, (liked ? "点赞" : "取消点赞") + "成功，打卡记录ID：" + checkinId);
        return success(liked);
    }

    /**
     * 发表评论
     *
     * @param request       评论请求
     * @param bindingResult 参数校验结果
     * @return 评论ID
     */
    @PostMapping("/comment")
    public ApiResponse<Long> addComment(@Valid @RequestBody HabitCommentRequest request, BindingResult bindingResult) {
        handleValidationResult(bindingResult);
        Long userId = getLoginUserId();
        Long commentId = habitCheckinService.addComment(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "发表评论成功，评论ID：" + commentId);
        return success(commentId);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long commentId) {
        Long userId = getLoginUserId();
        habitCheckinService.deleteComment(commentId, userId);
        recordOperateLog(OperationEnum.USER_DELETE, "删除评论成功，评论ID：" + commentId);
        return success(true);
    }

    /**
     * 获取打卡记录的所有评论
     *
     * @param checkinId 打卡记录ID
     * @return 评论列表
     */
    @GetMapping("/comments/{checkinId}")
    public ApiResponse<List<HabitCheckinCommentResponse>> getComments(@PathVariable Long checkinId) {
        Long userId = getLoginUserId();
        List<HabitCheckinCommentResponse> comments = habitCheckinService.getComments(checkinId, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "获取打卡评论列表成功，打卡记录ID：" + checkinId);
        return success(comments);
    }
}
