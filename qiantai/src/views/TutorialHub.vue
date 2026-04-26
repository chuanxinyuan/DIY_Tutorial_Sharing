<template>
  <div class="tp-page">
    <div class="tp-header">
      <h2>教程库</h2>
      <div class="tp-actions">
        <el-input v-model="keyword" placeholder="搜索成品/材料" style="width: 220px" @keyup.enter.native="search" />
        <el-select v-model="sort" style="width: 100px" @change="search">
          <el-option label="热门" value="hot" />
          <el-option label="最新" value="new" />
        </el-select>
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button type="success" @click="$router.push('/tutorial/publish')">发布教程</el-button>
      </div>
    </div>

    <el-card>
      <el-form inline>
        <el-form-item label="制作难度">
          <el-select v-model="difficultyLevel" clearable style="width: 120px" @change="search">
            <el-option v-for="d in meta.difficultyLevels" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="制作时间">
          <el-select v-model="timeRange" clearable style="width: 130px" @change="search">
            <el-option v-for="t in meta.timeRanges" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="制作材料">
          <el-select v-model="materialId" clearable filterable style="width: 150px" @change="search">
            <el-option v-for="m in meta.materials" :key="m.id" :label="m.material_name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签浏览">
          <el-select v-model="tagId" clearable filterable style="width: 150px" @change="search">
            <el-option v-for="t in meta.tags" :key="t.id" :label="t.tag_name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="8" v-for="item in list" :key="item.id" style="margin-bottom: 16px">
        <tutorial-case-card :item="item" :default-cover="defaultCover" @preview="openTutorialDetail" />
      </el-col>
    </el-row>

    <el-pagination
      background
      layout="prev, pager, next, total"
      :current-page="current"
      :page-size="pageSize"
      :total="total"
      @current-change="pageChange"
    />
  </div>
</template>

<script>
export default {
  name: 'TutorialHub',
  data() {
    return {
      defaultCover: '/api/files/download/31',
      keyword: '',
      sort: 'hot',
      difficultyLevel: null,
      timeRange: null,
      materialId: null,
      tagId: null,
      current: 1,
      pageSize: 9,
      total: 0,
      list: [],
      meta: {
        materials: [],
        tags: [],
        difficultyLevels: [],
        timeRanges: []
      }
    }
  },
  created() {
    this.keyword = this.$route.query.keyword || ''
    this.loadMeta()
    this.loadList()
  },
  methods: {
    pageChange(page) {
      this.current = page
      this.loadList()
    },
    search() {
      this.current = 1
      this.loadList()
    },
    loadMeta() {
      this.$axios.get('/api/front/v2/tutorial/filter-meta').then(res => {
        this.meta = res.data.data || this.meta
      })
    },
    openTutorialDetail(row) {
      this.$router.push('/tutorial/detail/' + row.id)
    },
    loadList() {
      this.$axios.post('/api/front/v2/tutorial/page', {
        current: this.current,
        pageSize: this.pageSize,
        keyword: this.keyword,
        sort: this.sort,
        difficultyLevel: this.difficultyLevel,
        timeRange: this.timeRange,
        materialId: this.materialId,
        tagId: this.tagId
      }).then(res => {
        const data = res.data.data || {}
        this.list = data.list || []
        this.total = data.total || 0
      })
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
.tp-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.tp-actions { display: flex; gap: 8px; align-items: center; }
</style>
