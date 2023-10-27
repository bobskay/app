<template>
    <div>
        <el-form ref="form" :model="traceReportDto" label-width="100px" :inline="true">
            <el-form-item>
                <el-date-picker v-model="timeRange" type="datetimerange" :default-time="['00:00:00', '23:59:59']"
                    value-format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
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

            <el-form-item>
                <el-button type="primary" @click="queryPage()">查询</el-button>
            </el-form-item>
        </el-form>


    
        <el-table border :data="data" v-if="showTable">
            <el-table-column prop="time" label="time" />
            <el-table-column prop="count" label="count" />
            <el-table-column prop="profit" label="profit" />
            <el-table-column prop="avgDuration" label="平均用时" />
        </el-table>
    </div>
</template>

<script>

export default {
    data() {
        return {
            data: {},
            selectedDate: 'today',
            traceReportDto: {
                start: null,
                end: null,
            },
            timeRange: [],
            showTable:false,
        }
    },
    methods: {
        queryPage() {
            this.traceReportDto.start = this.timeRange[0];
            this.traceReportDto.end = this.timeRange[1];
            this.$http.post("/traceReport/list", this.traceReportDto).then(resp => {
                this.showTable=true;
                this.data = resp.data;

                //增加总计
                var totalCount=0;
                var totalProfit=0;
                for (let i = 0; i < this.data.length ; i++) {
                    totalCount+=this.data[i].count;
                    totalProfit+=this.data[i].profit;
                }
                var avgProfilt=totalProfit/this.data.length;
                var avgCount=totalCount/this.data.length;
                this.data.push({
                    time:'总计',
                    count:totalCount+" ("+this.numberFormatter(avgCount)+")",
                    profit:this.numberFormatter(totalProfit)+" ("+this.numberFormatter(avgProfilt)+")",
                });
            });
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
            this.timeRange = [start.format('yyyy-MM-dd 00:00:00'), end.format('yyyy-MM-dd 23:59:59')];
            this.queryPage();
        },
        numberFormatter(data){
            let temp = data||"notNum";
            if(isNaN(temp)){
                temp = 0;
            }
            return parseFloat(temp.toFixed(2))+"";
        }
    },
    created() {
        this.selectedDate = '今日';
        this.changeDate();
    }

}
</script>

