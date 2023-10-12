<template>
    <div>
        <el-form ref="form" :model="traceInfoDto" label-width="100px" :inline="true">
            <el-form-item>
                <el-date-picker v-model="buyStartQuery" type="datetimerange" :default-time="['00:00:00', '23:59:59']"
                    value-format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
            </el-form-item>


            <el-form-item>
                <el-select v-model="traceInfoDto.traceState">
                    <el-option v-for="item in this.traceStates" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="queryPage()">查询</el-button>
            </el-form-item>

            <br />
            <el-form-item>
                <el-radio-group v-model="selectedDate" @change="changeDate">
                    <el-radio-button label="今日"></el-radio-button>
                    <el-radio-button label="昨日"></el-radio-button>
                    <el-radio-button label="本周"></el-radio-button>
                    <el-radio-button label="本月"></el-radio-button>
                </el-radio-group>
            </el-form-item>
        </el-form>

        <el-table border :data="data.records" style="width: 100%">
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="buyId" label="buyId" />
            <el-table-column prop="buyStart" label="buyStart" />
            <el-table-column prop="sellId" label="sellId" />
            <el-table-column prop="sellStart" label="sellStart" />
            <el-table-column prop="sellEnd" label="sellEnd" />
            <el-table-column prop="buyPrice" label="买入价" />
            <el-table-column prop="sellPrice" label="卖出价" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="profit" label="利润" />
            <el-table-column prop="durationSeconds" label="用时(s)" />
            <el-table-column prop="traceState" label="状态" />


        </el-table>

        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="data.currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="traceInfoDto.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="data.total">
        </el-pagination>

    </div>
</template>

<script>

export default {
    data() {
        return {
            data: {},
            selectedDate: 'today',
            traceInfoDto: {
                buyStartStart: null,
                buyStartEnd: null,
                traceState: null,
                pageNo: 1,
                pageSize: 10,
            },
            buyStartQuery: [],
            orderSides: [
                { value: null, label: '交易方向' },
                { value: '0', label: 'BUY' },
                { value: '1', label: 'SELL' },
            ],
            traceStates: [
                { value: null, label: '全部' },
                { value: 'buying', label: 'buying' },
                { value: 'selling', label: 'selling' },
                { value: 'finished', label: 'finished' },
            ],
        }
    },
    methods: {
        queryPage() {
            this.traceInfoDto.buyStartStart = this.buyStartQuery[0];
            this.traceInfoDto.buyStartEnd = this.buyStartQuery[1];
            this.$http.post("/traceInfo/page", this.traceInfoDto).then(resp => {
                this.data = resp.data;
            });
        },
        handleSizeChange(val) {
            this.traceInfoDto.pageSize = val;
            this.queryPage();
        },
        handleCurrentChange(val) {
            this.traceInfoDto.pageNo = val;
            this.queryPage();
        },
        changeDate() {
            var start = new Date();
            var end = new Date();
            if (this.selectedDate == '今日') {
                start = new Date();
            } else if (this.selectedDate == '昨日') {
                start.setDate(start.getDate() - 1);
                end.setDate(end.getDate() - 1);
            } else if (this.selectedDate == '本周') {
                const day = start.getDay();
                const diff = start.getDate() - day + (day === 0 ? -6 : 1); // Adjust when day is Sunday
                start = new Date(start.setDate(diff));
            } else if (this.selectedDate == '本月') {
                start = new Date(start.getFullYear(), start.getMonth(), 1);
            }
            this.buyStartQuery = [start.format('yyyy-MM-dd 00:00:00'), end.format('yyyy-MM-dd 23:59:59')];
            this.queryPage();
        }
    },
    created() {
        this.selectedDate = '今日';
        this.changeDate();
    }

}
</script>

<style lang="less" scoped></style>