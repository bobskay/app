<template>
    <div>
        <el-tabs type="border-card">
            <el-tab-pane>
                <span slot="label"> 现货</span>
                <el-table :data="account.assets.assets" style="width: 100%">
                    <el-table-column prop="asset" label="asset" />
                    <el-table-column prop="free" label="free" />
                    <el-table-column prop="btcValuation" label="btcValuation" />
                </el-table>
            </el-tab-pane>
            <el-tab-pane label="合约">
                <el-table :data="account.account.assets" style="width: 100%">
                    <el-table-column prop="asset" label="asset" />
                    <el-table-column prop="walletBalance" label="钱包余额" />
                    <el-table-column prop="unrealizedProfit" label="未实现盈亏" />
                    <el-table-column prop="marginBalance" label="保证金余额" />
                    <el-table-column prop="maxWithdrawAmount" label="maxWithdrawAmount" />
                    <el-table-column prop="crossWalletBalance" label="crossWalletBalance" />
                    <el-table-column prop="availableBalance" label="可划转余额" />
                </el-table>
                <el-table :data="account.account.positions" style="width: 100%">
                    <el-table-column prop="symbol" label="symbol" />
                    <el-table-column prop="positionAmt" label="positionAmt" />
                    <el-table-column prop="notional" label="notional" />
                </el-table>
            </el-tab-pane>
            <el-tab-pane label="挂单"> <el-table :data="account.openOrders" style="width: 100%">
                    <el-table-column type="index" width="50"></el-table-column>
                    <el-table-column prop="symbol" label="symbol" />
                    <el-table-column prop="side" label="side" :formatter="$utils.formatOrderSide"/>
                    <el-table-column prop="status" label="status" />
                    <el-table-column prop="clientOrderId" label="clientOrderId" min-width="100"/>
                    <el-table-column prop="price" label="price" />
                    <el-table-column prop="origQty" label="origQty" />
                    <el-table-column prop="time" label="time" />
                    <el-table-column prop="updateTime" label="updateTime" />
                </el-table></el-tab-pane>
        </el-tabs>








    </div>
</template>

<script>

export default {
    data() {
        return {
            account: {
                assets:{},
                account:{},
                openOrders:[],
            },
        }
    },
    methods: {
        getAccount() {
            this.$http.post("/accountInfo/get").then(resp => {
                this.account = resp.data;
            });
        },

    },
    created() {
        this.getAccount();
    }

}
</script>

<style lang="less" scoped>

</style>