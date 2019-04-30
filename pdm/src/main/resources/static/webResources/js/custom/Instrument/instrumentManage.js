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

	var colModel=[
     	{label:$.i18n.prop('instrument.name'),name:'name',index:'name', width: '120',  align: 'center'},
		{label:$.i18n.prop('instrument.code'),name:'code',index:'code', width: '120',  align: 'center'},
		{label:$.i18n.prop('instrument.mark'),name:'mark', index:'mark', width:'120', align: 'center'},
		{label:$.i18n.prop('instrument.produceDate'),name:'produceDate',index:'produceDate', width: '120', align: 'center'},
        {label:$.i18n.prop('instrument.lastCheckOutDate'),name:'lastCheckOutDate',index:'lastCheckOutDate', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                return '<a href="javascript:void(0);" onclick="updateCheckTime('+options.rowId+',\''+cellvalue+'\')" style="color: #0688EC;text-decoration: underline;">'+cellvalue+'</a>'
            }
        },
        {label:$.i18n.prop('dailyMaintenance.examineCycle'),name:'examineCycle',index:'examineCycle', width: '120',  align: 'center'},
        {label:$.i18n.prop('dailyMaintenance.checkRemindDate'),name:'checkRemindDate',index:'checkRemindDate', width: '120',  align: 'center'},
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
        {label:$.i18n.prop('instrument.note'),name:'note',index:'note', width: '120',  align: 'center'},
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
	 var url='/instrument/management/data/GRID';
	 var sort;
	 var selectRowId;

	 //请求参数
	 var params={}
    //设置请求需要的一些参数
	params['rowId']='id'
    params['deviceId']=$('#deviceId').val();
    params['showAttributes']='name,code,mark,produceDate,lastCheckOutDate,examineCycle,checkRemindDate,checkRemindStatus,note';//要获取的属性名
    params['page']=page;
    params['size']=rowNum;
    params['sort']=sort;
	 var instrumentOperate = new Vue({
	   el: '#instrumentOperate',
	   data: params,
	   methods:{
           downloadTemplate : function(){
               window.location.href="/webResources/templates/instrument.xlsx"
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
               createModalPage("批量上传","/instrument/save/batch/view?deviceId="+$('#deviceId').val(),func);
           }
	   },
	 })

	 var myGrid = jQuery("#instrumentTable");
	 var myPager = jQuery("#instrumentPager");

	 myGrid.jqGrid({
 		datatype: "json",
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

	 // 仪表检验编辑按钮绑定事件
	 $(document).on("click","a[edit]",function(){
		 var func=function(){
			 if($("#instrumentForm").valid()){
				 $("#instrumentForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("编辑仪表检验","/instrument/gotoEditInstrumentPage?id="+$(this).attr("edit"),func);
	 });

    // 删除按钮绑定事件
    $('.company-button').on("click", "#delete", function () {
        var id = [];
        var idArr=myGrid.getGridParam('selarrrow');
        for(var i=0;i<idArr.length;i++){
            id.push(idArr[i]);
        }
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
                    url: '/instrument/delete',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#instrumentTable").trigger("reloadGrid");
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

    //add按钮被点击
    $("#add").click(function () {
        var func = function () {
            if ($("#instrumentForm").valid()) {
                $("#instrumentForm").submit();
                return true;
            } else {
                return false;
            }
        };
        createModalPage("添加仪表检验信息", "/instrument/gotoAddInstrumentPage?deviceId="+$('#deviceId').val(), func);
    })

    updateCheckTime = function(rowId, value) {
        var func=function(){
            if($("#editTimeForm").valid()){
                $("#editTimeForm").submit();
                return true;
            }else{
                return false;
            }
        };
        createModalPage("编辑上次校验时间","/instrument/gotoEditTimePage?id="+rowId+"&date="+encodeURI(value),func);
    }

})
