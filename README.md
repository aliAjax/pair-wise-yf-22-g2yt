# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录和追溯查询。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

### 完工收尾（本次新增）

产线主管提交完工收尾后，系统按批次汇总末检结论和未关闭不良：

- 有批次末检（FINAL）结论为 `FAIL`，或存在未关闭的 `CRITICAL` 不良 → 工单回到 `PAUSED`，响应列出阻塞批次；
- 全部批次通过后工单变为 `FINISHED`，并落一条完工记录；
- 同一工单重复提交时取回第一次结果（`replayed: true`），不会重复改单；
- 被阻塞的工单在最后一条严重不良关闭后可再次提交，重新按当时状态评估。

```bash
# 1. 登录拿 token（种子用户：inspector1 质检员 / supervisor1 产线主管 / manager1 质量经理 / auditor1 审计员）
TOKEN=$(curl -s -X POST http://localhost:21114/api/auth/login \
  -H 'Content-Type: application/json' -d '{"username":"supervisor1"}' | jq -r .token)

# 2. 查询工单详情：各批次末检结论、未关闭不良数量、阻塞原因
curl -s http://localhost:21114/api/work-order/2 -H "Authorization: Bearer $TOKEN"

# 3. 提交完工收尾（工单 2 的批次 B-1002-A 带未关闭严重不良 → BLOCKED，工单回到 PAUSED）
curl -s -X POST http://localhost:21114/api/work-order/2/completion -H "Authorization: Bearer $TOKEN"

# 4. 质检员关闭最后一条严重不良
QC=$(curl -s -X POST http://localhost:21114/api/auth/login \
  -H 'Content-Type: application/json' -d '{"username":"inspector1"}' | jq -r .token)
curl -s -X POST http://localhost:21114/api/defect-record/301/close -H "Authorization: Bearer $QC"

# 5. 再次提交 → FINISHED；第三次提交 → 取回第一次结果（replayed: true）
curl -s -X POST http://localhost:21114/api/work-order/2/completion -H "Authorization: Bearer $TOKEN"
curl -s -X POST http://localhost:21114/api/work-order/2/completion -H "Authorization: Bearer $TOKEN"
```

### 接口清单

| 方法 | 路径 | 角色 | 说明 |
|---|---|---|---|
| POST | `/api/auth/login` | 公开 | 种子用户登录，返回 JWT |
| GET | `/api/work-order` | 登录用户 | 工单清单（原有接口，照常可用） |
| GET | `/api/work-order/{id}` | 登录用户 | 工单详情：批次结论、不良数量、阻塞原因 |
| POST | `/api/work-order/{id}/completion` | LINE_SUPERVISOR | 提交完工收尾，幂等 |
| GET | `/api/defect-record` | 登录用户 | 不良记录清单 |
| POST | `/api/defect-record/{id}/close` | QUALITY_INSPECTOR / QUALITY_MANAGER | 关闭不良记录 |
| GET | `/health` | 公开 | 健康检查 |

完工提交响应形状（`result` 为 `FINISHED` 或 `BLOCKED`）：

```json
{
  "workOrderId": 2,
  "orderNo": "WO-1002",
  "result": "BLOCKED",
  "status": "PAUSED",
  "replayed": false,
  "batches": [
    {"batchId": 201, "batchNo": "B-1002-A", "finalInspectionResult": "PASS",
     "openDefectCount": 2, "openCriticalDefectCount": 1,
     "blockReasons": ["OPEN_CRITICAL_DEFECTS"]}
  ],
  "blockingBatches": [201],
  "submittedBy": "supervisor1",
  "submittedAt": "2026-09-26T12:00:00Z"
}
```

## 本地开发方式

- 后端：进入 `backend` 后执行 `mvn spring-boot:run`（JDK 17 + Maven），接口统一挂在 `/api`。
- 数据：仓储层为内存种子数据，与 `database/init.sql` 中的种子保持一致；重启后回到初始状态。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── routes/         # 路径常量（按实体分文件）
├── controllers/    # 工单 / 批次 / 检验 / 检验项 / 不良 / 认证
├── services/       # 含 WorkOrderCompletionService（完工收尾核心逻辑）
├── models/         # 含 WorkOrderCompletion、BatchCompletionSummary
├── repositories/   # 内存仓储（种子数据），含 WorkOrderCompletionRepository
├── middlewares/    # AuthMiddleware(JWT) / RbacMiddleware / AuditLogMiddleware / ErrorHandlerMiddleware / RateLimitMiddleware
├── constants/      # 枚举、错误码、错误消息、日志模板
├── constructors/   # DTO 构造器
├── validators/     # 入参校验
├── types/          # 请求 Payload 类型
├── exceptions/     # ApiException
├── utils/          # Formatters / JwtUtils
└── config/         # AppConfig（注册审计拦截器）
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据
- `JWT_SECRET`: JWT 签名密钥，默认 `local-dev-secret`

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus（PLANNED/RUNNING/PAUSED/FINISHED/CANCELLED）: `constants/WorkOrderStatus`；`WorkOrderCompletionService`（完工置 FINISHED、阻塞置 PAUSED）、`WorkOrderRepository` 种子、`WorkOrderDtoFactory` 展示、`init.sql` 种子均有引用。
- InspectionResultStatus（PASS/FAIL/CONDITIONAL_PASS/RECHECK）: `constants/InspectionResultStatus`；`WorkOrderCompletionService` 用 FAIL 判定末检未通过、`QualityInspectionRepository` 种子、`init.sql` 种子均有引用。
- DefectSeverity（MINOR/MAJOR/CRITICAL）: `constants/DefectSeverity`；`WorkOrderCompletionService` 用 CRITICAL 判定严重不良、`DefectRecordRepository` 种子、`init.sql` 种子均有引用。
- InspectionType（FIRST/PATROL/FINAL）: `constants/InspectionType`；`QualityInspectionRepository.findLatestFinalByBatchId` 取末检记录。
- DefectDispositionStatus（OPEN/CLOSED）: `constants/DefectDispositionStatus`；`DefectRecordRepository.findOpenByBatchId`、`DefectRecordService.close`。
- CompletionResult（FINISHED/BLOCKED）: `constants/CompletionResult`；`WorkOrderCompletionService`、`WorkOrderDtoFactory`。
- CompletionBlockReason（FINAL_INSPECTION_FAILED/OPEN_CRITICAL_DEFECTS）: `constants/CompletionBlockReason`；`WorkOrderCompletionService.summarize` 产出，详情与完工响应中展示。
- ActorRole（QUALITY_INSPECTOR/LINE_SUPERVISOR/QUALITY_MANAGER/AUDITOR）: `constants/ActorRole`；`RbacMiddleware`、`WorkOrderController`、`DefectRecordController`、`AuthService`。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。本次完工收尾就同时触达了 constants（4 个新枚举）、models、repositories、services、constructors、controllers、routes、middlewares、validators、types、init.sql 与 README。

## License

MIT
