/**
 *
 */
$(document).ready(function () {
    iotxListData();
    //不使用jqgrid默认的参数
    $.extend(jQuery.jgrid.defaults, {
        prmNames: {
            id: "_rowid", page: "_page", rows: "_rows",
            oper: "_oper", sort: "_sidx", order: "_sord"
        }
    });

    var colModel = [
        {
            // label: $.i18n.prop('device.technicalParameter.name'),
            label: $.i18n.prop('data.content.name'),
            name: 'name',
            index: 'name',
            width: '150',
            align: 'center'
        },
        {
            // label: $.i18n.prop('device.technicalParameter.parameterDescribe'),
            label: $.i18n.prop('data.content.pointAddress'),
            name: 'pointAddress',
            index: 'pointAddress',
            width: '150',
            align: 'center'
        },
        {
            label: $.i18n.prop('operate'),
            name: 'operate',
            index: 'operate',
            width: 150,
            sortable: false,
            align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                console.log(options.rowId);
                console.log(rowObject.uid);
                // var detail = "<a href='#' title=" + $.i18n.prop('add') + " add='" + options.rowId + "'><img src='/webResources/img/operate/edit.png'/></a>";
                var detail = "<a href='#' title=" + $.i18n.prop('add') + " add='" + rowObject.uid + "'><img src='/webResources/img/operate/edit.png'/></a>";
                return detail;
            },
        },
    ];

    var serial_no;//设备的网元号

    //每页显示多少行
    var rowNum = 5;
    var page = 0;
    // var url = '/sensor/management/data/GRID';
    var url = '/iotx/getPlcPointData';
    var sort;

    //请求参数
    var params = {}
    //设置请求需要的一些参数
    //params['showAttributes'] = 'name,parameterDescribe,serialNo';//要获取的属性名
    params['page'] = page;
    params['size'] = rowNum;
    params['sort'] = sort;
    params['rowId'] = 'serialNo';
    //params['dust.device.serialNo'] = $("#deviceSN").val();
    params['serial_no'] = serial_no;

    var myGrid = jQuery("#parameterTable");
    var myPager = jQuery("#parameterPager");

    myGrid.jqGrid({
        datatype: "json",
        url: url,
        caption:"技术参数",
        postData: params,
        height: '100%',
        colModel: colModel,
        multiselect: false,
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

    var selectedGrid = jQuery("#selectedParameterTable");

    var selectedModel = [
        {
            // label: $.i18n.prop('device.technicalParameter.name'),
            label: $.i18n.prop('data.content.name'),
            name: 'name',
            index: 'name',
            width: '150',
            align: 'center'
        },
        {
            // label: $.i18n.prop('device.technicalParameter.parameterDescribe'),
            label: $.i18n.prop('data.content.pointAddress'),
            name: 'pointAddress',
            index: 'pointAddress',
            width: '150',
            align: 'center'
        },
        {
            label: $.i18n.prop('operate'),
            name: 'operate',
            index: 'operate',
            width: 150,
            sortable: false,
            align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                // var detail = "<a href='#' title=" + $.i18n.prop('remove') + " remove='" + options.rowId + "'><img src='/webResources/img/operate/delete.png'/></a>";
                var detail = "<a href='#' title=" + $.i18n.prop('remove') + " remove='" + rowObject.uid + "'><img src='/webResources/img/operate/delete.png'/></a>";
                return detail;
            },
        },
    ];

    /*
    已选择的技术参数
     */
    selectedGrid.jqGrid({
        datatype: "json",
        url: "/device/parameters/get",
        postData: {
            "deviceSN": $("#deviceSN").val(),
            "serial_no": $("#serial_no").val()
        },
        height: '100%',
        caption:"已选择的技术参数",
        colModel: selectedModel,
        multiselect: false,
        multiboxonly: true,
        multiselectWidth: 30,
        rowNum: rowNum,
        autowidth: true,
        forceFit: false,
        altRows: false,
        viewrecords: true,
    });

    /*
    获取与当前设备关联的网元
     */
    function iotxListData(){
        var iotxListJsonStr = JSON.parse($('#iotxListJsonStr').val());
        // for(var i=0;i<iotxListJsonStr.length;i++){
        // }
        //当前先固定取第一个网元的数据
        serial_no = iotxListJsonStr[0].serial_no;

    }



    // 添加按钮绑定事件
    $(document).on("click", "a[add]", function () {
        var sn = $(this).attr("add");
        $.ajax({
            url: '/device/parameters/select/add',
            data: {
                'deviceSN': $("#deviceSN").val(),
                'showParameter': sn
            },
            type: 'post',
            dataType: 'json',
            success: function (msg) {
                if (msg.result === 'success') {
                    $("#selectedParameterTable").trigger("reloadGrid");
                }
                 if (msg.result === 'error') {
                    warning('操作失败:' + msg.message);
                }
                if(msg.result==='exist'){
                    layer.alert("您已添加过", {icon: 5});
                    return;
                }
                if(msg.result==='over'){
                    layer.alert("请添加不超过5个",{icon:5});
                    return;
                }
            }
        });
    });

    // 删除按钮绑定事件
    $(document).on("click", "a[remove]", function () {
        var sn = $(this).attr("remove");
        $.ajax({
            url: '/device/parameters/select/remove',
            data: {
                'deviceSN': $("#deviceSN").val(),
                'showParameter': sn
            },
            type: 'post',
            dataType: 'json',
            success: function (msg) {
                if (msg.result == 'success') {
                    $("#selectedParameterTable").trigger("reloadGrid");
                } else if (msg.result == 'error') {
                    warning('操作失败:' + msg.message);
                } else {
                    warning('操作失败');
                }
            }
        });
    });

})