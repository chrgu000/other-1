/**
 * 
 */
$(document).ready(function() {
	$("#dailyMaintenanceForm").validate({
		//debug:true,
		//ignore: ":hidden:not(select)",
		rules : {
            mainCategory : {
				required : true,
			},
            subCategory : {
            	required: true,
			},
            code : {
            	required : true,
			},
		},
		/*errorPlacement: function(error, element) {
            if(element.parent('#selectMaintainer').length) {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        },*/
		submitHandler: function(form) {
			var options = {
				type : "post",
				url : '/dailyMaintenance/save?deviceId='+$('#deviceId').val(),
				success : function(data) {
					$.unblockUI();
					if(data.result=='success'){
						info('操作成功');
						$("#dailyMaintenanceTable").trigger("reloadGrid");
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
	

	
	jQuery.validator.addMethod("checkUniqueSerialNo", function(value, element) {
		var flag;
		$.ajax({
			url : '/device/checkExist',
			data : {
				'serialNo' : $("#serialNo").val(),
			},
			type : 'get',
			dataType : 'json',
			async:false,
			success : function(msg) {
				// true表示已经存在
				if (msg.result == true) {
					if($("#deviceId").val()!=null&&$("#deviceId").val()!=""){
						// 判断存在的序列号是否是当前设备
						$.ajax({
							url : '/device/checkExist',
							data : {
								'id' : $("#deviceId").val(),
								'serialNo' : $("#serialNo").val(),
							},
							type : 'get',
							dataType : 'json',
							async:false,
							success : function(msg) {
								// true当前序列号就是属于当前设备
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
	}, "您输入的序列号已存在");
	
	$("#lastCheckMaintainDate").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'})

    $("#lastExchangeMaintainDate").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'})
    $("#produceDate").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'})


	
    // 当选择一个条目时不离开文本域
    .bind( "keydown", function( event ) {
      if ( event.keyCode === $.ui.keyCode.TAB &&
          $( this ).data( "ui-autocomplete" ).menu.active ) {
        event.preventDefault();
      }
    })

	 
})