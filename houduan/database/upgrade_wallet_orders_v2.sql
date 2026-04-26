-- 钱包、订单状态扩展（在已有库上执行一次；若列已存在会报错，可忽略对应语句）
-- 订单状态语义：1待发货 2运输中 3待取货 4已取货 5已取消

ALTER TABLE tp_user ADD COLUMN wallet_balance DECIMAL(12,2) NOT NULL DEFAULT 1000.00 COMMENT '钱包余额(模拟)' AFTER phone;
ALTER TABLE tp_user ADD COLUMN total_earnings DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计收益(卖家)' AFTER wallet_balance;

ALTER TABLE tp_material_kit_order ADD COLUMN kit_desc_snapshot VARCHAR(500) DEFAULT NULL COMMENT '材料包描述快照' AFTER kit_name_snapshot;
ALTER TABLE tp_material_kit_order ADD COLUMN cover_image_snapshot VARCHAR(255) DEFAULT NULL COMMENT '教程封面快照' AFTER kit_desc_snapshot;

-- 旧状态 1待发货,2已发货,3已完成,4已取消 -> 新状态
UPDATE tp_material_kit_order SET order_status = CASE order_status
  WHEN 1 THEN 1
  WHEN 2 THEN 2
  WHEN 3 THEN 4
  WHEN 4 THEN 5
  ELSE order_status END;

ALTER TABLE tp_material_kit_order MODIFY COLUMN order_status TINYINT(4) NOT NULL DEFAULT 1
  COMMENT '1待发货2运输中3待取货4已取货5已取消';
