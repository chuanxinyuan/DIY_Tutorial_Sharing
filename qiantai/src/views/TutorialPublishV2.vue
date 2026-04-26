<template>
  <div class="tp-page">
    <el-card>
      <h2>分步上传教程</h2>
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="封面图">
          <tp-image-picker v-model="form.coverImageUrl" />
        </el-form-item>
        <el-form-item label="摘要"><el-input type="textarea" v-model="form.summary" :rows="2" /></el-form-item>
        <el-form-item label="制作难度">
          <el-select v-model="form.difficultyLevel">
            <el-option label="入门" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="进阶" :value="3" />
            <el-option label="困难" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="制作时间(分钟)"><el-input type="number" v-model.number="form.productionTimeMinutes" /></el-form-item>
        <el-form-item label="材料摘要"><el-input v-model="form.materialSummary" placeholder="如：旧牛仔裤,针线,剪刀" /></el-form-item>
        <el-form-item label="制作材料">
          <el-select v-model="form.materialIds" multiple filterable style="width: 100%">
            <el-option v-for="m in meta.materials" :key="m.id" :label="m.material_name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple filterable style="width: 100%">
            <el-option v-for="t in meta.tags" :key="t.id" :label="t.tag_name" :value="t.id" />
          </el-select>
        </el-form-item>

        <el-divider>图文步骤</el-divider>
        <div v-for="(s,idx) in form.steps" :key="idx" class="step-box">
          <el-form-item :label="'步骤' + (idx + 1) + '标题'"><el-input v-model="s.stepTitle" /></el-form-item>
          <el-form-item :label="'步骤' + (idx + 1) + '内容'"><el-input type="textarea" :rows="3" v-model="s.stepContent" /></el-form-item>
          <el-form-item :label="'步骤' + (idx + 1) + '配图'">
            <tp-image-picker v-model="s.stepImageUrl" />
          </el-form-item>
        </div>
        <el-button @click="addStep">新增步骤</el-button>

        <el-divider>材料包设置</el-divider>
        <el-form-item>
          <el-switch v-model="form.bindMaterialKit" active-text="绑定材料包售卖" inactive-text="仅发布教程" />
        </el-form-item>
        <template v-if="form.bindMaterialKit">
          <el-form-item label="材料包名称"><el-input v-model="form.kitName" /></el-form-item>
          <el-form-item label="价格"><el-input type="number" v-model.number="form.kitPrice" /></el-form-item>
          <el-form-item label="库存"><el-input type="number" v-model.number="form.kitStock" /></el-form-item>
          <el-form-item label="描述"><el-input type="textarea" v-model="form.kitDesc" /></el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="submit">提交发布</el-button>
          <el-button @click="$router.push('/tutorial/user-center')">进入我的教程管理</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TutorialPublishV2',
  data() {
    return {
      meta: { materials: [], tags: [] },
      form: {
        title: '', coverImageUrl: '', summary: '', difficultyLevel: 2, productionTimeMinutes: null,
        materialSummary: '', materialIds: [], tagIds: [],
        steps: [{ stepTitle: '', stepContent: '', stepImageUrl: '' }],
        bindMaterialKit: false, kitName: '', kitPrice: null, kitStock: 0, kitDesc: ''
      }
    }
  },
  created() {
    this.$axios.get('/api/front/v2/tutorial/filter-meta').then(res => {
      this.meta = res.data.data || this.meta
    })
  },
  methods: {
    addStep() {
      this.form.steps.push({ stepTitle: '', stepContent: '', stepImageUrl: '' })
    },
    submit() {
      const user = JSON.parse(localStorage.getItem('tp_user') || 'null')
      if (!user) {
        this.$message.warning('请先登录后再发布教程')
        this.$router.push({ path: '/login', query: { redirect: '/tutorial/publish' } })
        return
      }
      if (!this.form.title) return this.$message.error('标题不能为空')
      if (!this.form.steps.some(s => s.stepContent && s.stepContent.trim())) return this.$message.error('至少填写一个步骤内容')
      this.$axios.post('/api/front/v2/tutorial/publish', { userId: user.id, ...this.form }).then(() => {
        this.$message.success('发布成功')
        this.$router.push('/tutorial/hub')
      })
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
.step-box { border: 1px dashed #ddd; margin-bottom: 12px; padding: 8px; border-radius: 8px; }
</style>
