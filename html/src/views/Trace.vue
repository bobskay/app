<template>
    <div>
        <el-form ref="form" :model="accountInfo" label-width="100px">
            <el-col :span="12">
                <el-form-item label="最小持仓">
                    <el-input v-model="accountInfo.ruleConfig.min"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11">
                <el-form-item label="最大持仓">
                    <el-input v-model="accountInfo.ruleConfig.max"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="1">
                &nbsp;{{flushTime}}
            </el-col>

            <el-col :span="12">
                <el-form-item label="买入加价">
                    <el-input v-model="accountInfo.ruleConfig.buySub"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="买入减价">
                    <el-input v-model="accountInfo.ruleConfig.sellAdd"></el-input>
                </el-form-item>
            </el-col>

            <el-col :span="8">
                <el-form-item label="更新频率(s)">
                    <el-input v-model="accountInfo.ruleConfig.stepSecond"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="更新价格">
                    <el-input v-model="accountInfo.ruleConfig.step"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="最小利润">
                    <el-input v-model="accountInfo.ruleConfig.minProfit"></el-input>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item label="持仓">
                    <el-input v-model="accountInfo.hold"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="成本">
                    <el-input v-model="accountInfo.avgPrice"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="最后买入">
                    <el-input v-model="accountInfo.lastBuy"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="最后卖出">
                    <el-input v-model="accountInfo.lastSell"></el-input>
                </el-form-item>
            </el-col>

            
            <el-col :span="12">
                <el-form-item label="成交价">
                    <el-input v-model="accountInfo.confirmPrice"></el-input>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item label="当前价格">
                    <el-input v-model="currentPrice"></el-input>
                </el-form-item>
            </el-col>

            <el-col :span="24">
                <el-form-item label="最近买入价">
                    <el-input v-model="accountInfo.buyPrices"></el-input>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item>
                    <el-button type="primary" @click="updateAccount()">更新</el-button>
                </el-form-item>
            </el-col>
        </el-form>


        <el-table :data="accountInfo.openOrders" style="width: 100%">
            <el-table-column prop="clientOrderId" label="订单id" width="200" />
            <el-table-column prop="updateTime" label="更新时间" />
            <el-table-column prop="side" label="交易方向" />
            <el-table-column prop="symbol" label="币种" />
            <el-table-column prop="origQty" label="数量" />
            <el-table-column prop="price" label="当前价格" />
            <el-table-column prop="holdTime" label="持有时长" />
            <el-table-column prop="expectPriceDesc" label="期望价格" width="200" />
            <el-table-column fixed="right" label="操作" width="100">
                <template slot-scope="scope">
                    <el-button @click="handleCancel(scope.row)" type="text" size="small">取消</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>


export default {
    data() {
        return {
            accountInfo: {
                ruleConfig: {}
            },
            currentPrice: "",
            flushTime: 7
        }
    },
    methods: {
        handleCancel: function (row) {
            var param = { clientOrderId: row.clientOrderId };
            this.$http.post("/accountInfo/cancel", param).then(resp => {
                this.accountInfo = resp.data;
            });
        },
        updateAccount: function () {
            this.$http.post("/accountInfo/update", this.accountInfo).then(resp => {
                this.accountInfo = resp.data;
            });
        },
        getAccount: function () {
            this.$http.get("/accountInfo/get").then(resp => {
                this.accountInfo = resp.data;
            });
        },
        updatePrice: function () {
            this.$http.get("/accountInfo/currentPrice").then(resp => {
                this.currentPrice = resp.data;
            });
        }
    },
    created() {
        this.getAccount();

        const accountTimer = setInterval(() => {
            this.getAccount();
            this.flushTime = 7;
        }, 7000)

        const priceTimer = setInterval(() => {
            this.updatePrice();
            this.flushTime--;
        }, 1000)


        //销毁定时器
        this.$once('hook:beforeDestroy', () => {
            clearInterval(accountTimer);
            clearInterval(priceTimer)
        })
    }

}
</script>
