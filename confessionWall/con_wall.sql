-- 删除用户表（User）如果存在
DROP TABLE IF EXISTS User;

--  用户表（User） OpenId建立唯一键索引
CREATE TABLE User (
                      Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                      Username VARCHAR(255) NOT NULL COMMENT '用户名',
                      SchoolId INT COMMENT '学校ID',
                      OpenId VARCHAR(255) NOT NULL COMMENT '微信唯一ID',
                      CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      WXAccount VARCHAR(255) COMMENT '微信账号',
                      Gender TINYINT(2) DEFAULT NULL COMMENT '性别，1表示男性，2表示女性，0表示未知',
                      AvatarURL VARCHAR(255) COMMENT '头像地址',
                      Status TINYINT(2) DEFAULT 0 COMMENT '用户状态，0表示正常，1表示被禁止发布，2表示禁止评论，3表示评论和发布都不可以',
                      UNIQUE INDEX idx_openid (OpenId)
);
INSERT INTO `user` VALUES (1, '照抄且报错', 1, 'owFJn5JydzwLQx52JN9p7CtpzHFo',
                           '2023-08-30 16:22:28', '2023-08-30 16:22:28', NULL, NULL,
                           'http://127.0.0.1:2204/upload/20230830162221rLLzXX.jpg', 0);


-- 删除学校表（School）如果存在
DROP TABLE IF EXISTS School;

-- 学校表（School）  后面会在学校名字这里加唯一约束
CREATE TABLE School (
                        Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
                        SchoolName VARCHAR(255) NOT NULL COMMENT '学校名称',
                        AvatarURL VARCHAR(255) COMMENT '头像地址',
                        Description TEXT COMMENT '描述内容',
                        CreatorId INT COMMENT '创建者ID',
                        CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
INSERT INTO School (SchoolName, AvatarURL, Description, CreatorId, CreateTime)
VALUES ('学校A', 'https://example.com/avatar1.jpg', '这是学校A的描述', 1, '2023-08-30 16:22:28'),
       ('学校B', 'https://example.com/avatar2.jpg', '这是学校B的描述', 2, '2023-08-30 16:22:28');


-- 删除表白墙表（ConfessionWall）如果
DROP TABLE IF EXISTS ConfessionWall;
-- 表白墙表（ConfessionWall）
CREATE TABLE ConfessionWall (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '表白墙ID',
                                SchoolId INT COMMENT '学校ID',
                                CreatorUserId INT COMMENT '创建者用户ID',
                                AvatarURL VARCHAR(255) COMMENT '头像地址',
                                WallName VARCHAR(255) COMMENT '表白墙名字',
                                Description VARCHAR(255) NOT NULL COMMENT '表白墙描述',
                                CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                Status TINYINT COMMENT '状态，0表示正常，1表示被禁用'
);
-- ConfessionWall 表测试数据
INSERT INTO ConfessionWall (SchoolId, CreatorUserId, AvatarURL, WallName, Description, CreateTime, Status)
VALUES (1, 1, 'https://example.com/avatar1.jpg', '表白墙A', '这是表白墙A的描述', '2023-08-30 16:22:28', 0),
       (2, 2, 'https://example.com/avatar2.jpg', '表白墙B', '这是表白墙B的描述', '2023-08-30 16:22:28', 1);

DROP TABLE IF EXISTS ConfessionPost;
-- 表白墙发布内容表（ConfessionPost）
CREATE TABLE ConfessionPost (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '发布内容ID',
                                WallId INT COMMENT '所属表白墙ID',
                                UserId INT COMMENT '发布者用户ID',
                                Title VARCHAR(30) COMMENT '发布标题',
                                TextContent TEXT COMMENT '发布内容文字',
                                ImageURL VARCHAR(500) COMMENT '发布内容图片URL',
                                CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投稿时间',
                                PublishTime DATETIME COMMENT '实际发布时间',
                                IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志',
                                PostStatus TINYINT COMMENT '发布状态，0表示待审核，1表示审核通过，2表示审核拒绝'
);

-- ConfessionPost 表测试数据
INSERT INTO ConfessionPost (WallId, UserId, Title, TextContent, ImageURL, CreateTime, PublishTime, IsDeleted, PostStatus)
VALUES
    (1, 1, '标题1', '这是第一篇表白内容', 'https://example.com/image1.jpg', NOW(), NOW(), FALSE, 1),
    (2, 2, '标题2', '这是第二篇表白内容', 'https://example.com/image2.jpg', NOW(), NOW(), FALSE, 0);


-- 删除评论表（Comment）如果存在
DROP TABLE IF EXISTS Comment;

--  评论表（Comment）
CREATE TABLE Comment (
                         Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论记录ID',
                         ConfessionPostReviewId INT COMMENT '关联的表白墙发布内容表ID',
                         ParentCommentId INT COMMENT '父级评论ID',
                         UserId INT COMMENT '用户ID',
                         CommentContent TEXT COMMENT '评论内容',
                         CommentTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                         IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志'
);

-- Comment 表测试数据
INSERT INTO Comment (ConfessionPostReviewId, ParentCommentId, UserId, CommentContent, IsDeleted)
VALUES (1, NULL, 1, '这是第一篇评论', FALSE),
       (2, NULL, 2, '这是第二篇评论', FALSE);

-- 删除管理员表（Admin）如果存在
DROP TABLE IF EXISTS Admin;

--  管理员表（Admin）
CREATE TABLE Admin (
                       Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
                       SchoolId INT COMMENT '学校ID',
                       UserId INT COMMENT '用户ID',
                       ConfessionWallId INT COMMENT '表白墙ID',
                       PhoneNumber VARCHAR(20) COMMENT '手机号',
                       WeChatId VARCHAR(50) COMMENT '微信号',
                       CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
                       Permission TINYINT COMMENT '权限标识，0表示普通管理员，1表示超级管理员'
);

-- Admin 表测试数据
INSERT INTO Admin (SchoolId, UserId, ConfessionWallId, PhoneNumber, WeChatId, Permission)
VALUES (1, 1, 1, '1234567890', 'adminwx1', 1),
       (2, 2, 2, '9876543210', 'adminwx2', 0);
