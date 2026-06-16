// try { // 在Vue2中引入JS运动框架，封装JS动画函数
// 	var {startMove} = require('./components/move.js')
// 	Vue.prototype.$myAnim = (obj, param, nowrap)=>
// 	new Promise(resolve=>startMove(obj,param,()=>resolve(),nowrap))
// } catch(e){ }


// JS运动框架
export function startMove(obj, param, endMove, nowrap) {
  if (obj.timer){
    if (nowrap) {
      return
    }
    if (startMove.param != param) {
      clearInterval(obj.timer)
      obj.timer = null
    } else
      return
  }
  startMove.param = param
  if (param.duration) { // 控制动画时长
    var duration = param.duration
    delete param.duration
  }
  var flag = true
  obj.timer = setInterval(()=>{
    for (let attr in param) {
      let curr = 0;
      if (attr == 'opacity') {
        if (param[attr] <= 1) param[attr] = param[attr] *100;
        curr = Math.round(parseFloat(getStyle(obj,attr))*100)
      } else {
        param[attr] = parseInt(param[attr]);
        curr = parseInt(getStyle(obj,attr));
      }
      let speed = (param[attr]-curr) / (duration?duration:40)
      speed = speed > 0 ? Math.ceil(speed):Math.floor(speed);
      if (curr != param[attr]) {
        let ret = curr + speed
        obj.style[attr] = attr=='opacity' ? ret/100: ret+'px'
        flag = false;
      } else {
        flag = getFlag();
      }
      if (flag && obj.timer) {
        clearInterval(obj.timer);
        obj.timer = null;
        if (endMove) {
          endMove(obj);
        }
      }
    }
  }, 5);
  // 函数内部函数
  function getFlag() {
    for (let attr in param) {
      let curr = attr == 'opacity' ? 
        Math.round(parseFloat(getStyle(obj,attr))*100) :
        parseInt(getStyle(obj,attr));
      if (curr - param[attr] != 0) {
        return false
      }
    }
    return true
  }
  // getStyle函数
  function getStyle(obj, attr) {
    if (obj.currentStyle) {
      return obj.currentStyle[attr]
    } else {
      return getComputedStyle(obj, false)[attr]
    }
  }
}
