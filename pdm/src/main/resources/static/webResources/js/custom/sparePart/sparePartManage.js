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
     	{label:$.i18n.prop('sparePart.code'),name:'code',index:'code', width: '120',  align: 'center'},
		{label:$.i18n.prop('sparePart.specification'),name:'specification',index:'specification', width: '120',  align: 'center'},
		{label:$.i18n.prop('sparePart.name'),name:'name', index:'name', width:'120', align: 'center'},
		{label:$.i18n.prop('sparePart.num'),name:'num',index:'num', width: '120', align: 'center'},
		{label:$.i18n.prop('sparePart.lastYearUsed'),name:'lastYearUsed',index:'lastYearUsed', width: '120',  align: 'center'},
		{label:$.i18n.prop('sparePart.suggest'),name:'suggest',index:'suggest', width: '120',  align: 'center'},
        {label:$.i18n.prop('sparePart.needSupplement'),name:'needSupplement',index:'needSupplement', width: '120',  align: 'center'},
        // {label:$.i18n.prop('sparePart.remind'),name:'remind',index:'remind', width: '120',  align: 'center'},
		{
            label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
            	var detail ="<a href='#' id='edit' title="+$.i18n.prop('edit')+" edit='"+options.rowId+"'><img src='/webResources/img/operate/edit.png'/></a>";
            	return detail;
            },
        },
	];
	
	 //每页显示多少行
	 var rowNum=15;
	 var page=0;
	 var url='/sparePart/management/data/GRID';
	 var sort;
	 var selectRowId;
	 
	 //请求参数
	 var params={}
	 //设置请求需要的一些参数
	 params['deviceId']=$('#deviceId').val();
	 params['rowId']='id'
	 params['showAttributes']='code,specification,name,num,lastYearUsed,suggest,needSupplement,remind';//要获取的属性名
	 params['page']=page;
	 params['size']=rowNum;
	 params['sort']=sort;

	 var sparePartOperate = new Vue({
	   el: '#sparePartOperate',
	   data: params,
	   methods:{
           downloadTemplate : function(){
               window.location.href="/webResources/templates/sparePart.xlsx"
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
               createModalPage("批量上传","/sparePart/save/batch/view?deviceId="+$('#deviceId').val(),func);
           }
	   },
	 })
	 
	 var myGrid = jQuery("#sparePartTable");
	 var myPager = jQuery("#sparePartPager");
	 
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
	 
	 // 编辑按钮绑定事件
	 $(document).on("click","a[edit]",function(){
		 var func=function(){
			 if($("#sparePartForm").valid()){
				 $("#sparePartForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("修改备品备件","/sparePart/gotoEidtSparePartPage?id="+$(this).attr("edit"),func);
	 });

    // 删除按钮绑定事件
    $('.company-button').on("click", "#delete", function () {
        var id = [];
        var idArr=myGrid.getGridParam('selarrrow');
        for(var i=0;i<idArr.length;i++){
            id.push(idArr[i]);
        }
        if (id != null && id != "") {
            //console.log("id:" + id);
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
                    url: '/sparePart/delete',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#sparePartTable").trigger("reloadGrid");
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
            if ($("#sparePartForm").valid()) {
                $("#sparePartForm").submit();
                return true;
            } else {
                return false;
            }
        };
        createModalPage("添加备品备件", "/sparePart/gotoAddSparePartPage?deviceId="+$('#deviceId').val(), func);
    })

})