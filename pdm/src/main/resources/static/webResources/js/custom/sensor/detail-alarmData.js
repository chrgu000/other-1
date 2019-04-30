/**
 * 告警记录
 */
$(document).ready(function() {
	//不使用jqgrid默认的参数
	$.extend(jQuery.jgrid.defaults, {
	    prmNames: {
	        id: "_rowid", page: "_page", rows: "_rows",
	        oper: "_oper", sort: "_sidx", order: "_sord"
	    }
	});

	/*
	对值进行处理
	 */
	var fGridValRender = function(val,row,i,grid){
		if(val=='ABOVEUP'){
			return '高于标准';
		}else if(val=='ABOVELOW'){
			return '低于标准';
		}
	};

	var colModel=[
        {label:$.i18n.prop('iotxData.status'),name:'status',index:'status', width: '120',sortable: false,  align: 'center'},
        {label:$.i18n.prop('iotxData.val'),name:'val',index:'val', width: '120',sortable: false,  align: 'center',render:fGridValRender},
        {label:$.i18n.prop('iotxData.collectTime'),name:'collectTime',index:'collectTime', width: '150',  align: 'center'},
    	];
    	
	 //每页显示多少行
	 var rowNum=15;
	 var page=0;
	 var url='/alarmData/getAlarmData/GRID';
	 var sort;
	 
	 //请求参数
	 var params={}
	 //设置请求需要的一些参数
	 params['rowId']='id'
	 // params['showAttributes']='collectTime,message,val';//要获取的属性名
	 params['page']=page;
	 params['size']=rowNum;
	 params['sort']=sort;
	 params['iotSerialNo']=$("#iotSerialNo").val();
	 params['uid']=$("#serialNo").val();

	 var paging = new Vue({
 	   el: '#paging',
 	   data: {
 		   first : false,
 		   last : false,
 		   alarmQuantity : 0,
 	   },
 	   methods: {
 		  nextPageClick : function(){
 			 page+=1
 			 params['page'] = page
 			 myGrid.jqGrid().setGridParam({
 				url:url,
 				postData:params,
 			 }).trigger("reloadGrid");
 		 },
 		 lastPageClick : function(){
 			 page-=1
 			 params['page'] = page
 			 myGrid.jqGrid().setGridParam({
 				url:url,
 				postData:params,
 			 }).trigger("reloadGrid");
 		 }
 	   },
 	})

    /**
	 * 请求告警数据
     */
	var loadAlarm =  function(){
		$.ajax({
			url : "/alarmData/getAlarmData/one",
			data : {
				"iotSerialNo" : $("#iotSerialNo").val(),//网元号
				"uid" : $("#serialNo").val(),//点位id
			},
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				console.log("告警数据:"+data);
				paging.alarmQuantity = data.alarmQuantity;
			}
		})
	}
	 
	 //加载告警数量
	 // loadAlarm()
	 
	 var myGrid = jQuery("#alarmDataTable");
  	 var myPager = jQuery("#alarmDataPager");
  	 
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
	   		//判断是否第一页
	   		if(page==0){
	   			paging.first=false
	   		}else{
	   			paging.first=true
	   		}
	   		//判断是否最后一页
	   		if(page==lastPage-1){
	   			paging.last=false
	   		}else{
	   			paging.last=true
	   		}
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
  	 
  	$(document).on("click","button[confirm]",function(){
		 $.ajax({
			type:"post",
			url:'/alarmData/save',
			data : {
				'alarmDataId' : $(this).attr("confirm"),
				'closeTime' : new Date().format("yyyy-MM-dd HH:mm:ss"),
			},
			async:true,
			success:function(data){
				myGrid.jqGrid().setGridParam({
					url:url,
					postData:params,
				}).trigger("reloadGrid");
			}
		 });
	 });
	
})