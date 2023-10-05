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
            <el-form-item label="买入增加">
                <el-input v-model="configInfo.sellAdd" style="width:200px"></el-input>
            </el-form-item>
            <el-form-item label="数量">
                <el-input v-model="configInfo.quantity" style="width:200px"></el-input>
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
                "sellAdd": 10,
                "quantity": 0.1,
                "symbol": "ethbusd",
                "scale": 2,
                "maxHold": 20
            }
        }
    },
    methods: {
       
    },
    created() {
        this.$http.post("/wangGe/configInfo", this.traceInfoDto).then(resp => {
                this.configInfo = resp.data;
            });
    }

}
</script>

