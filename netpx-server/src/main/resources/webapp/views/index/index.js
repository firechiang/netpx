define(["text!./template.html"], function(template) {
	return {
		template : template,
		data : function() {
		    return {
		        user: {
		            id: 'maomao',
		            username: 'maomao',
		            avatar: 'https://element.eleme.cn/1.4/favicon.ico'
		        },
		        activeMenu: ''
		    };
		},
    	created: function(){
    	    this.activeMenu = this.$route.name;
    	    //this.user = JSON.parse(localStorage.getItem('user'));
    	},
    	watch: {
	        '$route': function(to, from) {
	            this.activeMenu = this.$route.name;
	            //this.user = JSON.parse(localStorage.getItem('user'));
	        }
        },
        methods: {
            logout: function () {
                this.$confirm('确定要注销吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(function() {
                    //localStorage.removeItem('user');
                    //this.$router.push({ path: '/login' });
                }).catch(function() {
                	
                	
                });
            }
        }
	}
});