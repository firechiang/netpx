define(["text!./template.html"], function(template) {
	return {
		template : template,
		data : function() {
			return {
				loading: false,
				addUserDialog: false,
		        currentPage1: 5,
		        currentPage2: 5,
		        currentPage3: 5,
		        currentPage4: 4,
		        userFormName: 'userForm',
		        user: {
		        	
		        	
		        },
				users: [
					{
						username: 'maomoa',
						accessKey: 'f4fdg5d5fgfd4g4fdgd4f5gdfd54f4d',
						secondaryDomain: 'org.coma.com',
						localPort: 80,
						disable: true,
						createTime: '2020-02-03 12:02:10',
						remark: '的是罚了多少省的浪费省的浪费类似的发牢骚的否'
					},
					{
						username: 'tiantia',
						accessKey: 'dg4f1gs45d4s4f54dd5f4ds1d4f5dsf',
						secondaryDomain: 'tiantia.coma.com',
						localPort: 80,
						disable: true,
						createTime: '2020-12-03 05:07:59',
						remark: '是代理商都发了都是撒大；阿萨德拉屎的拉'	
					},
					{
						username: 'wenjuan',
						accessKey: 'cv14vaada8w4a4dsd5as5x1c54s54d5',
						secondaryDomain: 'wenjuan.coma.com',
						localPort: 80,
						disable: true,
						createTime: '2020-07-25 10:36:22',
						remark: '都是范德萨了三大是了两块两款'
					}
				],
				// 用户表单验证规则
				userRules: {
			        username: [
			            { 
			            	required: true,trigger: 'blur', validator: function(rule, value, callback) {
			            		var callbackParam;
			            		if(!value) {
			            			callbackParam  = new Error('用户名不能为空');
			            		}
			                    if(!(/^[a-zA-Z0-9|_]{1,32}$/.test(value))) {
			                    	callbackParam  = new Error('用户名只能是字母数字或下划线');
			                    }
			                    callback(callbackParam);
			                } 
			            },
			        ],
			        localPort: [
			            { 
			            	required: true,trigger: 'blur', validator: function(rule, value, callback) {
			            		var callbackParam;
			                    if(!(/^[1-9]\d{2,4}$/.test(value))) {
			                    	callbackParam  = new Error('长度应在 2 到 5 个数字');
			                    }
			                    callback(callbackParam);
			                } 
			            },
			        ]
				},
			}
		},
    	created:function(){
    		
    	},
        methods: {
        	// 格式化时间
            formatDate: function(row) {
                //return new Date(row.date).toLocaleDateString();
            	return row.createTime;
            },
        	// 监听排序
        	handleSortChange: function(sortWay) {
        		console.info("排序："+sortWay);
        	},
        	// 监听选择列
            handleSelectionChange: function(val) {
            	console.info("已选择列");
            	console.info(val);
            },
            // 监听是否启用开关
            handleDisable: function(scope) {
            	scope.row.disable = !scope.row.disable;
            	console.info(scope);
            },
            // 监听搜索按钮
            handleSearch: function() {
            	
            },
            // 监听添加用户按钮
            handleAddUser: function() {
            	this.user = {};
            	this.addUserDialog = true;
            },
            // 监听确认添加用户按钮
            handleUserSave: function() {
                this.$refs[this.userFormName].validate((valid) => {
                    if (valid) {
                      alert('submit!');
                    } else {
                      console.log('error submit!!');
                      return false;
                    }
                });
            },
            handleSizeChange: function(val) {
                console.log(`每页 ${val} 条`);
              },
              handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
              }
        }
	}
});