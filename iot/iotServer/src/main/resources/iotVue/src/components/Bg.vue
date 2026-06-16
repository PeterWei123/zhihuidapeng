<template>
	<div :style="{
		'background-image':'url('+src+'),url('+(onerror?onerror:'')+')',
		'background-size':'cover',
		'background-position':'center',
		'width':width,
		'height':height,
	}"><slot></slot></div>
</template>

<script>
export default {
	props: ['src','width','height','onerror']
}
</script>

<style>
</style>