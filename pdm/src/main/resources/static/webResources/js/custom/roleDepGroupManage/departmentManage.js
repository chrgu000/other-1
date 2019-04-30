/**
 *
 */
$(document).ready(function() {
    //不使用jqgrid默认的参数
    $.extend(jQuery.jgrid.defaults, {
        prmNames: {
            id: "_rowid", page: "_page", rows: "_rows",
            oper: "_oper", sort: "_sidx", order: "_sord"
        }
    });


    var colModel=[
        {label:$.i18n.prop('department.name'),name:'name',index:'name', width: '120', sortable:true, align: 'center'},
        {label:$.i18n.prop('department.code'),name:'code',index:'code', width: '120', sortable:false, align: 'center'},
        /*{label:$.i18n.prop('depGroup.name'), name: 'depGroupList', index: 'depGroupList', width: 150,sortable: false, align:'center',
            formatter: function (cellvalue, options, rowObject) {
                console.log(cellvalue);
                console.log(options);
                console.log(rowObject);
                // return cellvalue.name;
            }
        }*/
    ];

    //每页显示多少行
    var rowNum=20;
    var page=0;
    var url='/department/management/data/GRID';
    var sort;
    var selectRowId;

    //请求参数
    var params={}
    //设置请求需要的一些参数
    params['rowId']='id'
    params['showAttributes']='name,code';//要获取的属性名
    params['page']=page;
    params['size']=rowNum;
    params['sort']=sort;

    var myGrid = jQuery("#departmentManageTable");
    var myPager = jQuery("#departmentManagePager");

    myGrid.jqGrid({
        datatype: "json",
        url:url,
        postData:params,
        height: '100%',
        colModel:colModel,
        multiselect: true,
        multiboxonly: true,
        multiselectWidth: 30,
        rowNum: rowNum,
        autowidth: true,
        forceFit: false,
        altRows: false,
        viewrecords: true,

        gridComplete:function(){
            var lastPage = myGrid.getGridParam('lastpage');//获取总页数
            createPage(myGrid,myPager,lastPage,params.page,11,url,params);//调用自定义的方法来生成pager
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

    //add按钮被点击
    $("#add").click(function(){
        var func=function(){
            if($("#departmentForm").valid()){
                $("#departmentForm").submit();
                return true;
            }else{
                return false;
            }
        };
        createModalPage("添加部门","/roleDepGroupManage/saveDepartment/view",func);
    })

    //edit按钮被点击
    $("#edit").click(function(){
        selectRowId = myGrid.getGridParam('selarrrow');
        // var rowid=myGrid.jqGrid('getGridParam','selrow');
        // console.log("选中的id："+rowid);
        if(selectRowId!=null&&selectRowId!=""&&selectRowId.length==1){
            var func=function(){
                if($("#departmentForm").valid()){
                    $("#departmentForm").submit();
                    return true;
                }else{
                    return false;
                }
            };
            createModalPage("修改部门","/roleDepGroupManage/saveDepartment/view?id="+selectRowId,func);
        }else{
            warning("编辑时请选择一行");
        }
    })

    // 删除按钮绑定事件
    $('#delete').on("click",function(){
        var id = [];
        var idArr=myGrid.getGridParam('selarrrow');
        for(var i=0;i<idArr.length;i++){
            id.push(idArr[i]);
        }
        if (id != null && id != "") {
            var func = function () {
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
                    url: '/roleDepGroupManage/deleteDepartment',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#departmentManageTable").trigger("reloadGrid");
                        } else if (msg.result == 'error') {
                            warning('操作失败:' + msg.message);
                        } else {
                            warning('操作失败');
                        }
                    }
                });
            };
            confirm("是否删除", func);
        }else {
            warning("请选择要删除的行");
        }
    });
});