-- ---------------------------------------------------------------------------
-- 用途：修复「业务字段仍写 /files/download/59，但 file 表无 id=59」导致的下载 404
-- 执行前请备份数据库；在 init1 库或当前业务库上执行均可。
-- ---------------------------------------------------------------------------

-- 1) 在 file 表插入 id=59：与 id=31 使用相同 fileName（与 init1 种子数据一致），共用同一物理文件
--    若你本地 id=31 的 fileName 不同，请先 SELECT id,fileName FROM file WHERE id IN (31,59); 再手工改下面 VALUES
INSERT INTO `file` (`id`, `originName`, `fileName`)
VALUES (59, 'placeholder_sync_31.jpeg', 'bdfb55b4e9fdd09e20e820388cd376211708659652060.jpeg')
ON DUPLICATE KEY UPDATE
  `originName` = VALUES(`originName`),
  `fileName` = VALUES(`fileName`);

-- 2) 可选：把各表中仍指向 59 的 URL 统一改成 31（若更希望减少重复 file 行，可取消注释执行）
-- UPDATE `tp_tutorial` SET `cover_image_url` = REPLACE(`cover_image_url`, '/files/download/59', '/files/download/31') WHERE `cover_image_url` LIKE '%/files/download/59%';
-- UPDATE `tp_tutorial_step` SET `step_image_url` = REPLACE(`step_image_url`, '/files/download/59', '/files/download/31') WHERE `step_image_url` LIKE '%/files/download/59%';
-- UPDATE `tp_material_kit_order` SET `cover_image_snapshot` = REPLACE(`cover_image_snapshot`, '/files/download/59', '/files/download/31') WHERE `cover_image_snapshot` LIKE '%/files/download/59%';
-- UPDATE `tp_user` SET `avatar_url` = REPLACE(`avatar_url`, '/files/download/59', '/files/download/31') WHERE `avatar_url` LIKE '%/files/download/59%';
