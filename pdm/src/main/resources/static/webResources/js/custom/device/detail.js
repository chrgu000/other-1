/**
 *
 */
$(document).ready(function() {
    iotxListData();
    var serial_no;
	var detail = new Vue({
	   el: '#detail',
	   data: {
		   detailData : {},
	   },
	   methods: {
           uploadTechnologyFile : function(){
               uploadTechnologyFile();
		   }
	   },
	   filters: {
		  yearFormat: function (value) {
			  return value.split(" ")[0];
		  },
		  timeFormat: function (value) {
			  return value.split(" ")[1];
		  }
	   }
	})

    var showParameters =[];

    var device_operate = new Vue({
        el: '#device_operate',
        data: {
            showParameters : [],
            lastVal:null
        },
        methods: {
            selectParameters : function(){
                var func=function(){
                    window.location.href="/device/management/detail/"+$("#deviceId").val()+"/view"
					return true;
                };
                createModalPage("选择技术参数","/device/parameters/select/view?deviceSN="+$("#deviceSN").val()+"&deviceId="+$('#deviceId').val(),func);
            }
        },
        filters: {

        }
    })

    // 配置参数下面位置的--技术参数
   var loadParameters = function(){
        // $.each(detail.detailData.showParameters,function(i,e){
            $.ajax({
                url : "/device/parameters/get",
                data : {
                    // 'showAttributes':'serialNo,name,actualValue,unit',
                    // 'serialNo': e,device/parameters/get
                    'deviceSN': $('#deviceSN').val()
                },
                type : 'get',
                dataType : 'json',
                success : function(data) {
                    //console.log(data.rows[0].name);
                    device_operate.showParameters = data.rows;
                }
            })
        // })
    }


	// 加载detail数据
	var loadContent = function(){
		$.ajax({
			url : "/device/management/data/one",
			data : {
				'showAttributes':'project.number,project.name,project.location,productName,productNo,productSpecifications,commissioningTime,serialNo,showParameters',
				'id':$("#deviceId").val(),
			},
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				detail.detailData = data;
				// 加载技术参数
                loadParameters();
			}
		})
	}

	loadContent();

    $(".carousel-content").carousel({
        carousel : ".carousel",//轮播图容器
        indexContainer : ".img-index",//下标容器
        prev : ".carousel-prev",//左按钮
        next : ".carousel-next",//右按钮
        timing : 5000,//自动播放间隔
        animateTime : 800,//动画时间
        auto : true,//是否自动播放
    });

    $(".carousel-prev").hover(function(){
        $(this).find("img").attr("src","/webResources/img/carousel/left_btn2.png");
    },function(){
        $(this).find("img").attr("src","/webResources/img/carousel/left_btn1.png");
    });
    $(".carousel-next").hover(function(){
        $(this).find("img").attr("src","/webResources/img/carousel/right_btn2.png");
    },function(){
        $(this).find("img").attr("src","/webResources/img/carousel/right_btn1.png");
    });

	function uploadTechnologyFile(){
        var func=function(){
            if($("#documentForm").valid()){
                $("#documentForm").submit();
                return true;
            }else{
                return false;
            }
        };
        createModalPage("文档上传","/device/document/upload/"+$("#deviceId").val()+"/view",func);
    }

	// 上传
	 $("#upload").click(function(){
         uploadTechnologyFile();
	 });

	 $("#viewDocument").click(function(){
		 window.location.href="/device/technologyDocument/manage/"+$("#deviceId").val()+"/view"
	 });

    /*var socket = new SockJS('http://goaland1.anosi.cn/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
    //var socket = new SockJS('http://localhost:8080/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
    stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
    stompClient.connect('guest', 'guest', function(frame) {//链接Web Socket的服务端。
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/broadcast/iotx/data/'+$("#deviceSN").val(), function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
            var valueArray = JSON.parse(JSON.parse(respnose.body).responseMessage);
            console.info(valueArray);
            $.each(device_operate.showParameters,function(index,element){
				// 根据序列号去查找valueArray
				var sensorSN = element.serialNo;
				$.each(valueArray,function(i,e){
					if(sensorSN == e.sensorSN){
						element.actualValue = e.val;
					}
				})
            })
        });
    });*/

    /*
    获取当前设备关联的网元号
     */
    function iotxListData(){
        var iotxList = JSON.parse($('#iotxListJsonStr').val());
        if(iotxList.length>0){
            serial_no = iotxList[0].serial_no;
        }
    }

    function socketConnect(){
        socket = new SockJS('http://106.14.45.13:8999/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
        stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
        stompClient.connect('guest', 'guest', function(frame) {//链接Web Socket的服务端。
            // stompClient.subscribe('/topic/netElement/data/'+$("#iotxList option:selected").val() , function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
            stompClient.subscribe('/topic/netElement/data/'+serial_no , function(respnose){ //订阅/topic/broadcast/iotx/data/目标发送的消息。
                var valueArray = JSON.parse(respnose.body);
                var uid = valueArray.uid;
                console.log('uid:'+uid);
                var pointAddress = (uid.split("_"))[2];
                // var ids = myGrid.getDataIDs();
                // var rowData = myGrid.getRowData();
                console.log('长度:'+device_operate.showParameters.length);
                for(var i=0;i<device_operate.showParameters.length;i++){
                    if(uid ==device_operate.showParameters[i].uid && device_operate.showParameters[i].pointAddress==pointAddress){
                        console.log("lastVal:"+valueArray.val);
                        device_operate.showParameters[i].lastVal=valueArray.val;
                        // myGrid.setCell(ids[i],"lastVal",valueArray.val);
                        // myGrid.setCell(ids[i],"collectTime",valueArray.collectTime);
                    }

                }
            });
        });
    }

    //socketConnect();


    var fForwardRcwh = function(){
        window.location.href="/dailyMaintenance/management/view?deviceId="+$("#deviceId").val();
    };
    var fForwardYbjc = function(){
        window.location.href="/instrument/management/view?deviceId="+$("#deviceId").val();
    };
    var fForwardXjjl = function(){
        window.location.href="/inspectionRecord/management/view?deviceId="+$("#deviceId").val()+"&deviceSN="+$('#deviceSN').val();
    };
    var fForwardBpbj = function(){
        window.location.href="/sparePart/management/view?deviceId="+$("#deviceId").val();
    };
    var fForwardGjqs = function(){
        window.location.href="/alarmRecord/management/view?deviceId="+$("#deviceId").val();
    };
    var fForwardYcxwh = function(){
        window.location.href="/materiel/management/view?deviceId="+$("#deviceId").val();
    };
    var fForwardWxjl = function(){
        window.location.href="/repairRecord/management/view?deviceId="+$("#deviceId").val();
        // window.location.href="/customerServiceProcess/allProcesses/view?menuId=customServiceProcessAllProcesses";
    };
    $('.rcwh').on('click',fForwardRcwh);
    $('.ybjc').on('click',fForwardYbjc);
    $('.xjjl').on('click',fForwardXjjl);
    $('.bpbj').on('click',fForwardBpbj);
    $('.gjqs').on('click',fForwardGjqs);
    $('.ycxwh').on('click',fForwardYcxwh);
    $('.wxjl').on('click',fForwardWxjl);

})
