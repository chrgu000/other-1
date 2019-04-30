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
    findIotx();
    var colModel=[
        {label:$.i18n.prop('iotxData.iotxSerialNo'),name:'iotSerialNo',index:'iotSerialNo', width: '120',  align: 'center'},
        {label:$.i18n.prop('data.content.name'),name:'pointName',index:'pointName', width: '120',  align: 'center'},
        {label:$.i18n.prop('iotxData.limitVal'),name:'limitVal',index:'limitVal', width: '120',  align: 'center'},
        {label:$.i18n.prop('iotxData.val'),name:'val',index:'val', width: '120',  align: 'center'},
        {label:$.i18n.prop('iotxData.status'),name:'status',index:'status', width: '120',  align: 'center',
            formatter: function (cellvalue, options, rowObject) {
                if (cellvalue == 'ABOVEUP') {
                    return '过高';
                } else if (cellvalue == 'ABOVELOW') {
                    return '过低';
                } else if (cellvalue == 'ABOVEETC') {
                    return '相等';
                } else {
                    return ''
                }
            }
        },
        {label:$.i18n.prop('iotxData.collectTime'),name:'collectTime',index:'collectTime', width: '120',  align: 'center'},
        {label:$.i18n.prop('describe'),name:'monitorDescribe',index:'monitorDescribe', width: '120',  align: 'center'},
    ];

    //每页显示多少行
    var rowNum=10;
    var page=0;
    var url='/alarmData/getAlarmData/GRID';
    var sort;
    var selectRowId;

    //请求参数
    var params={}
    //设置请求需要的一些参数
    params['rowId']='id';
    params['showAttributes']='id,iotSerialNo,uid,pointName,collectTime,finishTime,limitVal,status,monitorDescribe,alarmSchemeID,readingStatus,stringId';//要获取的属性名
    params['page']=page;
    params['size']=rowNum;
    params['sort']=sort;
    params['iotSerialNo']=$('#iotxList').val();
    // params['iotSerialNo']='AWILZ180521009';
    var myGrid = jQuery("#recordTable");
    var myPager = jQuery("#recordPager");

    myGrid.jqGrid({
        datatype: "json",
        url:url,
        postData:params,
        height: '100%',
        colModel:colModel,
        multiselect: false,
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

    //根据device获取iotx的Ajax请求
    function findIotx() {
        var deviceId = $("#deviceId").val();
        $.ajax({
            type:"GET",
            url: "/iotx/management/data?deviceId="+deviceId,
            async:false,
            success: function(data){
                data = JSON.parse(data);
                var html="";
                if(data != null && data.length > 0){
                    for(var i=0; i<data.length; i++){
                        var serialNo = data[i].serialNo || "";
                        html+="<option value="+serialNo+">"+serialNo+"</option>";
                    }
                }
                $("#iotxList").html(html);
            }
        })
    }

})
