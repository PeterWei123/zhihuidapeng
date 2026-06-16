<template>
  <div>
	<div style="padding: 5px;">
	  <el-button type="danger" @click="quit()" v-if="confirm">放弃</el-button>
	  <el-button type="primary" @click="save()" v-if="confirm">保存</el-button>
	  <el-button type="success" @click="add()" v-if="!confirm&&headhide!=1&&headhide!=12&&headhide!=123&&headhide!=13">添加</el-button>
	  <el-button type="danger" @click="del()" v-if="!confirm&&headhide!=2&&headhide!=12&&headhide!=23&&headhide!=123">删除</el-button>
	  <el-button plain type="primary" @click="update()" v-if="!confirm&&headhide!=3&&headhide!=23&&headhide!=123&&headhide!=13">修改</el-button>
	  <slot v-if="!confirm"></slot>
	</div>
	<el-table ref="singleTable" :data="data"
		highlight-current-row
	  :height="height?height:undefined"
	  :row-class-name="rowIndex"
	  :header-cell-style="{
	   borderTop:'1px solid #ebeef5',
	  background:'#F7F7F7'}"
	  style="margin-bottom: 20px;"
	  @current-change="record">
	  <el-table-column type="index"/>
	  <el-table-column style="position: relative;"
		:prop="key" :label="value0(value)" 
		:width="key.indexOf('time')!=-1||value2(value)=='file'||value3(value)?160:null"
		v-if="i>1" v-for="(value,key,i) in util" :key="key">
	    <template scope="scope">
		  <Bg style="cursor: pointer;" class="form-avatar avatar" 
			onerror="/onfail.png" 
			@dblclick.native="edit(scope.row, scope.$index)" 
			v-if="!scope.row.input&&value2(value)&&value2(value)=='file'"
		  	:src="host+scope.row[key]"></Bg>
		  <div style="cursor: pointer;" 
			@dblclick="edit(scope.row, scope.$index)" 
			v-else-if="!scope.row.input">
			<span v-if="value==='密码'">****</span>
			<span v-else>{{scope.row[key]}}</span>
			<span v-if="scope.row[key]!=undefined" v-text="value1(value)"></span>
		  </div>
		  <a :href="host+scope.row[key]"  target="_blank"
			  v-if="value2(value)&&value2(value)=='file'"
			:style="{color:'#606266',whiteSpace:'nowrap',
				cursor:'pointer'}">{{scope.row[key]}}</a>
		  <el-input-number class="form-item" size="small"
			v-if="scope.row.input&&value1(value)" :min="0"
			v-model="scope.row[key]">
		  </el-input-number>
		  <el-select class="form-item" size="small" clearable
			@change="optCheck($event, value2(value))" filterable
			v-if="scope.row.input&&value2(value)&&value2(value)!='file'"
			v-model="scope.row[key]">
			<el-option v-for="item in getOpt(value,key)"
			:key="item.value" :label="item.label" :value="item.value">
			</el-option>
		  </el-select>
		  <el-upload class="avatar-uploader form-avatar"
		    :action="host+util.httpUrl+'/'+value2(value)+'?column='+key"
			:on-success="uploadSuccess" :show-file-list="false"
			v-if="scope.row.input&&value2(value)&&value2(value)=='file'">
		    <Bg class="avatar" onerror="/onfail.png" 
				v-if="scope.row[key]" :src="host+scope.row[key]"></Bg>
			<i v-else class="el-icon-plus avatar-uploader-icon"></i>
		  </el-upload>
		  <el-date-picker class="form-item" size="small" 
			v-if="scope.row.input&&value3(value)"
			v-model="scope.row[key]" placeholder="请选择"
			:type="value3(value)=='时间'?'datetime':'date'"
			:value-format="value3(value)=='时间'?'yyyy-MM-dd HH:mm:ss':'yyyy-MM-dd'">
		  </el-date-picker>
		  <el-input class="form-item" size="small" 
			v-if="scope.row.input&&!value1(value)&&!value2(value)&&!value3(value)"
			v-model="scope.row[key]" :disabled="key=='id'||valTxt(value)==-1"
			v-show="openBtn||valTxt(value)!=-10"
			:maxlength="valTxt(value)?valTxt(value):50">
		  </el-input>
	    </template>
	  </el-table-column>
	</el-table>
	<el-pagination v-if="count!=0" :total="count"
	  style="text-align: center; margin-bottom: 20px;"
	  @current-change="changePage" :current-page="page"
	  background layout="prev, pager, next">
	</el-pagination>
  </div>
