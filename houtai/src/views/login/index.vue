<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="bg-gradient"></div>
      <div class="bg-pattern"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <div class="login-card">
        <!-- 左侧装饰区 -->
        <div class="login-left">
          <div class="brand-section">
            <div class="logo">
              <i class="el-icon-shopping-bag-1"></i>
            </div>
            <h2 class="brand-title">旧物新生平台</h2>
            <p class="brand-subtitle">让旧物重生 · 让创意持续分享</p>
          </div>

          <div class="features-list">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>安全交易</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>方便快捷</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>物美价廉</span>
            </div>
          </div>

          <div class="time-display" v-if="time.txt">
            <i class="el-icon-time"></i>
            <span>{{ time.txt }}</span>
          </div>
        </div>

        <!-- 右侧表单区 -->
        <div class="login-right">
          <div class="form-header">
            <h3 class="form-title">欢迎回来</h3>
            <p class="form-subtitle">登录您的旧物新生平台账号</p>
          </div>

          <el-form
              :model="ruleForm"
              :rules="rules"
              ref="ruleForm"
              class="login-form"
              @keyup.enter.native="submitForm('ruleForm')"
          >
            <!-- 用户名 -->
            <el-form-item prop="username">
              <el-input
                  v-model="ruleForm.username"
                  placeholder="请输入账号"
                  prefix-icon="el-icon-user"
                  clearable
                  size="medium"
              >
              </el-input>
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="password">
              <el-input
                  v-model="ruleForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="el-icon-lock"
                  show-password
                  size="medium"
              >
              </el-input>
            </el-form-item>

            <!-- 验证码 -->
            <el-form-item prop="code">
              <div class="code-row">
                <el-input
                    v-model="ruleForm.code"
                    maxlength="4"
                    placeholder="请输入验证码"
                    prefix-icon="el-icon-position"
                    clearable
                    size="medium"
                    class="code-input"
                >
                </el-input>
                <div class="code-box" @click="changeCode">
                  <span class="code-text">{{ code }}</span>
                </div>
              </div>
            </el-form-item>

            <!-- 角色选择 -->
            <el-form-item prop="role" class="role-item">
              <el-radio-group v-model="ruleForm.role" size="small">
<!--                <el-radio-button label="管理员">管理员</el-radio-button>-->
              </el-radio-group>
            </el-form-item>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <el-button
                  type="primary"
                  class="btn-login"
                  :loading="submit.loading"
                  @click="submitForm('ruleForm')"
              >
                <i class="el-icon-right"></i>
                <span>立即登录</span>
              </el-button>

            </div>
          </el-form>

                    <!-- 底部链接 -->
        </div>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>
    <div class="decoration-circle circle-3"></div>
    <div class="floating-icon icon-1"><i class="el-icon-reading"></i></div>
    <div class="floating-icon icon-2"><i class="el-icon-notebook-1"></i></div>
    <div class="floating-icon icon-3"><i class="el-icon-bicycle"></i></div>
  </div>
</template>

