import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import TaskInfo from '../views/TaskInfo.vue'
import Main from '../views/Main.vue'
import Trace from '../views/Trace.vue'
import Login from '../views/Login.vue'
import PageOne from '../views/PageOne.vue'
import PageTwo from '../views/PageTwo.vue'

Vue.use(VueRouter)


const router = new VueRouter({
    base:'/static',
    routes: [
        {
            path:'/',
            redirect: '/home',
            component: Main,
            children:[
                { path: 'login', component: Login },
                { path: 'home', component: Home },
                { path: 'taskInfo', component: TaskInfo },
                { path: 'trace', component: Trace },
                { path: 'pageOne', component: PageOne },
                { path: 'pageTwo', component: PageTwo }
            ]
        }
      
    ]
})


export default router