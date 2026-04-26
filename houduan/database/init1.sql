/*
Navicat MySQL Data Transfer

Source Server         : localhost_3307
Source Server Version : 50562
Source Host           : localhost:3307
Source Database       : init

Target Server Type    : MYSQL
Target Server Version : 50562
File Encoding         : 65001

Date: 2026-03-06 12:38:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `file`
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `originName` varchar(255) DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `guanliyuan`
-- ----------------------------
DROP TABLE IF EXISTS `guanliyuan`;
CREATE TABLE `guanliyuan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `yonghuming` varchar(50) DEFAULT NULL,
  `mima` varchar(50) DEFAULT NULL,
  `status` varchar(10) DEFAULT '0',
  `level` varchar(10) DEFAULT '管理员',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of guanliyuan
-- ----------------------------
INSERT INTO `guanliyuan` VALUES ('1', 'admin', '21232f297a57a5a743894a0e4a801fc3', '0', '管理员', '2026-02-11 02:49:46');

-- ----------------------------
-- Table structure for `tp_cms_page`
-- ----------------------------
DROP TABLE IF EXISTS `tp_cms_page`;
CREATE TABLE `tp_cms_page` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '页面ID',
  `page_key` varchar(50) NOT NULL COMMENT 'about_us,user_agreement,privacy_policy',
  `title` varchar(120) NOT NULL COMMENT '标题',
  `content` mediumtext NOT NULL COMMENT '内容',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1发布,0草稿',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_cms_page_key` (`page_key`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='静态页面';

-- ----------------------------
-- Records of tp_cms_page
-- ----------------------------
INSERT INTO `tp_cms_page` VALUES ('1', 'about_us', '关于我们', '<p>欢迎来到旧物改造教程分享平台，这是一个面向旧物改造爱好者的 UGC 分享社区。</p>', '1', '2026-03-05 07:55:19', '2026-03-05 07:55:19');
INSERT INTO `tp_cms_page` VALUES ('2', 'user_agreement', '用户协议', '<p>用户发布内容需符合平台规范，不得违法违规。</p>', '1', '2026-03-05 07:55:19', '2026-03-05 07:55:19');
INSERT INTO `tp_cms_page` VALUES ('3', 'privacy_policy', '隐私政策', '<p>我们尊重并保护用户隐私，仅在必要范围内使用数据。</p>', '1', '2026-03-05 07:55:19', '2026-03-05 07:55:19');

-- ----------------------------
-- Table structure for `tp_community_post`
-- ----------------------------
DROP TABLE IF EXISTS `tp_community_post`;
CREATE TABLE `tp_community_post` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `author_user_id` bigint(20) unsigned NOT NULL COMMENT '作者ID',
  `title` varchar(150) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '正文',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1正常,3删除',
  `view_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '浏览数',
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论数',
  `hot_score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '热度分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tp_community_post_status_created` (`status`,`created_at`),
  KEY `idx_tp_community_post_hot` (`hot_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='社区帖子';

-- ----------------------------
-- Table structure for `tp_community_post_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tp_community_post_comment`;
CREATE TABLE `tp_community_post_comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint(20) unsigned NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `parent_comment_id` bigint(20) unsigned DEFAULT NULL COMMENT '父评论ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1正常,2删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tp_community_post_comment_post` (`post_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='社区帖子评论';

-- ----------------------------
-- Table structure for `tp_material_dict`
-- ----------------------------
DROP TABLE IF EXISTS `tp_material_dict`;
CREATE TABLE `tp_material_dict` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '材料ID',
  `material_name` varchar(80) NOT NULL COMMENT '材料名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1启用,0停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_material_name` (`material_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='材料字典';

-- ----------------------------
-- Records of tp_material_dict
-- ----------------------------
INSERT INTO `tp_material_dict` VALUES ('1', '牛仔裤', '1');
INSERT INTO `tp_material_dict` VALUES ('2', '塑料瓶', '1');
INSERT INTO `tp_material_dict` VALUES ('3', '针线', '1');
INSERT INTO `tp_material_dict` VALUES ('4', '剪刀', '1');
INSERT INTO `tp_material_dict` VALUES ('5', '颜料', '1');
INSERT INTO `tp_material_dict` VALUES ('6', '胶水', '1');
INSERT INTO `tp_material_dict` VALUES ('7', '其他', '1');

-- ----------------------------
-- Table structure for `tp_material_kit`
-- ----------------------------
DROP TABLE IF EXISTS `tp_material_kit`;
CREATE TABLE `tp_material_kit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '材料包ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '绑定教程ID',
  `seller_user_id` bigint(20) unsigned NOT NULL COMMENT '卖家用户ID',
  `kit_name` varchar(150) NOT NULL COMMENT '材料包名称',
  `kit_desc` varchar(1000) DEFAULT NULL COMMENT '材料包描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '库存',
  `sales_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '销量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1上架,2下架,3违规下架,4删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_material_kit_tutorial` (`tutorial_id`),
  KEY `idx_tp_material_kit_seller` (`seller_user_id`),
  KEY `idx_tp_material_kit_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='材料包商品表';

-- ----------------------------
-- Table structure for `tp_material_kit_order`
-- ----------------------------
DROP TABLE IF EXISTS `tp_material_kit_order`;
CREATE TABLE `tp_material_kit_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(40) NOT NULL COMMENT '订单号',
  `buyer_user_id` bigint(20) unsigned NOT NULL COMMENT '买家ID',
  `seller_user_id` bigint(20) unsigned NOT NULL COMMENT '卖家ID',
  `material_kit_id` bigint(20) unsigned NOT NULL COMMENT '材料包ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `kit_name_snapshot` varchar(150) NOT NULL COMMENT '材料包名称快照',
  `kit_desc_snapshot` varchar(500) DEFAULT NULL COMMENT '材料包描述快照',
  `cover_image_snapshot` varchar(255) DEFAULT NULL COMMENT '教程封面快照',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int(10) unsigned NOT NULL DEFAULT '1' COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1待发货2运输中3待取货4已取货5已取消',
  `receiver_name` varchar(60) DEFAULT NULL COMMENT '收货人',
  `receiver_phone` varchar(30) DEFAULT NULL COMMENT '收货电话',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_material_kit_order_no` (`order_no`),
  KEY `idx_tp_mko_buyer_status` (`buyer_user_id`,`order_status`),
  KEY `idx_tp_mko_seller_status` (`seller_user_id`,`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='材料包订单表';

-- ----------------------------
-- Table structure for `tp_tutorial`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial`;
CREATE TABLE `tp_tutorial` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '教程ID',
  `author_user_id` bigint(20) unsigned NOT NULL COMMENT '作者ID',
  `title` varchar(150) NOT NULL COMMENT '教程标题/成品名称',
  `cover_image_url` varchar(255) DEFAULT NULL COMMENT '封面图',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `difficulty_level` tinyint(4) NOT NULL DEFAULT '2' COMMENT '1入门2普通3进阶4困难',
  `production_time_minutes` int(10) unsigned DEFAULT NULL COMMENT '制作耗时(分钟)',
  `material_summary` varchar(500) DEFAULT NULL COMMENT '材料摘要',
  `status` tinyint(4) NOT NULL DEFAULT '2' COMMENT '2已发布,4下架,5删除',
  `view_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '浏览数',
  `like_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '收藏数',
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论数',
  `hot_score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '热度分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tp_tutorial_author` (`author_user_id`),
  KEY `idx_tp_tutorial_status_created` (`status`,`created_at`),
  KEY `idx_tp_tutorial_hot` (`hot_score`),
  KEY `idx_tp_tutorial_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程主表';

-- ----------------------------
-- Table structure for `tp_tutorial_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_comment`;
CREATE TABLE `tp_tutorial_comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `parent_comment_id` bigint(20) unsigned DEFAULT NULL COMMENT '父评论ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1正常,2删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tp_tutorial_comment_tutorial` (`tutorial_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程评论';

-- ----------------------------
-- Table structure for `tp_tutorial_favorite`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_favorite`;
CREATE TABLE `tp_tutorial_favorite` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_favorite` (`tutorial_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程收藏';

-- ----------------------------
-- Table structure for `tp_tutorial_like`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_like`;
CREATE TABLE `tp_tutorial_like` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_like` (`tutorial_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程点赞';

-- ----------------------------
-- Table structure for `tp_tutorial_material_rel`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_material_rel`;
CREATE TABLE `tp_tutorial_material_rel` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `material_id` bigint(20) unsigned NOT NULL COMMENT '材料ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_material_rel` (`tutorial_id`,`material_id`),
  KEY `idx_tp_tutorial_material_rel_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程材料关联表';

-- ----------------------------
-- Table structure for `tp_tutorial_step`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_step`;
CREATE TABLE `tp_tutorial_step` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `step_no` int(10) unsigned NOT NULL COMMENT '步骤序号',
  `step_title` varchar(120) DEFAULT NULL COMMENT '步骤标题',
  `step_content` text NOT NULL COMMENT '步骤说明',
  `step_image_url` varchar(255) DEFAULT NULL COMMENT '步骤图',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_step_no` (`tutorial_id`,`step_no`),
  KEY `idx_tp_tutorial_step_tutorial` (`tutorial_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程步骤表';

-- ----------------------------
-- Table structure for `tp_tutorial_tag`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_tag`;
CREATE TABLE `tp_tutorial_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1启用,0停用',
  `sort_no` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='教程标签字典';

-- ----------------------------
-- Records of tp_tutorial_tag
-- ----------------------------
INSERT INTO `tp_tutorial_tag` VALUES ('1', '环保改造', '1', '1');
INSERT INTO `tp_tutorial_tag` VALUES ('2', '零成本手作', '1', '2');
INSERT INTO `tp_tutorial_tag` VALUES ('3', '收纳整理', '1', '3');
INSERT INTO `tp_tutorial_tag` VALUES ('4', '家居美化', '1', '4');

-- ----------------------------
-- Table structure for `tp_tutorial_tag_rel`
-- ----------------------------
DROP TABLE IF EXISTS `tp_tutorial_tag_rel`;
CREATE TABLE `tp_tutorial_tag_rel` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tutorial_id` bigint(20) unsigned NOT NULL COMMENT '教程ID',
  `tag_id` bigint(20) unsigned NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_tutorial_tag_rel` (`tutorial_id`,`tag_id`),
  KEY `idx_tp_tutorial_tag_rel_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教程标签关联表';

-- ----------------------------
-- Table structure for `tp_user`
-- ----------------------------
DROP TABLE IF EXISTS `tp_user`;
CREATE TABLE `tp_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password_hash` varchar(64) NOT NULL COMMENT 'MD5密码摘要',
  `nickname` varchar(60) NOT NULL COMMENT '昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号',
  `wallet_balance` decimal(12,2) NOT NULL DEFAULT '1000.00' COMMENT '钱包余额(模拟)',
  `total_earnings` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计收益(卖家)',
  `role_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1普通用户,9管理员',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1启用,0禁用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tp_user_username` (`username`),
  KEY `idx_tp_user_role_status` (`role_type`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of tp_user
-- ----------------------------
INSERT INTO `tp_user` VALUES ('1', 'admin', '21232f297a57a5a743894a0e4a801fc3', '系统管理员', null, null, '1000.00', '0.00', '9', '1', '2026-03-05 07:55:19', '2026-03-05 07:55:19');
