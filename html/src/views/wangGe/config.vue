<template>
    <div>
        <el-form ref="form" :model="configInfo" label-width="100px" >
            <el-form-item label="最大持仓">
                {{configInfo.maxHold}}
            </el-form-item>
            <el-form-item label="时间间隔">
                <el-input v-model="configInfo.interval" style="width:200px"></el-input>
            </el-form-item>
            <el-form-item label="价格间隔">
                <el-input v-model="configInfo.minInterval" style="width:200px"></el-input>
            </el-form-item>
            <el-form-item label="连续下跌">
                <el-input v-model="configInfo.down" style="width:200px"></el-input>
            </el-form-item>
            <el-form-item label="买入加价">
                <el-input v-model="configInfo.sellAdd" style="width:200px"></el-input>
            </el-form-item>
            <el-form-item label="默认数量">
                <el-input v-model="configInfo.quantity" style="width:200px"></el-input>
            </el-form-item>

            <el-form-item label="阶梯持仓">
                <el-input v-model="configInfo.hold1" style="width:66px"></el-input>
                <el-input v-model="configInfo.hold2" style="width:66px"></el-input>
                <el-input v-model="configInfo.hold3" style="width:66px"></el-input>
                <el-input v-model="configInfo.hold4" style="width:66px"></el-input>
                <el-input v-model="configInfo.hold5" style="width:66px"></el-input>
            </el-form-item>

            <el-form-item label="阶梯数量">
                <el-input v-model="configInfo.quantity1" style="width:66px"></el-input>
                <el-input v-model="configInfo.quantity2" style="width:66px"></el-input>
                <el-input v-model="configInfo.quantity3" style="width:66px"></el-input>
                <el-input v-model="configInfo.quantity4" style="width:66px"></el-input>
                <el-input v-model="configInfo.quantity5" style="width:66px"></el-input>
            </el-form-item>

            <el-form-item >
                <el-button type="primary" @click="updateConfig()">更新</el-button>
                </el-form-item>
        </el-form>
    </div>
    
</template>

<script>

export default {
    data() {
        return {
            configInfo:{
                "interval": "3600000",
                "minInterval": 5,
                "down": "10",
                "sellAdd": 10,
                "quantity": 0.1,
                "symbol": "ethbusd",
                "scale": 2,
                "maxHold": 20,
                "quantity1": 0.1,
                "quantity2": 0.1,
                "quantity3": 0.1,
                "quantity4": 0.1,
                "quantity5": 0.1,
                "hold1":0,
                "hold2":0,
                "hold3":0,
                "hold4":0,
                "hold5":0,
            }
        }
    },
    methods: {
        updateConfig(){
            this.$http.post("/wangGe/updateConfig", this.configInfo).then(resp => {
                this.$message.success('ok');
                this.$http.post("/wangGe/configInfo").then(resp => {
                    this.configInfo = resp.data;
                });
            });

        },
    },
    created() {
        this.$http.post("/wangGe/configInfo").then(resp => {
                this.configInfo = resp.data;
            });
    }

}
</script>

