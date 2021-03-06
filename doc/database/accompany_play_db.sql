/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : accompany_play_db

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-04-18 15:14:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签组ID',
  `name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态(1激活,0失效)',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `charges` decimal(2,0) DEFAULT NULL COMMENT '手续费',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `t_category_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ----------------------------
-- Records of t_category
-- ----------------------------

-- ----------------------------
-- Table structure for t_channel
-- ----------------------------
DROP TABLE IF EXISTS `t_channel`;
CREATE TABLE `t_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `charge` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_channel_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道商表';

-- ----------------------------
-- Records of t_channel
-- ----------------------------

-- ----------------------------
-- Table structure for t_comments
-- ----------------------------
DROP TABLE IF EXISTS `t_comments`;
CREATE TABLE `t_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT '关联商品',
  `star` int(11) DEFAULT NULL COMMENT '几星',
  `content` varchar(1000) DEFAULT NULL COMMENT '名称',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价字典表';

-- ----------------------------
-- Records of t_comments
-- ----------------------------

-- ----------------------------
-- Table structure for t_member
-- ----------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员表';

-- ----------------------------
-- Records of t_member
-- ----------------------------

-- ----------------------------
-- Table structure for t_money_details
-- ----------------------------
DROP TABLE IF EXISTS `t_money_details`;
CREATE TABLE `t_money_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  `money` varchar(255) DEFAULT NULL,
  `sum` varchar(255) DEFAULT NULL COMMENT '加零钱后的金额',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_money_details_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零钱流水表';

-- ----------------------------
-- Records of t_money_details
-- ----------------------------

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `commission_money` decimal(2,0) DEFAULT NULL,
  `total_money` decimal(2,0) DEFAULT NULL,
  `create_time` decimal(2,0) DEFAULT NULL COMMENT '订单价格',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_order_product
-- ----------------------------
DROP TABLE IF EXISTS `t_order_product`;
CREATE TABLE `t_order_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `product_id` int(11) DEFAULT NULL COMMENT '关联商品',
  `order_no` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `price` decimal(2,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `t_order_product_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单技能关联表';

-- ----------------------------
-- Records of t_order_product
-- ----------------------------

-- ----------------------------
-- Table structure for t_person_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_person_tag`;
CREATE TABLE `t_person_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) DEFAULT NULL COMMENT '标签Id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_id` (`tag_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_person_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`),
  CONSTRAINT `t_person_tag_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人标签关联表';

-- ----------------------------
-- Records of t_person_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_platform_money_details
-- ----------------------------
DROP TABLE IF EXISTS `t_platform_money_details`;
CREATE TABLE `t_platform_money_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `desc` varchar(255) DEFAULT NULL,
  `money` varchar(255) DEFAULT NULL,
  `sum` decimal(2,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台流水表';

-- ----------------------------
-- Records of t_platform_money_details
-- ----------------------------

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户商品',
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '关联技能',
  `price` decimal(2,0) DEFAULT NULL COMMENT '价格',
  `unit` varchar(255) DEFAULT NULL COMMENT '单位类型',
  `putaway_start_time` datetime DEFAULT NULL COMMENT '上架开始时间',
  `putaway_end_time` datetime DEFAULT NULL COMMENT '上架结束时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  CONSTRAINT `t_product_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_product_ibfk_2` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- Records of t_product
-- ----------------------------

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父类id（根目录0）',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名称',
  `gender` tinyint(4) DEFAULT NULL COMMENT '性别(0:不限制,1:男，2:女)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- Records of t_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_tech_attr
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_attr`;
CREATE TABLE `t_tech_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `name` int(11) DEFAULT NULL COMMENT '游戏ID',
  `status` varchar(255) DEFAULT NULL COMMENT '技能字典名称(段位)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能属性表';

-- ----------------------------
-- Records of t_tech_attr
-- ----------------------------

-- ----------------------------
-- Table structure for t_tech_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_tag`;
CREATE TABLE `t_tech_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '技能ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签ID',
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `t_tech_tag_ibfk_1` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`),
  CONSTRAINT `t_tech_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能标签关联表';

-- ----------------------------
-- Records of t_tech_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_tech_value
-- ----------------------------
DROP TABLE IF EXISTS `t_tech_value`;
CREATE TABLE `t_tech_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_attr_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(255) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_attr_id` (`tech_attr_id`),
  CONSTRAINT `t_tech_value_ibfk_1` FOREIGN KEY (`tech_attr_id`) REFERENCES `t_tech_attr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能属性值表';

-- ----------------------------
-- Records of t_tech_value
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别(1男,2女)',
  `head_portraits_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(255) DEFAULT NULL COMMENT '密码盐',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `idcard` varchar(255) DEFAULT NULL COMMENT '身份证',
  `type` tinyint(1) DEFAULT NULL COMMENT '1:普通用户,2:打手,3:渠道商',
  `user_info_auth` tinyint(1) DEFAULT '0' COMMENT '信息认证(0未完善,1已完善,2审核通过)',
  `balance` decimal(2,0) DEFAULT NULL COMMENT '零钱',
  `state` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth`;
CREATE TABLE `t_user_info_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `qq` varchar(255) DEFAULT NULL COMMENT 'qq号码',
  `wechat` varchar(255) DEFAULT NULL COMMENT '微信号',
  `main_pic_url` varchar(255) DEFAULT NULL COMMENT '主图',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信息认证表';

-- ----------------------------
-- Records of t_user_info_auth
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_auth_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_auth_file`;
CREATE TABLE `t_user_info_auth_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `info_auth_id` int(11) DEFAULT NULL COMMENT '关联信息认证ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL COMMENT '扩展名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `info_auth_id` (`info_auth_id`),
  CONSTRAINT `t_user_info_auth_file_ibfk_1` FOREIGN KEY (`info_auth_id`) REFERENCES `t_user_info_auth` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信息认证文件表（图片、声音）';

-- ----------------------------
-- Records of t_user_info_auth_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_info_file
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info_file`;
CREATE TABLE `t_user_info_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1图片,2声音)',
  `url` varchar(255) DEFAULT NULL,
  `ext` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_user_info_file_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息文件表(图片、声音)';

-- ----------------------------
-- Records of t_user_info_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_tech_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tech_auth`;
CREATE TABLE `t_user_tech_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL COMMENT '游戏ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '状态(0未审核,1审核通过)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_user_tech_auth_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`),
  CONSTRAINT `t_user_tech_auth_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能认证表';

-- ----------------------------
-- Records of t_user_tech_auth
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_tech_info
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tech_info`;
CREATE TABLE `t_user_tech_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tech_auth_id` int(11) DEFAULT NULL COMMENT '技能认证ID',
  `tech_attr_id` int(11) DEFAULT NULL COMMENT '技能属性ID',
  `attr` varchar(255) DEFAULT NULL COMMENT '技能属性名称',
  `tech_value_id` int(11) DEFAULT NULL COMMENT '技能属性值ID',
  `value` varchar(255) DEFAULT NULL COMMENT '技能属性值',
  `status` tinyint(4) DEFAULT NULL,
  `rank` varchar(255) DEFAULT NULL COMMENT '排名',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tech_auth_id` (`tech_auth_id`),
  KEY `tech_attr_id` (`tech_attr_id`),
  KEY `t_user_tech_info_ibfk_3` (`tech_value_id`),
  CONSTRAINT `t_user_tech_info_ibfk_1` FOREIGN KEY (`tech_auth_id`) REFERENCES `t_user_tech_auth` (`id`),
  CONSTRAINT `t_user_tech_info_ibfk_2` FOREIGN KEY (`tech_attr_id`) REFERENCES `t_tech_attr` (`id`),
  CONSTRAINT `t_user_tech_info_ibfk_3` FOREIGN KEY (`tech_value_id`) REFERENCES `t_tech_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户技能表';

-- ----------------------------
-- Records of t_user_tech_info
-- ----------------------------
