import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import TaskInfo from '../views/TaskInfo.vue'
import Main from '../views/Main.vue'
import Login from '../views/Login.vue'
import PageOne from '../views/PageOne.vue'
import PageTwo from '../views/PageTwo.vue'
import TraceOrder from '../views/TraceOrder.vue'
import Account from '../views/Account.vue'
import WangGe from  '../views/WangGe.vue'

Vue.use(VueRouter)


const router = new VueRouter({
    base:'/',
    routes: [
        {
            path:'/',
            redirect: '/home',
            component: Main,
            children:[
                { path: 'login', component: Login },
                { path: 'home', component: Home },
                { path: 'account', component: Account },
                { path: 'taskInfo', component: TaskInfo },
                { path: 'traceOrder', component: TraceOrder },
                { path: 'wangGe', component: WangGe },
                { path: 'pageOne', component: PageOne },
                { path: 'pageTwo', component: PageTwo }
            ]
        }
      
    ]
})


export default router