<template>
  <div v-if="tutorial" class="tp-tutorial-readonly">
    <el-form label-width="100px" size="small" class="readonly-form">
      <el-form-item v-if="showAdminMeta" label="状态">
        <el-input :value="statusText" disabled />
      </el-form-item>
      <el-form-item v-if="showAdminMeta" label="作者">
        <el-input :value="tutorial.author_name || '—'" disabled />
      </el-form-item>
      <el-form-item label="标题">
        <el-input :value="tutorial.title || ''" disabled />
      </el-form-item>
      <el-form-item label="封面图">
        <div class="preview-box">
          <img v-if="coverSrc" :src="coverSrc" alt="封面" />
          <div v-else class="placeholder">
            <i class="el-icon-picture-outline" />
            <span>未选择图片</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="摘要">
        <el-input type="textarea" :rows="3" :value="tutorial.summary || ''" disabled />
      </el-form-item>
      <el-form-item label="难度">
        <el-input :value="difficultyText" disabled />
      </el-form-item>
      <el-form-item label="时长(分钟)">
        <el-input :value="productionMinutesText" disabled />
      </el-form-item>
      <el-form-item v-if="hasMaterialSummary" label="材料摘要">
        <el-input type="textarea" :rows="2" :value="tutorial.material_summary || ''" disabled />
      </el-form-item>
      <el-form-item v-if="materialNames.length" label="关联材料">
        <div class="tag-row">
          <el-tag v-for="(name, i) in materialNames" :key="'m'+i" size="small" style="margin: 0 6px 6px 0;">{{ name }}</el-tag>
        </div>
      </el-form-item>
      <el-divider>图文步骤</el-divider>
      <div v-for="(s, idx) in steps" :key="'st'+idx" class="edit-step-box">
        <el-form-item :label="'步骤' + (idx + 1) + '标题'">
          <el-input :value="s.step_title || ''" disabled />
        </el-form-item>
        <el-form-item :label="'步骤' + (idx + 1) + '内容'">
          <el-input type="textarea" :rows="2" :value="s.step_content || ''" disabled />
        </el-form-item>
        <el-form-item :label="'步骤' + (idx + 1) + '配图'">
          <div class="preview-box step-preview">
            <img v-if="stepImgSrc(s)" :src="stepImgSrc(s)" alt="步骤配图" />
            <div v-else class="placeholder">
              <i class="el-icon-picture-outline" />
              <span>未选择图片</span>
            </div>
          </div>
        </el-form-item>
      </div>
      <p v-if="!steps.length" class="muted">暂无步骤</p>
      <el-form-item label="材料包名称">
        <el-input :value="kit ? kit.kit_name : ''" :placeholder="kit ? '' : '无关联材料包'" disabled />
      </el-form-item>
      <el-form-item label="材料包描述">
        <el-input type="textarea" :rows="4" :value="kit ? (kit.kit_desc || '') : ''" disabled />
      </el-form-item>
      <el-form-item label="材料包价格">
        <el-input :value="kitPriceText" disabled />
      </el-form-item>
      <el-form-item label="材料包库存">
        <el-input :value="kitStockText" disabled />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
/** 与前台「个人中心-我的教程-编辑」弹窗布局一致，仅只读展示 */
export default {
  name: 'TpTutorialDetailReadonly',
  props: {
    /** 接口返回：{ tutorial, steps, materials, materialKit } */
    detail: {
      type: Object,
      default: () => ({})
    },
    /** 是否显示管理端字段：状态、作者 */
    showAdminMeta: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    tutorial() {
      return (this.detail && this.detail.tutorial) || null;
    },
    steps() {
      return (this.detail && this.detail.steps) || [];
    },
    kit() {
      const k = this.detail && this.detail.materialKit;
      return k && typeof k === 'object' ? k : null;
    },
    materialNames() {
      const list = (this.detail && this.detail.materials) || [];
      return list.map(m => m.material_name || m.name || '').filter(Boolean);
    },
    hasMaterialSummary() {
      const v = this.tutorial && this.tutorial.material_summary;
      return v != null && String(v).trim() !== '';
    },
    statusText() {
      const n = Number(this.tutorial.status);
      if (n === 1) return '用户私密';
      if (n === 2) return '用户公开';
      if (n === 3) return '禁止公开';
      if (n === 5) return '已删除';
      return String(this.tutorial.status);
    },
    difficultyText() {
      const v = this.tutorial.difficulty_level;
      return v != null && v !== '' ? String(v) : '—';
    },
    productionMinutesText() {
      const v = this.tutorial.production_time_minutes;
      return v != null && v !== '' ? String(v) : '—';
    },
    kitPriceText() {
      return this.kit && this.kit.price != null ? String(this.kit.price) : '—';
    },
    kitStockText() {
      return this.kit && this.kit.stock != null ? String(this.kit.stock) : '—';
    },
    coverSrc() {
      return this.fileUrl(this.tutorial.cover_image_url);
    }
  },
  methods: {
    /** 与前台 TpImagePicker 一致：相对路径拼后端可访问地址 */
    fileUrl(path) {
      if (!path) return '';
      const p = String(path).trim();
      if (!p) return '';
      if (/^https?:\/\//i.test(p)) return p;
      const base = (process.env.VUE_APP_BASE_API || '').replace(/\/$/, '');
      const rel = p.startsWith('/') ? p : `/${p}`;
      return base ? `${base}${rel}` : `/api${rel}`;
    },
    stepImgSrc(s) {
      return this.fileUrl(s && s.step_image_url);
    }
  }
}
</script>

<style scoped>
.readonly-form >>> .el-input.is-disabled .el-input__inner,
.readonly-form >>> .el-textarea.is-disabled .el-textarea__inner {
  color: #606266;
  cursor: default;
}
.preview-box {
  width: 100%;
  max-width: 320px;
  min-height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  background: #fafafa;
}
.preview-box.step-preview {
  max-width: 280px;
}
.preview-box img {
  display: block;
  width: 100%;
  max-height: 200px;
  object-fit: contain;
}
.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  color: #909399;
  font-size: 13px;
  gap: 6px;
}
.placeholder i {
  font-size: 36px;
}
.edit-step-box {
  border: 1px dashed #e4e7ed;
  border-radius: 8px;
  padding: 8px;
  margin-bottom: 10px;
}
.muted {
  color: #909399;
  font-size: 13px;
  margin: 0 0 12px;
}
.tag-row {
  line-height: 1.6;
}
</style>
