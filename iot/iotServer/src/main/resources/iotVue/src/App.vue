<template>
  <div id="app">
	<div class="left-menu" v-if="index&&index!='/'&&index!='/login'">
	  <div style="background: #545c64;">
			<img src="../public/qfedu.png" width="120"
			style="margin-top: 9px; margin-left: 15px;"/>
		</div>
		<el-menu
		:default-active="index"
		:collapse="collapse"
		:style="{width:collapse?'auto':'200px'}"
		text-color="white" router
		unique-opened
		background-color="#545c64">
		<el-submenu index="折叠" 
			@click.native="toggleCollapse">
		  <template slot="title">
			<i class="el-icon-s-operation"></i>
		    <span>折叠</span>
		  </template>
		  <el-menu-item v-if="collapse"
		    @click="toggleCollapse">
		    <i class="el-icon-s-operation"></i>
		    <span>展开</span>
		  </el-menu-item>
		</el-submenu>

		<!-- 实例详情 -->
		<el-menu-item index="实例详情" 
			@click.native="toDetail()"
				v-if="status=='租户'">
        <i class="el-icon-setting"></i>
        <span slot="title">实例详情</span>
			</el-menu-item>

	    <el-submenu v-for="item in menus" :index="item.index" :key="item.index">
		  <template slot="title">
		    <i :class="'el-icon-'+item.icon"></i>
		    <span>{{item.index}}</span>
		  </template>
		  <el-menu-item :index="bean.path"
			v-if="!(bean.have)||(bean.have).indexOf(status)!=-1"
			v-for="bean in item.children">
			<i class="el-icon-menu"></i>
		    <span>{{bean.menu}}</span>
		  </el-menu-item>
	    </el-submenu>
	  </el-menu>
	</div>
	
	<router-view style="height: 100%; overflow: auto;">
	</router-view>
	
	<el-button class="logout"
	  v-if="index&&index!='/'&&index!='/login'"
	  plain type="primary" @click="logout">退出登录</el-button>
	<span class="welcome" 
	  v-if="index&&index!='/'&&index!='/login'">欢迎{{welcome}}</span>
  </div>
</template>

<script>
export default {
	data(){return{
		collapse: false,
		welcome: '',
		menus: [
			{ index: '设备管理', icon: 'platform-eleme', children: [
				{ path: '/user', menu: '租户管理', have: ['管理员'] },
				{ path: '/city', menu: '行政区管理', have: ['管理员'] },
				{ path: '/category', menu: '设备类型', have: ['管理员'] },
				{ path: '/product', menu: '产品管理', have: ['管理员'] },
				{ path: '/weather', menu: '天气', have: ['租户'] },
				{ path: '/pro', menu: '产品', have: ['租户'] },
				{ path: '/device', menu: '设备',  },
        { path: '/group', menu: '分组', have: ['租户'] },
				{ path: '/industry', menu: '行业',},
			]},
		],
		index: '',
		status: '',
	}},
	watch: { // 监听路由变化
		$route(to, from) {
			this.index = to.path;
			localStorage.setItem('index', this.index);
			this.isLogin();
		}
	},
	mounted(){
		this.collapse = JSON.parse(sessionStorage.getItem('collapse'));
		this.index = localStorage.getItem('index');
		if (this.isLogin()) {
			this.$router.push('/login');
		}
	},
	methods: {
		toggleCollapse() {
			this.collapse = !this.collapse;
			sessionStorage.setItem('collapse', this.collapse);
		},
		isLogin() {
			this.status = localStorage.getItem('status');
			if (this.status) {
				this.welcome = this.status + localStorage.getItem('user');
				return false;
			}
			return true;
		},
		logout() {
			localStorage.removeItem('status');
			localStorage.removeItem('user');
			this.$router.push('/login');
		},
		toDetail() {
			var devip = localStorage.getItem('devip')
			var msg = '第1次请先从 [设备管理]->[产品] 进入实例详情'
			if (!devip) return alert(msg)
			this.$router.push('/detail')
		}
	}
}
</script>

<style>
html,body,#app{
	padding:0;
	margin: 0;
	height: 100%;
	overflow: hidden;
}
.left-menu{
	float: left;
	height:100%;
}
.el-menu{
	height: 100%;
	border-right: none;
}
.el-menu-item.is-active{
	background-color: rgba(0,0,0,0.1)!important;
}
.logout{
	position: fixed;
	right: 15px;
	top: 5px;
}
.welcome{
	line-height: 49px;
	position: fixed;
	right: 125px;
	top: 0;
	pointer-events: none;
}
</style>