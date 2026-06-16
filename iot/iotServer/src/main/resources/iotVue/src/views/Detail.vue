<template>
  <div style="position: relative;">
    <Table ref="table" :util="{
      httpUrl: '/device',
      params: '/device?pid='+pid,
      device: '设备名称',
      idcard: '设备唯一标识',
      category: '设备类型//cid?key=category',
      product: '所属产品/-1'
    }" :default="{ pid }">
      <el-button type="warning">{{ product }}网关 {{ devip }}</el-button>
      <el-button plain type="success" v-if="auto"
        @click="myclick('自动手动')">{{auto}}</el-button>
      <el-button type="primary" @click="openClose('open')">开开</el-button>
      <el-button type="danger" @click="openClose('close')">关闭</el-button>
      <div class="group">
        <div :class="value=='已打开'?
          'cell el-button cellPadLeft0 active':
          'cell el-button cellPadLeft0'" 
          @click="myclick(key,value)" 
          v-for="(value,key,i) in sensor">
          <div class="item">
            <div class="key">{{ key }}</div>
            <div class="value">{{value}}</div>
          </div>
        </div>
      </div>
    </Table>
    <div id="main" class="main"></div>
    <div id="main2" class="main"></div>
    <div class="city">
      <span>地址：</span>
      <span style="color: grey;">{{city}}</span>
    </div>
  </div>
</template>

<script>
export default {
  data() {return {
    devip: this.$route.query.devip||localStorage.getItem('devip'),
    pid: this.$route.query.pid||localStorage.getItem('pid'),
    product: this.$route.query.product||localStorage.getItem('product'),
    city: this.$route.query.city||localStorage.getItem('city'),
    auto: '', // 自动手动
    sensor: [],
    timelist: [],
    templist: [],
    option: null,
    option2:null,
    myChart:null,
    myChart2:null
  }},
  mounted() {
    this.myChart = this.$echarts.init(main);
    this.myChart2 = this.$echarts.init(main2);
    this.option = {
      xAxis: {
        type: 'category',
        data: ['气温变化']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [],
          type: 'line',
          label: {
            show: true, // 显示标签
            position: 'top', // 标签的位置，这里设置为顶部
            formatter: '{c}℃' // {c}表示数据值，即显示数据点的数值
          }
        }
      ]
    };
    this.option2 = {
      tooltip: {
        formatter: '{a} <br/>{b} : {c}%'
      },
      series: [
        {
          name: 'Pressure',
          type: 'gauge',
          progress: {
            show: true
          },
          detail: {
            valueAnimation: true,
            formatter: '{value}'
          },
          data: [
            {
              value: 50,
              name: '空气湿度(%)'
            }
          ]
        }
      ]
    };
    this.myChart.setOption(this.option);
    // this.myChart2.setOption(this.option2);
    // WebSocket
    new WebSocket('ws://localhost:8080/websocket').onmessage = resp=>{
      var data = JSON.parse(resp.data)
      // if (!data['光照强度']) return //和emqx通信分开，目前emqx没用websocket
      if (this.checkDevip(data.devip)) {
        delete data.devip
        this.auto = data['自动手动']
        if (this.timelist.length == 5) {
          this.timelist.shift()
          this.templist.shift()
        }
        this.timelist.push(data['更新时间'])
        this.templist.push(data['大棚室温'].replace('℃',''))
        this.option.xAxis.data = this.timelist
        this.option.series[0].data = this.templist
        this.myChart.setOption(this.option);
        this.option2.series[0].data[0].value = data['空气湿度'].replace('%','')
        this.myChart2.setOption(this.option2)
        this.sensor = data // 更新九宫格
      } else {
        // alert('控制机IP有变，请先从 [设备管理]->[产品] 进入实例详情')
      }
    }
  },
  methods: { // 校验设备devip和websocket发过来的devip
    checkDevip(devip) {
      devip = devip.substring(0, devip.length-5)
      return this.devip.indexOf(devip) != -1
    },
    async myclick(key, value){
      if (key == '自动手动') {
        await this.$http.get(this.devip+'/api/ledAuto/随便一个参数')
        alert('切换成功，过一会儿即可看到变化')
      } else if (value == '已关闭' || value == '已打开') {
        for (var item of this.$refs.table.data) {
          if (item.category.indexOf(key) != -1) {
            var option = value == '已关闭' ? 'open' : 'close'
            this.openClose(option, item)
            break
          }
        }
      }
    },
    async openClose(option, item) {
      var row = this.$refs.table.row
      if (item) row = item
      if (!row) return alert('请先选择一行数据!')
      var category = row.category.split('(')[0].trim()
      await this.$http.get(this.devip+'/api/gpio/'+option+'/'+row.pin)
      // 让界面实时更新
      this.sensor[category] = option == 'open' ? '已打开' : '已关闭'
    }
  }
}
</script>

<style scoped>
.main{
  width: 450px;
  height: 300px;
}
#main{
  position: absolute;
  right: 0;
  bottom: 0;
}
#main2{
  position: absolute;
  left: 0;
  bottom: 0;
  transform: scale(0.9);
  margin-bottom: -10px;
}
.group{
  overflow: hidden;
}
.cellPadLeft0{
  padding-left: 0;
}
.cell{
  float: left;
  width: 9.5%;
  cursor: pointer;
  margin-left: 0;
  margin-top: 5px;
  margin-right: 5px;
  border-radius: 5px;
  border: 1px solid #ddd;
}
.item{
  padding: 15px;
}
.key{
  font-size: 14px;
}
.value{
  font-size: 20px;
  margin-top: 8px;
}
.active{
  background: #F1F9EA;
  color: black;
}
.city{
  position: absolute;
  right: 43px;
  bottom: 18px;
}
</style>