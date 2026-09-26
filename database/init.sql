CREATE TABLE IF NOT EXISTS work_order (
  id INTEGER PRIMARY KEY,
  order_no TEXT,
  product_code TEXT,
  product_name TEXT,
  planned_qty TEXT,
  line_code TEXT,
  start_at TEXT,
  status TEXT
);

CREATE TABLE IF NOT EXISTS product_batch (
  id INTEGER PRIMARY KEY,
  batch_no TEXT,
  work_order_id TEXT,
  quantity TEXT,
  material_lot_no TEXT,
  produced_at TEXT,
  batch_status TEXT
);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  inspector_id TEXT,
  inspection_type TEXT,
  standard_version TEXT,
  result_status TEXT,
  inspected_at TEXT
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id INTEGER PRIMARY KEY,
  inspection_id TEXT,
  item_code TEXT,
  item_name TEXT,
  measured_value TEXT,
  limit_min TEXT,
  limit_max TEXT,
  item_status TEXT
);

CREATE TABLE IF NOT EXISTS defect_record (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  defect_type TEXT,
  defect_qty TEXT,
  severity TEXT,
  root_cause TEXT,
  disposition_status TEXT
);

-- 完工收尾记录：每个工单一行，重发时取回第一次结果
CREATE TABLE IF NOT EXISTS work_order_completion (
  id INTEGER PRIMARY KEY,
  work_order_id TEXT,
  result TEXT,
  submitted_by TEXT,
  submitted_at TEXT,
  snapshot TEXT
);

CREATE TABLE IF NOT EXISTS audit_log (
  id INTEGER PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  created_at TEXT
);

-- 种子数据：与后端内存仓储保持一致
INSERT INTO work_order (id, order_no, product_code, product_name, planned_qty, line_code, start_at, status) VALUES
  (1, 'WO-1001', 'P-001', '轴套', '1000', 'L1', '2026-09-18', 'RUNNING'),
  (2, 'WO-1002', 'P-002', '齿轮', '500', 'L2', '2026-09-19', 'RUNNING'),
  (3, 'WO-1003', 'P-003', '法兰', '800', 'L1', '2026-09-20', 'RUNNING');

INSERT INTO product_batch (id, batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status) VALUES
  (101, 'B-1001-A', '1', '500', 'M-7701', '2026-09-18T10:00:00Z', 'PRODUCED'),
  (102, 'B-1001-B', '1', '500', 'M-7702', '2026-09-18T15:00:00Z', 'PRODUCED'),
  (201, 'B-1002-A', '2', '250', 'M-8801', '2026-09-19T09:00:00Z', 'PRODUCED'),
  (202, 'B-1002-B', '2', '250', 'M-8802', '2026-09-19T14:00:00Z', 'PRODUCED'),
  (301, 'B-1003-A', '3', '800', 'M-9901', '2026-09-20T08:00:00Z', 'PRODUCED');

INSERT INTO quality_inspection (id, batch_id, inspector_id, inspection_type, standard_version, result_status, inspected_at) VALUES
  (1001, '101', 'inspector1', 'FIRST', 'SIP-1.2', 'PASS', '2026-09-18T10:30:00Z'),
  (1002, '101', 'inspector1', 'FINAL', 'SIP-1.2', 'PASS', '2026-09-18T18:00:00Z'),
  (1003, '102', 'inspector1', 'FINAL', 'SIP-1.2', 'PASS', '2026-09-18T19:00:00Z'),
  (1004, '201', 'inspector1', 'PATROL', 'SIP-2.0', 'PASS', '2026-09-19T11:00:00Z'),
  (1005, '201', 'inspector1', 'FINAL', 'SIP-2.0', 'PASS', '2026-09-19T17:00:00Z'),
  (1006, '202', 'inspector1', 'FINAL', 'SIP-2.0', 'PASS', '2026-09-19T18:00:00Z'),
  (1007, '301', 'inspector1', 'FINAL', 'SIP-3.1', 'PASS', '2026-09-20T10:00:00Z'),
  (1008, '301', 'inspector1', 'FINAL', 'SIP-3.1', 'FAIL', '2026-09-20T12:00:00Z');

INSERT INTO defect_record (id, batch_id, defect_type, defect_qty, severity, root_cause, disposition_status) VALUES
  (301, '201', 'CRACK', '5', 'CRITICAL', '热处理温度偏高', 'OPEN'),
  (302, '201', 'SCRATCH', '12', 'MINOR', '搬运磕碰', 'OPEN'),
  (303, '102', 'DIMENSION', '2', 'MINOR', '刀具磨损', 'CLOSED'),
  (304, '202', 'BURR', '8', 'MAJOR', '模具老化', 'OPEN');
