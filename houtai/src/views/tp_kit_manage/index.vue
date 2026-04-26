<template>
  <div>
    <el-card>
      <div class="toolbar">
        <h3>旧物改造教程分享平台-材料包管理</h3>
        <el-button type="primary" size="small" @click="loadData">刷新</el-button>
      </div>

      <el-table
        :data="listData"
        border
        style="margin-top: 12px;"
        highlight-current-row
        @row-click="onKitRowClick"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="kit_name" label="材料包名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="关联教程" min-width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click.stop="openTutorialFromKit(scope.row)">{{ scope.row.tutorial_title || '—' }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="seller_name" label="卖家" min-width="120" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales_count" label="销量" width="80" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">{{ kitStatusLabel(scope.row.status) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" plain :disabled="Number(scope.row.status) === 4" @click.stop="setKitStatus(scope.row, 1)">在售</el-button>
            <el-button size="mini" type="warning" plain :disabled="Number(scope.row.status) === 4" @click.stop="setKitStatus(scope.row, 2)">停售</el-button>
            <el-button size="mini" type="info" plain :disabled="Number(scope.row.status) === 4" @click.stop="setKitStatus(scope.row, 3)">售完</el-button>
            <el-button size="mini" type="danger" plain :disabled="Number(scope.row.status) === 4" @click.stop="removeKit(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <p class="hint">提示：点击表格行可查看材料包描述内容；点击「关联教程」标题可查看教程详情。</p>
    </el-card>

    <el-dialog
      title="材料包内容（只读）"
      :visible.sync="kitDialogVisible"
      width="560px"
      custom-class="tp-kit-ro-dialog"
      append-to-body
      @closed="kitView = null"
    >
      <tp-material-kit-readonly
        v-if="kitView"
        :kit="kitView"
        :sales-count="kitView.sales_count"
        :status-label="kitView ? kitStatusLabel(kitView.status) : ''"
      />
    </el-dialog>

    <el-dialog
      title="关联教程详情（只读）"
      :visible.sync="tutorialDialogVisible"
      width="60%"
      top="5vh"
      custom-class="tp-tutorial-ro-dialog"
      append-to-body
      @closed="tutorialDetail = null"
    >
      <tp-tutorial-detail-readonly v-if="tutorialDetail" :detail="tutorialDetail" :show-admin-meta="true" />
    </el-dialog>
  </div>
</template>

<script>
import { tpAdminKits, tpAdminKitSetStatus, tpAdminDeleteKit, tpAdminTutorialDetail } from '@/api/tpadmin/tpAdminApi';
import { Session } from '@/utils/storage';
import TpTutorialDetailReadonly from '@/components/TpTutorialDetailReadonly.vue';
import TpMaterialKitReadonly from '@/components/TpMaterialKitReadonly.vue';

export default {
  name: 'tp_kit_manage',
  components: { TpTutorialDetailReadonly, TpMaterialKitReadonly },
  data() {
    return {
      listData: [],
      kitDialogVisible: false,
      kitView: null,
      tutorialDialogVisible: false,
      tutorialDetail: null
    }
  },
  created() {
    this.loadData();
  },
  methods: {
    adminUserId() {
      const user = Session.get('userInfo');
      return user ? user.id : null;
    },
    kitStatusLabel(s) {
      const n = Number(s);
      if (n === 1) return '在售';
      if (n === 2) return '停售';
      if (n === 3) return '售完';
      if (n === 4) return '已删除';
      return String(s);
    },
    loadData() {
      tpAdminKits(this.adminUserId()).then(res => {
        this.listData = res.data || [];
      });
    },
    onKitRowClick(row) {
      this.kitView = { ...row };
      this.kitDialogVisible = true;
    },
    openTutorialFromKit(row) {
      const tid = row.tutorial_id;
      if (!tid) {
        this.$message.warning('无关联教程');
        return;
      }
      tpAdminTutorialDetail(this.adminUserId(), tid).then(res => {
        this.tutorialDetail = res.data || {};
        this.tutorialDialogVisible = true;
      });
    },
    setKitStatus(row, status) {
      tpAdminKitSetStatus({ adminUserId: this.adminUserId(), kitId: row.id, status }).then(() => {
        this.$message.success('状态已更新');
        this.loadData();
      });
    },
    removeKit(row) {
      this.$confirm('删除后材料包标记为已删除，确定？', '提示', { type: 'warning' }).then(() => {
        return tpAdminDeleteKit({ adminUserId: this.adminUserId(), kitId: row.id });
      }).then(() => {
        this.$message.success('已删除');
        this.loadData();
        this.kitDialogVisible = false;
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.hint {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
<style>
.tp-tutorial-ro-dialog .el-dialog__body {
  max-height: 78vh;
  overflow-y: auto;
  padding-top: 10px;
}
.tp-kit-ro-dialog .el-dialog__body {
  max-height: 70vh;
  overflow-y: auto;
  padding-top: 10px;
}
</style>
