<template>
   <el-tabs type="border-card">
            <el-tab-pane label="挂单">
                 <running/>
            </el-tab-pane>
                            <el-tab-pane label="配置">
                               <config/>
                            </el-tab-pane>

                            <el-tab-pane label="历史记录">
                               
                                <history/>
                            </el-tab-pane>
                        </el-tabs>

</template>

<script>
import history from './history.vue'
import config from './config.vue'
import running from './running.vue'
export default {
  name: 'wangGe',
  props: [],
  components: {
    running,
    history,
    config,
  },
  data: function(){
    return {
      tabIndex: '0'
    }
  },
  created () {
    
  },
  methods: {

  },
}

</script>


