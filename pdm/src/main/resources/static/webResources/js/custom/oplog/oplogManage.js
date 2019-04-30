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
            label: $.i18n.prop('operator'),
            name: 'operator',
            index: 'operator',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('operationTime'),
            name: 'opTime',
            index: 'opTime',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('operatingTarget'),
            name: 'target',
            index: 'target',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('operate'),
            name: 'operation.name',
            index: 'operation.name',
            width: '120',
            align: 'center'
        },
        {
            label: $.i18n.prop('operateContent'),
            name: 'content',
            index: 'content',
            width: '120',
            align: 'center'
        }
    ];

    //每页显示多少行
    var rowNum = 20;
    var page = 0;
    var url = '/oplog/management/data/GRID';
    var sort;
    var selectRowId;

    //请求参数
    var params = {}
    //设置请求需要的一些参数
    params['rowId'] = 'id'
    params['showAttributes'] = 'operator,opTime,target,operation.name,content,stringId';//要获取的属性名
    params['page'] = page;
    params['size'] = rowNum;
    params['sort'] = sort;
    params['searchContent'] = null;

    var deviceSearch = new Vue({
        el: '#deviceSearch',
        data: params,
        methods: {
            empty: function () {
                $("#beginTime").val("")
                $("#endTime").val("")
            }
        }
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
        myGrid.jqGrid().setGridParam({
            url: url,
            postData: params,
        }).trigger("reloadGrid");
    })

    $("#confirm").click(function () {
        params['beginTime'] = $("#beginTime").val();
        params['endTime'] = $("#endTime").val();
        myGrid.jqGrid().setGridParam({
            url: url,
            postData: params,
        }).trigger("reloadGrid");
    })

})
