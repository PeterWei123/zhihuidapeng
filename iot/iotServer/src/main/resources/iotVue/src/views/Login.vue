<template>
  <div class="login-container">
    <el-card class="login-card">
      <h3 class="login-title">智慧物联系统登录/注册</h3>
      <el-form :model="form" label-width="40px" 
        @keyup.enter.native="login()"
        class="login-form">
        <el-form-item label="账号">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item label="手机" v-if="regist">
          <el-input v-model="form.mobile"></el-input>
        </el-form-item>
        <el-form-item style="text-align: right; margin-top: 35px;">
          <el-button type="danger" @click="register()">注册</el-button>
          <el-button type="primary" @click="login()">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data(){return{
    form: {
	  username:'',
	  password:'',
    mobile: '',
	},
	regist: false,
  }},
  methods: {
    login(){
      this.$http.get('/user/login', {params: this.form}).then(({data})=>{
        if (data.code == 1) {
	        localStorage.setItem('uid', data.uid);
	        localStorage.setItem('user', this.form.username);
	        localStorage.setItem('status', data.status);
	        this.$router.push('/industry');
        } else {
	        alert('用户名或密码错误');
        }
      });
    },
	register() {
	if (!this.regist) {
		this.regist = true;
	} else {
		this.$http.post('/user', this.form).then(({data})=>{
			if (data.code == 0) {
				alert(data.msg);
			} else {
				alert('注册成功请登录');
				this.regist = false;
			}
		});
	}},
  }
}
</script>

<style scoped>
.login-container {
  background-image: url('../../public/bg.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  height: 100vh;
  justify-content: right;
}
.login-card {
  width: 400px;
}
.login-form {
  margin-top: 40px;
}
.login-title {
  margin-top: 70px;
  font-size: 24px;
  text-align: center;
}
</style>