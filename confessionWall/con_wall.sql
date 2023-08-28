-- 删除用户表（User）如果存在
DROP TABLE IF EXISTS User;

--  用户表（User） OpenId之后可能要建立唯一键索引
CREATE TABLE User (
                      Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                      Username VARCHAR(255) NOT NULL COMMENT '用户名',
                      SchoolId INT COMMENT '学校ID',
                      OpenId VARCHAR(255) NOT NULL COMMENT '微信唯一ID',
                      CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT ' 时间',
                      UpdateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      WXAccount VARCHAR(255) COMMENT '微信账号',
                      Gender TINYINT(2) DEFAULT null COMMENT '性别，1 表示男性，2 表示女性，0 表示未知',
                      AvatarURL VARCHAR(255) COMMENT '头像地址',
                      UNIQUE INDEX idx_openid (OpenId)
);
-- 删除学校表（School）如果存在
DROP TABLE IF EXISTS School;

-- 学校表（School）  后面会在学校名字这里加唯一约束
CREATE TABLE School (
                        Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
                        SchoolName VARCHAR(255) NOT NULL COMMENT '学校名称',
                        AvatarURL VARCHAR(255) COMMENT '头像地址',
                        Description TEXT COMMENT '描述内容',
                        CreatorId INT COMMENT ' 者ID',
                        CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT ' 时间'
);
INSERT INTO School (SchoolName, AvatarURL, Description, CreatorId)
VALUES ('学校A', 'https://example.com/avatar1.jpg', '这是学校A的描述', 1),
       ('学校B', 'https://example.com/avatar2.jpg', '这是学校B的描述', 2);


-- 删除表白墙发布内容表（ConfessionPost）如果存在
DROP TABLE IF EXISTS ConfessionPost;
-- 表白墙表（ConfessionWall）
CREATE TABLE ConfessionWall (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '表白墙ID',
                                SchoolId INT COMMENT '学校ID',
                                CreatorUserId INT COMMENT ' 者用户ID',
                                AvatarURL VARCHAR(255) COMMENT '头像地址',
                                WallName VARCHAR(255) COMMENT '表白墙名字',
                                Description VARCHAR(255) NOT NULL COMMENT '表白墙描述',
                                CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT ' 时间',
                                Status TINYINT COMMENT '状态，0表示正常，1表示被禁用'
);

DROP TABLE IF EXISTS ConfessionPost;
-- 表白墙发布内容表（ConfessionPost）
CREATE TABLE ConfessionPost (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '发布内容ID',
                                WallId INT COMMENT '所属表白墙ID',
                                UserId INT COMMENT '发布者用户ID',
                                TextContent TEXT COMMENT '发布内容文字',
                                ImageURL VARCHAR(255) COMMENT '发布内容图片URL',
                                CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT ' 时间',
                                IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志',
                                PostStatus TINYINT COMMENT '发布状态，0表示待审核，1表示审核通过，2表示审核拒绝'
);

-- 删除评论表（Comment）如果存在
DROP TABLE IF EXISTS Comment;

--  评论表（Comment）
CREATE TABLE Comment (
                         Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论记录ID',
                         ConfessionPostReviewId INT COMMENT '关联的表白墙发布内容审核表ID',
                         ParentCommentId INT COMMENT '父级评论ID',
                         UserId INT COMMENT '用户ID',
                         CommentContent TEXT COMMENT '评论内容',
                         CommentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                         IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志'
);

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
                       CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT ' 时间',
                       Permission TINYINT COMMENT '权限标识，0表示普通管理员，1表示超级管理员'
);