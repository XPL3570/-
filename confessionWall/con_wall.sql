-- 删除用户表（User）如果存在
DROP TABLE IF EXISTS User;

-- 创建用户表（User） OpenId之后可能要建立唯一键索引
CREATE TABLE User (
                      Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                      Username VARCHAR(255) NOT NULL COMMENT '用户名',
                      SchoolId INT COMMENT '学校ID',
                      OpenId VARCHAR(255) NOT NULL COMMENT '微信唯一ID',
                      CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      WXAccount VARCHAR(255) COMMENT '微信账号',
                      Gender TINYINT(1) DEFAULT NULL COMMENT '性别，0表示女性，1表示男性，NULL表示未知',
                      AvatarURL VARCHAR(255) COMMENT '头像地址'
);

-- 删除学校表（School）如果存在
DROP TABLE IF EXISTS School;

-- 创建学校表（School）
CREATE TABLE School (
                        Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
                        SchoolName VARCHAR(255) NOT NULL COMMENT '学校名称',
                        AvatarURL VARCHAR(255) COMMENT '头像地址',
                        Description TEXT COMMENT '描述内容',
                        CreatorId INT COMMENT '创建者ID',
                        CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 删除表白墙表（ConfessionWall）如果存在
DROP TABLE IF EXISTS ConfessionWall;

-- 创建表白墙表（ConfessionWall）
CREATE TABLE ConfessionWall (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '表白墙ID',
                                SchoolId INT COMMENT '学校ID',
                                CreatorUserId INT COMMENT '创建者用户ID',
                                AvatarURL VARCHAR(255) COMMENT '头像地址',
                                WallName VARCHAR(255) COMMENT '表白墙名字',
                                Description VARCHAR(255) NOT NULL COMMENT '表白墙描述',
                                CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                Status TINYINT COMMENT '状态，0表示正常，1表示被禁用',
                                FOREIGN KEY (SchoolId) REFERENCES School(Id),
                                FOREIGN KEY (CreatorUserId) REFERENCES User(Id)
);

-- 删除表白墙发布内容表——要管理员审核（ConfessionPostReview）如果存在
DROP TABLE IF EXISTS ConfessionPostReview;

-- 创建表白墙发布内容表——要管理员审核（ConfessionPostReview）
CREATE TABLE ConfessionPostReview (
                                      Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '审核记录ID',
                                      PostIds JSON COMMENT '发布内容ID列表',
                                      ReviewerId INT COMMENT '审核人ID',
                                      ReviewStatus ENUM('Pending', 'Approved', 'Rejected') COMMENT '审核状态',
                                      ReviewTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
                                      FOREIGN KEY (ReviewerId) REFERENCES User(Id)
);

-- 删除表白墙发布内容表（ConfessionPost）如果存在
DROP TABLE IF EXISTS ConfessionPost;

-- 创建表白墙发布内容表（ConfessionPost）
CREATE TABLE ConfessionPost (
                                Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '发布内容ID',
                                WallId INT COMMENT '所属表白墙ID',
                                UserId INT COMMENT '发布者用户ID',
                                TextContent TEXT COMMENT '发布内容文字',
                                ImageURL VARCHAR(255) COMMENT '发布内容图片URL',
                                CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志',
                                FOREIGN KEY (WallId) REFERENCES ConfessionWall(Id),
                                FOREIGN KEY (UserId) REFERENCES User(Id)
);

-- 删除评论表（Comment）如果存在
DROP TABLE IF EXISTS Comment;

-- 创建评论表（Comment）
CREATE TABLE Comment (
                         Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论记录ID',
                         ConfessionPostReviewId INT COMMENT '关联的表白墙发布内容审核表ID',
                         ParentCommentId INT COMMENT '父级评论ID',
                         UserId INT COMMENT '用户ID',
                         CommentContent TEXT COMMENT '评论内容',
                         CommentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                         IsDeleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标志',
                         FOREIGN KEY (ConfessionPostReviewId) REFERENCES ConfessionPostReview(Id),
                         FOREIGN KEY (UserId) REFERENCES User(Id),
                         FOREIGN KEY (ParentCommentId) REFERENCES Comment(Id)
);

-- 删除管理员表（Admin）如果存在
DROP TABLE IF EXISTS Admin;

-- 创建管理员表（Admin）
CREATE TABLE Admin (
                       Id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
                       SchoolId INT COMMENT '学校ID',
                       UserId INT COMMENT '用户ID',
                       PhoneNumber VARCHAR(20) COMMENT '手机号',
                       WeChatId VARCHAR(50) COMMENT '微信号',
                       CreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);