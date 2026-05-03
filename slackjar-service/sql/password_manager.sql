create table password_entry
(
    id              bigint auto_increment primary key comment '主键ID',
    user_id         bigint       not null comment '用户ID',
    website         varchar(255) not null comment '网站地址',
    website_name    varchar(100) not null comment '网站名称',
    account         varchar(100) not null comment '账号',
    password        text         not null comment '加密后的密码',
    category        varchar(50)  not null comment '分类（work-工作、personal-个人、social-社交）',
    password_strength int default 0 not null comment '密码强度（0-弱、1-中、2-强）',
    last_login_time bigint            null comment '最后登录时间（毫秒时间戳）',
    notes           text              null comment '备注信息',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    index idx_category (category)
) engine=InnoDB default charset=utf8mb4 comment='密码条目表';

create table password_category
(
    id          bigint auto_increment primary key comment '主键ID',
    user_id     bigint       not null comment '用户ID',
    name        varchar(50)  not null comment '分类名称',
    code        varchar(50)  not null comment '分类编码',
    color       varchar(20)  null comment '分类颜色',
    create_time bigint       null comment '创建时间（毫秒时间戳）',
    update_time bigint       null comment '更新时间（毫秒时间戳）',
    deleted     tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version     bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    unique index uk_user_code (user_id, code)
) engine=InnoDB default charset=utf8mb4 comment='密码分类表';

insert into password_category (user_id, name, code, color, create_time, update_time) values
(0, '工作', 'work', '#1890ff', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(0, '个人', 'personal', '#52c41a', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(0, '社交', 'social', '#faad14', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