</template>

<script>
export default { // :height="525"
	props: ['util','curpage','headhide','default','height','auth'],
	data() {return{
		// 添加按钮0 修改1
		openBtn: 0,
		total: 0,
		count: 0,
		page: this.curpage,
		row: null,
		index: 0,
		confirm:false,
		data: [],
		parent: {},
		host: this.$http.defaults.baseURL,
		timeReg:"^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$",
	}},
	mounted(){
		this.list();
		this.option();
	},
	methods: {
		formatTime(datetime,type) {
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
		},
		formatDate(datetime,type, value){
			var {year,mth,day,hour,min,sec} = this.formatTime(datetime,type);
			return this.value3(value)=='日期'?year+'-'+mth+'-'+day:
					year+'-'+mth+'-'+day+' '+hour+':'+min+':'+sec;
		},
		changePage(page) {
			this.page = page;
			this.list();
		},
		rowIndex({ row, rowIndex }) {
		    row.index = rowIndex;
		},
		optCheck(value, key) {
			if (key.indexOf('?') != -1) {
				key = key.split('?')[0];
			}
			this.row[key] = value;
		},
		value0(v){
			return v.split('/')[0];
		},
		valTxt(v){
			return v.split('/')[1];
		},
		value1(v){
			v = v.split('/')[1];
			if (!isNaN(v)) {
				return null;
			}
			return v;
		},
		value2(v){
			return v.split('/')[2];
		},
		value3(v){
			return v.split('/')[3];
		},
		option() {
			var cache = [];
			for(var key in this.util) {
			if (this.util[key].indexOf('//') != -1 &&
				this.util[key].indexOf('///')== -1 &&
				cache.indexOf(this.value2(this.util[key])) == -1 &&
				this.value2(this.util[key]) != 'file') {
				cache.push(this.value2(this.util[key]));
				var url = this.util.httpUrl +'/'+ this.value2(this.util[key]);
				this.$http.get(url).then(({data})=>{
					console.log('获取下拉选项--', data);
					for (var i in data) {
					if(data[i] && data[i].length!=0){
						this.parent[i] = data[i];
					}}
				});
			}}
		},
		getOpt(value, key) {
			if (value.indexOf('//') !=-1
			  &&value.indexOf('///')==-1){
				return this.parent[key];
			}
		},
		record(row) {
			if(!this.row && row.index) {
				this.index= row.index;
			}
			if(row && this.row && this.row.input) {
				var url = this.row.id?
					this.util.httpUrl+'/'+this.row.id:
					this.util.httpUrl;
				for (var i in this.row) {
					// if (new RegExp(this.timeReg).test(this.row[i])) {
					// 	this.row[i] = this.formatDate(this.row[i],'-',this.util[i]);
					// }
					if (this.row[i]&&this.util[i]&& 
						this.row[i].length <= 11 && 
						this.value3(this.util[i])=='日期'){
						this.row[i] += ' 0:0:0';
					}
				}
				this.$http.post(url, this.row).then(({data})=>{
					console.log('添加修改 '+ url);
					if (isNaN(data)) {
						this.$refs.singleTable.setCurrentRow(this.data[this.index]);
						this.alert(data, '提示');
					} else {
						this.list();
					}
				});
			}
			this.row = row;
		},
		update() {
			this.openBtn = 1;
			if (!this.row) {
				this.alert('请先选中一行数据！');
				return;
			}
			if (this.auth) {
			var key = Object.keys(this.auth)[0];
			if (!this.auth.btn || this.auth.btn == 3) {
			if (this.auth[key] && this.row[key] != this.auth[key]) {
				this.alert(this.auth.msg?this.auth.msg:'没有操作权限！');
				return;
			}}}
			this.confirm = true;
			this.$set(this.row, 'input', true);
		},
		save() {
			this.record(this.row);
		},
		edit(row,index) {
			this.openBtn = 1;
			if (this.auth) {
			var key = Object.keys(this.auth)[0];
			if (!this.auth.btn || this.auth.btn == 3) {
			if (this.auth[key] && row[key] != this.auth[key]) {
				this.alert(this.auth.msg?this.auth.msg:'没有操作权限！');
				return;
			}}}
			if (!this.headhide||(this.headhide+'').indexOf('3')==-1){
				this.index = index;
				this.confirm = true;
				this.$set(row, 'input', true);
			}
		},
		list() {
			var url = this.util.httpUrl;
			if (this.util.params) { // 没有考虑分页
				url += this.util.params;
			} else if (this.page) {
			  url += '/page'+this.page;
			  this.$http.get(this.util.httpUrl+'/count').then(({data})=>{
				this.count = data +1;
			})}
			this.$http.get(url).then(({data})=>{
				if (this.row) {
					this.confirm = false;
					this.$set(this.row,'input',false);
				}
				// for (var row of data){
				// 	for (var i in row) {
				// 	if (new RegExp(this.timeReg).test(row[i])) {
				// 		row[i] = this.formatDate(row[i],'+',this.util[i]);
				// 	}}
				// }
				this.data = data;
				if (this.data) {
					this.total = this.data.length;
				}
			});
		},
		add() {
			this.openBtn = 0;
			var i = 0;
			var row = {};
			for (var key in this.util) {
				if (i <= 1 || key == 'id' || 
				 this.valTxt(this.util[key])<0 ||
				  this.util[key].indexOf('///')!= -1 ||
				   this.util[key].indexOf('//')!= -1) {
					i++;
					continue;
				}
				if (this.util[key].indexOf('/') != -1 &&
					isNaN(this.value1(this.util[key]))) {
					row[key] = 0;
				} else {
					row[key] = this.value0(this.util[key]);
				}
				i++;
			}
			this.confirm = true;
			this.$set(row, 'input', true);
			if (this.default) {
				for (var key of Object.keys(this.default)) {
					this.$set(row, key, this.default[key]);
				}
			}
			this.data.unshift(row);
			this.index = 0;
			this.$refs.singleTable.setCurrentRow(this.data[this.index]);
		},
		del() {
			if (!this.row) {
				this.alert('请先选中一行数据！');
				return;
			}
			if (this.auth) {
			var key = Object.keys(this.auth)[0];
			if (!this.auth.btn || this.auth.btn == 2) {
			if (this.auth[key] && this.row[key] != this.auth[key]) {
				this.alert(this.auth.msg?this.auth.msg:'没有操作权限！');
				return;
			}}}
			var url = this.util.httpUrl+'/'+this.row.id;
			this.$http.get(url).then(() => {
				console.log('数据删除 '+ url);
				this.list();
			});
		},
		quit() {
			this.list();
		},
		uploadSuccess(url) {
			var kw = '?column=';
			this.$set(this.row, url.split(kw)[1], url.split(kw)[0]);
		},
		// 外部调用函数
		checkRowCheck() {
			if (!this.row){
				this.alert('请先选中一行数据！');
				return null;
			}
			return this.row;
		},
		resetPage() {
			this.page = 1;
			this.count = 10;
		},
		alert(msg,title,cancel) {
			if (title) {
				this.$confirm(msg, title, {
					closeOnClickModal: false,
					confirmButtonText:'继续编辑',
					cancelButtonText: cancel,
					showClose: false,
					type: 'warning',
				}).catch(() => {
					this.list();
				});
			} else {
				this.$alert(msg,'提示').catch(()=>{});
			}
		},
	}
}
</script>

<style scoped>
.form-item{
	position: absolute;
	width: 95%;
	top: 8px;
	left: 0;
}
.form-avatar{
	position: absolute;
	width: 95%;
	left: 0;
	top: 50%;
	transform: translateY(-21px);
}
.avatar {
	width: 42px;
	height: 42px;
}
.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 42px;
	height: 42px;
	line-height: 42px;
	text-align: center;
}
</style>