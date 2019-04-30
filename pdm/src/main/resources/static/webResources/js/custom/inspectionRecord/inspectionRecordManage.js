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
        {label:$.i18n.prop('fileMetaData.fileName'),name:'fileName',index:'fileName', width: '120',  align: 'center'},
        {label:$.i18n.prop('fileMetaData.fileSize'),name:'fileSize',index:'fileSize', width: '120',  align: 'center'},
        {label:$.i18n.prop('fileSize.suffix'),name:'fileType',index:'fileType', width: '120',  align: 'center'},
        {label:$.i18n.prop('fileMetaData.uploader'),name:'uploader',index:'uploader', width: '120',  align: 'center'},
        {label:$.i18n.prop('fileMetaData.uploadTime'),name:'uploadTime',index:'uploadTime', width: '120',  align: 'center'},
		{
            label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
            	var detail = "";
            	detail +="<a href='/fileDownload/" + options.rowId + "' title="+$.i18n.prop('download')+"><img src='/webResources/img/file/download.png'/></a>";
            	/*删除文件*/
            	/*if($('#allowDelete').length>0){
                    detail +="<a class='delete' href='javascript:void(0)'  title="+$.i18n.prop('delete')+"><img src='/webResources/img/file/delete.png'/></a>";
				}*/
            	return detail;
            },
        }
	];

	 //每页显示多少行
	 var rowNum=20;
	 var page=0;
	 var url='/fileDownload/list/GRID';
	 var sort;
	 var selectRowId;

	 //请求参数
	 var params={}
	 //设置请求需要的一些参数
	 params['identification']="inspectionRecord-"+$('#deviceId').val()+"-"+$('#deviceSN').val();
	 params['rowId']='stringObjectId';
	 params['showAttributes']='fileName,fileSizeH,suffix,uploaderName,uploadTime,deviceId';//要获取的属性名
	 params['page']=page;
	 params['size']=rowNum;
	 params['sort']=sort;
     //params['identification']="inspectionRecord";

	 var inspectionRecordOperate = new Vue({
	   el: '#inspectionRecordOperate',
	   data: params,
	   methods:{
           upload : function(){
               var func=function(){
                   if($("#documentForm").valid()){
                       $("#documentForm").submit();
                       return true;
                   }else{
                       return false;
                   }
               };
               createModalPage("上传","/inspectionRecord/upload/view?deviceId="+$('#deviceId').val()+"&deviceSN="+$('#deviceSN').val(),func);
           }
	   },
	 })

	 var myGrid = jQuery("#inspectionRecordTable");
	 var myPager = jQuery("#inspectionRecordPager");

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

    // 删除按钮绑定事件
    $('#delete').on("click", function () {
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
                    url: '/deleteUploadFile/deleteById',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#inspectionRecordTable").trigger("reloadGrid");
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

})
