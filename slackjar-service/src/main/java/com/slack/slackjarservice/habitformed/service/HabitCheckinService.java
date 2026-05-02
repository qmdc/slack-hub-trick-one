package com.slack.slackjarservice.habitformed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.entity.HabitCheckin;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitCheckinRequest;
import com.slack.slackjarservice.habitformed.model.request.HabitCommentRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinCommentResponse;
import com.slack.slackjarservice.habitformed.model.response.HabitCheckinResponse;

import java.util.List;

/**
 * 打卡记录表(HabitCheckin)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface HabitCheckinService extends IService<HabitCheckin> {

    /**
     * 执行打卡
     *
     * @param request 打卡请求
     * @param userId  当前用户ID
     * @return 打卡记录ID
     */
    Long checkin(HabitCheckinRequest request, Long userId);

    /**
     * 取消打卡
     *
     * @param checkinId 打卡记录ID
     * @param userId    当前用户ID
     */
    void cancelCheckin(Long checkinId, Long userId);

    /**
     * 根据ID获取打卡详情
     *
     * @param id     打卡记录ID
     * @param userId 当前用户ID
     * @return 打卡详情
     */
    HabitCheckinResponse getCheckinById(Long id, Long userId);

    /**
     * 分页查询打卡记录
     *
     * @param query  分页查询条件
     * @param userId 当前用户ID
     * @return 分页结果
     */
    PageResult<HabitCheckinResponse> pageQueryCheckins(HabitCheckinPageQuery query, Long userId);

    /**
     * 获取好友动态
     *
     * @param userId   当前用户ID
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 好友打卡列表
     */
    PageResult<HabitCheckinResponse> getFriendFeed(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 点赞
     *
     * @param checkinId 打卡记录ID
     * @param userId    当前用户ID
     * @return 是否成功点赞
     */
    boolean toggleLike(Long checkinId, Long userId);

    /**
     * 发表评论
     *
     * @param request 评论请求
     * @param userId  当前用户ID
     * @return 评论ID
     */
    Long addComment(HabitCommentRequest request, Long userId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    当前用户ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取打卡记录的所有评论
     *
     * @param checkinId 打卡记录ID
     * @param userId    当前用户ID
     * @return 评论列表
     */
    List<HabitCheckinCommentResponse> getComments(Long checkinId, Long userId);

    /**
     * 获取用户总打卡天数
     *
     * @param userId 用户ID
     * @return 打卡天数
     */
    int getTotalCheckinDays(Long userId);

    /**
     * 获取用户获得的总点赞数
     *
     * @param userId 用户ID
     * @return 点赞数
     */
    int getTotalLikesReceived(Long userId);

    /**
     * 获取用户发表的总评论数
     *
     * @param userId 用户ID
     * @return 评论数
     */
    int getTotalCommentsMade(Long userId);
}
