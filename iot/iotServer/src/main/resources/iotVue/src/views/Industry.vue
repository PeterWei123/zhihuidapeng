<template>
  <Table :util="{
    httpUrl: '/industry',
    id: '主键',
    industry: '所属行业',
    title: '标题',
    content: '介绍内容',
    logo: '宣传图//file',
    ctime: '发布时间/-1',  //  /-1这个不是单位，是不允许修改这一列
  }":headhide="headhide">
    <el-button type="primary" plain
               onclick="location.reload()"
               v-if="status==='租户'">刷新</el-button>
  </Table>
</template>

<script>
export default {
  data(){
    return{
      headhide:'',
      status:localStorage.getItem('status'),
    }
  },
  mounted() {//当前页面的主函数
    //==是值判断
    //===是值+类型都判断
    if(this.status==='租户'){
      this.headhide=123
    }
  }
}

</script>