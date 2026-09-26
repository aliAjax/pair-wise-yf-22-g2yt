# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录和追溯查询。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

### 完工收尾（本次新增）

角色通过请求头 `X-Role` 携带：`INSPECTOR`（质检员）/ `LINE_SUPERVISOR`（产线主管）/ `QUALITY_MANAGER`（质量经理）/ `AUDITOR`（审计员）。读接口不强制角色，写接口按 RBAC 校验。

```bash
# 产线主管提交完工收尾：按批次汇总末检结论和未关闭不良
curl -X POST -H 'X-Role: LINE_SUPERVISOR' http://localhost:21114/api/work-order/1/closeout

# 同一工单重发：取回第一次的收尾结果（idempotentReplay=true），不重复评估
curl -X POST -H 'X-Role: LINE_SUPERVISOR' http://localhost:21114/api/work-order/1/closeout

# 质检员关闭不良；最后一条严重不良关闭后，该工单的收尾可再次提交
curl -X POST -H 'X-Role: INSPECTOR' http://localhost:21114/api/defect-record/1/close

# 查询工单详情：各批次末检结论、不良数量、阻塞原因、最近一次收尾结果
curl http://localhost:21114/api/work-order/1

# 原有清单接口照常可用
curl http://localhost:21114/api/work-order
```

收尾判定规则：

- 批次末检（`inspection_type=FINAL`）结论不是 `PASS` / `CONDITIONAL_PASS`，或没有末检记录 → 阻塞（`FINAL_INSPECTION_NOT_PASSED` / `FINAL_INSPECTION_MISSING`）。
- 批次带未关闭（`disposition_status != CLOSED`）的严重不良（`severity=CRITICAL`）→ 阻塞（`OPEN_CRITICAL_DEFECT`）。
- 任一批次阻塞 → 工单回到 `PAUSED`，响应列出 `blockedBatches`；全部通过 → 工单 `FINISHED`。
- 幂等：同一工单重发取回第一次结果；仅当最后一条未关闭严重不良被关闭后，被阻塞（BLOCKED）的收尾记录被释放，可再次提交。

种子数据演示路径：`WO-1001` 的批次 `B-1001B` 带一条未关闭 CRITICAL 不良（id=1）→ 首次提交被阻塞；关闭该不良后再次提交即完工。`WO-1003` 末检 `FAIL` 被阻塞；`WO-1002` 全部通过直接完工。

## 本地开发方式

- 后端：进入 `backend` 后执行 `mvn spring-boot:run`（或 `mvn -DskipTests package && java -jar target/quality-trace-0.1.0.jar`），接口统一挂在 `/api`。
- 当前仓储为内存实现，种子数据随应用启动加载；`database/init.sql` 保留完整表结构（含 `work_order_closeout`），供切换真实数据源使用。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── routes/         # 路径常量（按实体分文件）
├── controllers/    # WorkOrderController / DefectRecordController / ...
├── services/       # WorkOrderCloseoutService（收尾评估+幂等）/ WorkOrderService / DefectRecordService / ...
├── models/         # WorkOrder / ProductBatch / QualityInspection / DefectRecord / WorkOrderCloseout
├── repositories/   # 内存仓储 + 种子数据
├── middlewares/    # AuthMiddleware / RbacMiddleware / AuditLogMiddleware / ErrorHandlerMiddleware
├── constants/      # 枚举、错误码、日志模板
├── constructors/   # DTO 构造器
├── validators/     # 收尾提交校验 / 不良关闭校验
├── types/          # BatchCloseoutSummary / WorkOrderCloseoutPayload
├── utils/          # Formatters / ApiException
└── config/
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus: constants/WorkOrderStatus、models/WorkOrder、repositories/WorkOrderRepository（种子）、services/WorkOrderCloseoutService（PAUSED/FINISHED 流转）、validators/WorkOrderCloseoutValidator、utils/Formatters（statusText）、constructors/WorkOrderDtoFactory。
- InspectionResultStatus: constants/InspectionResultStatus、models/QualityInspection、repositories/QualityInspectionRepository（种子）、services/WorkOrderCloseoutService（末检通过口径）。
- DefectSeverity: constants/DefectSeverity、models/DefectRecord、repositories/DefectRecordRepository（种子与严重不良查询）、services/WorkOrderCloseoutService、services/DefectRecordService。
- InspectionType（FIRST/PATROL/FINAL）: constants/InspectionType、models/QualityInspection、repositories/QualityInspectionRepository（末检选取）。
- DefectDispositionStatus（OPEN/PROCESSING/CLOSED）: constants/DefectDispositionStatus、models/DefectRecord、services/DefectRecordService、validators/DefectCloseValidator。
- CloseoutOutcome（APPROVED/BLOCKED）: constants/CloseoutOutcome、models/WorkOrderCloseout、services/WorkOrderCloseoutService、repositories/WorkOrderCloseoutRepository。
- CloseoutBlockReason（FINAL_INSPECTION_MISSING/FINAL_INSPECTION_NOT_PASSED/OPEN_CRITICAL_DEFECT）: constants/CloseoutBlockReason、services/WorkOrderCloseoutService、constructors/WorkOrderCloseoutDtoFactory。
- Role（INSPECTOR/LINE_SUPERVISOR/QUALITY_MANAGER/AUDITOR）: constants/Role、middlewares/AuthMiddleware、middlewares/RbacMiddleware、controllers（写接口鉴权）。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。完工收尾进一步把工单、批次、检验、不良四个实体的仓储耦合进 `WorkOrderCloseoutService`，任何一方字段调整都会波及收尾评估、工单详情和幂等记录。

## License

MIT
