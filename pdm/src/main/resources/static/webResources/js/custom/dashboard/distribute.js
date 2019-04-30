/**
 *
 */
$(document).ready(function() {

	$.ajax({
		url : '/device/distribute/data',
		type : 'get',
		dataType : 'json',
		success : function(datas) {
			initPie(datas);
		},
		error : function(data) {
			warning('节点分布加载失败，请联系管理员或刷新页面重试');
		}
	});

	function initPie(datas) {
		var myPie = echarts.init(document.getElementById('right-pie'));
		myPie.showLoading();
		option = {
			color : [ '#0688EC', '#51ACF1', '#9BCFF7', '#E6F3FD', '#E6E6E6' ],
			tooltip : {
				trigger : 'item',
				formatter : "{a} <br/>{b}: {c} ({d}%)"
			},
			series : [ {
				name : '设备分布',
				type : 'pie',
				radius : '55%',
				center : [ '50%', '60%' ],
				data : (function() {
					var res = [];
					for (var i = 0; i < datas.length; i++) {
						res.push({
							name : datas[i].name,
							value : datas[i].amount,
						});
					}
					return res;
				})(),
				itemStyle : {
					emphasis : {
						shadowBlur : 10,
						shadowOffsetX : 0,
						shadowColor : 'rgba(0, 0, 0, 0.5)'
					}
				}
			} ]
		};
		myPie.hideLoading();
		myPie.setOption(option, true);
	}
})
