import Vue from 'vue';
import ElementUI from 'element-ui';
import App from './App.vue';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import http from './utils/request'
import utils from './utils/utils'


Vue.config.productionTip = false
Vue.use(ElementUI,{
  size:'mini'
});
Vue.prototype.$http=http;  
Vue.prototype.$utils=utils;  

var vue=new Vue({
  router,
  store,
  el: '#app',
  render: h => h(App)
}).$mount('#app')

Vue.prototype.$vue=vue;

export default vue;
