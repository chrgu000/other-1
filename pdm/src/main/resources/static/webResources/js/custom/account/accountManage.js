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
           	{label:$.i18n.prop('account.loginId'),name:'loginId',index:'loginId', sortable: true, width: '120', align: 'center'},
			{label:$.i18n.prop('account.name'),name:'name',index:'name', sortable: true, width: '120', align: 'center'},
			{label:$.i18n.prop('account.role.group.department'),name:'role.depGroup.department.name', index:'role.depGroup.department.name', sortable: false, width:'120', align: 'center'},
			{label:$.i18n.prop('account.role.group'),name:'role.depGroup.name', index:'role.depGroup.name',sortable: false, width:'120', align: 'center',
				formatter: function (cellvalue, options, rowObject) {
                    var detail="";
                    $.each(cellvalue,function(){
                    	detail += this["depGroup.name"]+"  ";
                    })
                    return detail;
                }
			},
			{label:$.i18n.prop('account.role'),name:'role.name', index:'role.name', width:'120',sortable: false, align: 'center',
				formatter: function (cellvalue, options, rowObject) {
                    var detail="";
                    $.each(cellvalue,function(){
                    	detail += this.name+"  ";
                    })
                    return detail;
                }
			},
			{label:$.i18n.prop('account.roleFunctionGroup'),name:'roleFunctionGroup.name', index:'roleFunctionGroup.name',sortable: false, width:'120', align: 'center',
				formatter: function (cellvalue, options, rowObject) {
                    var detail="";
                    $.each(cellvalue,function(){
                    	detail += this.name+"  ";
                    })
                    return detail;
                }
			},
             {
                 label: $.i18n.prop('account.status'),
                 name: 'status',
                 index: 'status',
                 sortable: true,
                 width: '120',
                 align: 'center',
                 formatter: function (cellvalue, options, rowObject) {
                     if (cellvalue == '0') {
                         return "禁用";
                     } else {
                         return "启用";
                     }
                 }
             }
	   	  ];
		 
		 //每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/account/management/data/GRID';
		 var sort;
		 var selectRowId;
		 //请求参数
		 var params={}
		 //设置请求需要的一些参数
		 params['rowId']='id'
		 params['showAttributes']='loginId,name,department.name,roleList*.depGroup.name,roleList*.name,roleFunctionGroupList*.name,status';//要获取的属性名
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 params['searchContent']=$('.form-control').val();
		 //params['id']=myGrid.getGridParam('selarrrow');
		 var companySearch = new Vue({
			   el: '#companySearch',
			   data: params,
		 })
		 
		 var myGrid = jQuery("#accountTable");
		 var myPager = jQuery("#accountPager");
		 
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
		 
		 //add和edit按钮被点击
		 $("#add").click(function(){
			 var func=function(){
				 if($("#accountForm").valid()){
					 $("#accountForm").submit();
					 return true;
				 }else{
					 return false;
				 }
			 };
			 createModalPage("添加用户","/account/toSaveAccountPage",func);
		 })
		 
		 //add和edit按钮被点击
		 $("#edit").click(function(){
			 selectRowId = myGrid.getGridParam('selarrrow');
			 if(selectRowId!=null&&selectRowId!=""&&selectRowId.length==1){
				 var func=function(){
					 if($("#accountForm").valid()){
						 $("#accountForm").submit();
						 return true;
					 }else{
						 return false;
					 }
				 };
                 createModalPage("编辑用户","/account/toSaveAccountPage?id="+selectRowId,func);
             }else{
				 warning("编辑时必须选择一行");
			 }
		 })
		 
		 $("#search").click(function(){
			 myGrid.jqGrid().setGridParam({
					url:url,
					postData:params,
			 }).trigger("reloadGrid");
		 })
		 
	});