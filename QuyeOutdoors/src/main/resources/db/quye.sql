CREATE DATABASE IF NOT EXISTS `quye` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quye`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `tb_note_comment`;
CREATE TABLE `tb_note_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论主键',
  `user_id` bigint NOT NULL COMMENT '评论用户',
  `note_id` bigint NOT NULL COMMENT '山野笔记',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '一级评论为0',
  `answer_id` bigint NOT NULL DEFAULT 0 COMMENT '回复评论为0',
  `content` varchar(512) NOT NULL COMMENT '评论内容',
  `liked` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0正常 1举报 2隐藏',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_note_id` (`note_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='山野笔记评论';

DROP TABLE IF EXISTS `tb_journey_note`;
CREATE TABLE `tb_journey_note` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '笔记主键',
  `place_id` bigint NOT NULL COMMENT '关联户外目的地',
  `user_id` bigint NOT NULL COMMENT '发布用户',
  `title` varchar(255) NOT NULL COMMENT '笔记标题',
  `images` varchar(2048) NOT NULL COMMENT '图片路径，逗号分隔',
  `content` varchar(4096) NOT NULL COMMENT '笔记正文',
  `liked` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comments` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_place_id` (`place_id`),
  KEY `idx_liked` (`liked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='山野笔记';

INSERT INTO `tb_journey_note` VALUES
(1, 1, 1, '雨后九溪，竹林像刚醒过来', '/imgs/covers/forest.jpg', '从九溪入口沿溪水慢慢走，雨后的石阶有些滑，但空气里全是竹叶和泥土的味道。全程强度不高，记得穿防滑鞋。', 128, 12, '2026-06-20 09:20:00', '2026-06-20 09:20:00'),
(2, 2, 2, '第一次在莫干山看见云海', '/imgs/covers/camp.jpg', '凌晨四点半从营地出发，六点前抵达观景台。路线清晰，后半程坡度稍大，带一件防风外套会舒服很多。', 96, 8, '2026-06-19 07:40:00', '2026-06-19 07:40:00'),
(3, 3, 3, '富春江边的轻量化露营清单', '/imgs/covers/water.jpg', '一顶双人帐、一张蛋卷桌、两把月亮椅就够了。江边早晚温差明显，睡袋舒适温标建议选到10℃。', 211, 19, '2026-06-18 18:10:00', '2026-06-18 18:10:00'),
(4, 4, 4, '龙井骑行：城市边缘的一圈绿色', '/imgs/covers/ridge.jpg', '从杨公堤出发，经龙井路到梅家坞，再从云栖返回。约26公里，连续爬坡不长，普通公路车和山地车都适合。', 74, 6, '2026-06-17 16:30:00', '2026-06-17 16:30:00'),
(5, 5, 5, '湘湖桨板新手第一次下水', '/imgs/covers/forest.jpg', '上午水面更平静，新手先跪姿找平衡。救生衣一定全程穿好，手机用防水袋系在板绳上。', 63, 5, '2026-06-16 12:00:00', '2026-06-16 12:00:00'),
(6, 6, 1, '临安溪谷溯溪，一双鞋有多重要', '/imgs/covers/camp.jpg', '普通运动鞋在湿石头上很容易打滑，建议使用包趾溯溪鞋。雨后水位变化快，出发前要确认天气与上游情况。', 88, 7, '2026-06-15 14:10:00', '2026-06-15 14:10:00'),
(7, 7, 2, '北高峰日落线，不赶时间的走法', '/imgs/covers/water.jpg', '从老和山上山，傍晚在山脊等日落，再从灵隐方向下山。务必带头灯，夜间下坡不要只依赖手机照明。', 157, 14, '2026-06-14 20:20:00', '2026-06-14 20:20:00'),
(8, 8, 3, '带孩子认识湿地里的十种鸟', '/imgs/covers/ridge.jpg', '准备一只轻便望远镜和一本常见鸟类图鉴，沿栈道慢走，保持安静。上午八点前更容易观察到活跃的水鸟。', 119, 11, '2026-06-13 10:00:00', '2026-06-13 10:00:00');

DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL COMMENT '关注者',
  `follow_user_id` bigint UNSIGNED NOT NULL COMMENT '被关注者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='创作者关注关系';

INSERT INTO `tb_follow` VALUES
(1, 1, 2, '2026-06-12 10:00:00'),
(2, 1, 3, '2026-06-12 10:10:00'),
(3, 2, 1, '2026-06-12 10:20:00'),
(4, 3, 1, '2026-06-12 10:30:00'),
(5, 4, 1, '2026-06-12 10:40:00'),
(6, 4, 2, '2026-06-12 10:50:00');

DROP TABLE IF EXISTS `tb_place_category`;
CREATE TABLE `tb_place_category` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '分类名称',
  `icon` varchar(255) NOT NULL COMMENT '分类图标',
  `sort` int UNSIGNED NOT NULL DEFAULT 0,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户外目的地分类';

INSERT INTO `tb_place_category` VALUES
(1, '露营地', '/imgs/categories/camping.svg', 1, NOW(), NOW()),
(2, '徒步路线', '/imgs/categories/hiking.svg', 2, NOW(), NOW()),
(3, '骑行路线', '/imgs/categories/cycling.svg', 3, NOW(), NOW()),
(4, '桨板皮划艇', '/imgs/categories/paddling.svg', 4, NOW(), NOW()),
(5, '攀岩抱石', '/imgs/categories/climbing.svg', 5, NOW(), NOW()),
(6, '溯溪玩水', '/imgs/categories/river.svg', 6, NOW(), NOW()),
(7, '登山线路', '/imgs/categories/mountain.svg', 7, NOW(), NOW()),
(8, '亲子自然', '/imgs/categories/family.svg', 8, NOW(), NOW()),
(9, '户外俱乐部', '/imgs/categories/club.svg', 9, NOW(), NOW()),
(10, '装备补给', '/imgs/categories/gear.svg', 10, NOW(), NOW());

DROP TABLE IF EXISTS `tb_outdoor_place`;
CREATE TABLE `tb_outdoor_place` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '目的地名称',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '户外分类',
  `images` varchar(2048) NOT NULL COMMENT '图片路径，逗号分隔',
  `area` varchar(128) DEFAULT NULL COMMENT '所在区域',
  `address` varchar(255) NOT NULL COMMENT '集合点或地址',
  `x` double UNSIGNED NOT NULL COMMENT '经度',
  `y` double UNSIGNED NOT NULL COMMENT '纬度',
  `avg_price` bigint UNSIGNED DEFAULT NULL COMMENT '参考费用',
  `sold` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与人次',
  `comments` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '笔记与评价数',
  `score` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '评分乘10',
  `open_hours` varchar(64) DEFAULT NULL COMMENT '适宜或开放时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_area` (`area`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户外目的地';

INSERT INTO `tb_outdoor_place` VALUES
(1, '云栖竹径轻徒步', 2, '/imgs/covers/forest.jpg', '西湖区', '梅灵南路云栖竹径入口', 120.087280, 30.180420, 0, 2315, 186, 48, '07:00-18:00', NOW(), NOW()),
(2, '莫干山云顶营地', 1, '/imgs/covers/camp.jpg', '德清', '莫干山镇劳岭村云顶路18号', 119.874510, 30.612630, 198, 1420, 96, 47, '09:00-次日11:00', NOW(), NOW()),
(3, '富春江畔草地营场', 1, '/imgs/covers/water.jpg', '桐庐', '富春江镇芦茨村江滨绿道', 119.695320, 29.795210, 128, 980, 73, 46, '08:00-22:00', NOW(), NOW()),
(4, '龙井环线骑行', 3, '/imgs/covers/ridge.jpg', '西湖区', '杨公堤花港观鱼西门集合', 120.133810, 30.229520, 0, 1860, 112, 47, '06:00-20:00', NOW(), NOW()),
(5, '湘湖静水桨板基地', 4, '/imgs/covers/forest.jpg', '萧山区', '湘湖路越王城山北码头', 120.233020, 30.146740, 168, 760, 58, 46, '08:30-18:30', NOW(), NOW()),
(6, '临安青山湖溪谷', 6, '/imgs/covers/camp.jpg', '临安区', '太湖源镇白沙村游客中心', 119.598420, 30.338230, 68, 1210, 89, 45, '08:00-17:30', NOW(), NOW()),
(7, '北高峰日落登山线', 7, '/imgs/covers/water.jpg', '西湖区', '老和山登山口', 120.108770, 30.272140, 0, 3520, 244, 48, '全天开放', NOW(), NOW()),
(8, '西溪湿地观鸟径', 8, '/imgs/covers/ridge.jpg', '西湖区', '天目山路周家村入口', 120.061990, 30.267120, 80, 2180, 137, 47, '07:30-18:00', NOW(), NOW()),
(9, '良渚山野俱乐部', 9, '/imgs/covers/forest.jpg', '余杭区', '良渚文化村玉鸟路16号', 120.016760, 30.383640, 99, 640, 52, 46, '10:00-21:00', NOW(), NOW()),
(10, '运河边户外补给站', 10, '/imgs/covers/camp.jpg', '拱墅区', '丽水路大兜路历史街区东口', 120.148330, 30.302810, 120, 430, 39, 45, '10:00-22:00', NOW(), NOW()),
(11, '径山古道竹海线', 2, '/imgs/covers/water.jpg', '余杭区', '径山镇桐桥停车场', 119.787170, 30.393510, 0, 1700, 104, 47, '06:30-18:30', NOW(), NOW()),
(12, '千岛湖绿道骑行', 3, '/imgs/covers/ridge.jpg', '淳安', '千岛湖广场骑行驿站', 119.027210, 29.608570, 88, 1100, 85, 48, '07:00-19:00', NOW(), NOW()),
(13, '大明山自然岩壁', 5, '/imgs/covers/forest.jpg', '临安区', '清凉峰镇白果村攀岩基地', 118.994510, 30.047760, 228, 380, 31, 46, '09:00-17:00', NOW(), NOW()),
(14, '大清谷亲子自然营', 8, '/imgs/covers/camp.jpg', '西湖区', '龙坞茶镇大清社区88号', 120.071520, 30.166910, 158, 820, 69, 47, '09:00-17:30', NOW(), NOW());

DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `phone` varchar(11) NOT NULL,
  `password` varchar(128) DEFAULT '',
  `nick_name` varchar(32) NOT NULL,
  `icon` varchar(255) DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='趣野用户';

INSERT INTO `tb_user` VALUES
(1, '13800000001', 'quye-demo-salt-2026@8c74d5c7ab96374382bfa305af350669', '林间小鹿', '/imgs/icons/girl.jpg', NOW(), NOW()),
(2, '13800000002', '', '山风阿木', '/imgs/icons/boy.jpg', NOW(), NOW()),
(3, '13800000003', '', '周末出逃计划', '/imgs/icons/wsq.jpg', NOW(), NOW()),
(4, '13800000004', '', '江边搭帐篷', '/imgs/icons/black.jpg', NOW(), NOW()),
(5, '13800000005', '', '路标收藏家', '/imgs/icons/black.jpg', NOW(), NOW());

DROP TABLE IF EXISTS `tb_user_profile`;
CREATE TABLE `tb_user_profile` (
  `user_id` bigint UNSIGNED NOT NULL,
  `city` varchar(64) DEFAULT NULL,
  `introduce` varchar(128) DEFAULT NULL,
  `fans` int UNSIGNED NOT NULL DEFAULT 0,
  `followee` int UNSIGNED NOT NULL DEFAULT 0,
  `gender` tinyint(1) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `credits` int UNSIGNED NOT NULL DEFAULT 0,
  `level` tinyint(1) NOT NULL DEFAULT 0,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户山野档案';

INSERT INTO `tb_user_profile` VALUES
(1, '杭州', '把城市的疲惫留在山脚下', 1200, 86, 1, '1998-05-16', 680, 1, NOW(), NOW()),
(2, '杭州', '轻量化徒步与露营爱好者', 860, 42, 0, '1996-10-02', 520, 1, NOW(), NOW()),
(3, '上海', '每周发现一条新路线', 630, 51, 1, '1999-03-21', 430, 0, NOW(), NOW()),
(4, '杭州', '江边露营和手冲咖啡', 310, 29, 0, '1997-12-08', 260, 0, NOW(), NOW()),
(5, '宁波', '记录路标，也记录风景', 270, 37, 1, '2000-07-12', 220, 0, NOW(), NOW());

DROP TABLE IF EXISTS `tb_experience_pass`;
CREATE TABLE `tb_experience_pass` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `place_id` bigint UNSIGNED NOT NULL COMMENT '关联目的地',
  `title` varchar(64) NOT NULL COMMENT '体验券标题',
  `sub_title` varchar(128) DEFAULT NULL,
  `rules` varchar(1024) DEFAULT NULL,
  `pay_value` bigint UNSIGNED NOT NULL COMMENT '支付金额，分',
  `actual_value` bigint UNSIGNED NOT NULL COMMENT '票面金额，分',
  `type` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '0普通 1限量',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '1上架 2下架 3过期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_place_id` (`place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户外体验券';

INSERT INTO `tb_experience_pass` VALUES
(1, 2, '云顶双人营位早鸟券', '含双人营位与公共天幕使用', '提前一天预约；节假日需补差价；每人限领一次', 9900, 19800, 1, 1, NOW(), NOW()),
(2, 5, '桨板新手体验券', '教练带队与全套安全装备', '需会游泳；受天气与水域开放情况影响；每人限领一次', 6900, 16800, 1, 1, NOW(), NOW()),
(3, 13, '自然岩壁初体验', '基础教学、装备与保险', '限14至55周岁；现场评估身体状态；每人限领一次', 12800, 22800, 1, 1, NOW(), NOW()),
(4, 9, '周末徒步活动抵扣券', '俱乐部公开活动通用', '活动报名时使用；不可与其他优惠叠加', 3900, 5000, 0, 1, NOW(), NOW());

DROP TABLE IF EXISTS `tb_flash_pass`;
CREATE TABLE `tb_flash_pass` (
  `pass_id` bigint UNSIGNED NOT NULL,
  `stock` int NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `begin_time` timestamp NOT NULL,
  `end_time` timestamp NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pass_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='限量体验券库存';

INSERT INTO `tb_flash_pass` VALUES
(1, 30, NOW(), '2026-01-01 00:00:00', '2027-12-31 23:59:59', NOW()),
(2, 40, NOW(), '2026-01-01 00:00:00', '2027-12-31 23:59:59', NOW()),
(3, 20, NOW(), '2026-01-01 00:00:00', '2027-12-31 23:59:59', NOW());

DROP TABLE IF EXISTS `tb_pass_order`;
CREATE TABLE `tb_pass_order` (
  `id` bigint NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `pass_id` bigint UNSIGNED NOT NULL,
  `pay_type` tinyint UNSIGNED NOT NULL DEFAULT 1,
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pay_time` timestamp NULL DEFAULT NULL,
  `use_time` timestamp NULL DEFAULT NULL,
  `refund_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_pass` (`user_id`,`pass_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体验券订单';

INSERT INTO `tb_note_comment` VALUES
(1, 2, 1, 0, 0, '雨后石阶确实滑，登山杖很有帮助。', 8, 0, NOW(), NOW()),
(2, 3, 3, 0, 0, '这份轻量清单很适合第一次露营。', 5, 0, NOW(), NOW()),
(3, 1, 7, 0, 0, '头灯真的不能省，安全第一。', 11, 0, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
