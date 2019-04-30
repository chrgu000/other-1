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
             {label:$.i18n.prop('category.name'),name:'name',index:'name', sortable: true, width: '120', align: 'center'},
             {label:$.i18n.prop('category.code'),name:'code',index:'code', sortable: true, width: '120', align: 'center'}
	   	  ];

		 //每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/category/management/data/GRID';
		 var sort;
		 var selectRowId;
		 //请求参数
		 var params={}
		 //设置请求需要的一些参数
		 params['rowId']='id'
		 params['showAttributes']='name,code,id';//要获取的属性名
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 // params['searchContent']=$('.form-control').val();
		 //params['id']=myGrid.getGridParam('selarrrow');
		 var companySearch = new Vue({
			   el: '#companySearch',
			   data: params,
		 })

		 var myGrid = jQuery("#categoryTable");
		 var myPager = jQuery("#categoryPager");

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
				 if($("#categoryForm").valid()){
					 $("#categoryForm").submit();
					 return true;
				 }else{
					 return false;
				 }
			 };
			 createModalPage("添加分类","/category/save/view",func);
		 })

		 //add和edit按钮被点击
		 $("#edit").click(function(){
			 selectRowId = myGrid.getGridParam('selarrrow');
			 if(selectRowId!=null&&selectRowId!=""&&selectRowId.length==1){
				 var func=function(){
					 if($("#categoryForm").valid()){
						 $("#categoryForm").submit();
						 return true;
					 }else{
						 return false;
					 }
				 };
                 createModalPage("编辑分类","/category/save/view?id="+selectRowId,func);
             }else{
				 warning("编辑时必须选择一行");
			 }
		 })

		 /*$("#search").click(function(){
			 myGrid.jqGrid().setGridParam({
					url:url,
					postData:params,
			 }).trigger("reloadGrid");
		 })*/

	});
