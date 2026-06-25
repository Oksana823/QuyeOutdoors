# 趣野户外生活平台

趣野是一个面向户外生活场景的本地生活与社交平台，包含前端静态页面和 Spring Boot 后端服务。平台围绕户外目的地、山野笔记、关注动态、点赞签到和限量体验券等功能展开，适合作为 Redis 高并发与社交 Feed 场景的练习项目。

## 项目结构

```text
Platform
├─ QuyeOutdoors          # 后端 Spring Boot 项目
└─ nginx-1.18.0
   └─ html
      └─ Frontend       # 前端静态页面
```

## 技术栈

后端：

- Java 21
- Spring Boot 3
- MyBatis-Plus
- MySQL
- Redis / Redisson / Lua
- RabbitMQ
- Hutool、Lombok

前端：

- HTML / CSS / JavaScript
- Vue 2
- Axios
- Nginx 静态资源服务与接口代理

## 主要功能

- 用户登录、退出、个人档案编辑
- 户外目的地分类、搜索、详情展示
- 山野笔记发布、图片上传、笔记详情、热门笔记
- 点赞、点赞用户列表、关注/取关、共同关注
- 关注动态 Feed 流
- 每日签到与连续签到统计
- 限量体验券领取与订单创建

## 本地启动

### 1. 准备基础服务

需要先启动：

- MySQL
- Redis
- RabbitMQ

数据库初始化 SQL：

```text
QuyeOutdoors/src/main/resources/db/quye.sql
```

后端配置文件：

```text
QuyeOutdoors/src/main/resources/application.yaml
```

### 2. 启动后端

```powershell
cd QuyeOutdoors
mvn spring-boot:run
```

后端默认端口：

```text
8081
```

### 3. 启动前端

进入 Nginx 目录：

```powershell
cd nginx-1.18.0
start nginx.exe
```

前端访问地址：

```text
http://localhost:8080/
```

Nginx 会将 `/api` 请求代理到后端：

```text
http://127.0.0.1:8081
```

## 常用账号

项目支持验证码登录，也支持已初始化用户的密码登录。具体账号可查看数据库初始化 SQL 中的用户数据。

## 备注

- 前端图片资源位于 `nginx-1.18.0/html/Frontend/imgs`。
- 用户发布笔记上传的图片默认保存到 `imgs/journeys` 目录。
- 如修改前端目录或上传目录，需要同步检查 Nginx 配置和后端 `quye.upload-dir` 配置。
