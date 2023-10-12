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

 Date: 05/09/2023 23:32:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 668 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of confessionpost
-- ----------------------------
INSERT INTO `confessionpost` VALUES (22, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (23, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (24, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (25, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (26, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (27, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (30, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (31, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (32, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (33, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (34, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (35, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (44, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (45, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (46, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (47, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (48, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (49, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (50, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (51, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (52, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 0, 2);
INSERT INTO `confessionpost` VALUES (53, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (54, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (55, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (60, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (65, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (70, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (80, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (90, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (111, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (222, 1, 1, '打发色规范', '佛挡杀佛', '', '2023-09-02 17:48:25', '2023-09-02 17:48:26', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (333, 1, 1, '53425墙', '分啊发斯蒂芬萨达', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-02 17:50:08', '2023-09-04 19:19:38', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (444, 1, 1, '张杰吃屎', '11111dewagarwstg', 'http://127.0.0.1:2204/upload/20230903130500ranpuh.jpg', '2023-09-03 13:05:02', '2023-09-13 19:19:43', 0, 0, 1);
INSERT INTO `confessionpost` VALUES (554, 1, 1, '这是我的一条投稿记录', 'lfgshdgttoweriqhgorsi', '', '2023-09-03 13:06:08', '2023-09-03 13:06:09', 0, 1, 1);
INSERT INTO `confessionpost` VALUES (555, 1, 1, '阿法狗让我哥', '佛挡杀佛', '', '2023-09-02 17:47:04', '2023-09-02 17:47:05', 0, 1, 0);
INSERT INTO `confessionpost` VALUES (666, 1, 1, 'zhqangj', 'nitashi sdahbfukseagf别下头', 'http://127.0.0.1:2204/upload/20230904110317yiQXwo.png;http://127.0.0.1:2204/upload/20230904110320fQRtiC.jpg', '2023-09-04 11:03:21', '2023-09-04 19:19:47', 0, 0, 0);
INSERT INTO `confessionpost` VALUES (667, 1, 1, '我发货扫地付i哈上课的路刚发哈斯入额八嘎', '温热还给我开个会∑(っ°Д°;)っ卧槽，不见了∑(っ°Д°;)っ卧槽，不见了(✪ω✪)(✪ω✪)|ू･ω･` )๑Ő௰Ő๑)曾经瘦过，你也是厉害！๑Ő௰Ő๑)曾经瘦过，你也是厉害！', 'http://127.0.0.1:2204/upload/20230905203023aqdhaK.jpg;http://127.0.0.1:2204/upload/20230905203145blEkJK.jpg', '2023-09-05 20:31:49', NULL, 0, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
