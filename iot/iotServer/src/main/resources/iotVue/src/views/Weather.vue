<template>
  <div style="padding: 20px">
    <!-- 新增下拉框 -->
    <select v-model="selectedCityCode" @change="getWeather">
      <option value="58362">上海</option>
      <option value="54857" selected>青岛</option>
      <option value="54511">北京</option>
    </select>
    <div id="main" style="width: 600px;height: 400px;"></div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedCityCode: "54857", // 默认选中青岛
      myChart: null // 保存echarts实例
    };
  },
  async mounted() {
    // 初始化echarts
    this.myChart = this.$echarts.init(document.getElementById('main'));
    await this.getWeather(); // 初始加载天气
  },
  methods: {
    async getWeather() {
      // 传递城市编码到后端
      var record = await this.$httpUrl(`/weather/today?code=${this.selectedCityCode}`);
      console.log(record);
      // 更新图表
      var option = {
        title: { text: '未来一周气温变化' },
        tooltip: { trigger: 'axis' },
        legend: {},
        toolbox: {
          show: true,
          feature: {
            dataZoom: { yAxisIndex: 'none' },
            dataView: { readOnly: false },
            magicType: { type: ['line', 'bar'] },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: [
          { type: 'category', boundaryGap: false, data: record.statusList },
          { type: 'category', boundaryGap: false, data: record.timeList }
        ],
        yAxis: {
          type: 'value',
          axisLabel: { formatter: '{value} °C' }
        },
        series: [
          {
            name: '高温',
            type: 'line',
            data: record.highest,
            markPoint: { data: [{ type: 'max', name: 'Max' }, { type: 'min', name: 'Min' }] }
          },
          { name: '低温', type: 'line', data: record.lowest }
        ]
      };
      this.myChart.setOption(option);
    }
  }
};
</script>