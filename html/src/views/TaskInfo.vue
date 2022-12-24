<template>
    <div>
        <el-table :data="taskInfo" style="width: 100%">
            <el-table-column prop="id" label="id" />
            <el-table-column prop="strategy" label="类型" />
            <el-table-column prop="taskState" label="状态" />
            <el-table-column prop="intervalSecond" label="间隔(s)/剩余">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.intervalSecond"></el-input>
                    {{ scope.row.nextRemain }}
                </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="时间" min-width="200">
                <template slot-scope="scope">
                    创建: {{ scope.row.createdAt }}<br>
                    更新: {{ scope.row.updatedAt }}<br>
                    下次: {{ scope.row.nextAt }}<br>
                </template>
            </el-table-column>/>

            <el-table-column label="执行" min-width="100">
                <template slot-scope="scope">
                    执行: {{ scope.row.runCount }}<br>
                    失败: {{ scope.row.errorCount }}<br>
                    最大失败: {{ scope.row.maxError }}<br>
                </template>
            </el-table-column>
            <el-table-column label="详情" min-width="800">
                <template slot-scope="scope">
                    <div v-if="scope.row.strategy == 'tunBiBao'">
                        <el-table :data="scope.row.dataObj.currency" style="width: 100%">
                            <el-table-column prop="currency" label="币种" />
                            <el-table-column prop="percent" label="初始占比" />
                            <el-table-column prop="hold" label="持仓量" />
                            <el-table-column prop="price" label="单格" />
                            <el-table-column prop="usdt" label="总价" />
                        </el-table>
                        <el-row>
                            <el-col :span="4">&nbsp;</el-col>
                            <el-col :span="4" class="price">初始价格:&nbsp;{{ scope.row.dataObj.initUsdt }}</el-col>
                            <el-col :span="4" class="price">总价:&nbsp;{{ scope.row.dataObj.currentUsdt }}</el-col>
                            <el-col :span="4" class="price">重平价格:&nbsp;{{ rebalance(scope.row.dataObj) }}</el-col>
                            <el-col :span="4" class="price">当前差价:&nbsp;{{ scope.row.dataObj.diff }}</el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="4" style="text-align:right">重平比例:&nbsp;</el-col>
                            <el-col :span="4">
                                <el-input size="mini" v-model="scope.row.dataObj.rebalance"></el-input>
                            </el-col>
                            <el-col :span="8">
                                <el-button size="mini" type="primary" @click="updateTask(scope.row)">修改</el-button>
                                <el-button size="mini" type="info" @click="syncTask(scope.row)">同步</el-button>
                                <el-button size="mini" type="success" @click="runTask(scope.row)">执行</el-button>
                            </el-col>
                        </el-row>
                    </div>


                    <div v-if="scope.row.strategy == 'wangGe'">

                        <el-table :data="allWangGeOrder(scope.row.dataObj.wangGeOrders)" style="width: 100%">
                            <el-table-column prop="createdAt" label="时间" />
                            <el-table-column prop="name" label="name" />
                            <el-table-column prop="type" label="type" />
                            <el-table-column prop="price" label="price" />
                            <el-table-column prop="quantity" label="quantity" />
                            <el-table-column label="操作">
                                <template slot-scope="subScope">
                                    <el-button size="mini" type="danger"
                                        @click="cancelOrder(scope.row.dataObj, subScope.row)">取消</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <el-row>
                            <el-col :span="8">&nbsp;</el-col>
                            <el-col :span="4" class="price">持仓:&nbsp;{{ scope.row.dataObj.hold }}</el-col>
                            <el-col :span="4" class="price">当前价格:&nbsp;{{ scope.row.dataObj.price }}</el-col>

                        </el-row>
                        <el-row>
                            <el-col :span="4" style="text-align:right">最大持仓:&nbsp;</el-col>
                            <el-col :span="4">
                                <el-input size="mini" v-model="scope.row.dataObj.maxHold"></el-input>
                            </el-col>

                            <el-col :span="8">
                                <el-button size="mini" type="primary" @click="updateTask(scope.row)">修改</el-button>
                                <el-button size="mini" type="info" @click="syncTask(scope.row)">同步</el-button>
                                <el-button size="mini" type="success" @click="runTask(scope.row)">执行</el-button>
                            </el-col>
                        </el-row>

                        <el-table :data="scope.row.dataObj.rules" style="width: 100%">
                            <el-table-column prop="min" label="min" >
                                <template slot-scope="scope">
                                    <el-input size="mini" v-model="scope.row.min"></el-input>
                </template>
                                </el-table-column>
                            <el-table-column prop="max" label="max" >
                                <template slot-scope="scope">
                                <el-input size="mini" v-model="scope.row.max"></el-input>
                                </template>

                            </el-table-column>
                            <el-table-column prop="step" label="间隔" >
                                <template slot-scope="scope">
                                <el-input size="mini" v-model="scope.row.step"></el-input>
                                </template>
                            </el-table-column>
                            <el-table-column prop="quantity" label="数量" >
                                <template slot-scope="scope">
                                <el-input size="mini" v-model="scope.row.quantity"></el-input>
                                </template>

                            </el-table-column>
                        </el-table>
                    </div>

                </template>
            </el-table-column>

        </el-table>

    </div>
