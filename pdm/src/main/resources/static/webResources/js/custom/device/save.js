/**
 *
 */
$(document).ready(function() {
    var groupId;//组id
    var serial_noArr;//存放设备关联的网元号
    getIotxInfo();//根据组获取网元信息
    findIotx();
    $("#deviceForm").validate({
		//debug:true,
		ignore: ":hidden:not(select)",
		rules : {
			"project.number" : {
				required : true,
			},
			productNo : {
				required : true,
			},
			productName : {
				required : true,
			},
			productSpecifications : {
				required : true,
			},
			devCategory : {
				required : true,
			},
			serialNo : {
				required : true,
				checkUniqueSerialNo : true,
			},
           serial_no : {
				// required : true,
				checkUniqueSerial_no : true,
			},
			rfid : {
				checkUniqueRfid : true,
			},
			remindReceivers : {
				required : true,
			},
		},
		errorPlacement: function(error, element) {
            if(element.parent('#remindReceiversSelect').length ) {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        },
		submitHandler: function(form) {
			$("#project\\.number").attr("disabled","disabled")
			var options = {
				type : "post",
				url : '/device/save',
				data :  $.extend($("#deviceForm").serialize(),{
                    deviceId:$("#deviceId").val()
				}),
				success : function(data) {
					//alert($('#group').val());

					$.unblockUI();
					if(data.result=='success'){
                        //$('#groupId').val($('#group').val());
						info('操作成功');
						$("#deviceTable").trigger("reloadGrid");
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

	 $("#remindReceivers").chosen();
	 $("#owners").chosen();

    $("#fileUpLoad").fileinput({
        language: 'zh', //设置语言
        maxFileCount: 10,
        showUpload: false, //是否显示上传按钮
        maxFileSize : 50000,
        allowedFileExtensions: ["jpg","jpeg","png"]
    });

    $("#topology").fileinput({
        language: 'zh', //设置语言
        maxFileCount: 1,
        showUpload: false, //是否显示上传按钮
        maxFileSize : 50000,
        allowedFileExtensions: ["jpg","jpeg","png"]
    });

     //var iotxIds = $('#iotxIds').val().replace("[","").replace("]","");
	//设备id不为空表示-修改功能
	 if($("#deviceId").val()!=null&&$("#deviceId").val()!=""){
		$.each(eval($("#receiverIds").val()),function(index,item){
			$("#remindReceivers option[value='"+ item.id +"']").attr("selected","selected");
		});

         $("#group option[value='"+  groupId +"']").attr("selected","selected");
         getIotxInfo();
		//网元序列号
		//var iotxList = JSON.parse($('#iotxIds').val());
		 console.log("网元号:"+serial_noArr);
		$.each(eval($("#ownerIds").val()),function(index,item){
			$("#owners option[value='"+ item.id +"']").attr("selected","selected");
		});
	 }

    /**
	 * 取消绑定按钮的显示和隐藏
     */
	 if(serial_noArr){
         $('#btn-cancel').show();
	 }else{
         $('#btn-cancel').hide();
	 }


    //根据device获取关联的iotx网元信息
    function findIotx() {
        var deviceId = $("#deviceId").val();
        $.ajax({
            type:"GET",
            url: "/iotx/management/data?deviceId="+deviceId,
            async:false,
            success: function(data){
                if(data != null && data.length > 0){
                    data = JSON.parse(data);
                    console.log(data);
                    for(var i=0; i<data.length; i++) {
                        groupId = data[i].groupId;//组id
                        serial_noArr=data[i].serialNo;//网元号
                        //$("#serial_no").html("<option value=" + data[i].serialNo + ">" + data[i].serialNo + "</option>");
                    }
                }
            }
        })
    };


    /**
     * 根据组获取网元信息
     */
    function getIotxInfo(){
        var groupNo = $("#group").val();
        $("#serial_no").html("");
        $("#serial_no option").remove();
        $.ajax({
            type:"GET",
            url: "/iotx/group/iotxList?id="+groupNo,
            /*data:{'bankNo':bankNo},*/
            success: function(data){
                if(data){
                    data = jQuery.parseJSON(data);
                }
                /* alert(data);
                alert(data.length);*/
                if(data != null && data.length > 0){
                    var html="";
                    for(var i=0; i<data.length; i++){
                        if(serial_noArr && serial_noArr==data[i].serialNo){
                            html+="<option value="+data[i].serialNo+" selected='selected'>"+data[i].serialNo+"</option>";
                        }else{
                            html+="<option value="+data[i].serialNo+">"+data[i].serialNo+"</option>";
                        }
                    }
                }
                $("#serial_no").html(html);
            }
        })
    };


    /**
	 * 取消关联点击事件
     */
    $('#btn-cancel').click(function () {
        var fDeleteIotx = function(){
            $.ajax({
                type : "post",
                async : false, //同步执行
                data: {
                	deviceId:$("#deviceId").val(),
                    serial_no:$("#serial_no").val()
				},
                url:'/device/cancelConnect',
                success:function(data){
                    data = jQuery.parseJSON(data);
                    $.unblockUI();
                    if(data.result=='success'){
                        var func = function(){
                            window.location.href="/device/management/view";
                        }
                        infoAndFunc('操作成功',func);
                    }else{
                        warning('操作失败');
                    }
                },
                error : function(errorMsg) {
                    $.popAlert("请求数据失败啦!");
                }
            });
            $.blockUI({
                message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
                // 指的是提示框的css
                css: {
                    width: "0px",
                    top: "40%",
                    left: "50%"
                },
            });
        };
        confirm("是否删除",fDeleteIotx);
    })

	/*
	设备序列号是否存在校验
	 */
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

    /**
	 * 判断网元号是否已被绑定
     */
    jQuery.validator.addMethod("checkUniqueSerial_no", function(value, element) {
       var flag ;
        $.ajax({
            url : '/device/checkSerial_noExist',
            data : {
                'serial_no' : $("#serial_no").val(),
                'deviceId' : $("#deviceId").val()
            },
            type : 'get',
            dataType : 'json',
			async : false,//异步
            success : function(msg) {
            	//result==true 已存在
                if (msg.result === false) {
                    flag =  true;
                }
                if(msg.result === true){
                    flag = false;
                }

            }
        });
        return flag;
    }, "该网元已被其它设备绑定");


    $("#productionTime").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true',endDate:new Date()});
    $("#commissioningTime").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true',startDate:$('#productionTime').val(),endDate:new Date()});
    $("#productionTime").change(function () {
        $("#commissioningTime").datetimepicker("setStartDate",$('#productionTime').val());
    });
	jQuery.validator.addMethod("checkUniqueRfid", function(value, element) {
		if($("#rfid").val() == null || $("#rfid").val() == ""){
			return true
		}
		var flag;
		$.ajax({
			url : '/device/checkExist',
			data : {
				'rfid' : $("#rfid").val(),
			},
			type : 'get',
			dataType : 'json',
			async:false,
			success : function(msg) {
				// true表示已经存在
				if (msg.result == true) {
					// 判断存在的rfid是否是当前设备
					if($("#deviceId").val()!=null&&$("#deviceId").val()!=""){
						$.ajax({
							url : '/device/checkExist',
							data : {
								'id' : $("#deviceId").val(),
								'rfid' : $("#rfid").val(),
							},
							type : 'get',
							dataType : 'json',
							async:false,
							success : function(msg) {
								// true当前rfid就是属于当前设备
								if (msg.result == true) {
									flag = true;
								} else{
									flag = false;
								}
							}
						});
					} else {
						flag = false;
					}

				} else{
					flag = true;
				}
			}
		});
		return flag;
	}, "您输入的rfid已存在");

	//search autocomplete
	 $( "#project\\.number" )
    // 当选择一个条目时不离开文本域
    .bind( "keydown", function( event ) {
      if ( event.keyCode === $.ui.keyCode.TAB &&
          $( this ).data( "ui-autocomplete" ).menu.active ) {
        event.preventDefault();
      }
    })
    .autocomplete({
      source: function( request, response ) {

        $.ajax({
				url : '/project/autocomplete',
				data : {
					'number' :  request.term,
				},
				type : 'get',
				dataType : 'json',
				success : function( datas ) {
					response($.each(datas,function(i,value) {
						return {
							label : this.label,
							value : this.value
						}
					}));
				}

        });

      },
      search: function() {
        // 自定义最小长度
        var term = this.value;
        if ( term.length < 1 ) {
          return false;
        }
      },
      focus: function() {
        // 防止在获得焦点时插入值
        return false;
      },
      select: function( event, ui ) {
        var words=ui.item.label.split("-")
        $( "#project\\.number" ).val(words[0]);
        $( "#project\\.name" ).val(words[1]);
        $( "#project\\.location" ).val(words[2]);
        $( "#project\\.id" ).val(ui.item.value);
        return false;
      }
    });


    // $("#group").prepend("<option  value='0'>请选择</option>");
    $("#group").change(function(){
        getIotxInfo();
    });



})
