<template>
    <el-row>
        <el-col :span="8">
            <el-card class="box-card">
                <div class="user">
                    <img src="../assets/images/user-default.jpg" alt=""/>
                    <div class="userInfo">
                        <P class="name">Admin</P>
                        <p class="access">管理员</p>
                    </div>
                </div>
                <div class="loginInfo">
                    <p>上次登录时间: <span>2022-01-01</span></p>
                    <p>上次登录地点: <span>武汉</span></p>
                </div>
            </el-card>

            
        </el-col>
        <el-col :span="16">
        
        </el-col>
    </el-row>
</template>

<script>
export default {
    data() {
        return {

        }
    },
    mounted(){
        this.$http.get('/book/list').then((response)=>{
            console.log(response.data);
        });
    }
}
</script>

<style lang="less" scoped>
.user{
    padding-bottom: 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid #ccc;
    display: flex;
    align-items: center;
    img{
        margin-right: 40px;
        width:150px;
        height: 150px;
        border-radius: 50%;
    }
    .userInfo{
        .name{
            font-size: 32px;
            margin-bottom: 10px;
        }
        .access{
            color: #999999;
        }
    }
}
.loginInfo{
        p{
            line-height: 28px;
            font-size: 14px;
            color: #999999;
            span{
                color: #666666;
                margin-left: 60px;
            }
        }
    }


</style>