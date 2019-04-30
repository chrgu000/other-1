/**
 *
 */
$(document).ready(function(){
    var now = new Date();
    now.setHours(0,0,0,0);
    $("#lowerLimitPLC").attr("value",now.format('yyyy-MM-dd HH:mm:ss'));
    $("#upperLimitPLC").attr("value",new Date().format('yyyy-MM-dd HH:mm:ss'));
    $("#lowerLimitPLC").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});
    $("#upperLimitPLC").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});
    var serialNo = $('#serialNo').val();//点位id
    var deviceSN = $('#deviceSN').val();//设备序列号
    var iotSerialNo = $('#iotSerialNo').val();//网元号
    var deviceId;
    //线图
    var myChart = echarts.init(document.getElementById('loy-tab'));
    //var ecUrl = '/sensor/getIotxData/GRID'; //曲线图数据地址
    //var ecUrl='/sensor/getIotxDataByUidAndSerialNo?iotx='+iotx+'&startTime='+$("#lowerLimitPLC").val()+'&endTime='+$("#upperLimitPLC").val();
    var temperature=[];//温度

    var dust=[];//粉尘
    var pressure=[];//压力
    var airVolume=[];//风量
    var dropoutVol=[];//压差
    var humidity=[];//湿度

	var detail = new Vue({
	   el: '#detail',
	   data: {
           // iotxUrl:'/iotx/management/detail/'+$('#deviceId').val()+'/view', //目前不给展示iotx管理信息
           iotxUrl:'#',
		   // deviceUrl:'/device/management/detail/'+$('#deviceId').val()+'/view',
		   deviceUrl:'#',
		   detailData : {},
		   deviceId : deviceId,
		   uid : null,
           deviceSN : null,
           iotSerialNo : null,
           val:null,
		   nowReading : 0
	   },
	   methods: {

	   },
	   filters: {

	   }
	})

    var option = {
        title: {
            text: ''
        },
        grid: {
            left: '10%',
            right: '10%',
            top: '16%',
            bottom: '6%',
            containLabel: true
        },
        tooltip: {
            trigger: 'axis',
            /*formatter: function (params) {
                 param = params[0];
                 return param.name +"<br/>"+'值 : ' + param.value[1];
            },*/
            formatter: "读数 : {c}<br/>{b}",
            axisPointer: {
                animation: false
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            /*splitLine: {
                show: false
            }*/
            date:[],
            axisLabel :{
                // interval:10
            }
        },
        yAxis: {
            type: 'value',
            /*boundaryGap: [0, '50%'],
            splitLine: {
                show: false
            }*/
            axisLabel: {
                formatter: '{value} '
            }
        },
        series: [{
            name: 'dynamicData',
            type: 'line',
            showAllSymbol: true,//标注所有数据点
            smooth: true,
            data:[]
        }]
    };

    /**
     * 历史数据初始化
     */
    //不使用jqgrid默认的参数
    $.extend(jQuery.jgrid.defaults, {
        prmNames: {
            id: "_rowid", page: "_page", rows: "_rows",
            oper: "_oper", sort: "_sidx", order: "_sord"
        }
    });

    var colModel=[
        // {label:$.i18n.prop('data.content.type'),name:'name', index:'name', sortable: false, width:'400', align: 'center'},
        {label:$.i18n.prop('data.content.collectTime'),name:'collectTime',index:'collectTime', width: '400',  align: 'center'},
        {label:$.i18n.prop('data.content.val'),name:'val',index:'val', width: '300',  align: 'center'},
    ];

    //每页显示多少行
    var rowNum=6;
    var page=0;
    // var url='/iotxData/management/data/GRID';
    // var url='/sensor/getIotxDataByUidAndSerialNoFormatGrid';
    var url='/sensor/getIotxDataByUidAndSerialNoFormatGrid';
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



    /**
     * 获取顶部网元-传感器-设备-当前数值信息
     */
	var loadDetail = function(){
		$.ajax({
			// url : "/sensor/management/data/one",
			url : "/sensor/getIotxDataByUidAndSerialNo",
			data : {
				"pointUid" : $("#serialNo").val(),
				"iotxId" : $('#iotSerialNo').val()

				// "showAttributes" : "serialNo,dust.iotx.id,dust.iotx.serialNo,dust.device.serialNo,alarmQuantity,maxVal,minVal,unit,isWorked",
			},
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				var content = data.content;
				var lastIndex = (content.length)-1;
                //detail.val = content[lastIndex].val;
                detail.uid = serialNo;
                detail.iotSerialNo = iotSerialNo;
                detail.deviceSN = $('#deviceSN').val();
				/*$.ajax({
					url : "/device/management/data/one",
					data : {
						"serialNo" : deviceSN,
						"showAttributes" : "id,serialNo",
					},
					type : 'get',
					dataType : 'json',
					success : function( data ) {
                        deviceId = data.id;
						detail.deviceSN = data.serialNo;
					}
				})*/
			}
		})
	}

	//加载detail
	loadDetail();




	//图表显示提示信息
    // myChart.showLoading();

	//获取历史曲线数据
	function historyChart(){
		$.ajax({
			type:"get",
			// url:'/iotxData/dynamicData',
			url:'/sensor/getIotxData/GRID',
			data : {
                "pointUid" : $("#serialNo").val(),
                "iotxId" : $('#iotSerialNo').val(),
                "startTime":$("#lowerLimitPLC").val(),
                "endTime" :$("#upperLimitPLC").val()
			},
			async:true,
			success:function(result){
                myChart.hideLoading();
                //加载数据之前先清空表格数据
                var value=[];
                var collectTime=[];//采集时间
                var x;
                if (result) {
                    x=JSON.parse(result).data;
                    for(var i=0;i<x.length;i++){
                        value.push(x[i].val);
                      /*  if (x[i].name=='新增空调主机出口温度'){
                        // if (x[i].name=='粉尘浓度'){
                            // dust.push(x[i].val);
                            value.push(x[i].val);
                        }else if (x[i].name=='放冷端供水温度'){
                        // }else if (x[i].name=='压力'){
                            // pressure.push(x[i].val);
                            value.push(x[i].val);

                        }else if (x[i].name=='放冷板换入口温度'){
                        // }else if (x[i].name=='温度'){
                            // temperature.push(x[i].val);
                            value.push(x[i].val);
                        }else if (x[i].name=='蓄冷槽下端入口温度'){
                        // }else if (x[i].name=='湿度'){
                            // humidity.push(x[i].val);
                            value.push(x[i].val);
                        }else if (x[i].name=='蓄冷槽上端入口温度'){
                        // }else if (x[i].name=='风量'){
                            // airVolume.push(x[i].val);
                            value.push(x[i].val);
                        }else if (x[i].name=='环境温度'){
                        // }else if (x[i].name=='压差'){
                            // dropoutVol.push(x[i].val);
                            value.push(x[i].val);
                        }*/
                        collectTime.push(x[i].collectTime);
                    }
                    option.series[0].data = eval(value);
                    //数组去重复
                    var set = new Set(collectTime);
                    var newArr = Array.from(set);
                    option.xAxis.data = eval(newArr);
                    myChart.setOption(option, true);
                }
			},
			error:function(errorMsg){
                $.popAlert("图表请求数据失败啦!");
			}
		});
	}



    /**
     * 获取历史数据
     */
    function historyData(){
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
                params['startTime']=$("#lowerLimitPLC").val();
                params['endTime']=$("#upperLimitPLC").val();
                createPage(myGrid,myPager,lastPage,params.page,5,url,params);//调用自定义的方法来生成pager
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
    }

    // historyData();


    /**
	 * 清空按钮
     */
    $("#emptyPLC").click(function(){
        $("#lowerLimitPLC").val("");
        $("#upperLimitPLC").val("");
    });

    /**
     * 导出
     */
    $("#exportHistory").click(function () {
        window.location.href = '/sensor/export?pointUid='+$("#serialNo").val()
            +'&iotxId='+$('#iotSerialNo').val()
            +'&startTime='+$("#lowerLimitPLC").val()
            +'&endTime='+$("#upperLimitPLC").val();
    });

    /*
   确定按钮点击事件
    */
    $("#confirmPLC").click(function(){
        if($('.nav-tabs>.active').attr('data-tab')=='tab1'){
            myChart.showLoading();
            historyChart();
        }else if($('.nav-tabs>.active').attr('data-tab')=='tab2'){
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
            // historyData();
            myGrid.jqGrid().setGridParam({
                url:url,
                postData:params,
            }).trigger("reloadGrid");
            // myGrid.jqGrid().trigger("reloadGrid");
        }
       /* var start = $("#lowerLimitPLC").val();
        var end = $("#upperLimitPLC").val();
        ecUrl='';
        //加载数据

        if (start==''&&end==''){
            ecUrl='/sensor/getIotxData/GRUD?iotxId='+ $('#iotSerialNo').val()+"&pointUid="+$("#serialNo").val();
            historyChart();
        }else{
            ecUrl='/sensor/getIotxData/GRUD?iotxId='+ $('#iotSerialNo').val()+"&pointUid="+$("#serialNo").val()+'&startTime='+start+'&endTime='+end;
            historyChart();
        }*/
    });

    /**
	 * 数据类型改变事件
     */
    /*$("#changeTypePLC").bind("change",function(){
        var type = $("#typePLC option:selected").val();
        alert(type);
        if(type=='temperature'){
            option.series[0].data = eval(temperature);
        }else if (type == 'humidity') {
            option.series[0].data = eval(humidity);
        }else if (type == 'dust') {
            option.series[0].data = eval(dust);
        }else if (type == 'pressure') {
            option.series[0].data = eval(pressure);
        }else if (type == 'airVolume') {
            option.series[0].data = eval(airVolume);
        }else if (type == 'dropoutVol') {
            option.series[0].data = eval(dropoutVol);
        }
        console.log(type);
        myChart.setOption(option,true);
    });*/

	//定时任务
	/*setInterval(function () {

		historyChart();

	}, 5000);*/


    function socketConnect() {
        var nowReading=$('.nowReading').val();
        socket = new SockJS('http://106.14.45.13:8999/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
        stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
        stompClient.connect('guest', 'guest', function (frame) {//链接Web Socket的服务端。
            // stompClient.subscribe('/topic/netElement/data/'+$("#iotxList option:selected").val() , function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
            stompClient.subscribe('/topic/netElement/data/' + $('#iotSerialNo').val(), function (respnose) { //订阅/topic/broadcast/iotx/data/目标发送的消息。
                var valueArray = JSON.parse(respnose.body);
                if(valueArray.uid==$('#serialNo').val()){
                    detail.val=valueArray.val;
                }
            });
        });
    }
    socketConnect();

	$(".sensor-top-left-div").mouseover(function() {
        $(this).attr('class','sensor-top-left-overdiv');
        $(".sensor-top-left-overdiv").mouseout(function() {
            $(this).attr('class','sensor-top-left-div');
        });
    });

    historyChart();
    historyData();

})
