/**
 *
 */
$(document).ready(function() {
    $(window).resize(function(){
        $("#parameterTable").setGridWidth($(".data-table").width()-2);
    });
    //iotxListData();//获取网元列表
    findIotx();
  /*  findDeviceByDvCategory();
    if($('#device').val()){
        findIotx();
    }*/
	//不使用jqgrid默认的参数
	$.extend(jQuery.jgrid.defaults, {
	    prmNames: {
	        id: "_rowid", page: "_page", rows: "_rows",
	        oper: "_oper", sort: "_sidx", order: "_sord"
	    }
	});

	var colModel=[
	    {label:$.i18n.prop('data.content.name'),name:'name',index:'name', width: '150',  align: 'center'},
	    {label:$.i18n.prop('data.content.uid'),name:'uid',index:'uid', width: '150',  align: 'center', hidden: true},
       	{label:$.i18n.prop('data.content.pointAddress'),name:'pointAddress',index:'pointAddress', width: '150',  align: 'center', hidden: true},
  		{label:$.i18n.prop('data.content.type'),name:'type',index:'type', width: '150',  align: 'center', hidden: true},
        {label:$.i18n.prop('device.technicalParameter.unit'),name:'unit',index:'unit', width: '150',  align: 'center'},
        {label:$.i18n.prop('data.content.lastVal'),name:'lastVal',index:'lastVal', width: '150',  align: 'center'},
        {label:$.i18n.prop('data.content.collectTime'),name:'collectTime',index:'collectTime', width: '150',  align: 'center'},
  		{
            label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 150,sortable: false, align:'center',
            // label:$.i18n.prop('data.content.uid'), name: 'uid', index: 'uid', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
                // console.log("cellvalue:"+cellvalue);
                // console.log("options:"+options.rowId);
                // console.log("rowObject:"+rowObject.uid);
            	var url = "/sensor/management/detail/"+ encodeURIComponent(rowObject.uid) +"/view"+"?iotSerialNo="+encodeURIComponent(rowObject.iotSerialNo)+"&deviceSN="+encodeURIComponent($('#deviceSN').val())+"&deviceId="+encodeURIComponent($('#deviceId').val())+"&name="+encodeURIComponent(rowObject.name);
            	var detail = "<a href="+ url +"><img src='/webResources/img/operate/detail.png'/></a>";
            	return detail;
            },
        },
  	];

  	 //每页显示多少行
  	 var rowNum=12;
  	 var page=0;
  	 // var url='/sensor/management/data/GRID';
  	 var url='/iotx/getPlcPointData';
  	 var sort;

  	 //请求参数
  	 var params={}
  	 //设置请求需要的一些参数
  	 //params['showAttributes']='uid,name,iotSerialNo,pointAddress,type,lastVal,collectTime,serialNo';//要获取的属性名
  	 params['page']=page;
  	 params['size']=rowNum;
  	 params['sort']=sort;
  	 params['rowId']='serialNo';
  	 params['serial_no']=$('#iotxList').val();
  	 //params['dust.device.serialNo']=$("#deviceSN").val();

  	 var myGrid = jQuery("#parameterTable");
	 var myPager = jQuery("#parameterPager");

	 function technicalParameterTable() {
         myGrid.jqGrid({
             datatype: "json",
             url: url,
             postData: params,
             height: '100%',
             colModel: colModel,
             // multiselect: true,
             multiboxonly: true,
             multiselectWidth: 30,
             rowNum: rowNum,
             autowidth: true,
             forceFit: false,
             altRows: false,
             viewrecords: true,

             gridComplete: function () {
                 var lastPage = myGrid.getGridParam('lastpage');//获取总页数
                 createPage(myGrid, myPager, lastPage, params.page, 11, url, params);//调用自定义的方法来生成pager
             },

             //当触发排序时
             onSortCol: function (index, iCol, sortorder) {
                 params['sort'] = index + "," + sortorder;
                 myGrid.jqGrid().setGridParam({
                     url: url,
                     postData: params,
                 }).trigger("reloadGrid");
             }

         });
     }

    technicalParameterTable();

   /* // webSocket监听
    var stompClient = null;

    var socket = new SockJS('http://goaland1.anosi.cn/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
    //var socket = new SockJS('http://localhost:8080/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
    stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
    stompClient.connect('guest', 'guest', function(frame) {//链接Web Socket的服务端。
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/broadcast/iotx/data/'+$('#iotxList').val(), function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
			var valueArray = JSON.parse(JSON.parse(respnose.body).responseMessage);
            console.info(valueArray);
            $.each(valueArray,function(){
                myGrid.setCell(this.sensorSN,"actualValue",this.val);
            })
        });
    });*/

    /**
     * 获取所有网元下拉列表数据
     */
    function iotxListData(){
        var html="";
        var iotxs =  JSON.parse($('#iotxListJsonStr').val());
        if (iotxs) {
            for(var i=0; i<iotxs.length; i++){
                html+="<option value="+iotxs[i].serial_no+">"+iotxs[i].serial_no+"</option>";
            }
            $("#iotxList").html(html);
        } else {
            $("#iotxList").html("<option value=''></option>");
        }
    };

    //根据device获取iotx的Ajax请求
    function findIotx() {
        var deviceId = $("#deviceId").val();
        $.ajax({
            type:"GET",
            url: "/iotx/management/data?deviceId="+deviceId,
            async:false,
            success: function(data){
                data = JSON.parse(data);
                var html="";
                if(data != null && data.length > 0){
                    for(var i=0; i<data.length; i++){
                        var serialNo = data[i].serialNo || "";
                        html+="<option value="+serialNo+">"+serialNo+"</option>";
                    }
                }
                $("#iotxList").html(html);
            }
        })
    }

    //根据设备种类获取设备信息
    function findDeviceByDvCategory() {
        var categoryId = $("#devCategory").val();
        $.ajax({
            type:"GET",
            url: "/report/findDeviceByDvCategory?categoryId="+categoryId,
            async:false,
            success: function(data){
                var data = JSON.parse(data);
                if(data != null && data.length > 0){
                    var html="";
                    for(var i=0; i<data.length; i++){
                        html+="<option value="+data[i].id+">"+data[i].productName+'-'+data[i].serialNo+"</option>";
                    }
                }
                $("#device").html(html);
            }
        })
    }

    /**
     * 根据选择种类的不同显示不同的设备
     */
    $('#devCategory').change(function () {
        $("#device").html("");
        $("#device option").remove();
        findDeviceByDvCategory();
        //alert($('#device').val());
        if(!$('#device').val()){
            $("#iotxList").html("");
            $("#iotxList option").remove();
        }else {
            findIotx();
        }
    });


    /**
     * 根据选择设备的不同显示不同的网元
     */
    $("#device").change(function(){
        $("#iotxList").html("");
        $("#iotxList option").remove();
        findIotx();
    });

    /*
    确定按钮点击事件
     */
    $("#confirm").click(function(){
        //alert('查询');
        technicalParameterTable();

	});



    function socketConnect(){
        /*function forround(num,n){
            var  dd=1;
            var  tempnum;
            for(i=0;i<n;i++){
                dd*=10;
            }
            tempnum = num*dd;
            tempnum = Math.round(tempnum);
            //alert(tempnum/dd);
        };*/
        socket = new SockJS('http://106.14.45.13:8999/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
        stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
        stompClient.connect('guest', 'guest', function(frame) {//链接Web Socket的服务端。
            // stompClient.subscribe('/topic/netElement/data/'+$("#iotxList option:selected").val() , function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
            stompClient.subscribe('/topic/netElement/data/'+$('#iotxList').val() , function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
                var valueArray = JSON.parse(respnose.body);
                var uid = valueArray.uid;
                var pointAddress = (uid.split("_"))[2];
                var ids = myGrid.getDataIDs();
                var rowData = myGrid.getRowData();
                if (valueArray.collectTime) {
                    var end_str = (valueArray.collectTime).replace(/-/g,"/");
                    if (new Date() - new Date(end_str) < 1000 * 3600 * 24) {
                        for(var i=0;i<rowData.length;i++){
                            //if(uid != 'AWIGZ180709001_1343534397_0'&&rowData[i].pointAddress==pointAddress){
                            if(uid != 'AWIGZ180709001_1343534397_0'&&rowData[i].uid==uid){
                                myGrid.setCell(ids[i],"lastVal",valueArray.val.substring(0,valueArray.val.indexOf(".") + 2));
                                myGrid.setCell(ids[i],"collectTime",valueArray.collectTime);
                            }

                        }
                    }
                }
                /*for (var i = 0; i < ids.length; i++) {
                    var rowData = myGrid.getRowData(ids[i]);
                }

                if(uid=='AWIGZ180709001_-1150370546_24'){
                    myGrid.setCell(ids[0],"lastVal",valueArray.val);
                    myGrid.setCell(ids[0],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1230278704_8'){
                // if (point[2]=='1008'){
                    myGrid.setCell(ids[1],"lastVal",valueArray.val);
                    myGrid.setCell(ids[1],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1343537134_4'){
                // if (point[2]=='1012'){
                    myGrid.setCell(ids[2],"lastVal",valueArray.val);
                    myGrid.setCell(ids[2],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1343534396_6'){
                // if (point[2]=='1016'){
                    myGrid.setCell(ids[3],"lastVal",valueArray.val);
                    myGrid.setCell(ids[3],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1343534396_2'){
                // if (point[2]=='1020'){
                    myGrid.setCell(ids[4],"lastVal",valueArray.val);
                    myGrid.setCell(ids[4],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1343534396_0'){
                // if (point[2]=='1024'){
                    myGrid.setCell(ids[5],"lastVal",valueArray.val);
                    myGrid.setCell(ids[5],"collectTime",valueArray.collectTime);
                }
                if (uid=='AWIGZ180709001_1343534396_38'){
                // if (point[2]=='1028'){
                    myGrid.setCell(ids[6],"lastVal",valueArray.val);
                    myGrid.setCell(ids[6],"collectTime",valueArray.collectTime);
                }*/

            });
        });
    }

    socketConnect();

})
