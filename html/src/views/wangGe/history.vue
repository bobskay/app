<template>
    <div>
        <el-form ref="form" :model="traceInfoDto" label-width="100px" :inline="true">
            <el-form-item>
                <el-date-picker v-model="buyStartQuery" type="datetimerange"
                        :default-time="['00:00:00', '23:59:59']"
                        value-format="yyyy-MM-dd HH:mm:ss"
                     >
                    </el-date-picker>
            </el-form-item>
            
            <el-form-item>
                <el-select v-model="traceInfoDto.traceState">
                        <el-option v-for="item in this.traceStates" :key="item.value" :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
            </el-form-item>

            <el-form-item>

                    <el-button type="primary" @click="queryPage()">查询</el-button>

                </el-form-item>

        </el-form>

        <el-table border :data="data.records" style="width: 100%">
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="buyStart" label="buyStart" />
            <el-table-column prop="buyEnd" label="buyEnd" />
            <el-table-column prop="sellStart" label="sellStart" />
            <el-table-column prop="sellEnd" label="sellEnd" />
            <el-table-column prop="quantity" label="quantity" />
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
            traceInfoDto: {
                buyStartStart: null,
                buyStartEnd: null,
                traceState:null,
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
            this.traceInfoDto.pageSize=val;
            this.queryPage();
        },
        handleCurrentChange(val) {
            this.traceInfoDto.pageNo=val;
            this.queryPage();
        },
    },
    created() {
        let start = new Date().format('yyyy-MM-dd 00:00:00');
        let end = new Date().format('yyyy-MM-dd 23:59:59');
        this.buyStartQuery = [start, end];
        this.queryPage();
    }

}
</script>

<style lang="less" scoped>

</style>