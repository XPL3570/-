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

 Date: 19/09/2023 00:11:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `SchoolId` int(11) NULL DEFAULT NULL COMMENT '学校ID',
  `UserId` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `ConfessionWallId` int(11) NULL DEFAULT NULL COMMENT '表白墙ID',
  `PhoneNumber` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `WeChatId` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信号',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `CreateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '时间',
  `Permission` tinyint(4) NULL DEFAULT NULL COMMENT '权限标识，0表示普通管理员，1表示超级管理员',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 1, 1, 1, '1234567890', 'adminwx1', 0, '2023-09-02 09:40:19', 1);
INSERT INTO `admin` VALUES (2, 2, 2, 2, '9876543210', 'adminwx2', 0, '2023-09-02 09:40:19', 0);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论记录ID',
  `ConfessionPostReviewId` int(11) NULL DEFAULT NULL COMMENT '关联的表白墙发布内容表ID',
  `ParentCommentId` int(11) NULL DEFAULT NULL COMMENT '父级评论ID',
  `UserId` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `CommentContent` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评论内容',
  `CommentTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '评论时间',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, NULL, 1, '这是第一篇评论', '2023-09-02 09:40:19', 0);
INSERT INTO `comment` VALUES (2, 1, 1, 2, '这是第二篇评论', '2023-09-02 09:40:19', 0);
INSERT INTO `comment` VALUES (3, 1, 5, 1, '张杰吃屎', '2023-09-09 23:24:53', 0);
INSERT INTO `comment` VALUES (4, 1, NULL, 1, '滴答滴答滴答滴答滴答滴答哒哒哒哒哒哒哒哒哒flsdjkhgiskl;fdhgoiufdh', '2023-09-10 14:01:27', 0);
INSERT INTO `comment` VALUES (5, 1, 2, 2, '11111111111', '2023-09-10 14:01:54', 0);
INSERT INTO `comment` VALUES (6, 1, NULL, 1, '别叫了', '2023-09-11 22:20:11', 0);
INSERT INTO `comment` VALUES (8, 1, NULL, 1, '等会睡觉了', '2023-09-12 00:44:24', 0);
INSERT INTO `comment` VALUES (9, 1, NULL, 1, 'qqqqqqqq', '2023-09-14 19:27:10', 0);
INSERT INTO `comment` VALUES (10, 23, NULL, 1, 'qqqqqqqqqqqqqqqqq', '2023-09-14 19:27:18', 0);
INSERT INTO `comment` VALUES (11, 23, 10, 1, '现在是晚上11点', '2023-09-14 23:04:57', 0);
INSERT INTO `comment` VALUES (12, 1, 3, 1, '测试是否死循环', '2023-09-16 22:42:05', 0);
INSERT INTO `comment` VALUES (13, 1, 12, 1, '测试死循环', '2023-09-16 22:44:50', 0);
INSERT INTO `comment` VALUES (14, 1, NULL, 1, '111111111111111', '2023-09-18 14:53:01', 0);
INSERT INTO `comment` VALUES (15, 1, 8, 1, '那你睡吧', '2023-09-18 14:56:57', 0);
INSERT INTO `comment` VALUES (16, 1, NULL, 1, 'wwwwwwwwwwwwwww', '2023-09-18 14:58:12', 0);

-- ----------------------------
-- Table structure for confessionpost
-- ----------------------------
DROP TABLE IF EXISTS `confessionpost`;
CREATE TABLE `confessionpost`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '发布内容ID',
  `WallId` int(11) NULL DEFAULT NULL COMMENT '所属表白墙ID',
  `UserId` int(11) NULL DEFAULT NULL COMMENT '发布者用户ID',
  `Title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布标题',
  `TextContent` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '发布内容文字',
  `ImageURL` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布内容图片URL',
  `CreateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '投稿时间',
  `PublishTime` datetime(0) NULL DEFAULT NULL COMMENT '实际发布时间',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `IsAnonymous` tinyint(1) NULL DEFAULT 0 COMMENT '是否匿名',
  `PostStatus` tinyint(4) NULL DEFAULT NULL COMMENT '发布状态，0表示待审核，1表示审核通过，2表示审核拒绝',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 682 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of confessionpost
-- ----------------------------
INSERT INTO `confessionpost` VALUES (1, 1, 1, '阿法狗让我哥', '佛挡杀佛', 'http://127.0.0.1:2204/upload/20230906000952JhUXBy.jpg;http://127.0.0.1:2204/upload/20230906000957UEcZvI.jpg', '2023-09-02 17:47:04', '2023-09-03 17:47:05', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (23, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (24, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (25, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (26, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (27, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (30, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (31, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 1, 2);
INSERT INTO `confessionpost` VALUES (32, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (33, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (34, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (35, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (44, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 1, 2);
INSERT INTO `confessionpost` VALUES (45, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (46, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 2);
INSERT INTO `confessionpost` VALUES (47, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (48, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (49, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (50, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (51, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (52, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 2);
INSERT INTO `confessionpost` VALUES (53, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (54, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (55, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (60, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (65, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (70, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (80, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (90, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (111, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (222, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (333, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (444, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (554, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (555, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (666, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (667, 1, 1, '我发货扫地付i哈上课的路刚发哈斯入额八嘎', '温热还给我开个会∑(っ°Д°;)っ卧槽，不见了∑(っ°Д°;)っ卧槽，不见了(✪ω✪)(✪ω✪)|ू･ω･` )๑Ő௰Ő๑)曾经瘦过，你也是厉害！๑Ő௰Ő๑)曾经瘦过，你也是厉害！', 'http://127.0.0.1:2204/upload/20230905203023aqdhaK.jpg;http://127.0.0.1:2204/upload/20230905203145blEkJK.jpg', '2023-09-05 20:31:49', NULL, 0, 0, 2);
INSERT INTO `confessionpost` VALUES (668, 1, 1, 'fdASGSDf', 'sdafsadfsd', 'http://127.0.0.1:2204/upload/20230905234327aWbdBe.jpg;http://127.0.0.1:2204/upload/20230905234333JKTfMr.jpg', '2023-09-05 23:43:33', NULL, 0, 0, 1);
INSERT INTO `confessionpost` VALUES (669, 1, 1, '你好，我等会要睡觉', '噶骷髅王好的时光环绕诶u干哈人大gahwklrwaoighrolaghkwer阿利乌i和个人热爱十六大合法光大卡拉萨。ghfsdkljghardwj阿是党和国家考拉水电费哭', 'http://127.0.0.1:2204/upload/20230906000952JhUXBy.jpg;http://127.0.0.1:2204/upload/20230906000957UEcZvI.jpg', '2023-09-06 00:09:59', NULL, 0, 0, 1);
INSERT INTO `confessionpost` VALUES (671, 1, 1, '4343', '545435', '', '2023-09-06 00:13:25', '2023-09-06 00:13:14', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (672, 1, 1, '454436', '54rwyserruyert', '', '2023-09-06 00:15:44', '2023-09-06 00:15:45', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (673, 1, 1, 'wetr', 'feawgjl;rwik\n\n\n\n\nafweiltghri;uklhg\n\n\n\n\n\n\n\ngilasrhgyosaw;re', '', '2023-09-07 18:34:17', '2023-09-07 18:34:17', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (674, 1, 1, '你好是这样的', '你说我要怎么样你才满意啊', 'http://127.0.0.1:2204/upload/20230908175421eLdKnH.png;http://127.0.0.1:2204/upload/20230908175425mWygZo.png', '2023-09-08 17:54:49', NULL, 0, 1, 0);
INSERT INTO `confessionpost` VALUES (678, 1, 1, '仨人帝国时代富商大贾', '佛挡杀佛水电费是的', '', '2023-09-08 18:33:53', '2023-09-08 18:33:54', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (679, 1, 1, '345gg', '33333333', '', '2023-09-15 01:44:50', '2023-09-15 01:44:50', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (680, 1, 1, 'dasdA', 'ssssssssssssssssss', '', '2023-09-16 02:10:59', '2023-09-16 02:10:59', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (681, 1, 1, 'dsadasdas', 'dddddddddddddddddddddsadasdas', 'http://127.0.0.1:2204/upload/20230918145836SwbrQB.jpg', '2023-09-18 14:58:37', NULL, 0, 1, 0);

-- ----------------------------
-- Table structure for confessionwall
-- ----------------------------
DROP TABLE IF EXISTS `confessionwall`;
CREATE TABLE `confessionwall`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表白墙ID',
  `SchoolId` int(11) NULL DEFAULT NULL COMMENT '学校ID',
  `CreatorUserId` int(11) NULL DEFAULT NULL COMMENT '创建者用户ID',
  `AvatarURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `WallName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表白墙名字',
  `Description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表白墙描述',
  `CreateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `Status` tinyint(4) NULL DEFAULT NULL COMMENT '状态，0表示正常，1表示被禁用',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of confessionwall
-- ----------------------------
INSERT INTO `confessionwall` VALUES (1, 1, 1, 'https://example.com/avatar1.jpg', '阿巴阿巴表白墙', '这是表白墙A的描述', '2023-08-30 16:22:28', 0, 0);
INSERT INTO `confessionwall` VALUES (2, 2, 2, 'https://example.com/avatar2.jpg', '表白墙B', '这是表白墙B的描述', '2023-08-30 16:22:28', 1, 0);

-- ----------------------------
-- Table structure for lottery
-- ----------------------------
DROP TABLE IF EXISTS `lottery`;
CREATE TABLE `lottery`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '抽奖记录的唯一标识',
  `SchoolId` int(11) NOT NULL COMMENT '学校ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `ImageUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `ContactInfo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `Introduction` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '介绍',
  `Gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别，0表示男性，1表示女性',
  `CreatedAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录创建时间',
  `DrawCount` int(11) NULL DEFAULT 0 COMMENT '被抽到次数',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lottery
-- ----------------------------
INSERT INTO `lottery` VALUES (11, 1, 1, 'http://127.0.0.1:2204/upload/20230917202935oYbRwO.jpg', '测试男生账号', '唱歌篮球，aaaaaaaaaaaaaaaaaaaaaaaa', 0, '2023-09-07 03:03:14', 9, 0);
INSERT INTO `lottery` VALUES (12, 1, 1, 'http://127.0.0.1:2204/upload/20230917202935oYbRwO.jpg', '测试女生账号', '我是张杰aaaaaaaaaaa', 1, '2023-09-07 03:03:41', 7, 0);
INSERT INTO `lottery` VALUES (15, 1, 1, 'http://127.0.0.1:2204/upload/20230917202935oYbRwO.jpg', '这是女生的qq号', 'dasfadsfdddddddd', 1, '2023-09-17 20:29:42', 7, 0);
INSERT INTO `lottery` VALUES (16, 1, 1, 'http://127.0.0.1:2204/upload/20230917203915rkBFQf.png;http://127.0.0.1:2204/upload/20230917203919oRRhLb.jpg', 'sddddddddddd', 'sdsdddddddddd', 0, '2023-09-17 20:39:20', 9, 0);
INSERT INTO `lottery` VALUES (17, 1, 1, 'http://127.0.0.1:2204/upload/20230917211042rPZoOy.jpg;http://127.0.0.1:2204/upload/20230917211047JuPkuX.png', '3333333333', '333333333333333', 0, '2023-09-17 21:10:48', 10, 0);
INSERT INTO `lottery` VALUES (18, 1, 1, '', '测试女生账号', 'aaaassssssssss', 1, '2023-09-17 23:35:58', 7, 0);
INSERT INTO `lottery` VALUES (19, 1, 1, 'http://127.0.0.1:2204/upload/20230918145748OVGjTA.png', '11111111', 'sdfsaf222222222222', 0, '2023-09-18 14:57:52', 2, 0);
INSERT INTO `lottery` VALUES (20, 1, 1, '', '11111111', '111111111111111', 1, '2023-09-18 16:12:42', 2, 0);
INSERT INTO `lottery` VALUES (21, 1, 1, '', '111111111111', '1111111111111', 0, '2023-09-18 18:24:13', 1, 0);

-- ----------------------------
-- Table structure for lotteryrecord
-- ----------------------------
DROP TABLE IF EXISTS `lotteryrecord`;
CREATE TABLE `lotteryrecord`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '抽奖记录的唯一标识',
  `LotteryId` int(11) NOT NULL COMMENT '抽奖ID',
  `UserId` int(11) NOT NULL COMMENT '用户ID',
  `DrawAt` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '抽奖时间',
  PRIMARY KEY (`Id`) USING BTREE,
  INDEX `UserId`(`UserId`) USING BTREE,
  CONSTRAINT `lotteryrecord_ibfk_1` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lotteryrecord
-- ----------------------------
INSERT INTO `lotteryrecord` VALUES (46, 16, 1, '2023-09-17 23:52:31');
INSERT INTO `lotteryrecord` VALUES (54, 19, 1, '2023-09-18 18:18:21');
INSERT INTO `lotteryrecord` VALUES (55, 20, 1, '2023-09-18 18:19:02');
INSERT INTO `lotteryrecord` VALUES (56, 12, 1, '2023-09-18 18:19:26');
INSERT INTO `lotteryrecord` VALUES (57, 18, 1, '2023-09-18 18:19:50');
INSERT INTO `lotteryrecord` VALUES (58, 21, 1, '2023-09-18 18:24:14');

-- ----------------------------
-- Table structure for msgconfiguration
-- ----------------------------
DROP TABLE IF EXISTS `msgconfiguration`;
CREATE TABLE `msgconfiguration`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `MainSwitch` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of msgconfiguration
-- ----------------------------
INSERT INTO `msgconfiguration` VALUES (1, '欢迎来到同校表白墙', 1);

-- ----------------------------
-- Table structure for school
-- ----------------------------
DROP TABLE IF EXISTS `school`;
CREATE TABLE `school`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `SchoolName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学校名称',
  `AvatarURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `Description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '描述内容',
  `CreatorId` int(11) NULL DEFAULT NULL COMMENT '创建者ID',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `CreateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `CarouselImages` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '录播图地址',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of school
-- ----------------------------
INSERT INTO `school` VALUES (1, '学校A', 'https://example.com/avatar1.jpg', '这是学校A的描述', 1, 0, '2023-08-30 16:22:28', NULL);
INSERT INTO `school` VALUES (2, '学校B', 'https://example.com/avatar2.jpg', '这是学校B的描述', 2, 0, '2023-08-30 16:22:28', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `Username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `SchoolId` int(11) NULL DEFAULT NULL COMMENT '学校ID',
  `OpenId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '微信唯一ID',
  `CreateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `UpdateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `WXAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信账号',
  `Gender` tinyint(2) NULL DEFAULT NULL COMMENT '性别，1表示男性，2表示女性，0表示未知',
  `AvatarURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `Status` tinyint(2) NULL DEFAULT 0 COMMENT '用户状态，0表示正常，1表示被禁止发布，2表示禁止评论，3表示评论和发布都不可以',
  `IsDeleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`Id`) USING BTREE,
  UNIQUE INDEX `idx_openid`(`OpenId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '照抄且报错', 1, 'owFJn5JydzwLQx52JN9p7CtpzHFo', '2023-08-30 16:22:28', '2023-09-10 10:27:03', NULL, NULL, 'http://127.0.0.1:2204/upload/20230910102703eJUwRH.png', 0, 0);
INSERT INTO `user` VALUES (2, '进厂且坐牢', 1, 'aa', '2023-09-09 23:25:41', '2023-09-09 23:26:00', NULL, NULL, 'http://127.0.0.1:2204/upload/20230830162221rLLzXX.jpg', 0, 0);
INSERT INTO `user` VALUES (3, '张杰吃屎', 1, '22', '2023-09-09 23:26:16', '2023-09-09 23:26:20', NULL, NULL, 'http://127.0.0.1:2204/upload/20230830162221rLLzXX.jpg', 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
