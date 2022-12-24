import Vue from 'vue';
import ElementUI from 'element-ui';
import App from './App.vue';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import http from './utils/request'

Vue.config.productionTip = false
Vue.use(ElementUI);
Vue.prototype.$http=http;  

new Vue({
  router,
  store,
  el: '#app',
  render: h => h(App)
}).$mount('#app')