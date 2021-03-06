/**
 *
 */
$(document).ready(function () {
    //不使用jqgrid默认的参数
    $.extend(jQuery.jgrid.defaults, {
        prmNames: {
            id: "_rowid", page: "_page", rows: "_rows",
            oper: "_oper", sort: "_sidx", order: "_sord"
        }
    });

    var colModel = [
        {
            label: $.i18n.prop('customerService.projectNo'),
            name: 'project.number',
            index: 'project.number',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('customerService.projectName'),
            name: 'project.name',
            index: 'project.name',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('customerService.projectLocation'),
            name: 'project.location',
            index: 'project.location',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('customerService.productNo'),
            name: 'productNo',
            index: 'productNo',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('customerService.productName'),
            name: 'productName',
            index: 'productName',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('customerService.productSpecifications'),
            name: 'productSpecifications',
            index: 'productSpecifications',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('device.devCategory'),
            name: 'devCategory.categoryType.name',
            index: 'devCategory.categoryType.name',
            width: '120',
            sortable: false,
            align: 'center'
        },
        {
            label: $.i18n.prop('device.productionTime'),
            name: 'commissioningTime',
            index: 'commissioningTime',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('device.commissioningTime'),
            name: 'commissioningTime',
            index: 'commissioningTime',
            width: '120',
            align: 'center'
        },
        {label: $.i18n.prop('device.serialNo'), name: 'serialNo', index: 'serialNo', width: '120', align: 'center'},
        {label: $.i18n.prop('device.rfid'), name: 'rfid', index: 'rfid', width: '120', align: 'center'},
        {
            label: $.i18n.prop('operate'),
            name: 'operate',
            index: 'operate',
            width: 150,
            sortable: false,
            align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                var detail = "";
                if ($("#allowEdit").length > 0) {
                    detail += "<a href='#' title=" + $.i18n.prop('edit') + " edit='" + options.rowId + "' style='margin: 0 5px;'><img src='/webResources/img/operate/edit.png'/></a>"
                }
                if ($("#allowDelete").length > 0) {
                    detail += "<a href='#' title=" + $.i18n.prop('delete') + " delete='" + options.rowId + "' style='margin: 0 5px;'><img src='/webResources/img/operate/delete.png'/></a>"
                }
                if ($("#allowViewDetail").length > 0) {
                    detail += "<a href='#' title=" + $.i18n.prop('detail') + " detail='" + options.rowId + "' style='margin: 0 5px;'><img src='/webResources/img/operate/detail.png'/></a>"
                }
                return detail;
            },
        },
    ];

    //每页显示多少行
    var rowNum = 20;
    var page = 0;
    var url = '/device/management/data/GRID';
    var sort;
    var selectRowId;

    //请求参数
    var params = {}
    //设置请求需要的一些参数
    params['rowId'] = 'id'
    params['showAttributes'] = 'project.number,project.name,project.location,productNo,productName,productSpecifications,devCategory.categoryType.name,productionTime,commissioningTime,serialNo,rfid';//要获取的属性名
    params['page'] = page;
    params['size'] = rowNum;
    params['sort'] = sort;
    params['searchContent'] = null
    if ($("#categoryType").val() != "" || $("#categoryType").val() != null) {
        params['devCategory.categoryType.name'] = $("#categoryType").val();
    }

    var deviceSearch = new Vue({
        el: '#deviceSearch',
        data: params,
        methods: {
            empty: function () {
                $("#beginTime").val("")
                $("#endTime").val("")
            },
            downloadTemplate: function () {
                window.location.href = "/webResources/templates/device.xlsx"
            },
            batchSave: function () {
                var func = function () {
                    if ($("#excelForm").valid()) {
                        $("#excelForm").submit();
                        return true;
                    } else {
                        return false;
                    }
                };
                createModalPage("批量上传", "/device/save/batch/view", func);
            }
        },
    })

    $("#beginTime").datetimepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd hh:ii:ss',
        todayBtn: 'true',
        todayHighlight: 'true'
    });
    $("#endTime").datetimepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd hh:ii:ss',
        todayBtn: 'true',
        todayHighlight: 'true'
    });

    var myGrid = jQuery("#deviceTable");
    var myPager = jQuery("#devicePager");
    if($("#companyName").val()){
        url = '/device/company/data/GRID';
        params['project.name'] = $("#companyName").val()
    }else{
        params['name'] = null
        url =  '/device/management/data/GRID';
    }
    myGrid.jqGrid({
        datatype: "json",
        url: url,
        postData: params,
        height: '100%',
        colModel: colModel,
        multiselect: true,
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

    $("#search").click(function () {
        url =  '/device/management/data/GRID';
        params['project.name'] = null
        myGrid.jqGrid().setGridParam({
            url: url,
            postData: params,
        }).trigger("reloadGrid");
    })

    $("#confirm").click(function () {
        url =  '/device/management/data/GRID';
        params['project.name'] = null
        params['beginTime'] = $("#beginTime").val();
        params['endTime'] = $("#endTime").val();
        myGrid.jqGrid().setGridParam({
            url: url,
            postData: params,
        }).trigger("reloadGrid");
    })

    //add按钮被点击
    $("#add").click(function () {
        var func = function () {
            if ($("#deviceForm").valid()) {
                $("#deviceForm").submit();
                return true;
            } else {
                return false;
            }
        };
        createModalPage("添加设备", "/device/save", func);
    })


    // 编辑按钮绑定事件
    $(document).on("click", "a[edit]", function () {
        selectRowId = myGrid.getGridParam('selarrrow');
        if (selectRowId != null && selectRowId != "" && selectRowId.length == 1) {
            var func = function () {
                if ($("#deviceForm").valid()) {
                    $("#deviceForm").submit();
                    return true;
                } else {
                    return false;
                }
            };
            createModalPage("编辑设备", "/device/save?id=" + $(this).attr("edit"), func);
        } else {
            warning("编辑时必须选择一行");
        }
    });

    // 删除按钮绑定事件
    /*$('.company-button').on("click", "#delete", function () {
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
                    url: '/device/delete',
                    data: {
                        'id': id.join('-'),
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (msg) {
                        $.unblockUI();
                        if (msg.result == 'success') {
                            info('操作成功');
                            $("#deviceTable").trigger("reloadGrid");
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
    });*/
    // 删除按钮绑定事件
    $(document).on("click","a[delete]",function(){
        var id = $(this).attr("delete");
        var func = function(){
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
                url : '/device/delete',
                data : {
                    'id' : id,
                },
                type : 'post',
                dataType : 'json',
                success : function(msg) {
                    $.unblockUI();
                    if(msg.result=='success'){
                        info('操作成功');
                        $("#deviceTable").trigger("reloadGrid");
                    }else if(msg.result=='error'){
                        warning('操作失败:'+msg.message);
                    }else{
                        warning('操作失败');
                    }
                }
            });
        };
        confirm("是否删除",func);
    });

    // 查看详情按钮绑定事件
    $(document).on("click", "a[detail]", function () {
        window.location.href = "/device/management/detail/" + $(this).attr("detail") + "/view"
    });


})
