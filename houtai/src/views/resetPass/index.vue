<template>
	<div>
			<el-form label-width="120px" :model="editForm" :rules="rules" ref="editForm">
				<el-form-item label="原密码" prop="password">
					<el-input size="small" v-model="editForm.password" show-password auto-complete="off" placeholder="请输入原密码"></el-input>
				</el-form-item>
				<el-form-item label="新密码" prop="newPassword">
					<el-input size="small" v-model="editForm.newPassword" show-password auto-complete="off" placeholder="请输入新密码"></el-input>
				</el-form-item>
				<el-form-item label="新密码重复" prop="newPassword2">
					<el-input size="small" v-model="editForm.newPassword2" show-password auto-complete="off" placeholder="请重复新密码"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editForm')">修改</el-button>
			</div>
	</div>
</template>

<script>
	import Pagination from "@/layout/pagination/Pagination";
	import {Session} from "@/utils/storage";
	import {yonghuxinxiUpdatePassword} from "@/api/yonghuxinxi/yonghuxinxiApi";	
	import {guanliyuanUpdatePassword} from "@/api/guanliyuan/guanliyuanApi";
	export default {
		name: 'user',
		data() {
			return {
				loading: false, //是显示加载
				title: '',
				editFormVisible: false, //控制编辑页面显示与隐藏
				editForm: {
				},
				user:[],
				// rules表单验证
				rules: {
					password: [
						{ required: true, message: '请输入原密码', trigger: 'blur' }
					],
					newPassword: [
						{ required: true, message: '请输入新密码', trigger: 'blur' }
					],
					newPassword2: [
						{ required: true, message: '请输入新密码', trigger: 'blur' }
					],
				},
				// 请求数据参数
				formInline: {
					page: 1,
					limit: 10,
				},
				listData: [],

				// 选中
				checkmenu: [],
				// 分页参数
				pageparm: {
					currentPage: 1,
					pageSize: 10,
					total: 0
				}
			};
		},
		methods: {
			// 编辑、增加页面保存方法
			submitForm: function (editData) {
				this.$refs[editData].validate(valid => {
					if (valid) {
						if (this.editForm.newPassword === this.editForm.newPassword2) {
							this.userInfo = Session.get('userInfo');
							this.editForm.id = this.userInfo.id;
							if (this.userInfo.roles[0] == 'guanliyuan') {
								guanliyuanUpdatePassword(this.editForm).then(res => {
									if (res.code == "0") {
										this.$message.success('修改成功，为了安全，系统将自动清空session，请您重新登陆')
										setTimeout(() => {
											Session.clear();
											// 使用 reload 时，不需要调用 resetRoute() 重置路由
											window.location.reload();
										}, 2000);
									}
								}).catch(err => {
									this.loading = false
									this.$message.error(err)
								})

							}
							else if (this.userInfo.roles[0] == 'yonghu') {
								yonghuxinxiUpdatePassword(this.editForm).then(res => {
									if (res.code == "0") {
										this.$message.success('修改成功，为了安全，系统将自动清空session，请您重新登陆')
										setTimeout(() => {
											Session.clear();
											window.location.reload();
										}, 2000);
									}
								}).catch(err => {
									this.loading = false
									this.$message.error(err)
								})
							}
							
						}
						else {
							this.$message({
								type: 'error',
								message: '两次密码输入不一致'
							})
						}
					} else {
						return false
					}
				})
			},
		}
	};
</script>

<style scoped lang="scss">
	@import './index.scss';
</style>
