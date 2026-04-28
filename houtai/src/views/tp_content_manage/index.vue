<template>
  <div>
    <el-card>
      <div class="toolbar">
        <h3>旧物改造教程分享平台-内容管控</h3>
        <el-button type="primary" size="small" @click="loadData">刷新</el-button>
      </div>

      <el-tabs v-model="activeName" style="margin-top: 12px;">
        <el-tab-pane label="教程管理" name="tutorial">
          <div class="toolbar-right" style="margin-bottom:12px">
            <el-input
              v-model="tutoKeyword"
              placeholder="搜索 ID / 标题 / 作者"
              clearable size="small" style="width:260px;margin-right:8px"
              @keyup.enter.native="loadTutorials"
            />
            <el-select v-model="tutoStatus" placeholder="状态" size="small" style="width:100px;margin-right:8px" clearable>
              <!-- <el-option label="私密" :value="1" /> -->
              <el-option label="公开" :value="2" />
              <el-option label="禁止公开" :value="3" />
            </el-select>
            <el-button size="small" type="primary" @click="loadTutorials">查询</el-button>
            <el-button size="small" @click="tutoKeyword='';tutoStatus=null;loadTutorials()">重置</el-button>
          </div>
          <el-table :data="tutorials" border style="margin-top:10px">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="教程标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="author_name" label="作者" min-width="120" />
            <el-table-column label="状态" width="120">
              <template slot-scope="scope">{{ tutorialStatusLabel(scope.row.status) }}</template>
            </el-table-column>
            <el-table-column prop="hot_score" label="热度" width="90" />
            <el-table-column label="操作" width="280" fixed="right">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" plain @click="openTutorialDetail(scope.row)">查看详情</el-button>
                <el-button
                  v-if="Number(scope.row.status) === 2"
                  size="mini"
                  type="warning"
                  @click="toggleBanTutorial(scope.row, 3)"
                >禁止公开</el-button>
                <el-button
                  v-if="Number(scope.row.status) === 3"
                  size="mini"
                  type="success"
                  @click="toggleBanTutorial(scope.row, 2)"
                >恢复公开</el-button>
                <el-button size="mini" type="danger" @click="removeTutorial(scope.row)">强制删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="教程评论管理" name="tutorialComment">
          <div class="toolbar-right" style="margin-bottom:12px">
            <el-input
              v-model="tcKeyword"
              placeholder="搜索 教程标题 / 评论内容 / 评论用户"
              clearable size="small" style="width:300px;margin-right:8px"
              @keyup.enter.native="loadTutorialComments"
            />
            <el-select v-model="tcStatus" placeholder="状态" size="small" style="width:100px;margin-right:8px" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="违规" :value="2" />
            </el-select>
            <el-button size="small" type="primary" @click="loadTutorialComments">查询</el-button>
            <el-button size="small" @click="tcKeyword='';tcStatus=null;loadTutorialComments()">重置</el-button>
          </div>
          <el-table :data="tutorialComments" border style="margin-top: 10px;">
            <el-table-column prop="id" label="评论ID" width="90" />
            <el-table-column prop="tutorial_id" label="教程ID" width="90" />
            <el-table-column prop="tutorial_title" label="所属教程" min-width="180" show-overflow-tooltip />
            <el-table-column prop="user_name" label="评论用户" min-width="120" />
            <el-table-column prop="content" label="评论内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">{{ commentStatusLabel(scope.row.status) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="260" fixed="right">
              <template slot-scope="scope">
                <el-button
                    v-if="Number(scope.row.status) === 1"
                    size="mini"
                    type="warning"
                    @click="setTutorialCommentStatus(scope.row, 2)"
                >标记违规</el-button>
                <el-button
                    v-if="Number(scope.row.status) === 2"
                    size="mini"
                    type="success"
                    @click="setTutorialCommentStatus(scope.row, 1)"
                >恢复正常</el-button>
                <el-button
                    size="mini"
                    type="danger"
                    plain
                    @click="removeTutorialComment(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="帖子管理" name="post">
          <div class="toolbar-right" style="margin-bottom:12px">
            <el-input
              v-model="postKeyword"
              placeholder="搜索 ID / 标题 / 作者"
              clearable size="small" style="width:260px;margin-right:8px"
              @keyup.enter.native="loadPosts"
            />
            <el-select v-model="postStatus" placeholder="状态" size="small" style="width:100px;margin-right:8px" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="违规" :value="2" />
            </el-select>
            <el-button size="small" type="primary" @click="loadPosts">查询</el-button>
            <el-button size="small" @click="postKeyword='';postStatus=null;loadPosts()">重置</el-button>
          </div>
          <el-table :data="posts" border style="margin-top:10px">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="帖子标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="author_name" label="作者" min-width="120" />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">{{ postStatusLabel(scope.row.status) }}</template>
            </el-table-column>
            <el-table-column prop="comment_count" label="评论数" width="90" />
            <el-table-column label="操作" width="260" fixed="right">
              <template slot-scope="scope">
                <el-button
                  v-if="Number(scope.row.status) === 1"
                  size="mini"
                  type="warning"
                  @click="setPostStatus(scope.row, 2)"
                >标记违规</el-button>
                <el-button
                  v-if="Number(scope.row.status) === 2"
                  size="mini"
                  type="success"
                  @click="setPostStatus(scope.row, 1)"
                >恢复正常</el-button>
                <el-button
                  v-if="Number(scope.row.status) !== 3"
                  size="mini"
                  type="danger"
                  plain
                  @click="setPostStatus(scope.row, 3)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="评论管理" name="comment">
          <div class="toolbar-right" style="margin-bottom:12px">
            <el-input
              v-model="pcKeyword"
              placeholder="搜索 帖子标题 / 评论内容 / 评论用户"
              clearable size="small" style="width:300px;margin-right:8px"
              @keyup.enter.native="loadComments"
            />
            <el-select v-model="pcStatus" placeholder="状态" size="small" style="width:100px;margin-right:8px" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="违规" :value="2" />
            </el-select>
            <el-button size="small" type="primary" @click="loadComments">查询</el-button>
            <el-button size="small" @click="pcKeyword='';pcStatus=null;loadComments()">重置</el-button>
          </div>
          <el-table :data="comments" border style="margin-top: 10px;">
            <el-table-column prop="id" label="评论ID" width="90" />
            <el-table-column prop="post_id" label="帖子ID" width="90" />
            <el-table-column prop="post_title" label="所属帖子" min-width="180" show-overflow-tooltip />
            <el-table-column prop="user_name" label="评论用户" min-width="120" />
            <el-table-column prop="content" label="评论内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">{{ commentStatusLabel(scope.row.status) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <el-button
                  v-if="Number(scope.row.status) === 1"
                  size="mini"
                  type="warning"
                  @click="setCommentStatus(scope.row, 2)"
                >标记违规</el-button>
                <el-button
                  v-if="Number(scope.row.status) === 2"
                  size="mini"
                  type="success"
                  @click="setCommentStatus(scope.row, 1)"
                >恢复正常</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  plain
                  @click="removePostComment(scope.row)"
                >删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>


      </el-tabs>
    </el-card>

    <el-dialog
      title="教程详情（只读）"
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
import {
  tpAdminTutorials,
  tpAdminTutorialDetail,
  tpAdminRemoveTutorial,
  tpAdminTutorialBanPublic,
  tpAdminPosts,
  tpAdminPostSetStatus,
  tpAdminPostComments,
  tpAdminPostCommentSetStatus,
  tpAdminTutorialComments,
  tpAdminTutorialCommentSetStatus,
  tpAdminRemoveTutorialComment,
  tpAdminRemovePostComment
} from '@/api/tpadmin/tpAdminApi';
import { Session } from '@/utils/storage';
import TpTutorialDetailReadonly from '@/components/TpTutorialDetailReadonly.vue';

export default {
  name: 'tp_content_manage',
  components: { TpTutorialDetailReadonly },
  data() {
    return {
      activeName: 'tutorial',
      tutoKeyword: '',
      tutoStatus: null,
      postKeyword: '',
      postStatus: null,
      tcKeyword: '',
      tcStatus: null,
      pcKeyword: '',
      pcStatus: null,
      tutorials: [],
      posts: [],
      comments: [],
      tutorialComments: [],
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
    tutorialStatusLabel(s) {
      const n = Number(s);
      if (n === 1) return '用户私密';
      if (n === 2) return '用户公开';
      if (n === 3) return '禁止公开';
      if (n === 5) return '已删除';
      return String(s);
    },
    postStatusLabel(s) {
      const n = Number(s);
      if (n === 1) return '正常';
      if (n === 2) return '违规';
      if (n === 3) return '已删除';
      return String(s);
    },
    commentStatusLabel(s) {
      const n = Number(s);
      if (n === 1) return '正常';
      if (n === 2) return '违规';
      return String(s);
    },
    loadData() {
      this.loadTutorials();
      this.loadPosts();
      this.loadComments();
      this.loadTutorialComments();
    },
    loadTutorials() {
      const uid = this.adminUserId();
      if (!uid) return;
      tpAdminTutorials(uid, {
        keyword: this.tutoKeyword || undefined,
        status: this.tutoStatus !== null && this.tutoStatus !== '' ? this.tutoStatus : undefined
      }).then(res => {
        this.tutorials = res.data || [];
      }).catch(() => {
        this.tutorials = [];
      });
    },
    loadPosts() {
      const uid = this.adminUserId();
      if (!uid) return;
      tpAdminPosts(uid, {
        keyword: this.postKeyword || undefined,
        status: this.postStatus !== null && this.postStatus !== '' ? this.postStatus : undefined
      }).then(res => {
        this.posts = res.data || [];
      }).catch(() => {
        this.posts = [];
      });
    },
    loadComments() {
      const uid = this.adminUserId();
      if (!uid) return;
      tpAdminPostComments(uid, {
        keyword: this.pcKeyword || undefined,
        status: this.pcStatus !== null && this.pcStatus !== '' ? this.pcStatus : undefined
      }).then(res => {
        this.comments = res.data || [];
      }).catch(() => {
        this.comments = [];
      });
    },
    loadTutorialComments() {
      const uid = this.adminUserId();
      if (!uid) return;
      tpAdminTutorialComments(uid, {
        keyword: this.tcKeyword || undefined,
        status: this.tcStatus !== null && this.tcStatus !== '' ? this.tcStatus : undefined
      }).then(res => {
        this.tutorialComments = res.data || [];
      }).catch(() => {
        this.tutorialComments = [];
      });
    },
    removePostComment(row) {
      this.$confirm('确定删除该帖子评论？删除后不可恢复。', '提示', { type: 'warning' }).then(() => {
        return tpAdminRemovePostComment({ adminUserId: this.adminUserId(), commentId: row.id });
      }).then(() => {
        this.$message.success('评论已删除');
        this.loadComments();
      }).catch(() => {});
    },
    setTutorialCommentStatus(row, status) {
      tpAdminTutorialCommentSetStatus({ adminUserId: this.adminUserId(), commentId: row.id, status }).then(() => {
        this.$message.success('已更新');
        this.loadTutorialComments();
      }).catch(() => {
        this.$message.error('操作失败');
      });
    },
    removeTutorialComment(row) {
      this.$confirm('确定删除该教程评论？删除后不可恢复。', '提示', { type: 'warning' }).then(() => {
        return tpAdminRemoveTutorialComment({ adminUserId: this.adminUserId(), commentId: row.id });
      }).then(() => {
        this.$message.success('评论已删除');
        this.loadTutorialComments();
      }).catch(() => {});
    },
    openTutorialDetail(row) {
      tpAdminTutorialDetail(this.adminUserId(), row.id).then(res => {
        this.tutorialDetail = res.data || {};
        this.tutorialDialogVisible = true;
      }).catch(() => {
        this.$message.error('获取教程详情失败');
      });
    },
    toggleBanTutorial(row, targetStatus) {
      const tip = targetStatus === 3 ? '确定禁止该教程公开？前台将不再展示。' : '确定恢复该教程公开？前台将重新展示。';
      this.$confirm(tip, '提示', { type: 'warning' }).then(() => {
        return tpAdminTutorialBanPublic({ adminUserId: this.adminUserId(), tutorialId: row.id, targetStatus });
      }).then(() => {
        this.$message.success(targetStatus === 3 ? '已禁止公开' : '已恢复公开');
        this.loadData();
        if (this.tutorialDialogVisible) this.tutorialDialogVisible = false;
      }).catch(() => {});
    },
    removeTutorial(row) {
      this.$confirm('强制删除后不可恢复，确定？', '危险操作', { type: 'warning' }).then(() => {
        return tpAdminRemoveTutorial({ adminUserId: this.adminUserId(), tutorialId: row.id });
      }).then(() => {
        this.$message.success('教程已强制删除');
        this.loadData();
        this.tutorialDialogVisible = false;
      }).catch(() => {});
    },
    setPostStatus(row, status) {
      const tip = status === 3 ? '确定删除该帖子？' : '确定更新帖子状态？';
      this.$confirm(tip, '提示', { type: 'warning' }).then(() => {
        return tpAdminPostSetStatus({ adminUserId: this.adminUserId(), postId: row.id, status });
      }).then(() => {
        this.$message.success('已更新');
        this.loadData();
      }).catch(() => {});
    },
    setCommentStatus(row, status) {
      tpAdminPostCommentSetStatus({ adminUserId: this.adminUserId(), commentId: row.id, status }).then(() => {
        this.$message.success('已更新');
        this.loadComments();
        this.loadData();
      }).catch(() => {
        this.$message.error('操作失败');
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
}
.comment-filter {
  margin-bottom: 4px;
}
</style>
<style>
/* 弹窗挂载到 body，需全局样式控制正文滚动 */
.tp-tutorial-ro-dialog .el-dialog__body {
  max-height: 78vh;
  overflow-y: auto;
  padding-top: 10px;
}
</style>
