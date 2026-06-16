<script setup>

</script>

<template>
  <Table :util="{
    httpUrl: '/device',
    id: '主键',
    device: '设备名称',
    idcard: '设备唯一标识/-1',
    category: '设备类型//cid?key=category',
    product:'所属产品//pid?key=product',
    ctime: '发布时间/-1',  //  /-1这个不是单位，是不允许修改这一列
  }"></Table>
</template>

<style scoped>

</style>