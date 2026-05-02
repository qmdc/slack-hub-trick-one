export {login, logout, register, getUserInfo, pushIpCityInfo, getUserRoles, updateUserInfo, pageQueryUsers, updateUserStatus, assignRoles,
    getUserDevices, forceLogoutDevice, pageQueryUserDevices, changeEmail, changePassword, changePhone} from './modules/auth'
export type {LoginParams, RegisterParams, LoginResult, UserInfo, Role, UpdateUserInfoParams, UserPageQuery, UserDevice, UserDevicePageQuery} from './modules/auth'

export {saveConfig, getConfigByCategory, saveConfigEntity, deleteConfigById, pageQueryConfigs} from './modules/config'
export type {SysConfigRequest, ConfigItem, SysConfigResponse, SysConfigSaveRequest, SysConfigPageQuery, SysConfigItemResponse} from './modules/config'

export {pageQueryRoles, saveRole, deleteRole, assignPermissions, getRolePermissionsBatch as getRolePermissionsBatchFromRole, assignUsersToRole, getUsersByRoleId, pageQueryRoleUsers} from './modules/role'
export type {RolePageQuery, RoleRequest, RoleItem, PermissionItem as RolePermissionItem, SysUser, RoleUserPageQuery} from './modules/role'

export {pageQueryPermissions, savePermission, deletePermission, getPermissionDetail, assignRolesToPermission, getPermissionTree} from './modules/permission'
export type {PermissionPageQuery, PermissionRequest, PermissionItem, PermissionResponse, PermissionDetail} from './modules/permission'

export {uploadFile, downloadFile, batchDeleteFiles} from './modules/file'
export type {FileUploadResponse, BatchDeleteResponse} from './modules/file'

export {getHitokoto, getIpInfo} from './modules/third'
export type {HitokotoResponse, IpInfoResponse} from './modules/third'

export {saveDict, deleteDict, getDictById, getDictByCode, pageQueryDicts} from './modules/dict'
export type {DictItem, SysDictRequest, DictItemRequest, SysDictResponse, DictItemResponse, SysDictPageQuery} from './modules/dict'

export {
    getHomePageData, getStatistics,
    saveGoal, getGoalDetail, deleteGoal, pageQueryGoals, getUserGoals, getTodayPendingGoals, getTodayCompletedGoals,
    checkin, cancelCheckin, getCheckinDetail, pageQueryCheckins, getFriendFeed, toggleLike, addComment, deleteComment, getComments,
    sendFriendRequest, confirmFriendRequest, rejectFriendRequest, cancelFriendRequest, removeFriend,
    getFriendList, pageQueryFriends, getPendingRequests, getSentRequests, getPendingRequestCount, searchUsers, isFriend,
    getAllAchievements, getUserAchievements, getRecentAchievement, getTotalAchievements, getEarnedAchievements
} from './modules/habitformed'
export type {
    HomePageResponse, HabitStatisticsResponse, GoalCompletion, MonthlyRate,
    HabitGoalResponse, HabitGoalSaveRequest, HabitGoalPageQuery,
    HabitCheckinResponse, HabitCheckinRequest, HabitCheckinPageQuery,
    HabitCommentRequest, HabitCheckinCommentResponse,
    HabitFriendResponse, HabitFriendshipRequest, HabitFriendshipPageQuery,
    HabitAchievementResponse
} from './modules/habitformed'

export type {ResponseData, PageResult} from './modules/types'
