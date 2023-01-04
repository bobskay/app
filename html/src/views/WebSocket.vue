<template>
    <div>
      <p>
        <button @click="send('1')">1分钟</button>
        <button @click="send('2')">10分钟</button>
        <button @click="send('3')">1小时</button>
        <button @click="send('4')">24小时</button>
      </p>
      <ul>
        <li v-for="(item,index) in tableData" :key="index">{{ item.name }}:{{ item.value }}</li>
      </ul>
    </div>
  </template>
  
  <script>
  import Stomp from 'stompjs'
  import SockJS from 'sockjs-client'
  
  // const headers = {}
  const headers = {
    login: 'mylogin',
    passcode: 'mypasscode',
    "Access-Control-Allow-Origin":"http://localhost:8082",
    'client-id': 'my-client-id',
    Author: '1235465tyrgw32rsgg'
  };
  export default {
    data () {
      return {
        socketUrl: 'http://127.0.0.1:8888/stomp/websocketJS', // ws://localhost:8080/websocket/ws
        tableData: [],
        reconnecting: false,
        socket: null,
        stompClient: null
      }
    },
    mounted () {
      this.initWebsocket()
    },
    beforeDestroy() {
      this.closeSocket()
    },
    methods: {
      /* 只需要在连接服务器注册端点endPoint时，写访问服务器的全路径URL：
        new SockJS('http://127.0.0.1:9091/sbjm-cheng/endpointOyzc');
        其他监听指定服务器广播的URL不需要写全路径
        stompClient.subscribe('/topic/getResponse',function(response){})
      */
      /* 创建stompClient： (1) 使用原生的websocket (2) 使用定制的websocket(例如sockjs包裹的websocket)
      ① Stomp.client("ws://localhost:61614/stomp")
      ② SockJS的url是http、https协议，而不是 ws
        const socket = new SockJS("http://127.0.0.1:9091/sbjm-cheng/endpointOyzc"); 
         Stomp.over(socket)
  
         const ws = new Websocket("ws://localhost:61614/ws")
         Stomp.over(ws)
      */
      initWebsocket () {
        /* 
        ① 创建sockJS对象；
        ② 创建stomp客户端
        ③ stompClient客户端 连接 stomp服务器
        */
        this.socket = new SockJS(this.socketUrl)
        this.stompClient = Stomp.over(this.socket)
  
        this.stompClient.connect(
          headers, // headers头部信息。可添加客户端的认证信息。也可以不添加信息，headers 直接就设置为 {}
          frame => {
            // 连接成功： 订阅服务器的地址。为了浏览器可以接收到消息，必须先订阅服务器的地址
            this.connectSucceed()
          }, err => {
            console.log('连接失败');
            console.log(err);
            // 连接失败的回调
          //  this.reconnect(this.socketUrl, this.connectSucceed)
          })
      },
      /* 连接成功的回调：订阅服务器的地址。为了浏览器可以接收到消息，必须先订阅服务器的地址 */
      connectSucceed () {
        // 设置心跳发送接受频率（ms）默认为10000ms。 heart-beating是利用window.setInterval()去规律地发送heart-beats或者检查服务端的heart-beats。
        this.stompClient.heartbeat.outgoing = 10000
        this.stompClient.heartbeat.incoming = 0
  
        this.stompClient.subscribe('/topic/dashboard/data', res => {
          this.tableData = res.body.list
        })
  
      /* 
      当客户端与服务端连接成功后，可以调用 send()来发送STOMP消息。这个方法必须有一个参数，用来描述对应的STOMP的目的地。
      另外可以有两个可选的参数：headers，object类型包含额外的信息头部；body，一个String类型的参数。
  
      client.send("/queue/test", {priority: 9}, "Hello, STOMP");
      // client会发送一个STOMP发送帧给/queue/test，这个帧包含一个设置了priority为9的header和内容为“Hello, STOMP”的body。
      */
        this.stompClient.send('/topic/dashboard/send',{}, '1')
      },
      reconnect (socketUrl, callback) {
        this.reconnecting = true
        let connected = false
        const timer = setInterval(() => {
          this.socket = new SockJS(socketUrl)
          this.stompClient = Stomp.over(this.socket)
          this.stompClient.connect(headers, frame => {
            this.reconnectting = false
            connected = true
            clearInterval(timer)
            callback()
          }, err => {
            console.log('Reconnect failed！');
            if(!connected) console.log(err);
          })
        }, 1000);
      },
      closeSocket(){
        if(this.stompClient != null){
          this.stompClient.disconnect()
          // this.stompClient.disconnect(()=>{
          //   console.log('连接关闭')
          // });
        }
      },
      send(flag){
        this.stompCLient.send('/topic/dashboard/send',{}, flag)
      }
    },
  }
  </script>