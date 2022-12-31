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
                                <el-button size="mini" type="success" @click="runTask(scope.row)">执行</el-button>
                            </el-col>
                        </el-row>
                    </div>


                    <div v-if="scope.row.strategy == 'wangGe'">
                        <el-tabs type="border-card">
                            <el-tab-pane>
                                <span slot="label"> 挂单</span>
                                <el-table :data="scope.row.openOrders" style="width: 100%">
                                    <el-table-column prop="time" label="时间" />
                                    <el-table-column prop="symbol" label="symbol" />
                                    <el-table-column prop="side" label="side" :formatter="$utils.formatOrderSide" />
                                    <el-table-column prop="price" label="price" />
                                    <el-table-column prop="origQty" label="origQty" />
                                    <el-table-column label="操作">
                                        <template slot-scope="subScope">
                                            <el-button size="mini" type="danger"
                                                @click="cancelOrder(subScope.row)">取消</el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </el-tab-pane>

                            <el-tab-pane label="规则">
                                <el-table :data="scope.row.dataObj.rules" style="width: 100%">
                                    <el-table-column prop="min" label="min">
                                        <template slot-scope="scope">
                                            <el-input size="mini" v-model="scope.row.min"></el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="max" label="max">
                                        <template slot-scope="scope">
                                            <el-input size="mini" v-model="scope.row.max"></el-input>
                                        </template>

                                    </el-table-column>
                                    <el-table-column prop="step" label="买入减">
                                        <template slot-scope="scope">
                                            <el-input size="mini" v-model="scope.row.buySub"></el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="step" label="卖出加">
                                        <template slot-scope="scope">
                                            <el-input size="mini" v-model="scope.row.sellAdd"></el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="quantity" label="数量">
                                        <template slot-scope="scope">
                                            <el-input size="mini" v-model="scope.row.quantity"></el-input>
                                        </template>

                                    </el-table-column>
                                </el-table>
                            </el-tab-pane>
                        </el-tabs>

                        <el-row>
                            <el-col :span="8">&nbsp;</el-col>
                            <el-col :span="4" class="price">币种:&nbsp;{{ scope.row.dataObj.symbol }}</el-col>
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
                                <el-button size="mini" type="success" @click="runTask(scope.row)">执行</el-button>
                                <el-button size="mini" type="warning" @click="prepareFilled(scope.row)">手动成交</el-button>
                            </el-col>
                        </el-row>


                    </div>

                </template>
            </el-table-column>
        </el-table>



        <el-dialog title="手动成交" :visible.sync="filledVisible" width="30%" >
            <el-form ref="form" :model="orderFilledDto" label-width="80px">
                <el-form-item label="任务id">
                    <el-input v-model="orderFilledDto.taskInfoId"></el-input>
                </el-form-item>
                <el-form-item label="price">
                    <el-input v-model="orderFilledDto.price"></el-input>
                </el-form-item>
                <el-form-item label="orderSide">
                    <el-input v-model="orderFilledDto.orderSide"></el-input>
                </el-form-item>
                <el-form-item label="quantity">
                    <el-input v-model="orderFilledDto.quantity"></el-input>
                </el-form-item>
            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button @click="filledVisible = false">取 消</el-button>
                <el-button type="primary" @click="orderFilled">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>

export default {
    data() {
        return {
            taskInfo: [],
            filledVisible: false,
            orderFilledDto: {
               
            }
        }
    },
    methods: {
        getTaskInfo() {
            this.$http.post("/taskInfo/list").then(resp => {
                resp.data.forEach(el => {
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
                this.getTaskInfo();
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
        cancelOrder(openOrder) {
            var param = {
                clientOrderId: openOrder.clientOrderId,
                symbol: openOrder.symbol,
            }
            this.$http.post("/traceOrder/cancel", param).then(resp => {
                this.getTaskInfo();
            });
        },
        prepareFilled(taskInfo){
            this.filledVisible = true;
            this.orderFilledDto={
                taskInfoId:taskInfo.id,
                price:taskInfo.dataObj.maxSell,
                orderSide:'BUY',
                quantity:taskInfo.dataObj.rule.quantity,
            }
        },
        orderFilled(){
            this.$http.post("/taskInfo/filled", this.orderFilledDto).then(resp => {
                this.filledVisible = false;
                this.getTaskInfo();
            });
        }

    },
    created() {
        this.getTaskInfo();

        const remainTimer = setInterval(() => {
            // this.updateRemain();
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