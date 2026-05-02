package com.slack.slackjarservice.common.constant;

/**
 * redis常量
 * @author zhn
 */
public class RedisConstants {

    public static final String PREFIX = "slackjar:";

    public static final String HABIT_PREFIX = PREFIX + "habit:";

    public static final String HABIT_CHECKIN_STATUS = HABIT_PREFIX + "checkin:status:";

    public static final String HABIT_CHECKIN_TODAY = HABIT_PREFIX + "checkin:today:";

    public static final String HABIT_GOAL_STATS = HABIT_PREFIX + "goal:stats:";

    public static final String HABIT_STREAK = HABIT_PREFIX + "streak:";

    public static final String HABIT_FRIEND_FEED = HABIT_PREFIX + "friend:feed:";

    public static final String HABIT_FRIEND_REQUEST = HABIT_PREFIX + "friend:request:";

    public static final String HABIT_ACHIEVEMENT = HABIT_PREFIX + "achievement:";

    public static final String HABIT_USER_STATS = HABIT_PREFIX + "user:stats:";

    public static final String HABIT_WEEKLY_DATA = HABIT_PREFIX + "weekly:";

    public static final String HABIT_MONTHLY_DATA = HABIT_PREFIX + "monthly:";

    public static final int DEFAULT_EXPIRE = 24 * 60 * 60;

    public static final int WEEK_EXPIRE = 7 * 24 * 60 * 60;

    public static final int MONTH_EXPIRE = 30 * 24 * 60 * 60;

    public static String getCheckinTodayKey(Long userId) {
        return HABIT_CHECKIN_TODAY + userId;
    }

    public static String getCheckinStatusKey(Long userId, Long goalId) {
        return HABIT_CHECKIN_STATUS + userId + ":" + goalId;
    }

    public static String getStreakKey(Long userId, Long goalId) {
        return HABIT_STREAK + userId + ":" + goalId;
    }

    public static String getGoalStatsKey(Long goalId) {
        return HABIT_GOAL_STATS + goalId;
    }

    public static String getFriendFeedKey(Long userId) {
        return HABIT_FRIEND_FEED + userId;
    }

    public static String getUserStatsKey(Long userId) {
        return HABIT_USER_STATS + userId;
    }

    public static String getWeeklyDataKey(Long userId, int year, int week) {
        return HABIT_WEEKLY_DATA + userId + ":" + year + ":" + week;
    }

    public static String getMonthlyDataKey(Long userId, int year, int month) {
        return HABIT_MONTHLY_DATA + userId + ":" + year + ":" + month;
    }

    public static String getAchievementKey(Long userId, String achievementCode) {
        return HABIT_ACHIEVEMENT + userId + ":" + achievementCode;
    }

    public static String getFriendRequestKey(Long userId) {
        return HABIT_FRIEND_REQUEST + userId;
    }
}
