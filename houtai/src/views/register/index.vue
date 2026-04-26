<template>
	<div class="login">
		<div class="bg"></div>
		<div class="container">
			<div class="line bouncein">
				<div class="xs6 xm4 xs3-move xm4-move">
					<div style="height:150px;"></div>
					<div class="media media-y margin-big-bottom">
					</div>
					<el-form :model="ruleForm" :rules="rules" ref="ruleForm">
						<div class="panel loginbox">
							<div class="text-center margin-big padding-big-top"><h3>用户注册</h3></div>
							<div class="panel-body" style="padding:30px; padding-bottom:10px; padding-top:10px;">
								<el-form-item style="margin-left: 0px" prop="username">
									<el-input type="text" :placeholder="$t('message.login.placeholder1')" prefix-icon="el-icon-user" v-model="ruleForm.username" clearable autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item style="margin-left: 0px" prop="password">
									<el-input type="password" :placeholder="$t('message.login.placeholder2')" prefix-icon="el-icon-lock" v-model="ruleForm.password" autocomplete="off" :show-password="true" ></el-input>
								</el-form-item>
								<el-form-item style="margin-left: 0px" prop="password2">
									<el-input type="password" placeholder="请输入确认密码" prefix-icon="el-icon-lock" v-model="ruleForm.password2" autocomplete="off" :show-password="true" ></el-input>
								</el-form-item>
							</div>
							<el-form-item style="margin: 40px 0px;text-align: center;">
								<el-button type="primary" class="login-submit" @click="submitForm('ruleForm')" :loading="submit.loading">
									<span>注册</span>
								</el-button>
								<el-button type="primary" class="login-submit" @click="fanhui" :loading="submit.loading">
									<span>返回登录</span>
								</el-button>
							</el-form-item>
						</div>
					</el-form>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
import { Session } from '@/utils/storage';
import { formatDate, formatAxis } from '@/utils/formatTime';
import { PrevLoading } from '@/utils/loading.js';
import { quotationsList } from './mock';
import { register } from '../../api/login/index.js'
export default {
	name: 'login',
	data() {
		return {
			quotationsList,
			quotations: {},
			isView: false,
			submit: {
				loading: false,
			},
			ruleForm: {
				username: '',
				password: '',
				code: '',
				role: '管理员'
			},
			time: {
				txt: '',
				fun: null,
			},
			rules: {
				username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
				password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
				password2: [{ required: true, message: '请重新输入密码', trigger: 'blur' }],
				code: [{ required: true,trigger: 'blur',validator: this.codeValidator }],
				role:[{required:true,message:'请选择角色'}]
			},
			code:'',
		};
	},
	computed: {
		// 获取当前时间
		currentTime() {
			return formatAxis(new Date());
		},
		// 获取布局配置信息
		getThemeConfig() {
			return this.$store.state.themeConfig.themeConfig;
		},
	},
	created() {
		this.initTime();
		this.initCode();
	},
	mounted() {
		this.initRandomQuotations();
	},
	methods: {
		fanhui(){
			this.$router.go(-1);
		},
		codeValidator(rule,value,callback){
			let res
			if(!value) {
				res = rule.required ? new Error('请输入验证码信息') : undefined
			} else {
				const val = value.trim();
				if(val != this.code){
					res = new Error("验证码信息不正确")
				}
			}
			callback(res)
		},
		// 随机语录
		initRandomQuotations() {
			this.quotations = this.quotationsList[Math.floor(Math.random() * this.quotationsList.length)];
		},
		// 初始化左上角时间显示
		initTime() {
			this.time.txt = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS WWW QQQQ');
			this.time.fun = setInterval(() => {
				this.time.txt = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS WWW QQQQ');
			}, 1000);
		},
		initCode(){
			let code = Math.floor(Math.random()*(9999-1000))+1000;
			this.code = code;
		},
		changeCode(){
			this.initCode();
		},
		// 登录按钮点击
		submitForm(formName) {
			this.$refs[formName].validate(valid => {
				if (valid) {
					if(this.ruleForm.password == this.ruleForm.password2){
						register(this.ruleForm).then(res=> {
							this.$message({
								type: 'success',
								message: '注册成功'
							})
							setTimeout(() => {
								this.$router.push('/login');
							}, 300);
						}).catch(msg =>{
							this.submit.loading = false;
							this.$message({
								type: 'error',
								message: msg
							})
						})
					}else {
						this.$message({
							type: 'error',
							message: '两次密码输入不一致'
						})
					}
				}
			})
		},
	},
	destroyed() {
		clearInterval(this.time.fun);
	},
};
</script>

