/**
 *
 */
$(document).ready(function(){
	var pointData=[];

	var dom = document.getElementById("allmap");
    var myChart = echarts.init(dom);
    var planePath = 'arrow';
    myChart.showLoading();
    var option = {
            bmap: {
                center: [104.114129, 37.550339],
                zoom: 5,
                roam: true,
                mapStyle: {
                    styleJson: [{
                        'featureType': 'water',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#d1d1d1'
                        }
                    }, {
                        'featureType': 'land',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#f3f3f3'
                        }
                    }, {
                        'featureType': 'railway',
                        'elementType': 'all',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'highway',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#fdfdfd'
                        }
                    }, {
                        'featureType': 'highway',
                        'elementType': 'labels',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'arterial',
                        'elementType': 'geometry',
                        'stylers': {
                            'color': '#fefefe'
                        }
                    }, {
                        'featureType': 'arterial',
                        'elementType': 'geometry.fill',
                        'stylers': {
                            'color': '#fefefe'
                        }
                    }, {
                        'featureType': 'poi',
                        'elementType': 'all',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'green',
                        'elementType': 'all',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'subway',
                        'elementType': 'all',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'manmade',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#d1d1d1'
                        }
                    }, {
                        'featureType': 'local',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#d1d1d1'
                        }
                    }, {
                        'featureType': 'arterial',
                        'elementType': 'labels',
                        'stylers': {
                            'visibility': 'off'
                        }
                    }, {
                        'featureType': 'boundary',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#fefefe'
                        }
                    }, {
                        'featureType': 'building',
                        'elementType': 'all',
                        'stylers': {
                            'color': '#d1d1d1'
                        }
                    }, {
                        'featureType': 'label',
                        'elementType': 'labels.text.fill',
                        'stylers': {
                            'color': '#999999'
                        }
                    }]
                }
            },
            legend: {
                orient: 'vertical',
                y: 'bottom',
                x:'right',
                data:['表示设备分布'],
                textStyle: {
                    color: '#fff'
                }
            },
            // tooltip: {
            //     trigger: 'item'
            // },
            series: [{
                name:'设备分布',
                type: 'scatter',
                mapType: 'china',
                coordinateSystem: 'bmap',
                symbol:'image:///webResources/img/icon/pin2.png',
                symbolSize: [24,32],
                // zlevel: 4, /*圆点大小 */
                // data: [ [120, 30, "fdsafdsa", "fdsafdsafds"] ],
                data: pointData,
                label: {
                    normal: {
                        formatter: '{a|{b}}{b|\n{@[2]}}',
                        backgroundColor: {
                            image: '/webResources/img/icon/note1.png'
                        },
                        color: 'rgba(60,90,112,1)',
                        position: ['0','26'],
                        width: '177',
                        height: '169',
                        fontSize: 12,
                        align: 'center',
                        // lineHeight: 20,
                        show: false,
                        rich: {
                            a: {
                                padding: [0,10,40,10],
                                color: 'rgba(60,90,112,1)',
                                fontSize: 14,
                                // lineHeight: 20
                            },
                            b: {
                                color: 'rgba(60,90,112,1)',
                                fontSize: 12,
                                lineHeight: 30
                            }
                        }
                    },
                    emphasis: {
                        show: true
                    }

                }
            }],

        };
    myChart.hideLoading();
    myChart.setOption(option);


    myChart.on('click', function (params) {
        window.location.href="/device/management/view?searchContent="+params.data.name;
    });

    // 添加点方法
	function addMarker(longitude, latitude,name,location) {
		pointData.push({'name':name,'value':[longitude,latitude,location]})
	}

    // 加载device节点
	function loadDevice() {
		$.ajax({
			url : '/project/management/data/REMOTE',
			data : {
				'showAttributes' : 'id,name,location,baiduLongitude,baiduLatitude',
			},
			type : 'get',
			dataType : 'json',
			success : function(datas) {
				var longitude;
				var latitude;
				var id;
				$.each(datas.content, function(i, value) {
					longitude = value.baiduLongitude;
					latitude = value.baiduLatitude;
					id = value.serialNo;
					// 添加点
					addMarker(longitude, latitude, value.name,value.location)
				});
				myChart.setOption(option);
				// 在这里做一个点击事件的监听
		        myChart.on('click', function(param){
		        	window.location.href = '/device/management/view/?searchContent=' + param.name;
		        });
			},
			error : function(data) {
				warning('节点加载失败，请联系管理员或刷新页面重试');
			}
		});
	}

	loadDevice();
})
