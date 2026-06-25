# 趣野户外生活平台后端

趣野后端基于 Spring Boot 构建，提供户外目的地、山野笔记、关注关系、点赞、签到、限量体验券等核心接口。项目重点复用了 Redis 在缓存、会话、Feed 流、点赞排行、签到统计和高并发库存扣减中的能力。

## 技术栈

- Java 21
- Spring Boot 3
- MyBatis-Plus
- MySQL
- Redis / Redisson / Lua
- RabbitMQ
- Hutool
- Lombok

## 主要功能

- 用户登录：验证码登录、密码登录、Token 会话管理
- 户外目的地：分类查询、关键词搜索、详情查询
- 山野笔记：发布笔记、图片上传、详情查询、热门列表
- 社交关系：关注、取关、共同关注、关注动态 Feed
- 互动能力：点赞、点赞 TopN 用户展示
- 签到统计：基于 Redis Bitmap 统计连续签到天数
- 限量体验券：Redis Lua 预扣库存，RabbitMQ 异步创建订单，Redisson 控制一人一单

## 本地运行

### 1. 准备环境

需要提前启动：

- MySQL
- Redis
- RabbitMQ

默认配置在：

```text
src/main/resources/application.yaml
```

默认数据库为：

```text
quye
```

### 2. 初始化数据库

执行 SQL 文件：

```text
src/main/resources/db/quye.sql
```

### 3. 启动后端

在后端目录执行：

```powershell
cd QuyeOutdoors
mvn spring-boot:run
```

后端默认端口：

```text
8081
```

### 4. 图片上传目录

默认上传到前端静态资源目录：

```text
../nginx-1.18.0/html/Frontend/imgs
```

如需修改，可通过环境变量覆盖：

```powershell
$env:QUYE_UPLOAD_DIR="你的上传目录"
```

## 说明

后端接口统一由前端 Nginx 通过 `/api` 代理到 `http://127.0.0.1:8081`。
