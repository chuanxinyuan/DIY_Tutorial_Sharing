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
              <i class="el-icon-s-promotion"></i>
            </div>
            <h2 class="brand-title">旧物改造教程</h2>
            <p class="brand-subtitle">动手改造 · 让旧物焕发新生</p>
          </div>

          <div class="features-list">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>海量创意教程</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>分享改造心得</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>材料包便捷购买</span>
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
            <p class="form-subtitle">登录您的账号，开始改造之旅</p>
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
                  placeholder="请输入用户名"
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
              <el-button
                  class="btn-register"
                  @click="goRegister"
              >
                <i class="el-icon-plus"></i>
                <span>注册账号</span>
              </el-button>
            </div>
          </el-form>

          <!-- 底部链接 -->
          <div class="form-footer">
            <a href="javascript:;" class="link-help" @click="goHome">返回首页</a>
          </div>
        </div>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>
    <div class="decoration-circle circle-3"></div>
    <div class="floating-icon icon-1"><i class="el-icon-scissors"></i></div>
    <div class="floating-icon icon-2"><i class="el-icon-s-operation"></i></div>
    <div class="floating-icon icon-3"><i class="el-icon-brush"></i></div>
  </div>
</template>

<script>
import Axios from 'axios'

export default {
  name: 'authLogin',
  data() {
    return {
      submit: {
        loading: false,
      },
      ruleForm: {
        username: '',
        password: '',
        code: '',
      },
      time: {
        txt: '',
        fun: null,
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ required: true, trigger: 'blur', validator: this.codeValidator }],
      },
      code: '',
    };
  },
  created() {
    this.initTime();
    this.initCode();
    document.body.style.overflow = 'hidden';
  },
  methods: {
    codeValidator(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入验证码'))
      } else {
        if (value.trim() != this.code) {
          callback(new Error('验证码错误'))
        } else {
          callback()
        }
      }
    },
    goRegister() {
      this.$router.push({ name: 'authRegister' })
    },
    goHome() {
      this.$router.push({ name: 'TutorialHomeV2' })
    },
    initTime() {
      this.time.txt = this.formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS');
      this.time.fun = setInterval(() => {
        this.time.txt = this.formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS');
      }, 1000);
    },
    formatDate(date, fmt) {
      const o = {
        'Y+': date.getFullYear(),
        'm+': date.getMonth() + 1,
        'd+': date.getDate(),
        'H+': date.getHours(),
        'M+': date.getMinutes(),
        'S+': date.getSeconds(),
      };
      for (let k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (o[k] + '').padStart(2, '0'));
        }
      }
      return fmt;
    },
    initCode() {
      this.code = Math.floor(Math.random() * (9999 - 1000)) + 1000;
    },
    changeCode() {
      this.initCode();
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.submit.loading = true;
          const payload = {
            username: this.ruleForm.username,
            password: this.ruleForm.password
          };
          Axios.post('/api/front/v2/auth/login', payload).then(res => {
            this.submit.loading = false;
            const data = res.data;
            if (data.code === '0') {
              const userData = data.data;
              if (!userData || userData.id == null) {
                this.$message.error('登录返回数据异常');
                return;
              }
              // 存入 localStorage（与 UserCenterV2 一致）
              localStorage.setItem('tp_user', JSON.stringify(userData));
              window.dispatchEvent(new Event('tp-user-changed'));
              this.$message.success('登录成功！欢迎回来');
              const redirect = this.$route.query.redirect || '/';
              this.$router.push(redirect);
            } else {
              this.$message.error(data.msg || '登录失败');
            }
          }).catch(() => {
            this.submit.loading = false;
            this.$message.error('网络错误，请稍后再试');
          })
        }
      })
    },
  },
  destroyed() {
    clearInterval(this.time.fun);
    document.body.style.overflow = '';
  },
};
</script>

