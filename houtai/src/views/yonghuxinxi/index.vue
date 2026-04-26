<template>
	<div>
		<el-form :inline="true" :model="formInline" class="user-search">
			<el-form-item label=""><el-input size="small" v-model="formInline.yonghuming" placeholder="输入用户名"></el-input></el-form-item>
			<el-form-item label=""><el-input size="small" v-model="formInline.xingming" placeholder="输入姓名"></el-input></el-form-item>
			<el-form-item   prop="xingbie"><el-select size="small" v-model="formInline.xingbie" placeholder="请选择性别"><el-option selected label="请选择性别" value=""></el-option><el-option label="男" value="true"></el-option><el-option label="女" value="false"></el-option></el-select></el-form-item>
			
			<el-form-item>
				<el-button size="small" type="warning" icon="el-icon-search" @click="search">搜索</el-button>
				<el-button size="small" type="primary" icon="el-icon-plus" @click="handleEdit()" >添加</el-button>
				<el-button size="small" type="success" icon="el-icon-s-data" @click="daochu()" v-if="userInfo.roles[0] == 'guanliyuan'">导出</el-button>
				<el-button size="small" type="danger" icon="el-icon-delete" @click="handleDeleteList()" v-if="userInfo.roles[0] == 'guanliyuan'">批量删除</el-button>
				
			</el-form-item>
			<el-form-item>
				<el-upload class="upload-demo" style="float: left; padding-right: 10px;" action='http://localhost:9999/yonghuxinxi/upload' :headers='headers' :show-file-list='false' :on-success='handleDaoruSuccess' :before-upload='beforeDaoruUpload' ><el-button size="small" type="info" icon="el-icon-sell" @click="daoru()" v-if="userInfo.roles[0] == 'guanliyuan'">导入</el-button></el-upload>
			</el-form-item>
		</el-form>



			<el-table-column sortable prop="yonghuming" label="用户名"></el-table-column>
			<el-table-column sortable prop="xingming" label="姓名"></el-table-column>
			<el-table-column align="center" sortable prop="xingbie" label="性别" ><template slot-scope="scope"><el-switch v-model="scope.row.xingbie ? true : false" active-color="#13ce66" active-text="男" inactive-text="女" inactive-color="#ff4949" @change="editxingbie(scope.$index, scope.row)"></el-switch></template></el-table-column><el-table-column sortable prop="shoujihao" label="手机号"></el-table-column>
			<el-table-column align='center' prop='zhaopian' label='照片' width='120'><template slot-scope='scope'><el-image :src='scope.row.zhaopian' style='height:70px'  v-if="scope.row.zhaopian"/><img src="../../../static/images/guanli.jpg" style='height:70px' v-else/></template></el-table-column>
			
			<el-table-column sortable prop="addtime" label="添加时间" width="160">
				<template slot-scope="scope">
				</template>
			</el-table-column>
			
			<el-table-column align="center" label="操作" min-width="160">
				<template slot-scope="scope">
					


					<el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)" v-if="userInfo.roles[0] == 'guanliyuan' ">编辑</el-button>
					<el-button size="mini" type="danger" @click="deleteYonghuxinxi(scope.$index, scope.row)" v-if="userInfo.roles[0] == 'guanliyuan' ">删除</el-button>
					<el-button size='mini' type='primary' @click="handleDetail(scope.$index, scope.row)">详细</el-button>
					







				<el-form-item  label="用户名" prop="yonghuming"><el-input size="small" v-model="editForm.yonghuming" auto-complete="off" placeholder="请输入用户名" style='width:50%'></el-input></el-form-item>
		
		<el-form-item  label="姓名" prop="xingming"><el-input size="small" v-model="editForm.xingming" auto-complete="off" placeholder="请输入姓名" style='width:50%'></el-input></el-form-item>
		<el-form-item  label="性别" prop="xingbie"><el-radio v-model="editForm.xingbie" :label=true>男</el-radio><el-radio v-model="editForm.xingbie" :label=false>女</el-radio></el-form-item>
		<el-form-item  label="手机号" prop="shoujihao"><el-input size="small" v-model="editForm.shoujihao" auto-complete="off" placeholder="请输入手机号" style='width:50%'></el-input></el-form-item>
		<el-form-item  label='照片' prop='zhaopian'><el-upload class='avatar-uploader' action='http://localhost:9999/files/upload' :headers='headers' :show-file-list='false' :on-success='handleZhaopianSuccess' :before-upload='beforeZhaopianUpload'><img v-if='editForm.zhaopian' :src='editForm.zhaopian' class='avatar'><i v-else class='el-icon-plus avatar-uploader-icon'></i></el-upload></el-form-item>
		<el-form-item  label="备注" prop="beizhu"><el-input type="textarea" size="small" v-model="editForm.beizhu" auto-complete="off" placeholder="请输入备注" style='width:100%'></el-input></el-form-item>
		
				
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button size="small" @click="closeDialog">取消</el-button>
				<el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editForm')">保存</el-button>
			</div>
		</el-dialog>
			
			
			<!--xiangxidhk-->
	</div>