</template>

<script>

export default {
    data() {
        return {
            taskInfo: [],
        }
    },
    methods: {
        getTaskInfo() {
            this.$http.post("/taskInfo/list").then(resp => {
                resp.data.forEach(el => {
                    el.dataObj = eval("(" + el.data + ")");
                    console.log(el.dataObj);
                });
                this.taskInfo = resp.data;
            });
        },
        rebalance(obj) {
            var total = obj.currentUsdt;
            var percent = obj.rebalance;
            var balance = total * percent;
            return balance.toFixed(4);
        },
        updateTask(taskInfo) {
            taskInfo.data = JSON.stringify(taskInfo.dataObj);
            this.$http.post("/taskInfo/update", taskInfo).then(resp => {
                console.log('ok');
            });
        },
        syncTask(taskInfo) {
            this.$http.post("/taskInfo/syncTask", taskInfo).then(resp => {
                console.log('ok');
            });
        },
        runTask(taskInfo) {
            this.$http.post("/taskInfo/run", taskInfo).then(resp => {
                console.log('ok');
            });
        },
        updateRemain: function () {
            this.taskInfo.forEach(item => {
                var remain = item.nextRemain--;
                if (remain <= 0 && remain % 10 == 0) {
                    this.getTaskInfo();
                }
            });
        },
        allWangGeOrder(wangGeOrder) {
            var orders = [];

            if (wangGeOrder.sell2) {
                wangGeOrder.sell2.name = "sell2";
                orders.push(wangGeOrder.sell2);
            }
            if (wangGeOrder.sell1) {
                wangGeOrder.sell1.name = "sell1";
                orders.push(wangGeOrder.sell1);
            }
            if (wangGeOrder.buy1) {
                wangGeOrder.buy1.name = "buy1";
                orders.push(wangGeOrder.buy1);
            }
            if (wangGeOrder.buy2) {
                wangGeOrder.buy2.name = "buy2";
                orders.push(wangGeOrder.buy2);
            }
            return orders;
        },
        cancelOrder(wangGeData, wangGeOrder) {
            var param = {
                clientOrderId: wangGeOrder.clientOrderId,
                symbol: wangGeData.symbol,
            }
            this.$http.post("/traceOrder/cancel", param).then(resp => {
                this.getTaskInfo();
            });
        }

    },
    created() {
        this.getTaskInfo();

        const remainTimer = setInterval(() => {
            this.updateRemain();
        }, 1000)


        //销毁定时器
        this.$once('hook:beforeDestroy', () => {
            clearInterval(remainTimer);
        })
    }

}
</script>

<style lang="less" scoped>
.priceDiv {
    margin-top: 20px;
    text-align: right;
    margin-right: 80px;
}

.price {
    margin-right: 10px;
}
</style>