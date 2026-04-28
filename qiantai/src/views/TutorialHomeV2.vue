We need to apply the suggested edit to the original code. The suggested edit is essentially the entire new file content with changes: replaced el-row/el-col with div with class tutorial-masonry and masonry-item, added CSS for waterfall layout. So output the complete modified file as per suggested edit. No changes to script or other parts. Just the template changed and style added. Ensure to output exactly the suggested code, preserving existing comments? The original had no comments. The suggested edit adds comments in style section. So output as given.<template>
  <div class="tp-page">
    <el-card>
      <h2>首页推荐</h2>
      <el-tabs>
        <el-tab-pane label="热门教程">
          <div class="tutorial-masonry">
            <div class="masonry-item" v-for="item in hotList" :key="'h'+item.id">
              <tutorial-case-card :item="item" @preview="goDetail" />
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="最新教程">
          <div class="tutorial-masonry">
            <div class="masonry-item" v-for="item in newList" :key="'n'+item.id">
              <tutorial-case-card :item="item" @preview="goDetail" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card style="margin-top:12px;">
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <h3>社区板块（热门）</h3>
        <el-button type="text" @click="$router.push('/tutorial/community')">查看更多</el-button>
      </div>
      <el-table :data="communityList" size="mini">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="author_name" label="作者" width="120" />
        <el-table-column prop="comment_count" label="评论" width="80" />
        <el-table-column prop="preview" label="预览" min-width="220" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TutorialHomeV2',
  data() {
    return { hotList: [], newList: [], communityList: [] }
  },
  created() {
    this.$axios.get('/api/front/v2/home', { params: { sort: 'hot' } }).then(res => { this.hotList = res.data.data || [] })
    this.$axios.get('/api/front/v2/home', { params: { sort: 'new' } }).then(res => { this.newList = res.data.data || [] })
    this.$axios.get('/api/front/v2/community/page', { params: { sort: 'hot', pageSize: 5 } }).then(res => {
      this.communityList = (res.data.data || {}).list || []
    })
  },
  methods: {
    goDetail(item) {
      this.$router.push('/tutorial/detail/' + item.id)
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
/* 瀑布流布局：取消等高，自然排列 */
.tutorial-masonry {
  column-count: 3;
  column-gap: 16px;
}
.masonry-item {
  break-inside: avoid;
  margin-bottom: 16px;
}
/* 响应式 */
@media (max-width: 992px) {
  .tutorial-masonry { column-count: 2; }
}
@media (max-width: 576px) {
  .tutorial-masonry { column-count: 1; }
}
</style>

