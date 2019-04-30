/**
 *
 */
$(document).ready(function() {
	var files = new Vue({
	  el: '#files',
	  data: {
	    fileDatas : [],
	    seenDetail : false,
	    pdf : ['TXT','XLS','XLSX','DOC','DOCX','CSV','PDF'],
	    picture : ['GIF','PNG','JPEG','BMP','ICON'],
	    isDevice : $("#type").val() == "TECHNOLOGYDOCUMENT",
	    deviceSN : $("#deviceSN").val(),
	    projectNo : $("#projectNo").val(),
	  },
		methods: {
            deleteFile(fileId,id) {

                console.log("id:" + fileId);
                var func = function () {
                    // $.blockUI({
                    //     message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
                    //     // 指的是提示框的css
                    //     css: {
                    //         width: "0px",
                    //         top: "40%",
                    //         left: "50%"
                    //     },
                    // });
                    $.ajax({
                        url: '/deleteTechnologyDocument',
                        data: {
                            'id': id,
							'fileId': fileId
                        },
                        type: 'post',
                        dataType: 'json',
                        success: function (msg) {
                            // $.unblockUI();
                            if (msg.result == 'success') {
                                info('操作成功');
                                $("#search").click();
                            } else if (msg.result == 'error') {
                                warning('操作失败:' + msg.message);
                            } else {
                                warning('操作失败');
                            }
                        }
                    });
                };
                confirm("是否删除", func);
            }
		}
	})
	 //每页显示多少行
	 var rowNum=5;
	 var page=0;//初始页
	 var pageNum=0;//总页数
	 var url='/technologyDocument/search/REMOTE';
	 var sort;
     // var myGrid = jQuery("#instrumentTable");
     // var fileDate;
	 //请求参数
	 var params={}
	 params['showAttributes']='suffix,highLightFileName,highLightContent,uploaderName,uploadTime,type,fileId,id';//要获取的属性名
	 params['size']=rowNum;
	 params['sort']=sort;

	 /***
	  * 获取文档数据
	  */
	 var reloadContent = function(page){
		 params['page']=page;
		 $.ajax({
			url : url,
			data : params,
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				pageNum=data.total;
				page=data.page;
				files.fileDatas = data.content;
				//刷新分页插件
				createPage($("#dataPager"),pageNum,page,11,reloadContent);
			}
		 });
	 }

	 //上传
	 $("#upload").click(function(){
		 var func=function(){
			 if($("#documentForm").valid()){
				 $("#documentForm").submit();
				 return true;
			 }else{
				 return false;
			 }
		 };
		 createModalPage("文档上传","/technologyDocument/upload/view",func);
	 });

	 $("#empty").click(function(){
		 $("#lowerLimit").val("")
		 $("#upperLimit").val("")
         files.deviceSN = ""
		 files.projectNo = ""
         $("#searchContent").val("")
	 })

	 $("#confirm").click(function(){
		//加载数据
		$("#search").click()
	 })

	 $("#lowerLimit").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});
	 $("#upperLimit").datetimepicker({language: 'zh-CN', format: 'yyyy-mm-dd hh:ii:ss',todayBtn:'true',todayHighlight:'true'});

     $('select').selectOrDie({});

	 //搜索
	 $("#search").click(function(){
		 params['type']=$("#type").val();
		 params['lowerLimit']=$("#lowerLimit").val();
		 params['upperLimit']=$("#upperLimit").val();
		 if($("#searchContent").val()!=null&&$("#searchContent").val()!=""){
			 params['searchContent']=$("#searchContent").val();
			 params['size']=3;
		 }else{
			 delete params['searchContent']
			 params['size']=5;
		 }
		 checkIdentification();
		 search(0);
	 })

	 function checkIdentification(){
		 if(files.deviceSN != "" && files.projectNo != ""){
			 params['identification']=files.projectNo + "_" + files.deviceSN
		 }else if( files.deviceSN == "" && files.projectNo != ""){
			 params['identification']="start$" + files.projectNo + "_"
		 }else if(files.deviceSN != "" && files.projectNo == ""){
			 params['identification']="end$" + "_" + files.deviceSN
		 }else if(files.deviceSN == "" && files.projectNo == ""){
			 delete params['identification']
		 }
	 }

	 var search = function(page){
		 params['page']=page;
		 $.ajax({
			url : url,
			data : params,
			type : 'get',
			dataType : 'json',
			success : function( data ) {
				pageNum=data.total;
				page=data.page;
				if(params.searchContent==""||params.searchContent==null){
					files.seenDetail=false;
				}else{
					files.seenDetail=true;
				}
				files.fileDatas = data.content;
				$.each(files.fileDatas,function(){
					this.type=$.i18n.prop('document.type.'+this.type)
				})
				//刷新分页插件
				createPage($("#dataPager"),pageNum,page,11,search)
			}
		 });
	 }

	 //search autocomplete
	 $( "#searchContent" )
      // 当选择一个条目时不离开文本域
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "ui-autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {

          $.ajax({
				url : '/searchRecord/autocomplete',
				data : {
					'searchContent' :  request.term,
				},
				type : 'get',
				dataType : 'json',
				success : function( datas ) {
					response($.each(datas,function(i,value) {
						return {
							label : this.label,
							value : this.value,
						}
					}));
				}

          });

        },
        search: function() {
          // 自定义最小长度
          var term = this.value;
          if ( term.length < 1 || term.length >10 ) {
            return false;
          }
        },
        focus: function() {
          // 防止在获得焦点时插入值
          return false;
        },
        select: function( event, ui ) {
          console.info(ui.item);
          $( "#searchContent" ).val(ui.item.label);
          return false;
        }
      });

	 //加载数据
	 $("#search").click()

	 function checkType(){
		 if($("#isDevice").val()=="true"){
			 $("#type").val("TECHNOLOGYDOCUMENT")
		 }
	 }

	 checkType()

	 $("#type").change(function(){
		 if(this.value=="TECHNOLOGYDOCUMENT"){
			 files.isDevice = true
		 }else{
			 files.deviceSN = ""
			 files.projectNo = ""
			 files.isDevice = false
		 }
		 $("#search").click()
	 })
})