<style scoped lang="scss">
	@import "index.css";
	.login {
		height: 100%;
		width: 100%;
		.vue-particles {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: radial-gradient(ellipse at top left, rgba(105, 155, 200, 1) 0%, rgba(181, 197, 216, 1) 57%);
		}
		.login-weaper {
			margin: auto;
			height: 500px;
			display: flex;
			box-sizing: border-box;
			position: relative;
			z-index: 1;
			border: none;
			box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
			.login-left {
				width: 491px;
				padding: 20px;
				font-size: 16px;
				color: var(--prev-color-text-white);
				position: relative;
				background-color: var(--prev-color-primary);
				display: flex;
				flex-direction: column;
				border-top-left-radius: 4px;
				border-bottom-left-radius: 4px;
				.login-time {
					width: 100%;
					color: var(--prev-color-text-white);
					opacity: 0.9;
					font-size: 14px;
					overflow: hidden;
				}
				.login-left-box {
					flex: 1;
					overflow: hidden;
					position: relative;
					z-index: 1;
					display: flex;
					align-items: center;
					padding: 80px 45px;
					.logo {
						font-size: 22px;
						margin-bottom: 15px;
					}
					.title {
						color: var(--prev-color-text-white);
						font-weight: 300;
						letter-spacing: 2px;
						font-size: 16px;
					}
					.msg {
						color: var(--prev-color-text-white);
						font-size: 13px;
						margin-top: 35px;
						.msg-author {
							opacity: 0.6;
							span:last-of-type {
								margin-left: 15px;
							}
						}
						.msg-txt {
							margin-top: 15px;
							line-height: 22px;
						}
					}
				}
			}
			.login-right {
				width: 491px;
				padding: 20px;
				position: relative;
				align-items: center;
				display: flex;
				background-color: var(--prev-bg-white);
				border-top-right-radius: 4px;
				border-bottom-right-radius: 4px;
				.login-main {
					margin: 0 auto;
					width: 70%;
					.login-title {
						color: var(--prev-color-text-primary);
						margin-bottom: 40px;
						font-weight: 500;
						font-size: 22px;
						text-align: center;
						letter-spacing: 4px;
					}
					.login-form {
						margin: 10px 0;
						i {
							color: var(--prev-color-text-primary);
						}
						.el-form-item {
							margin-bottom: 20px !important;
						}
					}
					.login-menu {
						margin-top: 30px;
						width: 100%;
						text-align: left;
						a {
							color: var(--prev-color-text-secondary);
							font-size: 12px;
							margin: 0 8px;
							text-decoration: none;
							&:hover {
								color: var(--prev-color-primary);
								text-decoration: underline;
							}
						}
					}
				}
			}
		}
	}
	.login-code {
		display: flex;
		align-items: center;
		justify-content: space-around;
		margin: 0 0 0 10px;
		user-select: none;
		.login-code-img {
			margin-top: 2px;
			width: 100px;
			height: 38px;
			border: 1px solid var(--prev-border-color-base);
			color: var(--prev-color-text-primary);
			font-size: 14px;
			font-weight: 700;
			letter-spacing: 5px;
			line-height: 38px;
			text-indent: 5px;
			text-align: center;
			cursor: pointer;
			transition: all ease 0.2s;
			border-radius: 4px;
			&:hover {
				border-color: var(--prev-border-color-hover);
				transition: all ease 0.2s;
			}
		}
	}
	.login-submit {
		width: 45%;
		letter-spacing: 2px;
		margin-bottom: 40px;
	}
</style>