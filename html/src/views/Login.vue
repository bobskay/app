<template>
    <div>
        <el-form ref="form" :model="userInfo" label-width="80px">
            <el-col :span="6">
                <el-form-item label="用户名">
                    <el-input v-model="userInfo.name"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="密码">
                    <el-input v-model="userInfo.pwd"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="6">
            <el-form-item>
                <el-button type="primary" @click="login()">登录</el-button>
            </el-form-item>
            </el-col>
        </el-form>
    </div>
</template>

<script>


export default {
    data() {
        return {
            userInfo:{}
        }
    },
    methods: {
        login:function(){
            var token=JSON.stringify(this.userInfo)
            console.log("登录信息:"+token);
            localStorage.setItem("token",token );
            self.location.reload();
        }
    },
    created() {
       
    }

}
</script>
