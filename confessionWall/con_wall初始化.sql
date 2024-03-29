/*
 Navicat Premium Data Transfer

 Source Server         : zj
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : localhost:3306
 Source Schema         : con_wall

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 16/12/2023 05:19:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accept_user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `accept_user_feedback`;
CREATE TABLE `accept_user_feedback`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `UserId` int(11) NOT NULL COMMENT '用户id',
  `Score` int(8) NOT NULL COMMENT '用户反馈评分',
  `Message` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `IsRead` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `SchoolId` int(11) NULL DEFAULT NULL COMMENT '学校ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `ConfessionWallId` int(11) NOT NULL COMMENT '表白墙ID',
  `PhoneNumber` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `WeChatId` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信号',
  `Password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '时间',
  `Permission` tinyint(4) NOT NULL DEFAULT 0 COMMENT '权限标识，0表示普通管理员，1表示超级管理员',
  PRIMARY KEY (`Id`) USING BTREE,
  UNIQUE INDEX `wall_admin_constraint`(`SchoolId`, `UserId`, `ConfessionWallId`) USING BTREE COMMENT '表白墙管理员约束'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论记录ID',
  `ConfessionPostReviewId` int(11) NOT NULL COMMENT '关联的表白墙发布内容表ID',
  `ParentCommentId` int(11) NULL DEFAULT NULL COMMENT '父级评论ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `CommentContent` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
  `CommentTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '评论时间',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for confessionpost
-- ----------------------------
DROP TABLE IF EXISTS `confessionpost`;
CREATE TABLE `confessionpost`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '发布内容ID',
  `WallId` int(11) NOT NULL COMMENT '所属表白墙ID',
  `UserId` int(11) NOT NULL COMMENT '发布者用户ID',
  `Title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发布标题',
  `TextContent` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发布内容文字',
  `ImageURL` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布内容图片URL',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '投稿时间',
  `PublishTime` datetime(0) NULL DEFAULT NULL COMMENT '实际发布时间',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `IsAnonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名',
  `PostStatus` tinyint(3) NOT NULL DEFAULT 0 COMMENT '发布状态，0表示待审核，1表示审核通过，2表示审核拒绝',
  `IsAdminPost` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为管理员发布的内容',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 755 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for confessionwall
-- ----------------------------
DROP TABLE IF EXISTS `confessionwall`;
CREATE TABLE `confessionwall`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表白墙ID',
  `SchoolId` int(11) NOT NULL COMMENT '学校ID',
  `CreatorUserId` int(11) NOT NULL COMMENT '创建者用户ID',
  `AvatarURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `WallName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表白墙名字',
  `Description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表白墙描述',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `Status` tinyint(1) UNSIGNED ZEROFILL NOT NULL COMMENT '是否禁用状态，0表示正常，1表示被禁用',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for globalcarouselimage
-- ----------------------------
DROP TABLE IF EXISTS `globalcarouselimage`;
CREATE TABLE `globalcarouselimage`  (
  `Id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CarouselImage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `IsDisable` tinyint(1) NULL DEFAULT 0 COMMENT '是否禁用 0正常，1禁用',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for image_delete_record
-- ----------------------------
DROP TABLE IF EXISTS `image_delete_record`;
CREATE TABLE `image_delete_record`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ImagePath` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图片路径',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `CreateTime` datetime(0) NOT NULL COMMENT '创建时间',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除，默认为false',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lottery
-- ----------------------------
DROP TABLE IF EXISTS `lottery`;
CREATE TABLE `lottery`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '抽奖记录的唯一标识',
  `SchoolId` int(11) NOT NULL COMMENT '学校ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `ImageUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图片地址',
  `ContactInfo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系方式',
  `Introduction` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '介绍',
  `Gender` tinyint(1) NOT NULL COMMENT '性别，0表示男性，1表示女性',
  `CreatedAt` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录创建时间',
  `DrawCount` int(11) NOT NULL DEFAULT 0 COMMENT '被抽到次数',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lotteryrecord
-- ----------------------------
DROP TABLE IF EXISTS `lotteryrecord`;
CREATE TABLE `lotteryrecord`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '抽奖记录的唯一标识',
  `LotteryId` int(11) NOT NULL COMMENT '抽奖ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `DrawAt` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '抽奖时间',
  PRIMARY KEY (`Id`) USING BTREE,
  INDEX `UserId`(`UserId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msgconfiguration
-- ----------------------------
DROP TABLE IF EXISTS `msgconfiguration`;
CREATE TABLE `msgconfiguration`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '首页提示语',
  `MainSwitch` tinyint(1) NOT NULL COMMENT '是否开启所有学校使用该提示语',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report_record
-- ----------------------------
DROP TABLE IF EXISTS `report_record`;
CREATE TABLE `report_record`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `UserId` int(11) NOT NULL COMMENT '用户id',
  `ReportId` int(11) NOT NULL COMMENT '举报投稿id',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '举报时间',
  `Message` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '举报建议',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for school
-- ----------------------------
DROP TABLE IF EXISTS `school`;
CREATE TABLE `school`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `SchoolName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学校名称',
  `CreatorId` int(11) NOT NULL COMMENT '创建者ID',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `CarouselImages` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '轮播图图片地址',
  `Prompt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校提示语',
  `IsVerified` tinyint(2) NOT NULL DEFAULT 0 COMMENT '审核状态，0表示未审核，1表示通过，2未通过',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `UpdateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上次更新时间',
  `NumberLuckyDraws` int(5) NOT NULL DEFAULT 0 COMMENT '可抽奖次数，设置0表示没有开启，和策略也有关系',
  `NumberPaperInputs` int(5) NOT NULL DEFAULT 1 COMMENT '可放入次数，设置0表示没有开启，和策略也有关系',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for school_application
-- ----------------------------
DROP TABLE IF EXISTS `school_application`;
CREATE TABLE `school_application`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `SchoolId` int(11) NOT NULL COMMENT '学校ID',
  `WechatNumber` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '微信号',
  `PhoneNumber` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `IsApproved` tinyint(2) NOT NULL DEFAULT 0 COMMENT '审核状态，0表示未审核，1表示通过，2表示未通过',
  `ApprovedBy` int(11) NULL DEFAULT NULL COMMENT '审核通过的管理员ID',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `Username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `SchoolId` int(11) NULL DEFAULT NULL COMMENT '学校ID',
  `OpenId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '微信唯一ID',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `UpdateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `CanObtainWeChat` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否可以获取微信',
  `WXAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信账号',
  `Gender` tinyint(2) NOT NULL DEFAULT 0 COMMENT '性别，1表示男性，2表示女性，0表示未知',
  `AvatarURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '头像地址',
  `Status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '用户状态，0表示正常，1表示被禁止发布，2表示禁止评论，3表示评论和发布都不可以',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE,
  UNIQUE INDEX `idx_openid`(`OpenId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_contact
-- ----------------------------
DROP TABLE IF EXISTS `user_contact`;
CREATE TABLE `user_contact`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `RequesterId` int(11) NOT NULL COMMENT '请求方用户ID',
  `ReceiverId` int(11) NOT NULL COMMENT '接收方用户ID',
  `ApplicationReason` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '申请理由',
  `ContactValue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式值',
  `Status` int(3) NOT NULL COMMENT '状态-0未同意，1同意获取，2拒绝获取',
  `CreateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `UpdateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `IsDeleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `admin` VALUES (1, 1, 1, 1, '1111111', 'zhangjie', 'fBw1J60lNtsUifQp4tMSrA==', 0, '2023-09-02 09:40:19', 1);

INSERT INTO `msgconfiguration` VALUES (1, '欢迎来到同校表白墙， 请大家文件发言，您的投稿就是我坚持不懈的', 1);
