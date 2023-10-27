const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  outputDir: '../app/trace/src/main/resources/static',
  transpileDependencies: true,
  lintOnSave:false,
  publicPath:'/',
  devServer: {
    host: "localhost",
    port: 8888, // 端口号
    https: false, // https:{type:Boolean}
    open: false, //配置自动启动浏览器
    proxy: {
      "/api": {
        target: "http://47.74.53.129:9999/",
        //target: "http://127.0.0.1:9999/",
        ws: false,// 是否启用websockets
        changeOrigin: true, //开启代理：在本地会创建一个虚拟服务端，然后发送请求的数据，并同时接收请求的数据，这样服务端和服务端进行数据的交互就不会有跨域问题
        pathRewrite: {
            '^/api': '' //这里理解成用'/api'代替target里面的地址,比如我要调用'http://40.00.100.100:3002/user/add'，直接写'/api/user/add'即可
          }
      },
      "/socket": {
        target: "ws://127.0.0.1:8888/",
        ws: true,// 是否启用websockets
        changeOrigin: true, //开启代理：在本地会创建一个虚拟服务端，然后发送请求的数据，并同时接收请求的数据，这样服务端和服务端进行数据的交互就不会有跨域问题
        pathRewrite: {
            '^/socket': '/' //这里理解成用'/api'代替target里面的地址,比如我要调用'http://40.00.100.100:3002/user/add'，直接写'/api/user/add'即可
          }
      },
      
    }
  }
})