</template>

<script>
import Pagination from "@/layout/pagination/Pagination";
import { yonghuxinxiList, yonghuxinxiSave, yonghuxinxiDelete,yonghuxinxiEdit,yonghuxinxiDeleteList } from '@/api/yonghuxinxi/yonghuxinxiApi';


import {Session} from "@/utils/storage";
import axios from 'axios';
export default {
	name: 'user',
	data() {
		return {
			editForm: {
			},
			user:[],
			username:'',
			cx:'',
			
			rules: {







				yonghuming: [{ required: true, message: '请输入用户名', trigger: 'blur' },
				],
				mima: [{ required: true, message: '请输入密码', trigger: 'blur' },
				],
				shoujihao: [{ pattern: /^[1][3,4,5,7,8][0-9]{9}$/,required: true,message: '请输入正确的手机号',trigger: 'blur'}
				],
				
			},
			
			formInline: {
				page: 1,
				limit: 10,
			},
			
			
			listData: [],
			yonghuxinxis:[],
			
			checkmenu: [],
			pageparm: {
				currentPage: 1,
				pageSize: 10,
				total: 0
			}
		};
	},
	computed:{
		headers(){
			return {"token":Session.get("token")}
		}
	},
	watch: {
			'$route' (to, from) {
				// 路由发生变化页面刷新
				this.$router.go(0);
			}
		},
	components: {
		Pagination
	},
	created() {
		
		if (!Session.get('userInfo')) return false;
		this.userInfo = Session.get('userInfo');
		this.username=localStorage.getItem("username");
		this.cx=localStorage.getItem("cx");
		
		this.getdata(this.formInline)
		
	},
	methods: {
		
		handleShenheList(){
				const yonghuxinxis = this.yonghuxinxis;
				if(yonghuxinxis.length == 0){
					this.$message({
						type: 'error',
						message: '请至少选择一项'
					})
				}else {
					this.$confirm('确定要批量审核吗?', '信息', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					}).then(() => {
						yonghuxinxis.forEach(item=> {
							let data = {...item, issh: 1}
							yonghuxinxiEdit(data).then(res => {})
						})
						this.getdata(this.formInline)
						this.$message({
							type: 'success',
							message: '批量审核成功！'
						})
					}).catch(() => {
						this.$message({
							type: 'info',
							message: '已取消批量审核'
						})
					})
				}
			},
		getdata(parameter) {
			yonghuxinxiList(parameter)
					.then(res => {
						this.loading = false
						if (res.success == false) {
							this.$message({
								type: 'info',
								message: res.msg
							})
						} else {
							this.listData = res.data.list
							
							
							
							// 分页赋值
							this.pageparm.currentPage = this.formInline.current
							this.pageparm.pageSize = this.formInline.currentNum
							this.pageparm.total = res.data.total
						}
					})
					.catch(err => {
						this.loading = false
						this.$message.error('菜单加载失败，请稍后再试！')
					})
		},
		
				// 分页插件事件
		callFather(parm) {
			this.formInline.current = parm.currentPage
			this.getdata(this.formInline)
		},
		
		// 搜索事件
		search() {
			this.getdata(this.formInline)
		},
		//显示编辑界面
		handleEdit: function (index, row) {
			this.editFormVisible = true
			if (row != undefined && row != 'undefined') {
				this.title = '修改用户信息'
				this.editForm = row
			} else {
				this.title = '添加用户信息'
				this.editForm = {}
				
				

		
			}
		},
				daochu(){
			axios.get('/yonghuxinxi/getExcel', {
				responseType: 'blob',
				headers: { token: Session.get("token"), 'Content-Type': 'application/x-download' }
			}).then((res) => {
				if (res.status === 200) {
					var a = document.createElement('a')
					var blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
					var href = window.URL.createObjectURL(blob)
					a.href = href
					a.download = '用户信息.xlsx'
					document.body.appendChild(a)
					a.click() 
					document.body.removeChild(a) 
					window.URL.revokeObjectURL(href)
				}
			}).catch(err => {
				console.log(err)
			})
		},
		handleDaoruSuccess(res, file) {
			if(res.code == "0") {
				this.$message({
					type: 'success',
					message: '导入成功!'
				})
				this.getdata(this.formInline);
			}
		},
		beforeDaoruUpload(file) {
			const isJPG = file.type === 'application/vnd.ms-excel';
			if (!isJPG) {
				this.$message.error('上传只能是 excel 格式!');
			}
			return isJPG;
		},


		
		handleDetail(index, row) {const w = window.open("about:blank");w.location.href = 'http://localhost:8080/#/yonghuxinxidetail/'+row.id;},


				// 编辑 / 添加 提交方法
		submitForm(editData) {
			
			this.$refs[editData].validate(valid => {
				if (valid) {
					if(this.editForm.id){
						yonghuxinxiEdit(this.editForm).then(res => {
							this.editFormVisible = false
							
							this.loading = false
							if (res.code == '0') {
								this.getdata(this.formInline)
								this.$message({
									type: 'success',
									message: '用户信息修改成功！'
								})
							} else {
								this.$message({
									type: 'info',
									message: res.msg
								})
							}
						}).catch(err => {
							this.editFormVisible = false
							this.loading = false
							this.getdata(this.formInline)
						if(err){
								this.$message.error(err)
							}else {
								this.$message.error('操作失败，请稍后再试！')
							}
						})
					}else {
						yonghuxinxiSave(this.editForm).then(res => {
							this.editFormVisible = false
							this.loading = false
							if (res.code == '0') {
								
								this.getdata(this.formInline)
								this.$message({
									type: 'success',
									message: '用户信息添加成功！'
								})
							} else {
								this.$message({
									type: 'info',
									message: res.msg
								})
							}
						}).catch(err => {
							this.editFormVisible = false
							this.loading = false
						if(err){
								this.$message.error(err)
							}else {
								this.$message.error('操作失败，请稍后再试！')
							}
						})
					}
				} else {
					return false
				}
			})
		},
		// 删除公司
		deleteYonghuxinxi(index, row) {
			this.$confirm('确定要删除吗?', '信息', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				yonghuxinxiDelete(row.id).then(res => {
					if (res.code == '0') {
						this.$message({
								type: 'success',
							message: '用户信息已删除!'
						})
						this.getdata(this.formInline)
					} else {
						this.$message({
							type: 'info',
							message: res.msg
						})
					}
				}).catch(err => {
					this.loading = false
					this.$message.error('操作失败，请稍后再试')
				})
			}).catch(() => {
				this.$message({
					type: 'info',
					message: '已取消删除'
				})
			})
		},
		handleSelectionChange(val){
			this.yonghuxinxis = val;
		},
		handleDeleteList(){
			const yonghuxinxis = this.yonghuxinxis;
			if(yonghuxinxis.length == 0){
				this.$message({
					type: 'error',
					message: '请至少选择一项进行删除'
				})
			}else {
				this.$confirm('确定要批量删除吗?', '信息', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					yonghuxinxiDeleteList(yonghuxinxis).then(res => {
						if (res.code == '0') {
							this.$message({
								type: 'success',
								message: '批量删除成功!'
							})
							this.getdata(this.formInline)
						} else {
							this.$message({
								type: 'info',
								message: res.msg
							})
						}
					}).catch(err => {
						this.loading = false
						this.$message.error('操作失败，请稍后再试')
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					})
				})
			}
		},
		editxingbie(index,row){
			let data = {...row,xingbie:!row.xingbie}
			yonghuxinxiEdit(data).then(res => {
				this.editFormVisible = false
				this.loading = false
				if (res.code == '0') {
						this.$message({
							type: 'success',
							message: '性别修改成功！'
						})
					} else {
						this.$message({
							type: 'info',
							message: res.msg
						})
				}
						}).catch(err => {
				this.editFormVisible = false
				this.loading = false
				if(err){
					this.$message.error(err)
				}else {
					this.$message.error('操作失败，请稍后再试！')
				}
			})
		},
		
		handleZhaopianSuccess(res, file) {
				if(res.code == "0") {
					this.editForm.zhaopian = "/files/download/"+res.data.id
				}
			},
			beforeZhaopianUpload(file) {
				const isJPG = file.type === 'image/jpeg';
				const isLt2M = file.size / 1024 / 1024 < 2;

				if (!isJPG) {
					this.$message.error('上传图片只能是 JPG 格式!');
				}
				if (!isLt2M) {
					this.$message.error('上传图片大小不能超过 2MB!');
				}
				return isJPG && isLt2M;




			},
				
				// 关闭编辑弹出框
		closeDialog() {
			this.getdata(this.formInline)
			this.editFormVisible = false
		}
	},
};
</script>

<style scoped lang="scss">
	@import './index.scss';
</style>
