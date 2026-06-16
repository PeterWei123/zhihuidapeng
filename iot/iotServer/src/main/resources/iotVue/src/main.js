import Vue from 'vue'
import Router from 'vue-router'//路由
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue'
import Table from './components/Table.vue';
import Bg from './components/Bg.vue';
// 轮播 + echarts
import 'swiper/dist/css/swiper.min.css'
import Swiper from './components/Swiper.vue';
import * as echarts from 'echarts'
Vue.prototype.$echarts = echarts;

var baseURL = 'http://localhost:8080';
try {
	const axios = require('axios/dist/axios.js');
	axios.defaults.baseURL = baseURL;
	Vue.prototype.$http = axios;
	console.log('http请求库----axios');
} catch (e) {
	console.log('http请求Api----fetch');
	Vue.prototype.$http = new Http(baseURL);
	Vue.prototype.$http.defaults.baseURL = baseURL;
}
Vue.prototype.$host = baseURL;

// 二次封装$http
Vue.prototype.$httpUrl = async(url)=>
	(await Vue.prototype.$http.get(baseURL+url)).data
try { // 在Vue2中引入JS运动框架，封装JS动画函数
	var {startMove} = require('./components/move.js')
	Vue.prototype.$myAnim = (obj, param, nowrap)=>
	new Promise(resolve=>startMove(obj,param,()=>resolve(),nowrap))
} catch(e){ }

// 提交数据 - 时间；获得数据 + 时间；
Vue.prototype.$formatTime = (datetime,type)=>{
	var time= new Date(datetime).getTime();
	time = type== '-' ? time-8*60*60*1000 : time+8*60*60*1000;
	var date= new Date(time);
	var year= date.getFullYear();
	var mth = date.getMonth()+ 1;
	var day = date.getDate();
	var hour= date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	return {year,mth,day,hour,min,sec};
}

Vue.use(Router) // 注入到Vue中
Vue.use(ElementUI)
Vue.config.productionTip = false
Vue.component('Table',Table)//注册全局组件
Vue.component('Bg', Bg); // 注册背景图组件
Vue.component('Swiper', Swiper)//注册Swiper

const routes = []; // 路由配置表

require
  .context('./views', true, /\.vue$/)
  .keys()
  .map((item) => {
    var path = item.slice(1).replace('.vue', '');
    path = path.replace(path[0],path[0].toLowerCase());
    routes.push({
      path,
      name: path.slice(1),
      component: () => import(`./views${item.slice(1)}`),
    });
  });
// index页面注册为 登录页面
routes.unshift({
	path: '/', name: 'index', redirect: '/login'
});

// vue路由守卫----登录拦截器
const router = new Router({ routes });
router.beforeEach((to,from,next)=>{
	if (to.path == '/login') {
		return next();
	}
	if (!localStorage.getItem('user')) {
		return next('/login');
	}
	next();
});

new Vue({
  router: router,
  render: h => h(App),
}).$mount('#app')



// 封装了fetch
function Http(host) {
	this.defaults = {};
	this.get = function(url, json) {
		if (json) {
		  var {params} = json, kv = [];
		  for (var i in params)
			if (params[i])
			  kv.push(i+'='+params[i]);
			url += '?' + kv.join('&');
		}
		return fetch(host+url).then(resp=>resp.json()).then(data=>{return {data}}).catch(e=>{});
	};
	this.post= function(url,requestBody){
		var params = {
			method:'POST',
			body: JSON.stringify(requestBody),
			headers:{'Content-Type':'application/json'},
		};
		return fetch(host+url, params).then(resp=>resp.text()).then(data=>{
			try{data=JSON.parse(data)}
			catch(e){ }
			return {data};
		});
	};
}