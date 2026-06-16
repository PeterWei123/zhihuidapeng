<template>
  <Table :util="{
    httpUrl: '/product',
    id: '主键',
    product: '产品名称',
    city: '所属行政区//cid?key=city',
    user: '所属租户//uid?key=user',
    devip: '硬件IP地址',
    clientid: 'mqtt设备ID',
    ctime: '创建时间/-1',
  }"></Table>
</template>