<template>
    <div>
        <el-form ref="form" label-width="100px" :inline="true">
            <el-form-item label="">
                &nbsp;&nbsp;&nbsp;&nbsp;
                <el-button v-if="this.stopUpdate" type="primary" @click="stopUpdate=false;updatePrice()" >自动更新</el-button>
                <el-button v-if="!this.stopUpdate" type="primary" @click="stopUpdate=true" >停止更新</el-button>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <el-input v-model="testValue" style="width:200px"></el-input>
                <el-button @click="mockPrice">mock价格</el-button>
                <el-button @click="doTrace">doTrace</el-button>
                <el-button @click="doBuy">市价买入</el-button>
                <el-button @click="doFilled">成交</el-button>
            </el-form-item>
            <br/>
            <el-form-item label="持仓">
                {{ wangGe.hold }}
            </el-form-item>
            <el-form-item label="最近高点">
                {{ wangGe.high }}
            </el-form-item>
           
            <el-form-item label="买入价格">
                {{ wangGe.lastBuy }}
            </el-form-item>
            <el-form-item label="准备卖出">
                {{ wangGe.lastSell }}
            </el-form-item>

            <br/>

            <el-form-item label="当前价格">
                {{wangGe.current}}  
            </el-form-item>

            <el-form-item label="下次买入">
                {{wangGe.nextBuy}}({{ wangGe.quantity }})
            </el-form-item>

            <el-form-item label="买入用时">
                {{ wangGe.buyDiff }}
            </el-form-item>
         

        </el-form>

        <el-table :data="openOrders" style="width: 100%">
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="symbol" label="symbol" />
            <el-table-column prop="side" label="side" :formatter="$utils.formatOrderSide"/>
            <el-table-column prop="status" label="status" />
            <el-table-column prop="clientOrderId" label="clientOrderId" min-width="100"/>
            <el-table-column prop="price" label="price" />
            <el-table-column prop="origQty" label="origQty" />
            <el-table-column prop="time" label="time" />
            <el-table-column prop="updateTime" label="updateTime" />
        </el-table>
    </div>
</template>

<script>

export default {
    data() {
        return {
            wangGe: {
                "hold": 0,
                "current": 1000,
                "high": 1000,
                "lastSell": 1000,
                "lastBuy": 990,
                "quantity": 0.5,
                "minNext": 988,
                "nextBuy": 988,
                "down": null,
                "buyDiff": "0"
            },
            stopUpdate:false,
            testValue:"",
            openOrders:[],
        }
    },
    methods: {
        mockPrice() {
            if(!this.testValue){
                this.$message.error('金额不正确');
                return;
            }
            var data = { "price": this.testValue };
            this.$http.post("/wangGe/mockPrice", data).then(resp => {
                this.$message.success('ok');
            });
        },
        updatePrice() {
            this.$http.post("/wangGe/openOrders").then(resp => {
                this.openOrders=resp.data;
            });

            var stop=this.stopUpdate;
            this.$http.post("/wangGe/runInfo").then(resp => {
                this.wangGe = resp.data;
                if(stop){
                    return;
                }
                setTimeout(() => {
                    this.updatePrice();
                }, 3000);
            });
        },
        doBuy(){
            this.$http.post("/wangGe/doBuy").then(resp => {
                this.$message.success('ok');
            });
        },
        doTrace(){
            this.$http.post("/wangGe/doTrace").then(resp => {
                this.$message.success('ok');
            });
        },
        doFilled(){
            if(!this.testValue){
                this.$message.error('请输入订单号');
                return;
            }
            var data = { "clientOrderId": this.testValue };
            this.$http.post("/wangGe/doFilled",data).then(resp => {
                this.$message.success('ok');
            });
        }
    },
    created() {
        this.updatePrice();
    }

}
</script>

