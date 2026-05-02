-- 习惯养成社区系统数据库表

-- 打卡目标表
create table slackjar_trick_one.habit_goal
(
    id                  bigint auto_increment comment '目标ID'
        primary key,
    user_id             bigint            not null comment '用户ID（创建者）',
    goal_name           varchar(100)      not null comment '目标名称（如：早起、跑步、读书）',
    goal_icon           varchar(50)       null comment '目标图标（emoji或图标名称）',
    goal_color          varchar(20)       null comment '目标颜色',
    description         varchar(500)      null comment '目标描述',
    frequency_type      tinyint default 0 not null comment '频率类型：0-每天，1-每周，2-自定义',
    frequency_value     varchar(50)       null comment '频率值：每周选哪几天，用逗号分隔如1,3,5表示周一、三、五',
    remind_time         varchar(10)       null comment '提醒时间（HH:mm格式）',
    remind_enabled      tinyint default 0 not null comment '是否开启提醒：0-关闭，1-开启',
    status              tinyint default 1 not null comment '状态：0-停用，1-进行中，2-已完成',
    start_date          bigint            null comment '开始日期（毫秒时间戳）',
    end_date            bigint            null comment '结束日期（毫秒时间戳，可选）',
    total_days          int     default 0 not null comment '目标总天数',
    checkin_count       int     default 0 not null comment '已打卡天数',
    current_streak      int     default 0 not null comment '当前连续打卡天数',
    longest_streak      int     default 0 not null comment '最长连续打卡天数',
    last_checkin_date   bigint            null comment '最后打卡日期（毫秒时间戳）',
    create_time         bigint            null comment '创建时间（毫秒时间戳）',
    update_time         bigint            null comment '更新时间（毫秒时间戳）',
    deleted             tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version             bigint  default 1 not null comment '版本号（用于乐观锁）'
)
    comment '打卡目标表';

-- 打卡记录表
create table slackjar_trick_one.habit_checkin
(
    id              bigint auto_increment comment '打卡记录ID'
        primary key,
    goal_id         bigint            not null comment '目标ID',
    user_id         bigint            not null comment '用户ID',
    checkin_date    bigint            not null comment '打卡日期（毫秒时间戳，取当天0点）',
    checkin_time    bigint            not null comment '打卡具体时间（毫秒时间戳）',
    content         text              null comment '打卡内容/感想',
    image_ids       varchar(1024)     null comment '打卡图片ID列表，多个用逗号分隔',
    mood            varchar(20)       null comment '心情：happy, excited, tired, normal',
    visibility      tinyint default 1 not null comment '可见性：0-私密，1-好友可见，2-公开',
    like_count      int     default 0 not null comment '点赞数',
    comment_count   int     default 0 not null comment '评论数',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_goal_date (goal_id, checkin_date)
)
    comment '打卡记录表';

-- 好友关系表
create table slackjar_trick_one.habit_friendship
(
    id              bigint auto_increment comment '关系ID'
        primary key,
    user_id         bigint            not null comment '用户ID',
    friend_id       bigint            not null comment '好友ID',
    status          tinyint default 0 not null comment '状态：0-待确认，1-已确认，2-已拒绝，3-已取消',
    apply_user_id   bigint            not null comment '发起申请的用户ID',
    apply_reason    varchar(200)      null comment '申请理由',
    confirm_time    bigint            null comment '确认时间（毫秒时间戳）',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_user_friend (user_id, friend_id)
)
    comment '好友关系表';

-- 打卡点赞表
create table slackjar_trick_one.habit_checkin_like
(
    id              bigint auto_increment comment '点赞ID'
        primary key,
    checkin_id      bigint            not null comment '打卡记录ID',
    user_id         bigint            not null comment '点赞用户ID',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_checkin_user (checkin_id, user_id)
)
    comment '打卡点赞表';

-- 打卡评论表
create table slackjar_trick_one.habit_checkin_comment
(
    id              bigint auto_increment comment '评论ID'
        primary key,
    checkin_id      bigint            not null comment '打卡记录ID',
    user_id         bigint            not null comment '评论用户ID',
    reply_comment_id bigint           null comment '回复的评论ID（null表示一级评论）',
    reply_user_id   bigint            null comment '回复的用户ID',
    content         text              not null comment '评论内容',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）'
)
    comment '打卡评论表';

