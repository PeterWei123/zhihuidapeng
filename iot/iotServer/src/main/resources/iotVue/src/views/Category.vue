<template>
  <Table :util="{
    httpUrl: '/category',
    id: '主键',
    category: '设备类型',
    pin: '设备引脚/脚'
  }" :headhide="123">
    <el-button type="warning" plain>设备引脚不允许修改</el-button>
  </Table>
</template>