-- 对话流程表
-- 存储可视化设计的对话流程
CREATE TABLE slackjar_trick_one.df_flow (
    id              bigint auto_increment comment '主键ID'
        primary key,
    name            varchar(100)      not null comment '流程名称',
    description     varchar(500)      null comment '流程描述',
    flow_data       json              not null comment '流程数据（JSON格式，包含nodes和edges）',
    status          tinyint default 1 not null comment '状态：0-禁用，1-启用',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）'
)
    comment '对话流程表';

-- 节点类型字典（可选，用于管理节点类型）
CREATE TABLE slackjar_trick_one.df_node_type (
    id              bigint auto_increment comment '主键ID'
        primary key,
    type_code       varchar(50)       not null comment '节点类型编码',
    type_name       varchar(50)       not null comment '节点类型名称',
    description     varchar(200)      null comment '节点类型描述',
    icon            varchar(100)      null comment '节点图标',
    color           varchar(20)       null comment '节点颜色',
    sort_order      int     default 0 not null comment '排序号',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）'
)
    comment '节点类型字典表';

-- 初始化节点类型数据
INSERT INTO slackjar_trick_one.df_node_type (type_code, type_name, description, icon, color, sort_order) VALUES
('start', '开始', '流程入口节点', 'PlayCircleOutlined', '#52c41a', 1),
('userInput', '用户输入', '等待用户输入节点', 'UserOutlined', '#1890ff', 2),
('aiReply', 'AI回复', 'AI发送消息节点', 'RobotOutlined', '#722ed1', 3),
('condition', '条件分支', '根据条件分流节点', 'BranchesOutlined', '#fa8c16', 4),
('end', '结束', '流程结束节点', 'StopOutlined', '#ff4d4f', 5);
