<template>
  <div class="tp-img-picker" :class="{ compact: compact, 'hide-preview': hidePreview }">
    <div v-if="!hidePreview" class="preview-box">
      <img v-if="value" :src="'/api' + value" alt="预览" />
      <div v-else class="placeholder">
        <i class="el-icon-picture-outline"></i>
        <span>{{ placeholderText }}</span>
      </div>
    </div>
    <div class="btns">
      <el-upload
        class="uploader"
        action=""
        :disabled="uploading"
        :http-request="doUpload"
        :show-file-list="false"
        accept="image/png,image/jpeg,image/jpg,image/gif"
      >
        <el-button size="small" type="primary" :loading="uploading">{{ value ? '重新选择' : '选择图片' }}</el-button>
      </el-upload>
      <el-button v-if="value && allowClear" size="small" @click="clear">清除</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TpImagePicker',
  props: {
    value: { type: String, default: '' },
    allowClear: { type: Boolean, default: true },
    /** 紧凑布局（头像旁按钮） */
    compact: { type: Boolean, default: false },
    /** 仅按钮，不显示上方预览区（与 el-avatar 联用时） */
    hidePreview: { type: Boolean, default: false },
    placeholderText: { type: String, default: '未选择图片' }
  },
  data() {
    return { uploading: false }
  },
  methods: {
    doUpload(req) {
      this.uploading = true
      const fd = new FormData()
      fd.append('file', req.file)
      this.$axios
        .post('/api/front/v2/upload/image', fd, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        .then(res => {
          if (!res.data || res.data.code !== '0') {
            this.$message.error((res.data && res.data.msg) || '上传失败')
            return
          }
          const url = res.data.data && res.data.data.url
          if (url) {
            this.$emit('input', url)
            this.$message.success('上传成功')
          }
        })
        .catch(() => {
          this.$message.error('上传失败')
        })
        .finally(() => {
          this.uploading = false
        })
    },
    clear() {
      this.$emit('input', '')
    }
  }
}
</script>

<style scoped>
.tp-img-picker {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.tp-img-picker.compact {
  flex-direction: row;
  align-items: center;
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
.tp-img-picker.compact .preview-box {
  max-width: 160px;
  min-height: 100px;
}
.tp-img-picker.hide-preview .preview-box {
  display: none;
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
.btns {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
</style>
