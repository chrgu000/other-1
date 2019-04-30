/**
 * 
 */
$(document).ready(function() {
	$("#projectForm").validate({
		//debug:true,
		rules : {
			number : {
				required : true,
                checkUniqueNumber : true
			},
            name : {
                required : true,
            },
            location : {
                required : true,
            },
		},
		submitHandler: function(form) {
			var options = {
				type : "post",
				url : '/project/save',
				success : function(data) {
					$.unblockUI();
					if(data.result=='success'){
						info('操作成功');
						$("#projectTable").trigger("reloadGrid");
					}else if(data.result=='error'){
						warning('操作失败:'+data.message);
					}else{
						warning('操作失败');
					}
				}
			};
			
			$.blockUI({
				message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
				// 指的是提示框的css
				css: {
                    width: "0px",
                    top: "40%",
                    left: "50%"
                },
			});
			$(form).ajaxSubmit(options);     
		}  
	});
	 
	jQuery.validator.addMethod("checkUniqueNumber", function(value, element) {
		var flag;
		$.ajax({
			url : '/project/checkExist',
			data : {
				'number' : $("#number").val(),
			},
			type : 'get',
			dataType : 'json',
			async:false,
			success : function(msg) {
				if (msg.result == true) {
					if($("#id").val()!=null&&$("#id").val()!=""){
						$.ajax({
							url : '/project/checkExist',
							data : {
								'id' : $("#id").val(),
								'number' : $("#number").val(),
							},
							type : 'get',
							dataType : 'json',
							async:false,
							success : function(msg) {
								if (msg.result == true) {
									flag = true;
								} else{
									flag = false;
								}
							}
						});
					}else{
						flag = false;
					}
					
				} else{
					flag = true;
				}
			}
		});
		return flag;
	}, "您输入的编号已存在");
	
})