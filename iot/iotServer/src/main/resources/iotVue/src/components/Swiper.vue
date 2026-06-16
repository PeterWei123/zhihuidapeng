<template>
  <div class="swiper-container" :class="name">
    <div class="swiper-wrapper">
      <slot></slot>
    </div>
    <div class="swiper-pagination" v-show="indicator"></div>
  </div>
</template>

<script>
  import Swiper from 'swiper';
  import bus from './bus.js';
  export default {
    props: ['name','direction','autoplay','speed','indicator'],
    data() {return {
      swiper: null
    }},
    mounted() {
      var name = this.name;
      this.swiper = new Swiper('.'+this.name, {
        direction: this.direction,
        pagination: '.swiper-pagination',
        loop: this.autoplay ? true : false,
        autoplay: this.autoplay,
        autoplayDisableOnInteraction: false,
        speed: this.speed,
        observer: true,
        observeParents: true,
        onSlideChangeStart(swiper) {
          bus.$emit(name, swiper.activeIndex);
        }
      });
    }
  }
</script>

<style>
</style>