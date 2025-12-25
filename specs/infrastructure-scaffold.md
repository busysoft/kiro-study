# Spec: Spring Cloud Alibaba & PG16 Microservices Scaffold

## 1. Context & Objective
构建一个适配 JDK 17+ 和 PostgreSQL 16 的高性能微服务基础骨架。重点在于：**云原生适配、高性能数据库访问、阿里系中间件集成。**

## 2. Technical Stack (Technical Specs)
- **Runtime**: JDK 17 (LTS), GraalVM ready.
- **Spring Ecosystem**: 
  - Spring Boot 3.2.x (Mandatory for JDK 17 support)
  - Spring Cloud 2023.0.x
  - Spring Cloud Alibaba 2022.0.0.0+ (Latest Stable)
- **Infrastructure**:
  - **Registry/Config**: Nacos 2.x
  - **Gateway**: Spring Cloud Gateway
  - **Circuit Breaker**: Sentinel
  - **Database**: PostgreSQL 16 (Using HikariCP)
  - **ORM**: MyBatis Plus 3.5.5+ (Latest for Spring Boot 3)

## 3. Project Structure
.
├── common           # 基础依赖、全局异常、JSON 序列化配置、BaseEntity
├── api              # 纯声明式接口 (OpenFeign), DTOs, 公共常量
├── gateway          # 网关模块 (权限、限流、路由)
└── services         # 业务微服务
    └── user-service # 示例业务逻辑

## 4. Key Implementation Rules (Acceptance Criteria)
- [ ] **Observability**: 集成 Actuator 暴露 `/health` 和 `/prometheus` 节点。
- [ ] **DB Layer**: PostgreSQL 必须配置 `jsonb` 处理能力，Mapper 需适配 PG16 语法。
- [ ] **Native Image**: 必须包含 `native-maven-plugin` 以支持 GraalVM 编译。
- [ ] **DevOps**: 提供 `docker-compose.yml` 包含 Nacos, PG16, Redis。