CREATE TABLE IF NOT EXISTS quote_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(100) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    description VARCHAR(200) COMMENT '分类描述',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='名言分类表';

CREATE TABLE IF NOT EXISTS quote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '名言ID',
    content TEXT NOT NULL COMMENT '名言内容',
    author VARCHAR(100) COMMENT '作者',
    source VARCHAR(200) COMMENT '来源',
    category_id BIGINT COMMENT '分类ID',
    is_favorite TINYINT DEFAULT 0 COMMENT '是否收藏',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    INDEX idx_category_id (category_id),
    INDEX idx_is_favorite (is_favorite),
    FOREIGN KEY (category_id) REFERENCES quote_category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='名言表';

CREATE TABLE IF NOT EXISTS quote_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    quote_id BIGINT NOT NULL COMMENT '名言ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    UNIQUE KEY uk_quote_user (quote_id, user_id),
    INDEX idx_quote_id (quote_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (quote_id) REFERENCES quote(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='名言收藏表';

INSERT INTO quote_category (name, icon, sort_order, description) VALUES 
('励志', '🏆', 1, '激励人心的名言警句'),
('爱情', '❤️', 2, '关于爱情的经典语句'),
('学习', '📚', 3, '关于学习与成长的名言'),
('人生', '🌍', 4, '关于人生哲理的名言'),
('友情', '👭', 5, '关于友谊的名言'),
('成功', '✨', 6, '关于成功的名言');

INSERT INTO quote (content, author, source, category_id) VALUES 
('生活不是等待风暴过去，而是学会在雨中翩翩起舞。', '维维安·格林', '', 1),
('成功的秘诀在于始终如一地坚持目标。', '佚名', '', 1),
('每一个不曾起舞的日子，都是对生命的辜负。', '尼采', '', 1),
('爱情是生命的火花，友谊的升华，心灵的吻合。', '莎士比亚', '', 2),
('爱是理解的别名。', '泰戈尔', '', 2),
('两情若是久长时，又岂在朝朝暮暮。', '秦观', '鹊桥仙', 2),
('学而不思则罔，思而不学则殆。', '孔子', '论语', 3),
('书籍是人类进步的阶梯。', '高尔基', '', 3),
('知识就是力量。', '培根', '', 3),
('人生最大的挑战是发现自己是谁，而第二大的挑战是对所发现的感到满意。', '罗杰·塞尔夫', '', 4),
('人生没有彩排，每一天都是现场直播。', '佚名', '', 4),
('人生苦短，及时行乐。', '贺拉斯', '', 4),
('真正的友谊是一株缓慢生长的植物。', '华盛顿', '', 5),
('朋友是第二个自我。', '亚里士多德', '', 5),
('友谊使欢乐倍增，使痛苦减半。', '培根', '', 5),
('成功的唯一秘诀——坚持最后一分钟。', '柏拉图', '', 6),
('成功=艰苦劳动+正确方法+少说空话。', '爱因斯坦', '', 6),
('成功的路上并不拥挤，因为坚持的人不多。', '佚名', '', 6);