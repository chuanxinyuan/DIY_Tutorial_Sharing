<template>
  <div v-if="kit" class="tp-kit-readonly">
    <el-form label-width="100px" size="small" class="readonly-form">
      <el-form-item label="材料包名称">
        <el-input :value="kit.kit_name || ''" disabled />
      </el-form-item>
      <el-form-item label="材料包描述">
        <el-input type="textarea" :rows="4" :value="kit.kit_desc || ''" disabled />
      </el-form-item>
      <el-form-item label="材料包价格">
        <el-input :value="priceText" disabled />
      </el-form-item>
      <el-form-item label="材料包库存">
        <el-input :value="stockText" disabled />
      </el-form-item>
      <el-form-item v-if="extraSales" label="销量">
        <el-input :value="salesText" disabled />
      </el-form-item>
      <el-form-item v-if="extraStatusLabel" label="状态">
        <el-input :value="extraStatusLabel" disabled />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
/** 与前台「编辑我的教程」中材料包区块布局一致，仅只读；管理端可附加销量、状态 */
export default {
  name: 'TpMaterialKitReadonly',
  props: {
    kit: {
      type: Object,
      default: null
    },
    /** 管理端：销量 */
    salesCount: {
      type: [Number, String],
      default: null
    },
    /** 管理端：状态文案 */
    statusLabel: {
      type: String,
      default: ''
    }
  },
  computed: {
    priceText() {
      return this.kit && this.kit.price != null ? String(this.kit.price) : '—';
    },
    stockText() {
      return this.kit && this.kit.stock != null ? String(this.kit.stock) : '—';
    },
    extraSales() {
      return this.salesCount != null && this.salesCount !== '';
    },
    salesText() {
      return String(this.salesCount);
    },
    extraStatusLabel() {
      return (this.statusLabel || '').trim();
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
</style>
