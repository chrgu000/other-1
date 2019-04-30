/**
 *
 */
$(document).ready(function() {
	//不使用jqgrid默认的参数
	$.extend(jQuery.jgrid.defaults, {
	    prmNames: {
	        id: "_rowid", page: "_page", rows: "_rows",
	        oper: "_oper", sort: "_sidx", order: "_sord"
	    }
	});

/*    var fGridDate = function(cellValue, options, rowObject){
        var data = cellValue;
        if(data){
            return data.substring(0,11);
        }
    };*/

	var colModel=[
     	{label:$.i18n.prop('dailyMaintenance.mainCategory'),name:'mainCategory',index:'mainCategory', width: '120',  align: 'center'},
		{label:$.i18n.prop('dailyMaintenance.subCategory'),name:'subCategory',index:'subCategory', width: '120',  align: 'center'},
		{label:$.i18n.prop('dailyMaintenance.code'),name:'code', index:'code', width:'120', align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.mark'),name:'mark', index:'mark', width:'120', align: 'center'},
		{label:$.i18n.prop('dailyMaintenance.produceDate'),name:'produceDate',index:'produceDate', width: '120', align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.lastCheckMaintainDate'),name:'lastCheckMaintainDate',index:'lastCheckMaintainDate', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                return '<a href="javascript:void(0);" onclick="updateCheckTime(0,'+options.rowId+',\''+cellvalue+'\')" style="color: #0688EC;text-decoration: underline;">'+cellvalue+'</a>'
            }
        },
        {label:$.i18n.prop('dailyMaintenance.lastExchangeMaintainDate'),name:'lastExchangeMaintainDate',index:'lastExchangeMaintainDate', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                return '<a href="javascript:void(0);" onclick="updateCheckTime(1,'+options.rowId+',\''+cellvalue+'\')" style="color: #0688EC;text-decoration: underline;">'+cellvalue+'</a>'
            }
        },
        {label:$.i18n.prop('dailyMaintenance.examineCycle'),name:'examineCycle',index:'examineCycle', width: '120',  align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.changeCycle'),name:'changeCycle',index:'changeCycle', width: '120',  align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.checkRemindDate'),name:'checkRemindDate',index:'checkRemindDate', width: '120',  align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.exchangeRemindDate'),name:'changeCycle',index:'changeCycle', width: '120',  align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.checkRemindStatus'),name:'checkRemindStatus',index:'checkRemindStatus', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                var detail = ""
                if(cellvalue=="NORMAL"){
                    detail+="<img src='/webResources/img/device/material/clock2.png' style='margin-right: 10%;'>"
                }else if(cellvalue=="OVER"){
                    detail+="<img src='/webResources/img/device/material/clock1.png' style='margin-right: 10%;'>"
                }else if(cellvalue=="REMIND"){
                    detail+="<img src='/webResources/img/device/material/clock3.png' style='margin-right: 10%;'>"
                }
                detail += "<span style='color:#333333;'>"+$.i18n.prop('dailyMaintenance.checkRemindStatus.'+cellvalue)+"</span>";
                return detail;
            },
        },
        {label:$.i18n.prop('dailyMaintenance.exchangeRemindStatus'),name:'exchangeRemindStatus',index:'exchangeRemindStatus', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                var detail = ""
                if(cellvalue=="NORMAL"){
                    detail+="<img src='/webResources/img/device/material/clock2.png' style='margin-right: 10%;'>"
                }else if(cellvalue=="OVER"){
                    detail+="<img src='/webResources/img/device/material/clock1.png' style='margin-right: 10%;'>"
                }else if(cellvalue=="REMIND"){
                    detail+="<img src='/webResources/img/device/material/clock3.png' style='margin-right: 10%;'>"
                }
                detail += "<span style='color:#333333;'>"+$.i18n.prop('dailyMaintenance.exchangeRemindStatus.'+cellvalue)+"</span>";
                return detail;
            },
        },
        {label:$.i18n.prop('dailyMaintenance.maintainer'),name:'maintainer',index:'maintainer', width: '120',  align: 'center'},
		{
            label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
            	var detail ="<a href='#' title="+$.i18n.prop('edit')+" edit='"+options.rowId+"'><img src='/webResources/img/operate/edit.png'/></a>";
            	return detail;
            },
        },
	];

	 //每页显示多少行
	 var rowNum=15;
	 var page=0;
	 var url='/dailyMaintenance/management/data/GRID';
	 var sort;
	 var selectRowId;

	 //请求参数
	 var params={}
    //设置请求需要的一些参数
    params['rowId']='id';
    params['deviceId']=$('#deviceId').val();
    params['deviceSN']=$("#deviceSN").val();
    params['showAttributes']='mainCategory,subCategory,code,mark,produceDate,lastCheckMaintainDate,lastExchangeMaintainDate,examineCycle,changeCycle,checkRemindDate,exchangeRemindDate,checkRemindStatus,exchangeRemindStatus,maintainer';//要获取的属性名
    params['page']=page;
    params['size']=rowNum;
    params['sort']=sort;

	 var dailyMaintenanceOperate = new Vue({
	   el: '#dailyMaintenanceOperate',
	   data: params,
	   methods:{
           downloadTemplate : function(){
               window.location.href="/webResources/templates/dailyMaintenance.xlsx"
           },
           batchSave : function(){
               var func=function(){
                   if($("#excelForm").valid()){
                       $("#excelForm").submit();
                       return true;
                   }else{
                       return false;
                   }
               };
               createModalPage("批量上传","/dailyMaintenance/save/batch/view?deviceId="+$('#deviceId').val(),func);
           }
	   },
	 })

	 var myGrid = jQuery("#dailyMaintenanceTable");
	 var myPager = jQuery("#dailyMaintenancePager");

	 myGrid.jqGrid({
 		datatype: "json",
 		//url:url+"?deviceId="+$('#deviceId').val(),
 		url:url,
 		postData:params,
 		height: '100%',
 	   	colModel:colModel,
 	   	multiselect: true,
 	   	multiboxonly: true,
 	   	multiselectWidth: 30,
 	   	rowNum: rowNum,
 	   	autowidth: true,
 	   	forceFit: false,
 	   	altRows: false,
 	   	viewrecords: true,

 	   	gridComplete:function(){
 	   	 	var lastPage = myGrid.getGridParam('lastpage');//获取总页数
 	   		createPage(myGrid,myPager,lastPage,params.page,11,url,params);//调用自定义的方法来生成pager
	    },

    	//当触发排序时
    	onSortCol:function(index,iCol,sortorder){
    		params['sort']=index+","+sortorder;
    		myGrid.jqGrid().setGridParam({
				url:url,
				postData:params,
			}).trigger("reloadGrid");
    	}

     });

    //add按钮被点击
    $("#add").click(function () {
        var func = function () {
            if ($("#dailyMaintenanceForm").valid()) {
                $("#dailyMaintenanceForm").submit();
                return true;
            } else {
                return false;
            }
        };
        createModalPage("添加日常维护信息", "/dailyMaintenance/gotoAddDailyMaintenancePage?deviceId="+$('#deviceId').val(), func);
    })


	 // 修改按钮绑定事件
	 $(document).on("click","a[edit]",function(){
		 var func=function(){
			 if($("#dailyMaintenanceForm").valid()){
				 $("#dailyMaintenanceForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("编辑日常维护信息","/dailyMaintenance/gotoEditDailyMaintenancePage?id="+$(this).attr("edit"),func);
	 });


    // 删除按钮绑定事件
    $('.company-button').on("click", "#delete", function () {
        var id = [];
        var idArr=myGrid.getGridParam('selarrrow');
        for(var i=0;i<idArr.length;i++){
			id.push(idArr[i]);
		}
        console.log("选中id"+id);
        if (id != null && id != "") {
            console.log("id:" + id);
            var func = function () {
                $.blockUI({
                    message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
                    // 指的是提示框的css
                    css: {
                        width: "0px",
                        top: "40%",
                        left: "50%"
                    },
                });
                $.ajax({
                    url: '/dailyMaintenance/delete',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#dailyMaintenanceTable").trigger("reloadGrid");
                        } else if (msg.result == 'error') {
                            warning('操作失败:' + msg.message);
                        } else {
                            warning('操作失败');
                        }
                    }
                });
            };
            confirm("是否删除", func);
        }else {
            warning("请选择要删除的行");
		}
    });

    updateCheckTime = function(type,rowId, value) {
        var func=function(){
            if($("#editTimeForm").valid()){
                $("#editTimeForm").submit();
                return true;
            }else{
                return false;
            }
        };
        var str = type === 0 ? "编辑上次检查时间" : "编辑上次更换时间";
        createModalPage(str,"/dailyMaintenance/gotoEditTimePage?type="+type+"&id="+rowId+"&date="+encodeURI(value),func);
    }

})
