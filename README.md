# 校园论坛交流社区（Jwt方案）第一阶段
采用SpringBoot3 + Vue3编写的前后端分离模版项目，集成多种技术栈，使用JWT校验方案。
***

### 主要实现功能

- 论坛UI界面整体设计、动画等
- 用户个人信息管理，用户隐私设置，密码设置
- 用户头像设置
- 论坛贴列表展示，支持分类展示
- 论坛发帖功能，支持富文本编辑器含图片插入等功能
- 论坛评论功能，支持用户对帖子进行评论或是对评论进行嵌套评论
- 帖子点赞和收藏功能
- 当地实时天气展示

### 项目技术亮点

- 采用Vue3 + SpringBoot3构建，使用最新技术，走在时代前沿
- 天气模块实时对接第三方和风天气API接口，并使用Redis进行天气信息缓存，优化调用次数
- 用户头像基于Minio对象存储实现，更加简单轻松地存储和管理用户头像
- 帖子支持使用富文本编辑器编写，采用Delta数据格式，防止XSS攻击，优化数据传输
- 帖子编辑器支持一键图片粘贴上传，实时同步到Minio对象存储服务，使用更加简单方便
- 论坛帖子列表数据使用Redis进行短时间缓存，防止大量请求对数据库造成压力
- 点赞和收藏数据采用Redis进行存储并定时同步到MySQL数据库，防止高频操作对数据库造成压力
- 项目UI设计简单清晰，功能操作一步到位

### 额外技术亮点

- 采用Mybatis-Plus作为持久层框架，使用更便捷
- 采用Redis存储注册/重置操作验证码，带过期时间控制
- 采用RabbitMQ积压短信发送任务，再由监听器统一处理
- 采用SpringSecurity作为权限校验框架，手动整合Jwt校验方案
- 采用Redis进行IP地址限流处理，防刷接口
- 视图层对象和数据层对象分离，编写工具方法利用反射快速互相转换
- 错误和异常页面统一采用JSON格式返回，前端处理响应更统一
- 手动处理跨域，采用过滤器实现
- 使用Swagger作为接口文档自动生成，已自动配置登录相关接口
- 采用过滤器实现对所有请求自动生成雪花ID方便线上定位问题
- 针对于多环境进行处理，开发环境和生产环境采用不同的配置
- 日志中包含单次请求完整信息以及对应的雪花ID，支持文件记录
- 项目整体结构清晰，职责明确，注释全面，开箱即用
