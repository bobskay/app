<template>
    <div>
        <el-form ref="form" :model="traceOrderDto" label-width="100px">
            <el-row :gutter="2">
                <el-col :span="6">
                    <el-date-picker v-model="createdAtQuery" type="datetimerange"
                        :default-time="['00:00:00', '23:59:59']"
                        value-format="yyyy-MM-dd HH:mm:ss"
                     >
                    </el-date-picker>
                </el-col>
                <el-col :span="2">
                    <el-input v-model="traceOrderDto.symbol" placeholder="交易对"></el-input>
                </el-col>
                <el-col :span="2">
                    <el-select v-model="traceOrderDto.orderSide">
                        <el-option v-for="item in this.orderSides" :key="item.value" :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    <el-select v-model="traceOrderDto.orderStateList" multiple placeholder="订单状态">
                        <el-option v-for="item in this.orderStates" :key="item.value" :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="queryPage()">查询</el-button>
                </el-col>

            </el-row>
        </el-form>

        <el-table border :data="data.records" style="width: 100%">

            <el-table-column label="id">
                <template slot-scope="scope">
                    <el-popover trigger="hover" placement="top">
                        <p>id: {{ scope.row.id }}</p>
                        <p>businessId: {{ scope.row.businessId }}</p>
                        <p>clientOrderId: {{ scope.row.clientOrderId }}</p>
                        <div slot="reference" class="name-wrapper">
                            <el-tag size="medium">{{ scope.row.symbol }}</el-tag>
                        </div>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="createdAt" />
            <el-table-column prop="finishAt" label="finishAt" />
            <el-table-column prop="businessType" label="businessType" />
            <el-table-column label="orderSide" :formatter="$utils.formatOrderSide"/>
            <el-table-column prop="quantity" label="quantity" />
            <el-table-column prop="expectPrice" label="expectPrice" />
            <el-table-column prop="orderState" label="orderState" />
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button size="mini" type="primary" @click="showRefOrder(scope.row)">关联订单</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="data.currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="traceOrderDto.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="data.total">
        </el-pagination>

        <el-dialog title="关联订单" :visible.sync="refVisible" width="30%" >
            <el-form ref="from" :model="refOrder" label-width="80px">
                <el-form-item label="任务id">
                    <el-input v-model="refOrder.taskInfoId"></el-input>
                </el-form-item>
            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button @click="refVisible = false">取 消</el-button>
                <el-button type="primary" @click="refVisible">确 定</el-button>
            </span>
        </el-dialog>
    </div>

    
</template>

<script>

export default {
    data() {
        return {
            data: {},
            traceOrderDto: {
                createdAtStart: null,
                createdAtEnd: null,
                symbol: "",
                orderSide: null,
                orderStateList: ["FILLED"],
                pageNo: 1,
                pageSize: 10,
            },
            createdAtQuery: [],
            orderSides: [
                { value: null, label: '交易方向' },
                { value: '0', label: 'BUY' },
                { value: '1', label: 'SELL' },
            ],
            orderStates: [
                { value: 'SUBMITTED', label: 'SUBMITTED' },
                { value: 'CREATED', label: 'CREATED' },
                { value: 'PARTIALLY_FILLED', label: 'PARTIALLY_FILLED' },
                { value: 'CANCELLING', label: 'CANCELLING' },
                { value: 'FILLED', label: 'FILLED' },
                { value: 'NEW', label: 'NEW' },
                { value: 'CANCELED', label: 'CANCELED' },
            ],
            refVisible:false,
            refOrder:{

            },
        }
    },
    methods: {
        queryPage() {
            this.traceOrderDto.createdAtStart = this.createdAtQuery[0];
            this.traceOrderDto.createdAtEnd = this.createdAtQuery[1];
            this.$http.post("/traceOrder/page", this.traceOrderDto).then(resp => {
                this.data = resp.data;
            });
        },
        openOrder(row) {
            this.$http.post("/traceOrder/openOrder", row).then(resp => {
                console.log(resp.data);
            });
        },
        handleSizeChange(val) {
            this.traceOrderDto.pageSize=val;
            this.queryPage();
        },
        handleCurrentChange(val) {
            this.traceOrderDto.pageNo=val;
            this.queryPage();
        },
        showRefOrder(row){
            console.log(row);
            this.refVisible=true;
        }
    },
    created() {
        let start = new Date().format('yyyy-MM-dd 00:00:00');
        let end = new Date().format('yyyy-MM-dd 23:59:59');
        this.createdAtQuery = [start, end];
        this.queryPage();
    }

}
</script>

<style lang="less" scoped>

</style>