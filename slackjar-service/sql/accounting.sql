-- 账户类型枚举: CASH-现金, BANK_CARD-银行卡, ALIPAY-支付宝, WECHAT-微信
-- 收支类型枚举: INCOME-收入, EXPENSE-支出

-- 账户表
CREATE TABLE IF NOT EXISTS `account` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '账户名称',
    `type` varchar(20) NOT NULL COMMENT '账户类型(CASH/BANK_CARD/ALIPAY/WECHAT)',
    `balance` decimal(18,2) DEFAULT 0.00 COMMENT '账户余额',
    `bank_name` varchar(100) NULL COMMENT '银行名称（银行卡类型专用）',
    `card_number` varchar(50) NULL COMMENT '卡号（银行卡类型专用，脱敏存储）',
    `icon` varchar(255) NULL COMMENT '图标路径',
    `color` varchar(20) NULL COMMENT '显示颜色',
    `is_default` tinyint DEFAULT 0 COMMENT '是否默认账户(0-否,1-是)',
    `is_active` tinyint DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
    `remark` varchar(500) NULL COMMENT '备注',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '分类名称',
    `type` varchar(20) NOT NULL COMMENT '收支类型(INCOME/EXPENSE)',
    `icon` varchar(255) NULL COMMENT '图标路径',
    `color` varchar(20) NULL COMMENT '显示颜色',
    `parent_id` bigint DEFAULT 0 COMMENT '父分类ID（0表示一级分类）',
    `sort_order` int DEFAULT 0 COMMENT '排序号',
    `is_system` tinyint DEFAULT 0 COMMENT '是否系统内置(0-自定义,1-系统)',
    `is_active` tinyint DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
    `remark` varchar(500) NULL COMMENT '备注',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 账单表
CREATE TABLE IF NOT EXISTS `bill` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `amount` decimal(18,2) NOT NULL COMMENT '金额',
    `type` varchar(20) NOT NULL COMMENT '收支类型(INCOME/EXPENSE)',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `account_id` bigint NOT NULL COMMENT '账户ID',
    `payee` varchar(200) NULL COMMENT '收款人/付款人',
    `remark` varchar(500) NULL COMMENT '备注',
    `bill_date` bigint NOT NULL COMMENT '账单日期（毫秒时间戳）',
    `location` varchar(200) NULL COMMENT '消费地点',
    `receipt_image` varchar(255) NULL COMMENT '收据图片路径',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- 预算表
CREATE TABLE IF NOT EXISTS `budget` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `category_id` bigint NULL COMMENT '分类ID（NULL表示总预算）',
    `amount` decimal(18,2) NOT NULL COMMENT '预算金额',
    `period_type` varchar(20) NOT NULL COMMENT '周期类型(MONTHLY/YEARLY)',
    `year` int NOT NULL COMMENT '年份',
    `month` int NULL COMMENT '月份（月度预算必填）',
    `spent_amount` decimal(18,2) DEFAULT 0.00 COMMENT '已消费金额',
    `is_active` tinyint DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
    `remark` varchar(500) NULL COMMENT '备注',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- 转账记录表
CREATE TABLE IF NOT EXISTS `transfer` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `from_account_id` bigint NOT NULL COMMENT '转出账户ID',
    `to_account_id` bigint NOT NULL COMMENT '转入账户ID',
    `amount` decimal(18,2) NOT NULL COMMENT '转账金额',
    `transfer_date` bigint NOT NULL COMMENT '转账日期（毫秒时间戳）',
    `remark` varchar(500) NULL COMMENT '备注',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转账记录表';

-- 报表记录表（用于定期生成月报年报）
CREATE TABLE IF NOT EXISTS `report` (
    `id` bigint AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `report_type` varchar(20) NOT NULL COMMENT '报表类型(MONTHLY/YEARLY)',
    `year` int NOT NULL COMMENT '年份',
    `month` int NULL COMMENT '月份（月报必填）',
    `total_income` decimal(18,2) DEFAULT 0.00 COMMENT '总收入',
    `total_expense` decimal(18,2) DEFAULT 0.00 COMMENT '总支出',
    `savings_rate` decimal(10,2) DEFAULT 0.00 COMMENT '储蓄率',
    `data_json` text NULL COMMENT '报表详细数据(JSON格式)',
    `is_pushed` tinyint DEFAULT 0 COMMENT '是否已推送(0-未推送,1-已推送)',
    `push_time` bigint NULL COMMENT '推送时间',
    `create_time` bigint NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` bigint NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` tinyint DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` bigint DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表记录表';

-- 插入默认分类数据
INSERT INTO `category` (`name`, `type`, `parent_id`, `sort_order`, `is_system`) VALUES
('工资', 'INCOME', 0, 1, 1),
('奖金', 'INCOME', 0, 2, 1),
('投资收益', 'INCOME', 0, 3, 1),
('兼职收入', 'INCOME', 0, 4, 1),
('其他收入', 'INCOME', 0, 5, 1),
('餐饮', 'EXPENSE', 0, 1, 1),
('交通', 'EXPENSE', 0, 2, 1),
('购物', 'EXPENSE', 0, 3, 1),
('娱乐', 'EXPENSE', 0, 4, 1),
('医疗', 'EXPENSE', 0, 5, 1),
('教育', 'EXPENSE', 0, 6, 1),
('住房', 'EXPENSE', 0, 7, 1),
('水电燃气', 'EXPENSE', 0, 8, 1),
('通讯', 'EXPENSE', 0, 9, 1),
('其他支出', 'EXPENSE', 0, 10, 1);

-- 插入默认账户数据
INSERT INTO `account` (`name`, `type`, `balance`, `is_default`) VALUES
('现金', 'CASH', 0.00, 0),
('银行卡', 'BANK_CARD', 0.00, 1),
('支付宝', 'ALIPAY', 0.00, 0),
('微信', 'WECHAT', 0.00, 0);