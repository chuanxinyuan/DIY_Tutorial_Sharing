<template>
  <div>
    <el-card>
      <div class="toolbar">
        <h3>旧物改造教程分享平台-用户管理</h3>
        <div class="toolbar-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索 ID / 账号 / 昵称 / 手机号"
            clearable
            size="small"
            style="width: 260px; margin-right: 8px;"
            @keyup.enter.native="loadData"
          />
          <el-button type="primary" size="small" @click="loadData">查询</el-button>
          <el-button size="small" @click="clearSearch">重置</el-button>
        </div>
      </div>

      <el-table :data="listData" border style="margin-top: 12px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="账号" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="role_type" label="角色" width="100">
          <template slot-scope="scope">{{ scope.row.role_type === 9 ? '管理员' : '普通用户' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">{{ scope.row.status === 1 ? '启用' : '禁用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.role_type !== 9"
              size="mini"
              :type="Number(scope.row.status) === 1 ? 'warning' : 'success'"
              @click="toggleStatus(scope.row)"
            >{{ Number(scope.row.status) === 1 ? '禁用' : '启用' }}</el-button>
            <el-button size="mini" type="danger" plain @click="reset(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { tpAdminUsers, tpAdminToggleUser, tpAdminResetPassword } from '@/api/tpadmin/tpAdminApi';
import { Session } from '@/utils/storage';

export default {
  name: 'tp_user_manage',
  data() {
    return {
      listData: [],
      searchKeyword: ''
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
    loadData() {
      const kw = (this.searchKeyword || '').trim();
      tpAdminUsers(this.adminUserId(), kw || undefined).then(res => {
        this.listData = res.data || [];
      });
    },
    clearSearch() {
      this.searchKeyword = '';
      this.loadData();
    },
    toggleStatus(row) {
      if (row.role_type === 9) return;
      const next = Number(row.status) === 1 ? 0 : 1;
      tpAdminToggleUser({ adminUserId: this.adminUserId(), userId: row.id, status: next }).then(() => {
        this.$message.success('操作成功');
        this.loadData();
      });
    },
    reset(row) {
      tpAdminResetPassword({ adminUserId: this.adminUserId(), userId: row.id, newPassword: '123456' }).then(() => {
        this.$message.success('密码已重置为 123456');
      });
    }
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
</style>
