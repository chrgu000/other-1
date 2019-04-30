/**
 * 
 */
$(document).ready(function() {
    var now = new Date();
    now.setHours(0,0,0,0);
    $("#lowerLimitPLC").attr("value",now.format('yyyy-MM-dd HH:mm:ss'));
    $("#upperLimitPLC").attr("value",new Date().format('yyyy-MM-dd HH:mm:ss'));
    $("#lowerLimitPLC").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});
    $("#upperLimitPLC").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});
    var serialNo = $('#serialNo').val();//点位id
	//不使用jqgrid默认的参数
	$.extend(jQuery.jgrid.defaults, {
	    prmNames: {
	        id: "_rowid", page: "_page", rows: "_rows",
	        oper: "_oper", sort: "_sidx", order: "_sord"
	    }
	});
	
	var colModel=[
        	{label:$.i18n.prop('data.content.type'),name:'name', index:'name', sortable: false, width:'', align: 'center'},
           	{label:$.i18n.prop('data.content.collectTime'),name:'collectTime',index:'collectTime', width: '400',  align: 'center'},
      		{label:$.i18n.prop('data.content.val'),name:'val',index:'val', width: '400',  align: 'center'},
      	];
      	
  	 //每页显示多少行
  	 var rowNum=6;
  	 var page=0;
  	 // var url='/iotxData/management/data/GRID';
  	 // var url='/sensor/getIotxDataByUidAndSerialNoFormatGrid';
  	 var ecUrl='/sensor/getIotxDataByUidAndSerialNoFormatGrid';
  	 var sort;
     var serialNo = $('#serialNo').val();//点位id
     var deviceSN = $('#deviceSN').val();//设备序列号
     var iotSerialNo = $('#iotSerialNo').val();//网元号
  	 //请求参数
  	 var params={}
  	 //设置请求需要的一些参数
  	 params['rowId']='id'
  	 params['page']=page;
  	 params['size']=rowNum;
  	 params['sort']=sort;
  	 params['pointUid']=serialNo;
  	 params['iotxId']=iotSerialNo;
  	 params['startTime']=$("#lowerLimitPLC").val();
  	 params['endTime']=$("#upperLimitPLC").val();


  	 var myGrid = jQuery("#allDataTable");
	 var myPager = jQuery("#allDataPager");
	 function showTable(){
         myGrid.jqGrid({
            datatype: "json",
            url:ecUrl,
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
                createPage(myGrid,myPager,lastPage,params.page,5,ecUrl,params);//调用自定义的方法来生成pager
            },

            //当触发排序时
            onSortCol:function(index,iCol,sortorder){
                params['sort']=index+","+sortorder;
                myGrid.jqGrid().setGridParam({
                        url:ecUrl,
                        postData:params,
                    }).trigger("reloadGrid");
            }

        });
     }

    showTable();
    /**
     * 清空按钮
     */
    $("#emptyPLC").click(function(){
        $("#lowerLimitPLC").val("");
        $("#upperLimitPLC").val("");
    })

    /*
   确定按钮点击事件
    */
    $("#confirmPLC").click(function(){
        alert($('.nav-tabs>.active').attr('data-tab'));
        showTable();
    })
})