package com.slack.slackjarservice.habitformed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.habitformed.entity.HabitFriendship;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipPageQuery;
import com.slack.slackjarservice.habitformed.model.request.HabitFriendshipRequest;
import com.slack.slackjarservice.habitformed.model.response.HabitFriendResponse;

import java.util.List;

/**
 * 好友关系表(HabitFriendship)表服务接口
 *
 * @author zhn
 * @since 2026-05-02
 */
public interface HabitFriendshipService extends IService<HabitFriendship> {

    /**
     * 发送好友申请
     *
     * @param request 好友请求
     * @param userId  当前用户ID
     * @return 关系ID
     */
    Long sendFriendRequest(HabitFriendshipRequest request, Long userId);

    /**
     * 确认好友申请
     *
     * @param friendshipId 关系ID
     * @param userId       当前用户ID
     */
    void confirmFriendRequest(Long friendshipId, Long userId);

    /**
     * 拒绝好友申请
     *
     * @param friendshipId 关系ID
     * @param userId       当前用户ID
     */
    void rejectFriendRequest(Long friendshipId, Long userId);

    /**
     * 取消好友申请
     *
     * @param friendshipId 关系ID
     * @param userId       当前用户ID
     */
    void cancelFriendRequest(Long friendshipId, Long userId);

    /**
     * 删除好友
     *
     * @param friendId 好友ID
     * @param userId   当前用户ID
     */
    void removeFriend(Long friendId, Long userId);

    /**
     * 获取好友列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    List<HabitFriendResponse> getFriendList(Long userId);

    /**
     * 分页查询好友列表
     *
     * @param query  分页查询条件
     * @param userId 当前用户ID
     * @return 分页结果
     */
    PageResult<HabitFriendResponse> pageQueryFriends(HabitFriendshipPageQuery query, Long userId);

    /**
     * 获取待确认的好友申请列表
     *
     * @param userId 当前用户ID
     * @return 好友申请列表
     */
    List<HabitFriendResponse> getPendingRequests(Long userId);

    /**
     * 获取我发送的好友申请列表
     *
     * @param userId 当前用户ID
     * @return 好友申请列表
     */
    List<HabitFriendResponse> getSentRequests(Long userId);

    /**
     * 检查是否是好友
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return true/false
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 获取好友ID列表
     *
     * @param userId 用户ID
     * @return 好友ID列表
     */
    List<Long> getFriendIdList(Long userId);

    /**
     * 获取待确认申请数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int getPendingRequestCount(Long userId);

    /**
     * 搜索用户
     *
     * @param keyword 关键词
     * @param userId  当前用户ID
     * @return 用户列表
     */
    List<HabitFriendResponse> searchUsers(String keyword, Long userId);
}
