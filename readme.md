# 简历筛选平台

基于 Vue 3 + Spring Boot + MySQL + DeepSeek AI 的全栈简历筛选系统。

## 功能需求

### 1. 简历获取和分析

- 管理员上传 PDF 简历，系统自动解析
- 接入 DeepSeek AI 对简历进行分析，提取结构化信息

### 2. 简历筛选

#### 筛选方法

**第一步筛选（条件筛选）：**

- 期望工作地点（多选，符合其一即可）
- 工作年限（大于某年限）
- 学历（可多选，符合其一即可）
- 期望薪资（选择范围，与简历薪资范围有交集）
- 求职状态（单选）

> 每项中的多选（如地点：上海，北京），符合其一即可。

**第二步筛选（关键词搜索）：**

- 输入关键词，DeepSeek AI 计算匹配度（0-100分）
- 返回匹配文本片段

#### 筛选结果处理

- 不同的筛选条件用不同的高亮来展示
- 可以收藏简历（标识可能岗位）

#### 排序方法

1. 根据文本匹配度进行排序
2. 根据简历最近的更新时间进行排序

### 3. 收藏简历展示

- 展示已收藏的简历及标识的岗位
- 按岗位筛选收藏的简历
- 每个用户的收藏相互独立

---

## 系统架构

```
┌─────────────┐     ┌─────────────────┐     ┌─────────┐
│   Vue 3     │────→│  Spring Boot    │────→│  MySQL  │
│  前端 :3000 │←────│  后端 :8080     │←────│  :3306  │
└─────────────┘     └────────┬────────┘     └─────────┘
                             │
                             ↓
                    ┌─────────────────┐
                    │  DeepSeek AI    │
                    │  简历分析/匹配   │
                    └─────────────────┘
```

---

## 用户角色

| 角色 | 功能 |
|------|------|
| 管理员 (admin) | 上传简历、编辑简历、删除简历 |
| 普通用户 (user) | 浏览简历、筛选简历、收藏简历、查看收藏 |

管理员账号由数据库预置，不可注册。普通用户可自行注册。

---

## 接口设计

### 登录模块 `/api`

| 接口 | 方法 | 请求 | 响应 | 说明 |
|------|------|------|------|------|
| `/api/login` | POST | username, password | username, role | 验证后存入 Session |
| `/api/session` | GET | 无 | username, role | 从 Session 读取当前用户 |
| `/api/logout` | POST | 无 | null | 使 Session 失效 |
| `/api/register` | POST | username, password | 注册成功 | 仅注册普通用户 |

### 简历模块 `/api/resume`

| 接口 | 方法 | 请求 | 响应 | 权限 |
|------|------|------|------|------|
| `/api/resume/upload` | POST | PDF 文件 | 解析后的简历对象 | 管理员 |
| `/api/resume/{id}` | PUT | id + 所有信息 | 成功/失败 | 管理员 |
| `/api/resume/list` | GET | 筛选参数（均可空） | list, total, fittedPositions | 用户 |
| `/api/resume/{id}` | GET | 简历 id | 简历详情 + 收藏状态 | 用户 |
| `/api/resume/{id}` | DELETE | 简历 id | 成功/失败 | 管理员 |
| `/api/resume/{id}/favorite` | PUT | {isFavorite, fittedPosition?} | 成功/失败 | 用户 |

> 用户请求通过 Session Cookie 自动携带用户身份，后端从 Session 获取 userId。

---

## 数据层

**MySQL**，三张表：

### 简历表 (resume)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 姓名 |
| contact | VARCHAR(200) | 联系方式（多个用逗号分隔） |
| expected_locations | VARCHAR(500) | 期望工作地点（多个用逗号分隔） |
| work_years | INT | 工作年限 |
| education | VARCHAR(50) | 学历 |
| salary_min | INT | 期望薪资下限（千元） |
| salary_max | INT | 期望薪资上限（千元） |
| skills | TEXT | 技能 |
| project_experience | TEXT | 项目经验 |
| job_status | VARCHAR(20) | 求职状态：在职/离职/随时到岗 |
| update_time | DATETIME | 简历更新时间 |

### 收藏表 (user_resume_favorite)

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 用户 ID（联合主键） |
| resume_id | BIGINT | 简历 ID（联合主键） |
| fitted_position | VARCHAR(100) | 适配岗位 |

> 每个用户独立收藏，同一简历不同用户可标注不同适配岗位。

### 用户表 (user)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码 |
| role | VARCHAR(20) | 角色：admin / user |

---

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + Vite + Element Plus + Axios |
| 后端 | Spring Boot 3.2 + MyBatis + Java 17 |
| 数据库 | MySQL 8.0 |
| AI | DeepSeek Chat API |
| PDF 解析 | Apache PDFBox 3.0 |
| 部署 | Docker Compose |

---

## 本地启动

### 前提条件

- Java 17+
- Maven 3.9+
- MySQL 8.0
- Node.js 18+

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE resume_select CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入建表脚本
SOURCE init.sql;
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：http://localhost:8080

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：http://localhost:3000

### 4. Docker Compose 一键启动

```bash
docker compose up --build -d
```

前端地址：http://localhost，后端地址：http://localhost:8080

---

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通用户 | 111 | 123456 |

---

## 项目结构

```
resume-select/
├── init.sql                    # 数据库初始化脚本
├── docker-compose.yml          # Docker Compose 配置
├── .env                        # 环境变量（API Key、数据库密码）
├── backend/
│   └── src/main/java/com/resume/
│       ├── controller/         # 控制器（LoginController, ResumeController）
│       ├── service/            # 业务逻辑
│       ├── mapper/             # MyBatis Mapper
│       ├── entity/             # 实体类（Resume, User, UserResumeFavorite）
│       └── dto/                # 数据传输对象
├── backend/src/main/resources/
│   ├── application.yml         # Spring Boot 配置
│   └── mapper/                 # MyBatis XML 映射
└── frontend/
    └── src/
        ├── api/                # API 请求封装
        ├── views/              # 页面组件
        ├── router/             # 路由配置
        └── App.vue             # 根组件
```
