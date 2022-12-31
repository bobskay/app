<template>
    <el-menu default-active="1-4-1" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose"
        :collapse="isCollapse"
        background-color="#545c64" 
        text-color="#fff" active-text-color="#ffd04b">

        <h3>{{isCollapse?'后台':'后台管理系统'}}</h3>
        <el-menu-item v-for="item in noChildren" :key="item.name" :index="item.name" @click="clickMenu(item)">
            <i :class="`el-icon-${item.icon}`"></i>
            <span slot="title">{{ item.label }}</span>
        </el-menu-item>

        <el-submenu v-for="item in hasChildren" :key="item.label" :index="item.label">
            <template slot="title">
                <i :class="`el-icon-${item.icon}`"></i>
                <span slot="title">{{ item.label }}</span>
            </template>
            <el-menu-item-group v-for="subItem in item.children" :key="subItem.name">
                <el-menu-item  :index="subItem.name" @click="clickMenu(subItem)">{{ subItem.label }}</el-menu-item>
            </el-menu-item-group>
        </el-submenu>
    </el-menu>
</template>

<style lang="less" scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
}

.el-menu {
    height: 100vh;
    border-right: none;
    h3 {
        color: #fff;
        text-align: center;
        line-height: 48px ;
        font-size: 16px;
        font-weight: 400;
    }
}
</style>

<script>
export default {
    data() {
        return {
            menuData: [
                {
                    path: '/',
                    name: 'home',
                    label: '首页',
                    icon: 's-home',
                    url: '/Home'
                },
                {
                    path: '/account',
                    name: 'account',
                    label: '账户信息',
                    icon: 'user',
                },
                {
                    path: '/taskInfo',
                    name: 'taskInfo',
                    label: '任务管理',
                    icon: 'user',
                },
                {
                    path: '/traceOrder',
                    name: 'traceOrder',
                    label: '订单详情',
                    icon: 'user',
                },
                {
                    label: '其他',
                    icon: 'location',
                    children: [
                        {
                            path: '/pageOne',
                            name: 'page1',
                            label: '页面1',
                            url: 'Other/PageOne'
                        },
                        {
                            path: '/pageTwo',
                            name: 'page2',
                            label: '页面1',
                            url: 'Other/PageTwo'
                        }
                    ]
                }
            ]
        };
    },
    methods: {
        handleOpen(key, keyPath) {
            console.log(key, keyPath);
        },
        handleClose(key, keyPath) {
            console.log(key, keyPath);
        },
        clickMenu(item){
            var newPath=item.path;
            if(newPath=='/'){
                newPath='/home'
            }
            if(this.$route.path!=newPath){
                this.$router.push(newPath);
            }
        }
    },
    computed: {
        noChildren() {
            return this.menuData.filter(itme => !itme.children);
        },
        hasChildren() {
            return this.menuData.filter(itme => itme.children);
        },
        isCollapse(){
           return this.$store.state.tab.isCollapse;
        }
    }
}
</script>