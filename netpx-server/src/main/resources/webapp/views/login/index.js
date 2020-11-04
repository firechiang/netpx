define(["text!./template.html"], function(template) {
	return {
		template : template,
		data : function() {
			return {
				user: {
			        username: '',
			        password: '',
			        rememberMe: false,
				},
		        loginLoading: false
			}
		},
	    computed: {
		    loginBtnText: function() {
		      if (this.loginLoading) {
		    	  return '登录中...';
		      }
		      return '登录';
		    }
		},
		methods: {
	        login: function() {
			    if (!this.username) {
			        this.$message.error('请填写用户名！！！');
			        return;
			    }
			    if (!this.password) {
			        this.$message.error('请填写密码');
			        return;
			    }
			    let loginParams = {username: this.username, password: this.password};
			    this.isBtnLoading = true;
			    requestLogin(loginParams).then(data => {
			        this.isBtnLoading = false;
			        let { msg, code, user } = data;
			        if (code !== 200) {
			            this.$message.error(msg);
			        } else {
			            localStorage.setItem('user', JSON.stringify(user));
			            if (this.$route.query.redirect) {
			                this.$router.push({path: this.$route.query.redirect});
			            } else {
			                this.$router.push({path: '/list'});
			            }
			        }
			    });
		    }
	    }
	}
});