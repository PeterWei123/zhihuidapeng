<template>
  <div>
    <div class="headline">设备接入流程概览</div>
    <div class="group">
      <div class="cell" v-for="(item,i) in steps">
        <div class="left">0{{ i+1 }}</div>
        <div class="right">
          <div class="title">{{ item[0] }}</div>
          <div class="content">{{ item[1] }}</div>
        </div>
      </div>
    </div>
    <Table ref="table" :util="{
  httpUrl:'/product',
  params:'/pro?uid='+uid,//二级地址？uid=数字
  //id:'主键',
  product:'产品名称',
  //   //外键地址？key=视图中外键对应的属性
  city:'所属行政区//cid?key=city',
 // user:'所属租户//uid?key=user',
  devip:'设备IP（pythonIP）',
  ctime:'创建时间/-1',//-1不允许修改
}":default="{ uid }">
      <el-button type="primary"@click="detail()">实例详情</el-button>
    </Table>
  </div>
</template>
<script>
export default {
  methods: { // 定义函数
    async detail() { // ref="table" 找到这个标签 <Table>
      var row = this.$refs.table.row
      if (!row) { // false null  true !null
        return alert('请先选择一行数据!')
      }
      if (!row.devip) {
        return alert('请先设置物联网设备ip地址!')
      }
      var result = await this.$httpUrl('/api/javaip?devip='+row.devip )
      if (result === '成功') {
        localStorage.setItem('devip', row.devip)
        localStorage.setItem('pid', row.id)
        localStorage.setItem('product', row.product)
        localStorage.setItem('city', row.city)
        this.$router.push({ // vue路由机制访问其他网页
          path: '/detail', // 访问Detail.vue
          query: { // 带参数过去
            devip: row.devip,
            pid: row.id, // 特殊之处
            product: row.product,
            city: row.city,
          }
        })
      } else {
        alert(result)
      }
    }
  },
  data(){//定义变量
    return{
      uid:localStorage.getItem('uid'),//从浏览器内部取出uid
      steps: [['创建产品', '产品是设备的容器'],
        ['创建设备', '添加自己需要购买的设备组装产品'],
        ['设备端控制', '从产品进入设备端控制'],
        ['查看上报数据', '传感器数据会实时上传到产品设备端']
      ]
    }
  }
}
</script>
<style scoped>
.headline{
  font-size: 19px;
  font-style: italic;
  line-height: 40px;
  padding: 5px;
}
.group{
  overflow: hidden;
  padding: 5px;
  margin-bottom: 5px;
}
.cell{
  overflow: hidden;
  width: 200px;
  height: 60px;
  float: left;
  margin-right: 20px;
}
.left{
  float: left;
  font-size: 24px;
  line-height: 35px;
  width: 40px;
  color: #409EFF;
  text-align: center;
  font-style: italic;
  padding-right: 5px;
  padding-left: 5px;
}
.right{
  overflow: hidden;
}
.title{
  font-size: 16px;
  font-style: italic;
}
.content{
  margin-top: 3px;
  font-size: 14px;
}
</style>