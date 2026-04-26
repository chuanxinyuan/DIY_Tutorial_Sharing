<template>
	<div>
		<el-form :inline="true" :model="formInline" class="user-search">
			<el-form-item label=""><el-input size="small" v-model="formInline.wupinbianhao" placeholder="输入物品编号"></el-input></el-form-item>			<el-form-item label=""><el-input size="small" v-model="formInline.wupinmingcheng" placeholder="输入物品名称"></el-input></el-form-item>			<el-form-item label=""><el-input size="small" v-model="formInline.faburen" placeholder="输入发布人"></el-input></el-form-item>			
			<el-form-item>
				<el-button size="small" type="warning" icon="el-icon-search" @click="search">搜索</el-button>
				<el-button size="small" type="primary" icon="el-icon-plus" @click="handleEdit()" v-if="userInfo.roles[0] == 'yonghu'">添加</el-button>
				<el-button size="small" type="success" icon="el-icon-s-data" @click="daochu()" v-if="userInfo.roles[0] == 'guanliyuan'">导出</el-button>
				<el-button size="small" type="danger" icon="el-icon-delete" @click="handleDeleteList()" v-if="userInfo.roles[0] == 'guanliyuan'">批量删除</el-button>
				<el-button size="small" type="warning" icon="el-icon-finished" @click="handleShenheList()" v-if="userInfo.roles[0] == 'guanliyuan'">批量审核</el-button>
			</el-form-item>
			<el-form-item>
				<el-upload class="upload-demo" style="float: left; padding-right: 10px;" action='http://localhost:9999/ershouwupin/upload' :headers='headers' :show-file-list='false' :on-success='handleDaoruSuccess' :before-upload='beforeDaoruUpload' ><el-button size="small" type="info" icon="el-icon-sell" @click="daoru()" v-if="userInfo.roles[0] == 'guanliyuan'">导入</el-button></el-upload>
			</el-form-item>
		</el-form>
		
		<el-table size="small" :data="listData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" @selection-change="handleSelectionChange">
			<el-table-column align="center" type="selection" width="60"></el-table-column>
			<el-table-column sortable prop="wupinbianhao" label="物品编号"></el-table-column>			<el-table-column sortable prop="wupinmingcheng" label="物品名称"></el-table-column>			<el-table-column sortable prop="wupinleixing" label="物品类型"></el-table-column>			<el-table-column sortable prop="jiage" label="价格"></el-table-column>			<el-table-column align='center' prop='tupian' label='图片' width='120'><template slot-scope='scope'><el-image :src='scope.row.tupian' style='height:70px'  v-if="scope.row.tupian"/><img src="../../../static/images/guanli.jpg" style='height:70px' v-else/></template></el-table-column>			<el-table-column sortable prop="faburen" label="发布人"></el-table-column>			<el-table-column v-if="userInfo.roles[0] == 'guanliyuan'" align="center" sortable prop="issh" label="是否审核" >
				<template slot-scope="scope" >
					<el-switch   v-model="scope.row.issh ? true : false" active-color="#13ce66" active-text="是" inactive-text="否" inactive-color="#ff4949" @change="editissh(scope.$index, scope.row)" ></el-switch>
				</template>
			</el-table-column>
			<el-table-column v-else align="center" sortable prop="issh" label="是否审核" >
				<template slot-scope="scope" >
					<el-switch  disabled="disabled"  v-model="scope.row.issh ? true : false" active-color="#13ce66" active-text="是" inactive-text="否" inactive-color="#ff4949" @change="editissh(scope.$index, scope.row)" ></el-switch>
				</template>
			</el-table-column>			
			<el-table-column sortable prop="addtime" label="添加时间" width="160">
				<template slot-scope="scope">
					<div>{{scope.row.addtime|datePipe('yyyy-MM-dd hh:mm:ss')}}</div>
				</template>
			</el-table-column>
			
			<el-table-column align="center" label="操作" min-width="160">
				<template slot-scope="scope">
					
					<!--liangbuan-->
					<el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)" v-if="userInfo.roles[0] == 'guanliyuan'  || userInfo.roles[0] == 'yonghu'">编辑</el-button>
					<el-button size="mini" type="danger" @click="deleteErshouwupin(scope.$index, scope.row)" v-if="userInfo.roles[0] == 'guanliyuan'  || userInfo.roles[0] == 'yonghu'">删除</el-button>
					<el-button size='mini' type='primary' @click="handleDetail(scope.$index, scope.row)">详细</el-button>
					
				</template>
			</el-table-column>
		</el-table>
		<Pagination v-bind:child-msg="pageparm" @callFather="callFather"></Pagination>
		
		<el-dialog :title="title" :visible.sync="editFormVisible" width="30%" @click="closeDialog">
			<el-form label-width="120px" :model="editForm" :rules="rules" ref="editForm">
				<el-form-item  label="物品编号" prop="wupinbianhao"><el-input size="small" v-model="editForm.wupinbianhao" auto-complete="off" placeholder="请输入物品编号" style='width:50%'></el-input></el-form-item>		<el-form-item  label="物品名称" prop="wupinmingcheng"><el-input size="small" v-model="editForm.wupinmingcheng" auto-complete="off" placeholder="请输入物品名称" style='width:50%'></el-input></el-form-item>		<el-form-item  label="物品类型" prop="wupinleixing"><el-input size="small" v-model="editForm.wupinleixing" auto-complete="off" placeholder="请输入物品类型" style='width:50%'></el-input></el-form-item>		<el-form-item  label="描述" prop="miaoshu"><el-input type="textarea" size="small" v-model="editForm.miaoshu" auto-complete="off" placeholder="请输入描述" style='width:100%'></el-input></el-form-item>		<el-form-item  label="价格" prop="jiage"><el-input-number size="small" v-model="editForm.jiage" auto-complete="off" placeholder="请输入价格" style='width:50%'></el-input-number></el-form-item>		<el-form-item  label='图片' prop='tupian'><el-upload class='avatar-uploader' action='http://localhost:9999/files/upload' :headers='headers' :show-file-list='false' :on-success='handleTupianSuccess' :before-upload='beforeTupianUpload'><img v-if='editForm.tupian' :src='editForm.tupian' class='avatar'><i v-else class='el-icon-plus avatar-uploader-icon'></i></el-upload></el-form-item>		<el-form-item  label="发布人" prop="faburen"><el-input size="small" v-model="editForm.faburen" auto-complete="off" placeholder="请输入发布人" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="备注" prop="beizhu"><el-input type="textarea" size="small" v-model="editForm.beizhu" auto-complete="off" placeholder="请输入备注" style='width:100%'></el-input></el-form-item>		
				
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
import { ershouwupinList, ershouwupinSave, ershouwupinDelete,ershouwupinEdit,ershouwupinDeleteList } from '@/api/ershouwupin/ershouwupinApi';


