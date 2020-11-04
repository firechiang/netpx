require.config({
    baseUrl:"/static",
    paths: {
        'text': 'assets/require/text',
	    'vue' : 'assets/vue/vue.min',
	    'HeyUI': 'assets/heyui/heyui',
	    'ELEMENT': 'assets/elementui/index',
	    'vue-router':'assets/vue/vue-router.min'
    },
    // 添加统一参数
    /*urlArgs:function(id, url) {
        return (url.indexOf('?') === -1 ? '?' : '&') + '';
    },*/
    // 项目引导模块
    deps : ['main'] 
});
define("main",function(require) {
    //require("text");
	var Vue = require("vue");
    //var heyui = require("HeyUI");
    //Vue.use(heyui);
    var elementUI = require("ELEMENT");
	Vue.use(elementUI);
	//Vue.component("app-header",require("views/home/header/index"));
	new Vue({
		el:"#app",
		router:require("routes/index")
	});
});