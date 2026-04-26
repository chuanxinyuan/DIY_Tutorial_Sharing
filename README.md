# 旧物改造教程分享平台

## 项目架构

```
qiantai/  ──── 前台 (Vue 2 + Element UI)  :8080
houtai/   ──── 后台管理 (Vue 2 + Element UI)  :9999
houduan/  ──── 后端 (Spring Boot + JdbcTemplate + MyBatis-Plus)  :8888
houduan/database/init1.sql ──── 数据库msql初始化 
```

## 快速启动

```bash
# 1. 启动 MySQL（端口 3307，数据库 init1）
# 2. 后端
cd houduan && mvn spring-boot:run

# 3. 前台（用户端）
cd qiantai && npm run serve

# 4. 后台管理
cd houtai && npm run serve
```

## 功能模块

| 模块 | 前后端位置 | 说明 |
|------|-----------|------|
| 教程管理 | `TutorialPlatformController.java` + `TutorialHomeV2/DetailV2/PublishV2` | 发布/编辑/浏览教程，热度排序 |
| 社区论坛 | `TutorialPlatformController.java` + `CommunityV2.vue` | 发帖/评论/楼中楼 |
| 材料包交易 | `TutorialPlatformController.java` + `TutorialDetailV2.vue` | 购买/订单/物流模拟 |
| 用户系统 | `TutorialPlatformController.java` + `UserCenterV2.vue` | 注册/登录/充值/个人中心 |
| 管理后台 | `TutorialPlatformController.java` + `houtai tp_*` | 用户管理/内容管控/商品管理 |
| CRUD | `ErshouwupinController` 等 + `houtai/*` | 二手物品/订单/用户信息 |

