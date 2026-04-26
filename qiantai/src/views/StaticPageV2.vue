<template>
  <div class="tp-page">
    <el-card>
      <h2>{{ page.title }}</h2>
      <div v-html="page.content"></div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'StaticPageV2',
  data() {
    return { page: { title: '', content: '' } }
  },
  created() {
    this.loadPage()
  },
  watch: {
    '$route.params.key': 'loadPage'
  },
  methods: {
    loadPage() {
      this.$axios.get('/api/front/v2/static/' + this.$route.params.key).then(res => {
        this.page = res.data.data || { title: '', content: '' }
      })
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
</style>
