<template>
    <div>
        <el-form ref="form" label-width="100px" :inline="true">
            <el-form-item label="当前价格">
                <el-input v-model="price" style="width:166px"/> 
                <el-button v-if="this.stopUpdate" @click="stopUpdate=false;updatePrice()" >自动更新</el-button>
                <el-button v-if="!this.stopUpdate" @click="stopUpdate=true" >停止更新</el-button>
                <el-button @click="mockPrice">mock价格</el-button>
                <el-button @click="doBuy">下单</el-button>
            </el-form-item>
            <br/>
            <el-form-item label="最近高点">
                {{ wangGe.high }}
            </el-form-item>
            <el-form-item label="购买间隔">
                {{ wangGe.buyInterval }}
            </el-form-item>

            <br />
            <el-form-item label="准备卖出">
                {{ wangGe.lastSell }}
            </el-form-item>

            <el-form-item label="上次买入">
                {{ wangGe.lastBuy }}
            </el-form-item>
            <el-form-item label="下次买入">
                {{ wangGe.nextBuy }}
            </el-form-item>

        </el-form>
    </div>
</template>

<script>

export default {
    data() {
        return {
            wangGe: {

            },
            price: 0,
            stopUpdate:true,
        }
    },
    methods: {
        mockPrice() {
            var data = { "price": this.price };
            this.$http.post("/wangGe/mockPrice", data).then(resp => {
                this.$message.success('ok');
            });
        },
        updatePrice() {
            var stop=this.stopUpdate;
            this.$http.post("/wangGe/runInfo").then(resp => {
                this.wangGe = resp.data;
                this.price=this.wangGe.current;
                if(stop){
                    return;
                }
                setTimeout(() => {
                    this.updatePrice();
                }, 5000);
            });
        },
        doBuy(){
            this.$http.post("/wangGe/doBuy").then(resp => {
                this.$message.success('ok');
            });
        }
    },
    created() {
        this.updatePrice();
    }

}
</script>