<script>
import { Session } from '@/utils/storage';
import { formatDate, formatAxis } from '@/utils/formatTime';
import { PrevLoading } from '@/utils/loading.js';
import { quotationsList } from './mock';
import { login } from '../../api/login/index.js'

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
        username: [{ required: true, message: '请输入学号或手机号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ required: true, trigger: 'blur', validator: this.codeValidator }],
        role: [{ required: true, message: '请选择角色' }]
      },
      code: '',
    };
  },
  computed: {
    currentTime() {
      return formatAxis(new Date());
    },
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
    codeValidator(rule, value, callback) {
      let res
      if (!value) {
        res = rule.required ? new Error('请输入验证码') : undefined
      } else {
        const val = value.trim();
        if (val != this.code) {
          res = new Error("验证码错误")
        }
      }
      callback(res)
    },
    zhuce() {
      this.$router.push({ name: 'register' })
    },
    initRandomQuotations() {
      this.quotations = this.quotationsList[Math.floor(Math.random() * this.quotationsList.length)];
    },
    initTime() {
      this.time.txt = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS WWW');
      this.time.fun = setInterval(() => {
        this.time.txt = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS WWW');
      }, 1000);
    },
    initCode() {
      let code = Math.floor(Math.random() * (9999 - 1000)) + 1000;
      this.code = code;
    },
    changeCode() {
      this.initCode();
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.submit.loading = true;
          login(this.ruleForm).then(res => {
            let defaultRoles = [];
            let defaultAuthBtnList = [];
            let yonghuRoles = ['yonghu'];
            let guanliyuanRoles = ['guanliyuan'];
            let adminAuthBtnList = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];

            if (this.ruleForm.role === '管理员') {
              defaultRoles = guanliyuanRoles;
              defaultAuthBtnList = adminAuthBtnList;
            }
            if (this.ruleForm.role === '用户') {
              defaultRoles = yonghuRoles;
              defaultAuthBtnList = adminAuthBtnList;
            }

            let user = res.data.user;
            localStorage.setItem("username", this.ruleForm.username);
            localStorage.setItem("cx", this.ruleForm.role);
            user = {
              ...user,
              time: new Date().getTime(),
              roles: defaultRoles,
              authBtnList: defaultAuthBtnList
            }
            Session.set('token', res.data.token);
            Session.set('userInfo', user);
            this.$store.dispatch('userInfos/setUserInfos', user);
            PrevLoading.start();
            this.submit.loading = false;
            this.$router.push('/');
            setTimeout(() => {
              this.$message.success(`${this.currentTime}，登录成功！欢迎回到旧物改造教程分享平台`);
            }, 300);
          }).catch(msg => {
            this.submit.loading = false;
            this.$message({
              type: 'error',
              message: msg
            })
          })
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
// 变量定义 - 校园青春绿色系
$primary-color: #10b981;        // 翡翠绿
$primary-light: #34d399;        // 浅绿
$primary-dark: #059669;         // 深绿
$accent-color: #f59e0b;         // 活力橙点缀
$text-primary: #1f2937;
$text-secondary: #6b7280;
$bg-gray: #f3f4f6;
$white: #ffffff;
$shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);

