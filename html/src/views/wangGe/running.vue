<template>
    <div>
        <el-table border :data="data" style="width: 100%">
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="price" label="price" />
            <el-table-column prop="time" label="time" />
            <el-table-column prop="updateTime" label="updateTime" />
            <el-table-column prop="origQty" label="quantity" />
            <el-table-column prop="side" label="side" />
        </el-table>
    </div>
    
</template>

<script>

export default {
    data() {
        return {
            data:{},
        }
    },
    methods: {
       
    },
    created() {
        this.$http.post("/wangGe/openOrders").then(resp => {
                this.data = resp.data;
            });
    }

}
</script>

