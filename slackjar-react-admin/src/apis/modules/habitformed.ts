import request from '../request'
import type {PageResult, ResponseData} from './types'

/**
 * 获取首页数据
 */
export function getHomePageData(): Promise<ResponseData<HomePageResponse>> {
    return request.get('/habit/statistics/home')
}

/**
 * 获取统计数据
 */
export function getStatistics(): Promise<ResponseData<HabitStatisticsResponse>> {
    return request.get('/habit/statistics/summary')
}

// ============================================
// 目标管理相关接口
// ============================================

/**
 * 保存打卡目标
 */
export function saveGoal(data: HabitGoalSaveRequest): Promise<ResponseData<number>> {
    return request.post('/habit/goal/save', data)
}

/**
 * 获取目标详情
 */
export function getGoalDetail(id: number): Promise<ResponseData<HabitGoalResponse>> {
    return request.get(`/habit/goal/detail/${id}`)
}

/**
 * 删除目标
 */
export function deleteGoal(id: number): Promise<ResponseData<boolean>> {
    return request.delete(`/habit/goal/delete/${id}`)
}

/**
 * 分页查询目标列表
 */
export function pageQueryGoals(data: HabitGoalPageQuery): Promise<ResponseData<PageResult<HabitGoalResponse>>> {
    return request.post('/habit/goal/pageQuery', data)
}

/**
 * 获取用户所有目标
 */
export function getUserGoals(): Promise<ResponseData<HabitGoalResponse[]>> {
    return request.get('/habit/goal/list')
}

/**
 * 获取今日待打卡目标
 */
export function getTodayPendingGoals(): Promise<ResponseData<HabitGoalResponse[]>> {
    return request.get('/habit/goal/today/pending')
}

/**
 * 获取今日已完成目标
 */
export function getTodayCompletedGoals(): Promise<ResponseData<HabitGoalResponse[]>> {
    return request.get('/habit/goal/today/completed')
}

// ============================================
// 打卡记录相关接口
// ============================================

/**
 * 执行打卡
 */
export function checkin(data: HabitCheckinRequest): Promise<ResponseData<number>> {
    return request.post('/habit/checkin/checkin', data)
}

/**
 * 取消打卡
 */
export function cancelCheckin(checkinId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/habit/checkin/cancel/${checkinId}`)
}

/**
 * 获取打卡详情
 */
export function getCheckinDetail(id: number): Promise<ResponseData<HabitCheckinResponse>> {
    return request.get(`/habit/checkin/detail/${id}`)
}

/**
 * 分页查询打卡记录
 */
export function pageQueryCheckins(data: HabitCheckinPageQuery): Promise<ResponseData<PageResult<HabitCheckinResponse>>> {
    return request.post('/habit/checkin/pageQuery', data)
}

/**
 * 获取好友动态
 */
export function getFriendFeed(pageNo?: number, pageSize?: number): Promise<ResponseData<PageResult<HabitCheckinResponse>>> {
    return request.get('/habit/checkin/feed', {params: {pageNo, pageSize}})
}

/**
 * 点赞/取消点赞
 */
export function toggleLike(checkinId: number): Promise<ResponseData<boolean>> {
    return request.post(`/habit/checkin/like/${checkinId}`)
}

/**
 * 发表评论
 */
export function addComment(data: HabitCommentRequest): Promise<ResponseData<number>> {
    return request.post('/habit/checkin/comment', data)
}

/**
 * 删除评论
 */
export function deleteComment(commentId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/habit/checkin/comment/${commentId}`)
}

/**
 * 获取打卡记录的所有评论
 */
export function getComments(checkinId: number): Promise<ResponseData<HabitCheckinCommentResponse[]>> {
    return request.get(`/habit/checkin/comments/${checkinId}`)
}

// ============================================
// 好友关系相关接口
// ============================================

/**
 * 发送好友申请
 */
export function sendFriendRequest(data: HabitFriendshipRequest): Promise<ResponseData<number>> {
    return request.post('/habit/friend/request', data)
}