import {Session} from "@/utils/storage";
import axios from 'axios';
export default {
	name: 'user',
	data() {
		return {
			loading: false, //是显示加载
			title: '',
			
			editFormVisible: false, //控制编辑页面显示与隐藏
			//detaitFormVsisisble
			
			
			editForm: {
			},
			user:[],
			username:'',
			cx:'',
			
			rules: {
				wupinmingcheng: [{ required: true, message: '请输入物品名称', trigger: 'blur' },				],				jiage: [{ type: 'number', message: '价格必须为数字'},				],				
			},
			
			formInline: {
				page: 1,
				limit: 10,
			},
			
			
			listData: [],
			ershouwupins:[],
			
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
		if(this.userInfo.roles[0] == 'yonghu'){this.formInline.faburen= this.username;}		
		this.getdata(this.formInline)
		
	},
	methods: {
		
		handleShenheList(){
				const ershouwupins = this.ershouwupins;
				if(ershouwupins.length == 0){
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
						ershouwupins.forEach(item=> {
							let data = {...item, issh: 1}
							ershouwupinEdit(data).then(res => {})
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
			ershouwupinList(parameter)
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
				this.title = '修改二手物品'
				this.editForm = row
			} else {
				this.title = '添加二手物品'
				this.editForm = {}
				this.editForm.faburen= this.username;
				
				this.editForm.wupinbianhao = this.getProjectNum() + Math.floor(Math.random() * 10000);
			}
		},
		daochu(){
			axios.get('/ershouwupin/getExcel', {
				responseType: 'blob',
				headers: { token: Session.get("token"), 'Content-Type': 'application/x-download' }
			}).then((res) => {
				if (res.status === 200) {
					var a = document.createElement('a')
					var blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
					var href = window.URL.createObjectURL(blob)
					a.href = href
					a.download = '二手物品.xlsx'
					document.body.appendChild(a)
					a.click() 
					document.body.removeChild(a) 
					window.URL.revokeObjectURL(href)
				}
			}).catch((err) => {
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
		getProjectNum () {
			const projectTime = new Date() // 当前中国标准时间
			const Year = projectTime.getFullYear() // 获取当前年份 支持IE和火狐浏览器.
			const Month = projectTime.getMonth() + 1 // 获取中国区月份
			const Day = projectTime.getDate() // 获取几号
			var CurrentDate = Year
			if (Month >= 10) { // 判断月份和几号是否大于10或者小于10
				CurrentDate += Month
			} else {
				CurrentDate += '0' + Month
			}
			if (Day >= 10) {
				CurrentDate += Day
			} else {
				CurrentDate += '0' + Day
			}
			return CurrentDate
		},
		
		handleDetail(index, row) {const w = window.open("about:blank");w.location.href = 'http://localhost:8080/#/ershouwupindetail/'+row.id;},
		//liangbuedit
		// 编辑、增加页面保存方法
		submitForm(editData) {
			
			this.$refs[editData].validate(valid => {
				if (valid) {
					if(this.editForm.id){
						ershouwupinEdit(this.editForm).then(res => {
							this.editFormVisible = false
							
							this.loading = false
							if (res.code == '0') {
								this.getdata(this.formInline)
								this.$message({
									type: 'success',
									message: '二手物品修改成功！'
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
						ershouwupinSave(this.editForm).then(res => {
							this.editFormVisible = false
							this.loading = false
							if (res.code == '0') {
								
								this.getdata(this.formInline)
								this.$message({
									type: 'success',
									message: '二手物品添加成功！'
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
		deleteErshouwupin(index, row) {
			this.$confirm('确定要删除吗?', '信息', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				ershouwupinDelete(row.id).then(res => {
					if (res.code == '0') {
						this.$message({
								type: 'success',
							message: '二手物品已删除!'
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
			this.ershouwupins = val;
		},
		handleDeleteList(){
			const ershouwupins = this.ershouwupins;
			if(ershouwupins.length == 0){
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
					ershouwupinDeleteList(ershouwupins).then(res => {
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
		//xuaxnzeshxifou
		
		editissh(index,row){
				let data = {...row,issh:!row.issh}
				ershouwupinEdit(data).then(res => {
					this.editFormVisible = false
					this.loading = false
					if (res.code == '0') {
						this.getdata(this.formInline)
						this.$message({
							type: 'success',
							message: '审核状态修改成功！'
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
		
		handleTupianSuccess(res, file) {
				if(res.code == "0") {
					this.editForm.tupian = "/files/download/"+res.data.id
				}
			},
			beforeTupianUpload(file) {
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
		//youscwexnjiaxn
		// 关闭编辑、增加弹出框
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
