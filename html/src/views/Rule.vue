<template>
    <div>
        <el-table :data="ruleInfo.ruleConfigs" style="width: 100%">
            <el-table-column prop="min" label="min">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.min"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="max" label="max">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.max"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.quantity"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="buySub" label="买入减">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.buySub"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="sellAdd" label="卖出加">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.sellAdd"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="minProfit" label="最小利润">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.minProfit"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="stepSecond" label="更新频率(s)">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.stepSecond"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="step" label="更新价格">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.step"></el-input>
                </template>
            </el-table-column>
        </el-table>


        <el-form ref="form" :model="ruleInfo" label-width="80px">
            <el-col :span="3">
                <el-form-item label="最大持仓">
                    <el-input v-model="ruleInfo.maxHold"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="3">
            <el-form-item>
                <el-button type="primary" @click="updateRule()">更新</el-button>
            </el-form-item>
            </el-col>
        </el-form>
    </div>
</template>

<script>


export default {
    data() {
        return {
            ruleInfo: {
                maxHold: "",
                ruleConfigs: []
            }

        }
    },
    methods: {
        getRule: function () {
            this.$http.post("/rule/get").then(resp => {
                this.ruleInfo = resp.data;
            });
        },
        updateRule: function () {
            this.$http.post("/rule/update",this.ruleInfo).then(resp => {
                this.ruleConfigs = resp.data.ruleConfigs;
            });
        },
    },
    created() {
        this.getRule();
    }

}
</script>