/**
 * 确认好友申请
 */
export function confirmFriendRequest(friendshipId: number): Promise<ResponseData<boolean>> {
    return request.post(`/habit/friend/confirm/${friendshipId}`)
}

/**
 * 拒绝好友申请
 */
export function rejectFriendRequest(friendshipId: number): Promise<ResponseData<boolean>> {
    return request.post(`/habit/friend/reject/${friendshipId}`)
}

/**
 * 取消好友申请
 */
export function cancelFriendRequest(friendshipId: number): Promise<ResponseData<boolean>> {
    return request.post(`/habit/friend/cancel/${friendshipId}`)
}

/**
 * 删除好友
 */
export function removeFriend(friendId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/habit/friend/remove/${friendId}`)
}

/**
 * 获取好友列表
 */
export function getFriendList(): Promise<ResponseData<HabitFriendResponse[]>> {
    return request.get('/habit/friend/list')
}

/**
 * 分页查询好友列表
 */
export function pageQueryFriends(data: HabitFriendshipPageQuery): Promise<ResponseData<PageResult<HabitFriendResponse>>> {
    return request.post('/habit/friend/pageQuery', data)
}

/**
 * 获取待确认的好友申请列表
 */
export function getPendingRequests(): Promise<ResponseData<HabitFriendResponse[]>> {
    return request.get('/habit/friend/pending')
}

/**
 * 获取我发送的好友申请列表
 */
export function getSentRequests(): Promise<ResponseData<HabitFriendResponse[]>> {
    return request.get('/habit/friend/sent')
}

/**
 * 获取待确认申请数量
 */
export function getPendingRequestCount(): Promise<ResponseData<number>> {
    return request.get('/habit/friend/pending/count')
}

/**
 * 搜索用户
 */
export function searchUsers(keyword: string): Promise<ResponseData<HabitFriendResponse[]>> {
    return request.get('/habit/friend/search', {params: {keyword}})
}

/**
 * 检查是否是好友
 */
export function isFriend(friendId: number): Promise<ResponseData<boolean>> {
    return request.get(`/habit/friend/check/${friendId}`)
}

// ============================================
// 成就徽章相关接口
// ============================================

/**
 * 获取所有成就列表
 */
export function getAllAchievements(): Promise<ResponseData<HabitAchievementResponse[]>> {
    return request.get('/habit/achievement/list')
}

/**
 * 获取用户已解锁的成就
 */
export function getUserAchievements(): Promise<ResponseData<HabitAchievementResponse[]>> {
    return request.get('/habit/achievement/my')
}

/**
 * 获取用户最近解锁的成就
 */
export function getRecentAchievement(): Promise<ResponseData<HabitAchievementResponse>> {
    return request.get('/habit/achievement/recent')
}

/**
 * 获取总成就数
 */
export function getTotalAchievements(): Promise<ResponseData<number>> {
    return request.get('/habit/achievement/total')
}

/**
 * 获取用户已解锁成就数
 */
export function getEarnedAchievements(): Promise<ResponseData<number>> {
    return request.get('/habit/achievement/earned')
}

// ============================================
// 类型定义
// ============================================

/**
 * 首页响应
 */
export interface HomePageResponse {
    pendingGoals: HabitGoalResponse[]
    completedGoals: HabitGoalResponse[]
    todayCompletionRate: number
    currentStreak: number
    longestStreak: number
    friendFeed: HabitCheckinResponse[]
    pendingFriendRequests: number
    recentAchievement: HabitAchievementResponse
}

/**
 * 统计数据响应
 */
export interface HabitStatisticsResponse {
    totalGoals: number
    activeGoals: number
    totalCheckinDays: number
    longestStreak: number
    currentStreak: number
    earnedAchievements: number
    totalAchievements: number
    totalFriends: number
    totalLikes: number
    totalComments: number
    weeklyCheckinData: Record<string, number>
    monthlyCheckinData: Record<string, number>
    goalCompletions: GoalCompletion[]
    monthlyRates: MonthlyRate[]
}

export interface GoalCompletion {
    goalId: number
    goalName: string
    goalIcon: string
    checkinCount: number
    totalDays: number
    completionRate: number
}

export interface MonthlyRate {
    month: string
    rate: number
    checkinDays: number
}

/**
 * 打卡目标响应
 */
export interface HabitGoalResponse {
    id: number
    userId: number
    goalName: string
    goalIcon: string
    goalColor: string
    description: string
    frequencyType: number
    frequencyValue: string
    remindTime: string
    remindEnabled: number
    status: number
    startDate: number
    endDate: number
    totalDays: number
    checkinCount: number
    currentStreak: number
    longestStreak: number
    lastCheckinDate: number
    completionRate: number
    todayChecked: boolean
    createTime: number
}

/**
 * 打卡目标保存请求
 */
export interface HabitGoalSaveRequest {
    id?: number
    goalName: string
    goalIcon?: string
    goalColor?: string
    description?: string
    frequencyType?: number
    frequencyValue?: string
    remindTime?: string
    remindEnabled?: number
    startDate?: number
    endDate?: number
}

/**
 * 打卡目标分页查询请求
 */
export interface HabitGoalPageQuery {
    pageNo?: number
    pageSize?: number
    sortBy?: string
    sortOrder?: string
    userId?: number
    goalName?: string
    status?: number
}

/**
 * 打卡记录响应
 */
export interface HabitCheckinResponse {
    id: number
    goalId: number
    userId: number
    username: string
    nickname: string
    avatarUrl: string
    goalName: string
    goalIcon: string
    checkinDate: number
    checkinTime: number
    content: string
    imageUrls: string[]
    mood: string
    visibility: number
    likeCount: number
    commentCount: number
    liked: boolean
    createTime: number
}

/**
 * 打卡请求
 */
export interface HabitCheckinRequest {
    goalId: number
    checkinDate?: number
    content?: string
    imageIds?: string
    mood?: string
    visibility?: number
}

/**
 * 打卡记录分页查询请求
 */
export interface HabitCheckinPageQuery {
    pageNo?: number
    pageSize?: number
    sortBy?: string
    sortOrder?: string
    goalId?: number
    userId?: number
    startDate?: number
    endDate?: number
    visibility?: number
}

/**
 * 评论请求
 */
export interface HabitCommentRequest {
    checkinId: number
    replyCommentId?: number
    replyUserId?: number
    content: string
}

/**
 * 打卡评论响应
 */
export interface HabitCheckinCommentResponse {
    id: number
    checkinId: number
    userId: number
    username: string
    nickname: string
    avatarUrl: string
    replyCommentId: number
    replyUserId: number
    replyUsername: string
    replyNickname: string
    content: string
    createTime: number
    children: HabitCheckinCommentResponse[]
}

/**
 * 好友信息响应
 */
export interface HabitFriendResponse {
    id: number
    userId: number
    friendId: number
    friendUsername: string
    friendNickname: string
    friendAvatarUrl: string
    status: number
    applyUserId: number
    applyReason: string
    confirmTime: number
    todayCheckinCount: number
    longestStreak: number
    createTime: number
}

/**
 * 好友关系请求
 */
export interface HabitFriendshipRequest {
    friendId: number
    applyReason?: string
}

/**
 * 好友关系分页查询请求
 */
export interface HabitFriendshipPageQuery {
    pageNo?: number
    pageSize?: number
    sortBy?: string
    sortOrder?: string
    status?: number
    keyword?: string
}

/**
 * 成就徽章响应
 */
export interface HabitAchievementResponse {
    id: number
    achievementCode: string
    achievementName: string
    achievementIcon: string
    achievementColor: string
    description: string
    achievementType: number
    conditionType: number
    conditionValue: number
    sortOrder: number
    rarity: number
    unlocked: boolean
    unlockTime: number
    progress: number
    currentValue: number
}