.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);

  // 动态背景
  .bg-animation {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;

    .bg-gradient {
      position: absolute;
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #064e3b 0%, $primary-dark 50%, $primary-color 100%);
      opacity: 0.95;
    }

    .bg-pattern {
      position: absolute;
      width: 100%;
      height: 100%;
      background-image:
          radial-gradient(circle at 10% 20%, rgba(255,255,255,0.15) 0%, transparent 40%),
          radial-gradient(circle at 90% 80%, rgba(255,255,255,0.1) 0%, transparent 40%),
          radial-gradient(circle at 50% 50%, rgba(255,255,255,0.05) 0%, transparent 60%);
    }
  }

  // 装饰圆圈 - 更柔和的玻璃质感
  .decoration-circle {
    position: fixed;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    z-index: 1;

    &.circle-1 {
      width: 400px;
      height: 400px;
      top: -200px;
      right: -150px;
      animation: float 8s ease-in-out infinite;
    }

    &.circle-2 {
      width: 300px;
      height: 300px;
      bottom: -150px;
      left: -100px;
      animation: float 10s ease-in-out infinite reverse;
    }

    &.circle-3 {
      width: 200px;
      height: 200px;
      top: 40%;
      right: 5%;
      animation: float 6s ease-in-out infinite;
    }
  }

  // 浮动图标 - 校园元素
  .floating-icon {
    position: fixed;
    font-size: 40px;
    color: rgba(255, 255, 255, 0.15);
    z-index: 1;
    animation: float-icon 12s ease-in-out infinite;

    &.icon-1 {
      top: 20%;
      left: 10%;
      animation-delay: 0s;
    }

    &.icon-2 {
      top: 60%;
      right: 8%;
      animation-delay: 4s;
    }

    &.icon-3 {
      bottom: 20%;
      left: 15%;
      animation-delay: 8s;
    }
  }

  // 登录包装器
  .login-wrapper {
    position: relative;
    z-index: 10;
    width: 100%;
    max-width: 1000px;
    padding: 20px;
  }

  // 登录卡片 - 玻璃拟态效果
  .login-card {
    display: flex;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 24px;
    box-shadow: $shadow, 0 0 0 1px rgba(255, 255, 255, 0.5);
    overflow: hidden;
    min-height: 600px;
    backdrop-filter: blur(20px);
  }

  // 左侧装饰区 - 校园风格渐变
  .login-left {
    flex: 1;
    background: linear-gradient(160deg, $primary-dark 0%, $primary-color 60%, $primary-light 100%);
    padding: 60px 40px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    color: $white;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 100%;
      height: 100%;
      background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 60%);
    }

    &::after {
      content: '';
      position: absolute;
      bottom: -30%;
      left: -30%;
      width: 80%;
      height: 80%;
      background: radial-gradient(circle, rgba(245, 158, 11, 0.2) 0%, transparent 60%);
    }

    .brand-section {
      position: relative;
      z-index: 1;

      .logo {
        width: 80px;
        height: 80px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 28px;
        font-size: 40px;
        backdrop-filter: blur(10px);
        border: 2px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.05) rotate(5deg);
        }
      }

      .brand-title {
        font-size: 32px;
        font-weight: 800;
        margin-bottom: 12px;
        letter-spacing: 2px;
        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
      }

      .brand-subtitle {
        font-size: 15px;
        opacity: 0.95;
        font-weight: 400;
        letter-spacing: 1px;
      }
    }

    .features-list {
      position: relative;
      z-index: 1;
      margin: 40px 0;

      .feature-item {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        font-size: 15px;
        font-weight: 500;
        transition: transform 0.3s ease;

        &:hover {
          transform: translateX(5px);
        }

        i {
          width: 28px;
          height: 28px;
          background: rgba(255, 255, 255, 0.25);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 14px;
          font-size: 14px;
          font-weight: bold;
        }
      }
    }

    .time-display {
      position: relative;
      z-index: 1;
      display: flex;
      align-items: center;
      font-size: 14px;
      opacity: 0.9;
      padding-top: 24px;
      border-top: 1px solid rgba(255, 255, 255, 0.2);
      font-family: 'Courier New', monospace;

      i {
        margin-right: 10px;
        font-size: 16px;
      }
    }
  }

  // 右侧表单区
  .login-right {
    flex: 1;
    padding: 60px 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .form-header {
      text-align: center;
      margin-bottom: 40px;

      .form-title {
        font-size: 32px;
        font-weight: 800;
        color: $text-primary;
        margin-bottom: 10px;
        background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .form-subtitle {
        font-size: 15px;
        color: $text-secondary;
        font-weight: 400;
      }
    }

    .login-form {
      .el-form-item {
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }
      }

      // 输入框样式 - 现代圆润风格
      :deep(.el-input__inner) {
        height: 50px;
        border-radius: 12px;
        border: 2px solid #e5e7eb;
        padding-left: 45px;
        font-size: 15px;
        transition: all 0.3s ease;
        background: #fafafa;

        &:hover {
          border-color: $primary-light;
          background: #fff;
        }

        &:focus {
          border-color: $primary-color;
          background: #fff;
          box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.1);
        }

        &::placeholder {
          color: #9ca3af;
        }
      }

      :deep(.el-input__prefix) {
        left: 15px;
        color: $primary-color;
        font-size: 18px;
      }

      :deep(.el-input__icon) {
        line-height: 50px;
      }

      // 验证码行
      .code-row {
        display: flex;
        gap: 12px;

        .code-input {
          flex: 1;
        }

        .code-box {
          width: 120px;
          height: 50px;
          background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          user-select: none;
          border: 2px solid #a7f3d0;
          transition: all 0.3s ease;

          &:hover {
            border-color: $primary-color;
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
          }

          &:active {
            transform: scale(0.98);
          }

          .code-text {
            font-size: 20px;
            font-weight: 800;
            color: $primary-dark;
            letter-spacing: 6px;
            font-family: 'Courier New', monospace;
            text-shadow: 0 2px 4px rgba(0,0,0,0.05);
          }
        }
      }

      // 角色选择 - 胶囊样式
      .role-item {
        text-align: center;
        margin-bottom: 32px;

        :deep(.el-radio-group) {
          .el-radio-button__inner {
            padding: 12px 36px;
            font-size: 15px;
            font-weight: 600;
            border-radius: 25px;
            border: 2px solid #e5e7eb;
            background: #fff;
            color: $text-secondary;
            transition: all 0.3s ease;

            &:hover {
              color: $primary-color;
              border-color: $primary-light;
            }
          }

          .el-radio-button__orig-radio:checked + .el-radio-button__inner {
            background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
            border-color: $primary-color;
            color: #fff;
            box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
          }

          .el-radio-button:first-child .el-radio-button__inner {
            border-radius: 25px 0 0 25px;
          }

          .el-radio-button:last-child .el-radio-button__inner {
            border-radius: 0 25px 25px 0;
          }
        }
      }
    }

    // 操作按钮 - 现代渐变按钮
    .form-actions {
      display: flex;
      gap: 16px;
      margin-top: 10px;

      .btn-login,
      .btn-register {
        flex: 1;
        height: 52px;
        border-radius: 12px;
        font-size: 16px;
        font-weight: 600;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        transition: all 0.3s ease;
        border: none;

        i {
          font-size: 18px;
          transition: transform 0.3s ease;
        }
      }

      .btn-login {
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        color: #fff;
        box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);

          i {
            transform: translateX(3px);
          }
        }

        &:active {
          transform: translateY(0);
        }
      }

      .btn-register {
        background: #f3f4f6;
        color: $text-secondary;
        border: 2px solid #e5e7eb;

        &:hover {
          background: #fff;
          border-color: $primary-color;
          color: $primary-color;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
      }
    }

    // 底部链接
    .form-footer {
      text-align: center;
      margin-top: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;

      .link-forgot,
      .link-help {
        color: $text-secondary;
        font-size: 14px;
        text-decoration: none;
        font-weight: 500;
        transition: all 0.3s ease;
        position: relative;

        &:hover {
          color: $primary-color;
        }

        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 0;
          height: 2px;
          background: $primary-color;
          transition: width 0.3s ease;
        }

        &:hover::after {
          width: 100%;
        }
      }

      .divider {
        color: #d1d5db;
        font-weight: 300;
      }
    }
  }
}

// 动画
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(5deg);
  }
}

@keyframes float-icon {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.15;
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
    opacity: 0.25;
  }
}

// 响应式适配
@media (max-width: 768px) {
  .login-container {
    background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);

    .login-card {
      flex-direction: column;
      background: rgba(255, 255, 255, 0.98);
      margin: 10px;
    }

    .login-left {
      padding: 30px;
      min-height: 180px;
      text-align: center;

      .brand-section {
        .logo {
          margin: 0 auto 20px;
          width: 60px;
          height: 60px;
          font-size: 30px;
        }

        .brand-title {
          font-size: 24px;
        }
      }

      .features-list,
      .time-display {
        display: none;
      }
    }

    .login-right {
      padding: 30px 25px;

      .form-header {
        margin-bottom: 30px;

        .form-title {
          font-size: 26px;
        }
      }
    }

    .decoration-circle,
    .floating-icon {
      display: none;
    }
  }
}
</style>