<style scoped lang="scss">
// 变量定义
$primary-color: #10b981;
$primary-light: #34d399;
$primary-dark: #059669;
$accent-color: #f59e0b;
$text-primary: #1f2937;
$text-secondary: #6b7280;
$white: #ffffff;
$shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);

.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  z-index: 999;

  .bg-animation {
    position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0;

    .bg-gradient {
      position: absolute; width: 100%; height: 100%;
      background: linear-gradient(135deg, #064e3b 0%, $primary-dark 50%, $primary-color 100%);
      opacity: 0.95;
    }

    .bg-pattern {
      position: absolute; width: 100%; height: 100%;
      background-image:
          radial-gradient(circle at 10% 20%, rgba(255,255,255,0.15) 0%, transparent 40%),
          radial-gradient(circle at 90% 80%, rgba(255,255,255,0.1) 0%, transparent 40%),
          radial-gradient(circle at 50% 50%, rgba(255,255,255,0.05) 0%, transparent 60%);
    }
  }

  .decoration-circle {
    position: fixed; border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    z-index: 1;

    &.circle-1 { width: 400px; height: 400px; top: -200px; right: -150px; animation: float 8s ease-in-out infinite; }
    &.circle-2 { width: 300px; height: 300px; bottom: -150px; left: -100px; animation: float 10s ease-in-out infinite reverse; }
    &.circle-3 { width: 200px; height: 200px; top: 40%; right: 5%; animation: float 6s ease-in-out infinite; }
  }

  .floating-icon {
    position: fixed; font-size: 40px; color: rgba(255, 255, 255, 0.15); z-index: 1;
    animation: float-icon 12s ease-in-out infinite;

    &.icon-1 { top: 20%; left: 10%; animation-delay: 0s; }
    &.icon-2 { top: 60%; right: 8%; animation-delay: 4s; }
    &.icon-3 { bottom: 20%; left: 15%; animation-delay: 8s; }
  }

  .login-wrapper {
    position: relative; z-index: 10; width: 100%; max-width: 1000px; padding: 20px;
    max-height: 100vh;
    overflow-y: auto;
  }

  .login-card {
    display: flex; background: rgba(255, 255, 255, 0.95);
    border-radius: 24px; box-shadow: $shadow; overflow: hidden;
    min-height: 600px; backdrop-filter: blur(20px);
  }

  .login-left {
    flex: 1;
    background: linear-gradient(160deg, $primary-dark 0%, $primary-color 60%, $primary-light 100%);
    padding: 60px 40px; display: flex; flex-direction: column; justify-content: space-between;
    color: $white; position: relative; overflow: hidden;

    &::before {
      content: ''; position: absolute; top: -50%; right: -50%; width: 100%; height: 100%;
      background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 60%);
    }

    .brand-section {
      position: relative; z-index: 1;

      .logo {
        width: 80px; height: 80px; background: rgba(255, 255, 255, 0.2);
        border-radius: 20px; display: flex; align-items: center; justify-content: center;
        margin-bottom: 28px; font-size: 40px; backdrop-filter: blur(10px);
        border: 2px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;

        &:hover { transform: scale(1.05) rotate(5deg); }
      }

      .brand-title { font-size: 32px; font-weight: 800; margin-bottom: 12px; letter-spacing: 2px; text-shadow: 0 2px 4px rgba(0,0,0,0.1); }
      .brand-subtitle { font-size: 15px; opacity: 0.95; font-weight: 400; letter-spacing: 1px; }
    }

    .features-list {
      position: relative; z-index: 1; margin: 40px 0;

      .feature-item {
        display: flex; align-items: center; margin-bottom: 20px; font-size: 15px; font-weight: 500;
        transition: transform 0.3s ease;

        &:hover { transform: translateX(5px); }

        i {
          width: 28px; height: 28px; background: rgba(255, 255, 255, 0.25);
          border-radius: 50%; display: flex; align-items: center; justify-content: center;
          margin-right: 14px; font-size: 14px; font-weight: bold;
        }
      }
    }

    .time-display {
      position: relative; z-index: 1; display: flex; align-items: center;
      font-size: 14px; opacity: 0.9; padding-top: 24px;
      border-top: 1px solid rgba(255, 255, 255, 0.2);
      font-family: 'Courier New', monospace;

      i { margin-right: 10px; font-size: 16px; }
    }
  }

  .login-right {
    flex: 1; padding: 60px 50px; display: flex; flex-direction: column; justify-content: center;

    .form-header {
      text-align: center; margin-bottom: 40px;

      .form-title {
        font-size: 32px; font-weight: 800; color: $text-primary; margin-bottom: 10px;
        background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);
        -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
      }

      .form-subtitle { font-size: 15px; color: $text-secondary; font-weight: 400; }
    }

    .login-form {
      .el-form-item { margin-bottom: 24px; }

      :deep(.el-input__inner) {
        height: 50px; border-radius: 12px; border: 2px solid #e5e7eb;
        padding-left: 45px; font-size: 15px; transition: all 0.3s ease; background: #fafafa;

        &:hover { border-color: $primary-light; background: #fff; }
        &:focus {
          border-color: $primary-color; background: #fff;
          box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.1);
        }
        &::placeholder { color: #9ca3af; }
      }

      :deep(.el-input__prefix) { left: 15px; color: $primary-color; font-size: 18px; }
      :deep(.el-input__icon) { line-height: 50px; }

      .code-row {
        display: flex; gap: 12px;

        .code-input { flex: 1; }

        .code-box {
          width: 120px; height: 50px;
          background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
          border-radius: 12px; display: flex; align-items: center; justify-content: center;
          cursor: pointer; user-select: none;
          border: 2px solid #a7f3d0; transition: all 0.3s ease;

          &:hover {
            border-color: $primary-color; transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
          }
          &:active { transform: scale(0.98); }

          .code-text {
            font-size: 20px; font-weight: 800; color: $primary-dark;
            letter-spacing: 6px; font-family: 'Courier New', monospace;
          }
        }
      }
    }

    .form-actions {
      display: flex; gap: 16px; margin-top: 10px;

      .btn-login, .btn-register {
        flex: 1; height: 52px; border-radius: 12px; font-size: 16px; font-weight: 600;
        display: flex; align-items: center; justify-content: center; gap: 8px;
        transition: all 0.3s ease; border: none;

        i { font-size: 18px; transition: transform 0.3s ease; }
      }

      .btn-login {
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        color: #fff; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);

        &:hover {
          transform: translateY(-2px); box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
          i { transform: translateX(3px); }
        }
        &:active { transform: translateY(0); }
      }

      .btn-register {
        background: #f3f4f6; color: $text-secondary; border: 2px solid #e5e7eb;

        &:hover { background: #fff; border-color: $primary-color; color: $primary-color; }
      }
    }

    .form-footer {
      text-align: center; margin-top: 28px;

      .link-help {
        color: $text-secondary; font-size: 14px; text-decoration: none; font-weight: 500;
        transition: all 0.3s ease;

        &:hover { color: $primary-color; }
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}

@keyframes float-icon {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.15; }
  50% { transform: translateY(-20px) rotate(10deg); opacity: 0.25; }
}

@media (max-width: 768px) {
  .login-container {
    overflow-y: auto;

    .login-card { flex-direction: column; min-height: auto; }
    .login-left {
      padding: 30px; min-height: 180px; text-align: center;

      .brand-section .logo { margin: 0 auto 20px; width: 60px; height: 60px; font-size: 30px; }
      .brand-section .brand-title { font-size: 24px; }
      .features-list, .time-display { display: none; }
    }
    .login-right { padding: 30px 25px; }
    .decoration-circle, .floating-icon { display: none; }
  }
}
</style>
