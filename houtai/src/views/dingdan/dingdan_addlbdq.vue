<template>
	<div class="personal">
		<el-form label-width="100px" ref="editForm" :model="editForm" :rules="rules">
			<el-form-item  label="订单编号" prop="dingdanbianhao"><el-input size="small" v-model="editForm.dingdanbianhao" auto-complete="off" placeholder="请输入订单编号" style='width:50%'></el-input></el-form-item>		<el-form-item  label="物品编号" prop="wupinbianhao"><el-input size="small" v-model="editForm.wupinbianhao" auto-complete="off" placeholder="请输入物品编号" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="物品名称" prop="wupinmingcheng"><el-input size="small" v-model="editForm.wupinmingcheng" auto-complete="off" placeholder="请输入物品名称" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="物品类型" prop="wupinleixing"><el-input size="small" v-model="editForm.wupinleixing" auto-complete="off" placeholder="请输入物品类型" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="价格" prop="jiage"><el-input size="small" v-model="editForm.jiage" auto-complete="off" placeholder="请输入价格" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="发布人" prop="faburen"><el-input size="small" v-model="editForm.faburen" auto-complete="off" placeholder="请输入发布人" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="期望价格" prop="qiwangjiage"><el-input-number size="small" v-model="editForm.qiwangjiage" auto-complete="off" placeholder="请输入期望价格" style='width:50%'></el-input-number></el-form-item>		<el-form-item  label="购买人" prop="goumairen"><el-input size="small" v-model="editForm.goumairen" auto-complete="off" placeholder="请输入购买人" style='width:50%' disabled></el-input></el-form-item>		<el-form-item  label="联系电话" prop="lianxidianhua"><el-input size="small" v-model="editForm.lianxidianhua" auto-complete="off" placeholder="请输入联系电话" style='width:50%'></el-input></el-form-item>		<el-form-item  label="地址" prop="dizhi"><el-input size="small" v-model="editForm.dizhi" auto-complete="off" placeholder="请输入地址" style='width:100%'></el-input></el-form-item>		<el-form-item  label="备注" prop="beizhu"><el-input type="textarea" size="small" v-model="editForm.beizhu" auto-complete="off" placeholder="请输入备注" style='width:100%'></el-input></el-form-item>		
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editForm')">添加</el-button>
		</div>
	</div>
</template>

<script>
	import {dingdanEdit, dingdanSave,dingdanDetail} from "@/api/dingdan/dingdanApi";
	import {Session} from "@/utils/storage";
import {ershouwupinDetail,ershouwupinEdit} from "../../api/ershouwupin/ershouwupinApi";
//xiabiaoduan1
export default {
	name: 'personal',
	data() {
		return {
			rules: {
				//yztssssss1
			},
			editForm: {
				avater:'',
				wupinbianhao:'',				wupinmingcheng:'',				wupinleixing:'',				jiage:'',				faburen:'',				
			},
			username:'',
			cx:'',
			//xiabiaoduan2
		};
	},
	created() {
		if (!Session.get('userInfo')) return false;
		this.userInfo = Session.get('userInfo');
		this.username=localStorage.getItem("username");
		this.cx=localStorage.getItem("cx");
		let id = this.$route.params.id;
		//xiabiaoduan3
		this.getUserInfo(id);
	},
	computed: {
		headers(){
			return {"token":Session.get("token")}
		},
	},
	methods:{
		getUserInfo(id){
			ershouwupinDetail(id).then(res=>{
				if(res.code == '0'){
					this.ershouwupin = res.data
					this.editForm.wupinbianhao = this.ershouwupin.wupinbianhao;					this.editForm.wupinmingcheng = this.ershouwupin.wupinmingcheng;					this.editForm.wupinleixing = this.ershouwupin.wupinleixing;					this.editForm.jiage = this.ershouwupin.jiage;					this.editForm.faburen = this.ershouwupin.faburen;				
					this.editForm.goumairen= this.userInfo.yonghuming;
					this.editForm.dingdanbianhao = this.getProjectNum() + Math.floor(Math.random() * 10000);
					
				}
			})
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
		//qixuzhijixsuan3
		//xia2sui1
		// 编辑、增加页面保存方法
		submitForm(editData) {
			
			this.$refs[editData].validate(valid => {
				if (valid) {
					if(this.editForm.id){
						
					}else {
						dingdanSave(this.editForm).then(res => {
							if (res.code == '0') {
								
								this.$message({
									type: 'success',
									message: '添加成功！'
								})
							} else {
								this.$message({
									type: 'info',
									message: res.msg
								})
							}
						}).catch(err => {
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
		
		//youscwexnjiaxn
		//xiabiaoduan4
	}
};
</script>

<style scoped lang="scss">
	@import './index.scss';
</style>
