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
     	{label:$.i18n.prop('project.number'),name:'project.number',index:'project.number', width: '120',  align: 'center'},
		{label:$.i18n.prop('project.name'),name:'project.name',index:'project.name', width: '120',  align: 'center'},
		{label:$.i18n.prop('project.location'),name:'project.location', index:'project.location', width:'120', align: 'center'},
		{
            label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
            	var detail = "";
            	if($("#allowEdit").length>0){
            		detail += "<a href='#' title="+$.i18n.prop('edit')+" edit='"+options.rowId+"' style='margin: 0 5px;'><img src='/webResources/img/operate/edit.png'/></a>"
            	}
            	if($("#allowDelete").length>0){
            		detail += "<a href='#' title="+$.i18n.prop('delete')+" delete='"+options.rowId+"' style='margin: 0 5px;'><img src='/webResources/img/operate/delete.png'/></a>"
            	}
            	return detail;
            },
        },
	];

	 //每页显示多少行
	 var rowNum=20;
	 var page=0;
	 var url='/project/management/data/GRID';
	 var sort;
	 var selectRowId;

	 //请求参数
	 var params={}
	 //设置请求需要的一些参数
	 params['rowId']='id'
	 params['showAttributes']='number,name,location';//要获取的属性名
	 params['page']=page;
	 params['size']=rowNum;
	 params['sort']=sort;

	 var myGrid = jQuery("#projectTable");
	 var myPager = jQuery("#projectPager");

	 myGrid.jqGrid({
 		datatype: "json",
 		url:url,
 		postData:params,
 		height: '100%',
 	   	colModel:colModel,
 	   	multiselect: false,
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
	 $("#add").click(function(){
		 var func=function(){
			 if($("#projectForm").valid()){
				 $("#projectForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("添加项目","/project/save",func);
	 })

	 // 编辑按钮绑定事件
	 $(document).on("click","a[edit]",function(){
		 var func=function(){
			 if($("#projectForm").valid()){
				 $("#projectForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("编辑项目","/project/save?id="+$(this).attr("edit"),func);
	 });

	 // 删除按钮绑定事件
	 $(document).on("click","a[delete]",function(){
		 var id = $(this).attr("delete");
		 var func = function(){
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
				url : '/project/delete',
				data : {
					'id' : id,
				},
				type : 'post',
				dataType : 'json',
				success : function(msg) {
					$.unblockUI();
					if(msg.result=='success'){
						info('操作成功');
						$("#projectTable").trigger("reloadGrid");
					}else if(msg.result=='error'){
						warning('操作失败:'+msg.message);
					}else{
						warning('操作失败');
					}
				}
			});
		 };
		 confirm("是否删除",func);
	 });

})
