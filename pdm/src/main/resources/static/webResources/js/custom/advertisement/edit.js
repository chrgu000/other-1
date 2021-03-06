/**
 * 
 */
$(document).ready(function() {
	var container = new Vue({
		 el: '#container',
		 data: {
			 isPicture : false,
			 pictureId : null,
		 },
		 methods:{
			 
		 }
	 })
	
	var E = window.wangEditor
    var editor = new E('#div1', '#div2')
    
	// 加载菜单
    editor.customConfig.menus = [
        'head',  // 标题
        'bold',  // 粗体
        'italic',  // 斜体
        'underline',  // 下划线
        'strikeThrough',  // 删除线
        'foreColor',  // 文字颜色
        'backColor',  // 背景颜色
        'link',  // 插入链接
        'list',  // 列表
        'justify',  // 对齐方式
        'quote',  // 引用
        'emoticon',  // 表情
        'image',  // 插入图片
        'table',  // 表格
        //'video',  // 插入视频
        //'code',  // 插入代码
        'undo',  // 撤销
        'redo'  // 重复
    ]
	
	// 关闭粘贴样式的过滤
    editor.customConfig.pasteFilterStyle = false
    
    // 使用 base64 保存图片
    editor.customConfig.uploadImgShowBase64 = true   
	
	// 自定义 onchange 触发的延迟时间，默认为 200 ms
	editor.customConfig.onchangeTimeout = 1500 // 单位 ms
	
	editor.customConfig.onchange = function (html) {
        //保存到服务器
		console.info("onchange")
		$.ajax({
			url : '/advertisement/save',
			data : {
				"advertisementId" : $("#advertisementId").val(),
				"unPubishContent" : editor.txt.text(),
				"unPubishHtmlContent" : editor.txt.html(),
			},
			type : 'post',
			dataType : 'json',
			success : function( data ) {
				if(data.result == "success"){
                    jQuery().toast({
					  text : "自动保存成功", 
					  hideAfter : 2000,
					  position : 'mid-center'
					})     
				}
			}
		 })
		
    }
	
	// 配置服务器端地址
    editor.customConfig.uploadImgServer = '/fileUpload/multipartFiles/'+uuid()
	
	// 将图片大小限制为 3M
	editor.customConfig.uploadImgMaxSize = 3 * 1024 * 1024

	// 限制一次最多上传 5 张图片
	editor.customConfig.uploadImgMaxLength = 5

	// 自定义文件参数名
	editor.customConfig.uploadFileName = 'file_upload'
		
	// 自定义图片插入过程
	editor.customConfig.uploadImgHooks = {
        before: function (xhr, editor, files) {
            // 图片上传之前触发
            console.info("upload file")
        },
        success: function (xhr, editor, result) {
            // 图片上传并返回结果，图片插入成功之后触发
        	console.info("upload success")
        },
        fail: function (xhr, editor, result) {
            // 图片上传并返回结果，但图片插入错误时触发
        	console.info("upload success but insert fail")
        },
        error: function (xhr, editor) {
            // 图片上传出错时触发
        	console.info("upload fail")
        },
        timeout: function (xhr, editor) {
            // 图片上传超时时触发
        	console.info("time out")
        },

        // 如果服务器端返回的不是 {errno:0, data: [...]} 这种格式，可使用该配置
        // （但是，服务器端返回的必须是一个 JSON 格式字符串！！！否则会报错）
        customInsert: function (insertImg, result, editor) {
            // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
            // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果

            var identification = result.identification
            
            // 获取文件列表
            $.ajax({
				url : '/fileDownload/list/REMOTE',
				data : {
					"identification" : identification,
					"showAttributes" : "stringObjectId",
				},
				type : 'get',
				dataType : 'json',
				success : function( data ) {
					$.each(data.content,function(){
						insertImg("http://" + window.location.host + "/fileDownload/" + this.stringObjectId)
					})
				}
			 })
            
        }
    }
	
    editor.create()
    
    function checkLastContent(){
    	 if($("#advertisementId").val()!=null && $("#advertisementId").val()!=""){
			// 获取保存的内容
			$.ajax({
				url : '/advertisement/management/data/one',
				data : {
					"id" : $("#advertisementId").val(),
					"showAttributes" : "unPubishHtmlContent,coverPictureId",
				},
				type : 'get',
				dataType : 'json',
				success : function( data ) {
					editor.txt.html(data.unPubishHtmlContent)
					if("coverPictureId" in data){
						container.isPicture = true
						container.pictureId = data.coverPictureId
					}
				}
			 })
		}
    }
   
    checkLastContent()
    
    // 发布广告
    $("#publish").click(function(){
    	if(container.pictureId==null){
            jQuery().toast({
			  text : "发布前必须上传轮播图", 
			  hideAfter : 5000,
			  position : 'mid-center'
			})  
			return false;
    	}
    	
    	$.blockUI({
			message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
			// 指的是提示框的css
			css: {
             width: "0px",
             top: "40%",
             left: "50%"
         },
		}); 
    	
    	$.ajax({
			url : '/advertisement/publish',
			data : {
				"advertisementId" : $("#advertisementId").val(),
			},
			type : 'post',
			dataType : 'json',
			success : function( data ) {
				$.unblockUI();
				if(data.result=='success'){
                    $.toast({
					  text : "发布成功", 
					  hideAfter : 2000,
					  position : 'mid-center'
					})     
					window.location.href="/advertisement/management/view"
				}else if(data.result=='error'){
                    $.toast({
						  text : "发布失败:"+data.message, 
						  hideAfter : 5000,
						  position : 'mid-center'
						})     
				}else{
                    $.toast({
					  text : "发布失败", 
					  hideAfter : 5000,
					  position : 'mid-center'
					})     
				}
			}
		 })
    })
    
    var coverPicture = null;
    
    var photo = function(){
    	var clipArea = new bjj.PhotoClip("#clipArea", {
            size: [585, 260],
            outputSize: [720, 320],
            file: "#file",
            view: "#view",
            ok: "#clipBtn",
            loadStart: function() {
                console.log("照片读取中");
            },
            loadComplete: function() {
                console.log("照片读取完成");
            },
            clipFinish: function(dataURL) {
            	coverPicture = dataURL;
            }
        });
    }
    
    photo()
    
    $("#reCutOut").click(function(){
    	container.isPicture = false
    	photo()
    })
    
    $("#commit").click(function(){
    	if(coverPicture == null){
    		warning("尚未截取图片")
    		return false
    	}
    	
    	$.blockUI({
			message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
			// 指的是提示框的css
			css: {
             width: "0px",
             top: "40%",
             left: "50%"
         },
		}); 
    	
        $.ajax({
			url : '/advertisement/save',
			data : {
				"coverPictureBase64" : coverPicture,
				"advertisementId" : $("#advertisementId").val(),
			},
			type : "post",
			dataType : 'json',
			success : function( data ) {
				$.unblockUI();
				checkLastContent();
				if(data.result == "success"){
					$.toast({
					  text : "上传成功", 
					  hideAfter : 2000,
					  position : 'mid-center'
					})     
				}
			}
		 })
    })
    
})