-- 成就徽章表
create table slackjar_trick_one.habit_achievement
(
    id                      bigint auto_increment comment '成就ID'
        primary key,
    achievement_code        varchar(50)       not null comment '成就编码（唯一标识）',
    achievement_name        varchar(50)       not null comment '成就名称',
    achievement_icon        varchar(100)      null comment '成就图标',
    achievement_color       varchar(20)       null comment '成就颜色',
    description             varchar(200)      null comment '成就描述',
    achievement_type        tinyint default 0 not null comment '成就类型：0-打卡连续天数，1-总打卡次数，2-创建目标数，3-互动数',
    condition_type          tinyint default 0 not null comment '条件类型：0-大于等于，1-等于，2-首次达成',
    condition_value         int               not null comment '条件值（如连续7天填7）',
    related_goal_id         bigint            null comment '关联的目标ID（null表示通用成就）',
    sort_order              int     default 0 not null comment '排序号',
    rarity                  tinyint default 1 not null comment '稀有度：1-普通，2-稀有，3-史诗，4-传说',
    create_time             bigint            null comment '创建时间（毫秒时间戳）',
    update_time             bigint            null comment '更新时间（毫秒时间戳）',
    deleted                 tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version                 bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_achievement_code (achievement_code)
)
    comment '成就徽章表';

-- 用户成就表
create table slackjar_trick_one.habit_user_achievement
(
    id                      bigint auto_increment comment '记录ID'
        primary key,
    user_id                 bigint            not null comment '用户ID',
    achievement_id          bigint            not null comment '成就ID',
    unlock_time             bigint            not null comment '解锁时间（毫秒时间戳）',
    related_checkin_id      bigint            null comment '关联的打卡记录ID（如果有）',
    related_goal_id         bigint            null comment '关联的目标ID（如果有）',
    create_time             bigint            null comment '创建时间（毫秒时间戳）',
    update_time             bigint            null comment '更新时间（毫秒时间戳）',
    deleted                 tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version                 bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_user_achievement (user_id, achievement_id)
)
    comment '用户成就表';

-- 初始化成就数据
insert into slackjar_trick_one.habit_achievement
(achievement_code, achievement_name, achievement_icon, achievement_color, description, achievement_type,
 condition_type, condition_value, related_goal_id, sort_order, rarity, create_time, update_time, deleted, version)
values
('STREAK_7', '周而复始', '🔥', '#FF6B6B', '连续打卡7天', 0, 0, 7, null, 1, 1, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('STREAK_14', '坚持不懈', '💪', '#4ECDC4', '连续打卡14天', 0, 0, 14, null, 2, 2, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('STREAK_30', '月度达人', '🏆', '#FFD93D', '连续打卡30天', 0, 0, 30, null, 3, 2, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('STREAK_60', '习惯养成', '⭐', '#9B59B6', '连续打卡60天', 0, 0, 60, null, 4, 3, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('STREAK_100', '百日达人', '👑', '#E74C3C', '连续打卡100天', 0, 0, 100, null, 5, 4, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('STREAK_365', '年度冠军', '🌟', '#F39C12', '连续打卡365天', 0, 0, 365, null, 6, 4, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('TOTAL_10', '初露锋芒', '🌱', '#2ECC71', '累计打卡10次', 1, 0, 10, null, 7, 1, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('TOTAL_50', '渐入佳境', '🌳', '#3498DB', '累计打卡50次', 1, 0, 50, null, 8, 2, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('TOTAL_100', '百炼成钢', '🏅', '#9B59B6', '累计打卡100次', 1, 0, 100, null, 9, 3, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('TOTAL_500', '坚持达人', '🎖️', '#E67E22', '累计打卡500次', 1, 0, 500, null, 10, 4, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('GOAL_5', '目标管理大师', '📋', '#1ABC9C', '创建5个打卡目标', 2, 0, 5, null, 11, 1, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('GOAL_20', '习惯规划师', '📚', '#16A085', '创建20个打卡目标', 2, 0, 20, null, 12, 2, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('FRIEND_10', '社交达人', '👥', '#2980B9', '添加10位好友', 3, 0, 10, null, 13, 2, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('LIKE_100', '人气王', '❤️', '#C0392B', '获得100个赞', 3, 0, 100, null, 14, 3, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1),
('FIRST_CHECKIN', '第一步', '🚀', '#3498DB', '完成首次打卡', 0, 0, 1, null, 0, 1, unix_timestamp()*1000, unix_timestamp()*1000, 0, 1);
