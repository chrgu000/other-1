/**
 *
 */
$(document).ready(function(){
	var devices = new Vue({
		el: '#devices',
		data: {
		deviceDatas:[],
		},
		methods:{
            viewDevice : function(type){
            	window.location.href = "/device/management/view?categoryType="+type
			},
			getStyle: function (index) {
            	var style;
				switch (index % 5) {
					case 0:
                        style = {
                            'border-left': 'solid #0688EC 8px'
                        };
						break;
                    case 1:
                        style = {
                            'border-left': 'solid #00D3FF 8px'
                        };
                        break;
                    case 2:
                        style = {
                            'border-left': 'solid #4A4A4A 8px'
                        };
                        break;
                    case 3:
                        style = {
                            'border-left': 'solid #0688EC 8px'
                        };
                        break;
                    case 4:
                        style = {
                            'border-left': 'solid #F5A623 8px'
                        };
                        break;
                }
                return style;
            }
		},
	})

	 $.ajax({
			url : "/devCategory/count",
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				devices.deviceDatas = data;
			}
	 });
})

