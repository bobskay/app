(function(){"use strict";var e={3664:function(e,t,n){var r=n(6369),l=n(8499),o=n.n(l),a=function(){var e=this,t=e._self._c;return t("div",{attrs:{id:"app"}},[t("router-view")],1)},s=[],i={name:"App"},u=i,c=n(1001),p=(0,c.Z)(u,a,s,!1,null,null,null),d=p.exports,f=n(2631),h=function(){var e=this,t=e._self._c;return t("el-row",[t("el-col",{attrs:{span:8}},[t("el-card",{staticClass:"box-card"},[t("div",{staticClass:"user"},[t("img",{attrs:{src:n(5883),alt:""}}),t("div",{staticClass:"userInfo"},[t("P",{staticClass:"name"},[e._v("Admin")]),t("p",{staticClass:"access"},[e._v("管理员")])],1)]),t("div",{staticClass:"loginInfo"},[t("p",[e._v("上次登录时间: "),t("span",[e._v("2022-01-01")])]),t("p",[e._v("上次登录地点: "),t("span",[e._v("武汉")])])])])],1),t("el-col",{attrs:{span:16}})],1)},m=[],v={data(){return{}},mounted(){this.$http.get("/book/list").then((e=>{console.log(e.data)}))}},_=v,b=(0,c.Z)(_,h,m,!1,null,"9de24d42",null),g=b.exports,C=function(){var e=this,t=e._self._c;return t("h1",[e._v("我是user")])},w=[],x={data(){return{}}},y=x,O=(0,c.Z)(y,C,w,!1,null,null,null),k=O.exports,M=function(){var e=this,t=e._self._c;return t("div",[t("el-container",[t("el-aside",{attrs:{width:"auto"}},[t("common-aside")],1),t("el-container",[t("el-header",[t("common-header")],1),t("el-main",[t("router-view")],1)],1)],1)],1)},Z=[],P=function(){var e=this,t=e._self._c;return t("el-menu",{staticClass:"el-menu-vertical-demo",attrs:{"default-active":"1-4-1",collapse:e.isCollapse,"background-color":"#545c64","text-color":"#fff","active-text-color":"#ffd04b"},on:{open:e.handleOpen,close:e.handleClose}},[t("h3",[e._v(e._s(e.isCollapse?"后台":"后台管理系统"))]),e._l(e.noChildren,(function(n){return t("el-menu-item",{key:n.name,attrs:{index:n.name},on:{click:function(t){return e.clickMenu(n)}}},[t("i",{class:`el-icon-${n.icon}`}),t("span",{attrs:{slot:"title"},slot:"title"},[e._v(e._s(n.label))])])})),e._l(e.hasChildren,(function(n){return t("el-submenu",{key:n.label,attrs:{index:n.label}},[t("template",{slot:"title"},[t("i",{class:`el-icon-${n.icon}`}),t("span",{attrs:{slot:"title"},slot:"title"},[e._v(e._s(n.label))])]),e._l(n.children,(function(n){return t("el-menu-item-group",{key:n.name},[t("el-menu-item",{attrs:{index:n.name},on:{click:function(t){return e.clickMenu(n)}}},[e._v(e._s(n.label))])],1)}))],2)}))],2)},j=[],$=(n(7658),{data(){return{menuData:[{path:"/",name:"home",label:"首页",icon:"s-home",url:"/Home"},{path:"/mall",name:"mall",label:"商品管理",icon:"video-play",url:"MallMananger/MallManager"},{path:"/user",name:"user",label:"用户管理",icon:"user",url:"UserManager/UserManager"},{label:"其他",icon:"location",children:[{path:"/pageOne",name:"page1",label:"页面1",url:"Other/PageOne"},{path:"/pageTwo",name:"page2",label:"页面1",url:"Other/PageTwo"}]}]}},methods:{handleOpen(e,t){console.log(e,t)},handleClose(e,t){console.log(e,t)},clickMenu(e){var t=e.path;"/"==t&&(t="/home"),this.$route.path!=t&&this.$router.push(t)}},computed:{noChildren(){return this.menuData.filter((e=>!e.children))},hasChildren(){return this.menuData.filter((e=>e.children))},isCollapse(){return this.$store.state.tab.isCollapse}}}),T=$,S=(0,c.Z)(T,P,j,!1,null,"eb87c1c2",null),A=S.exports,D=function(){var e=this,t=e._self._c;return t("div",{staticClass:"header-container"},[t("div",{staticClass:"l-content"},[t("el-button",{attrs:{icon:"el-icon-menu",size:"mini"},on:{click:e.handleMenu}}),t("span",{staticClass:"text"},[e._v("首页")])],1),t("div",{staticClass:"r-content"},[t("el-dropdown",[t("span",{staticClass:"el-dropdown-link"},[t("img",{staticClass:"user",attrs:{src:n(5883)}})]),t("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[t("el-dropdown-item",[e._v("个人中心")]),t("el-dropdown-item",[e._v("退出")])],1)],1)],1)])},U=[],H={data(){return{}},methods:{handleMenu(){this.$store.state.tab.isCollapse=!this.$store.state.tab.isCollapse}}},I=H,q=(0,c.Z)(I,D,U,!1,null,"30d6b2fa",null),z=q.exports,E={data(){return{}},components:{CommonAside:A,CommonHeader:z}},F=E,L=(0,c.Z)(F,M,Z,!1,null,null,null),R=L.exports,B=function(){var e=this,t=e._self._c;return t("div",[e._v(" maill ")])},G=[],J={},K=(0,c.Z)(J,B,G,!1,null,null,null),N=K.exports,Q=function(){var e=this,t=e._self._c;return t("div",[e._v(" page1 ")])},V=[],W={data(){return{}}},X=W,Y=(0,c.Z)(X,Q,V,!1,null,null,null),ee=Y.exports,te=function(){var e=this,t=e._self._c;return t("div",[e._v(" 222222222222 ")])},ne=[],re={},le=(0,c.Z)(re,te,ne,!1,null,null,null),oe=le.exports;r["default"].use(f.ZP);const ae=new f.ZP({routes:[{path:"/",redirect:"/home",component:R,children:[{path:"home",component:g},{path:"user",component:k},{path:"mall",component:N},{path:"pageOne",component:ee},{path:"pageTwo",component:oe}]}]});var se=ae,ie=n(3822),ue={state:{isCollapse:!1},mutations:{collapseMenu(e){e.isCollapse=!e.isCollapse}}};r["default"].use(ie.ZP);var ce=new ie.ZP.Store({modules:{tab:ue}}),pe=n(4311);const de=pe.Z.create({baseURL:"/api/",timeout:1e4});de.interceptors.request.use((function(e){return e}),(function(e){return Promise.reject(e)})),de.interceptors.response.use((function(e){return e.data}),(function(e){return Promise.reject(e)}));var fe=de;r["default"].config.productionTip=!1,r["default"].use(o()),r["default"].prototype.$http=fe,new r["default"]({router:se,store:ce,el:"#app",render:e=>e(d)}).$mount("#app")},5883:function(e,t,n){e.exports=n.p+"img/user-default.092efa8b.jpg"}},t={};function n(r){var l=t[r];if(void 0!==l)return l.exports;var o=t[r]={exports:{}};return e[r](o,o.exports,n),o.exports}n.m=e,function(){var e=[];n.O=function(t,r,l,o){if(!r){var a=1/0;for(c=0;c<e.length;c++){r=e[c][0],l=e[c][1],o=e[c][2];for(var s=!0,i=0;i<r.length;i++)(!1&o||a>=o)&&Object.keys(n.O).every((function(e){return n.O[e](r[i])}))?r.splice(i--,1):(s=!1,o<a&&(a=o));if(s){e.splice(c--,1);var u=l();void 0!==u&&(t=u)}}return t}o=o||0;for(var c=e.length;c>0&&e[c-1][2]>o;c--)e[c]=e[c-1];e[c]=[r,l,o]}}(),function(){n.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return n.d(t,{a:t}),t}}(),function(){n.d=function(e,t){for(var r in t)n.o(t,r)&&!n.o(e,r)&&Object.defineProperty(e,r,{enumerable:!0,get:t[r]})}}(),function(){n.g=function(){if("object"===typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"===typeof window)return window}}()}(),function(){n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)}}(),function(){n.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}}(),function(){n.p="/"}(),function(){var e={143:0};n.O.j=function(t){return 0===e[t]};var t=function(t,r){var l,o,a=r[0],s=r[1],i=r[2],u=0;if(a.some((function(t){return 0!==e[t]}))){for(l in s)n.o(s,l)&&(n.m[l]=s[l]);if(i)var c=i(n)}for(t&&t(r);u<a.length;u++)o=a[u],n.o(e,o)&&e[o]&&e[o][0](),e[o]=0;return n.O(c)},r=self["webpackChunkmy_app"]=self["webpackChunkmy_app"]||[];r.forEach(t.bind(null,0)),r.push=t.bind(null,r.push.bind(r))}();var r=n.O(void 0,[998],(function(){return n(3664)}));r=n.O(r)})();
//# sourceMappingURL=app.4078881e.js.map