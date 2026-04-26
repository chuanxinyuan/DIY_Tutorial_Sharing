-- ---------------------------------------------------------------------------
-- 管理后台治理：状态枚举与历史数据迁移（在现有 init1 库上执行）
-- 教程 status: 1用户私密 2用户公开 3禁止公开 5已删除（原 4下架 -> 1私密）
-- 帖子 status: 1正常 2违规 3已删除（社区列表仅展示 1）
-- 帖子评论 status: 1正常 2违规（原「删除」与违规同为 2）
-- 材料包 status: 1在售 2停售 3售完 4已删除（原 3违规下架 -> 2停售）
-- ---------------------------------------------------------------------------

-- 教程：下架(4) -> 私密(1)
UPDATE `tp_tutorial` SET `status` = 1 WHERE `status` = 4;

-- 帖子：无结构变更；保持 1正常 3用户删除；新增管理「违规」用 2（若库中无 2 则无需迁移）
-- 若曾把已删标为 3，保持不变

-- 材料包：违规下架(3) -> 停售(2)
UPDATE `tp_material_kit` SET `status` = 2 WHERE `status` = 3;

-- 库存为 0 且仍为在售的改为售完
UPDATE `tp_material_kit` SET `status` = 3 WHERE `status` = 1 AND `stock` <= 0;

ALTER TABLE `tp_tutorial` MODIFY COLUMN `status` tinyint(4) NOT NULL DEFAULT '2' COMMENT '1用户私密 2用户公开 3禁止公开 5已删除';
ALTER TABLE `tp_community_post` MODIFY COLUMN `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1正常 2违规 3已删除';
ALTER TABLE `tp_community_post_comment` MODIFY COLUMN `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1正常 2违规';
ALTER TABLE `tp_material_kit` MODIFY COLUMN `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1在售 2停售 3售完 4已删除';
