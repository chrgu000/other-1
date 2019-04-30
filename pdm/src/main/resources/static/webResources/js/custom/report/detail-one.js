/**
 * 
 */
$(document).ready(function(){
	var myGrid = jQuery("#deviceTable");
    var myPager = "#devicePager";
    myGrid.jqGrid({
        height: '100%',
        datatype: "local",
        colNames:['企业名称','企业编号', '产品名称', '产品编号','产品规格', '投运时间', '生产厂家','联系地址', 'RFID编码', '操作'],
        colModel:[
            {name:'assetCoding', index:'assetCoding', width:'60', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'devFactory', index:'devFactory', width:'60', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'devType', index:'devType', width:'120', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'devModel', index:'devModel', width:'150', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'devProfession', index:'devProfession', width:'120', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'baseStation', index:'baseStation', width:'160', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'serialNo', index:'serialNo', width:'160', sortable: true, searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'networkCategory', index:'networkCategory', width:'80', sortable: true, searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'quantity', index:'quantity', width:'60', sortable: true, search: true,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'},
            {name:'remark', index:'remark', width:'200', sortable: true, search: false,searchoptions: {sopt: ['cn', 'eq'], required: true }, align: 'center'}
        ],
        multiselect: true,
        multiboxonly: true,
        multiselectWidth: 30,
        pager: myPager,
        rowNum: 20,
        rowList: [20, 50, 100],
        height: '100%',
        altRows: false,
        viewrecords: true,
        hidegrid: true,
        autowidth: true,
        forceFit: false,
        shrinkToFit: true,
        autoScroll: true,
    });

    myGrid.jqGrid('navGrid', myPager,
        {edit: false, add: false, addtext: "增加", del: false, deltext: '删除', search: false, searchtext: '查找', refreshtext: '刷新'},
        {}, // edit options
        {}, // add options
        {}, // del options
        {multipleSearch: false, multipleGroup: false, showQuery: false});// search options

    mydata = [
        {assetCoding: "荣信墨西哥BrovaBia钢厂电炉SVCLWW168水冷系统 ",devFactory: "密封式循环纯水冷却装置", devType: "12112458", devModel: "200124785", devProfession: "2017/05/31", baseStation: "广州高澜节能股份有限公司", serialNo: "广州市高新技术产业开发区科学城南云五路3号 ", networkCategory: "201710202-0001", quantity: "201710202-0001", remark: ""},
        {assetCoding: "荣信墨西哥BrovaBia钢厂电炉SVCLWW168水冷系统 ",devFactory: "密封式循环纯水冷却装置", devType: "12112458", devModel: "200124785", devProfession: "2017/05/31", baseStation: "广州高澜节能股份有限公司", serialNo: "广州市高新技术产业开发区科学城南云五路3号 ", networkCategory: "201710202-0001", quantity: "201710202-0001", remark: ""},
        {assetCoding: "荣信墨西哥BrovaBia钢厂电炉SVCLWW168水冷系统 ",devFactory: "密封式循环纯水冷却装置", devType: "12112458", devModel: "200124785", devProfession: "2017/05/31", baseStation: "广州高澜节能股份有限公司", serialNo: "广州市高新技术产业开发区科学城南云五路3号 ", networkCategory: "201710202-0001", quantity: "201710202-0001", remark: ""},
        {assetCoding: "荣信墨西哥BrovaBia钢厂电炉SVCLWW168水冷系统 ",devFactory: "密封式循环纯水冷却装置", devType: "12112458", devModel: "200124785", devProfession: "2017/05/31", baseStation: "广州高澜节能股份有限公司", serialNo: "广州市高新技术产业开发区科学城南云五路3号 ", networkCategory: "201710202-0001", quantity: "201710202-0001", remark: ""},
        {assetCoding: "荣信墨西哥BrovaBia钢厂电炉SVCLWW168水冷系统 ",devFactory: "密封式循环纯水冷却装置", devType: "12112458", devModel: "200124785", devProfession: "2017/05/31", baseStation: "广州高澜节能股份有限公司", serialNo: "广州市高新技术产业开发区科学城南云五路3号 ", networkCategory: "201710202-0001", quantity: "201710202-0001", remark: ""},
    ];

    for(var i = 0; i <= mydata.length; i++){
        myGrid.jqGrid('addRowData', i+1, mydata[i]);
    }
    myGrid.setGridParam({total: "2"}).trigger("reloadGrid");
    
    function loadyys1() {
        //第二个参数可以指定前面引入的主题
        var myChart = echarts.init(document.getElementById('charts'));
        //图表显示提示信息
        myChart.showLoading();
        option = {
            title: {
                left:'center',
                top:'20',
                text: '设备数量统计',
                subtext: '数据更新时间2017-10-24',
                textStyle: {
                    align: 'right',
                },
            },
            toolbox: {
                show: true,
                top:'50',
                feature: {
                    dataView: {
                        readOnly: false,
                        lang:['数据视图', '关闭', '导出'],
                    },
                    magicType: {type: [ 'bar']},
                    restore: {},
                    saveAsImage: {}
                }
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },

            grid: {

            },
            xAxis: {
                axisLine:{
                    lineStyle:{
                        type:'dashed',
                        /*color:'#BFBFBF',*/
                        width:0,   //这里是坐标轴的宽度,可以去掉
                    }
                },
                type: 'value',
                boundaryGap: [0, 0.01],
                splitNumber: 10,
                splitLine: {
                    show: true,
                    lineStyle: {
                        color: '#ccc',
                        type:'dashed',
                    }
                },
            },
            yAxis: {
                axisLine:{
                    lineStyle:{
                        type:'dashed',
                        /*color:'#BFBFBF',*/
                        width:0,   //这里是坐标轴的宽度,可以去掉
                    }
                },
                type: 'category',
                data: ['直流水冷设备','FACTS水冷设备','新能源水冷设备','变频器水冷设备','蓄冷设备'],
                axisLabel:{
                    interval: 0,//标签设置为全部显示
                    formatter:function(params){
                        var newParamsName = "";// 最终拼接成的字符串
                        var paramsNameNumber = params.length;// 实际标签的个数
                        var provideNumber = 7;// 每行能显示的字的个数
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);// 换行的话，需要显示几行，向上取整

                        // 条件等同于rowNumber>1
                        if (paramsNameNumber > provideNumber) {

                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";// 表示每一次截取的字符串
                                var start = p * provideNumber;// 开始截取的位置
                                var end = start + provideNumber;// 结束截取的位置
                                // 此处特殊处理最后一行的索引值
                                if (p == rowNumber - 1) {
                                    // 最后一次不换行
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    // 每一次拼接字符串并换行
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;// 最终拼成的字符串
                            }

                        } else {
                            // 将旧标签的值赋给新标签
                            newParamsName = params;
                        }
                        //将最终的字符串返回
                        return newParamsName
                    }

                }
            },
            series: [

                {

                    type: 'bar',
                    barWidth : 20,//柱图宽度
                    data: [18203, 23489, 29034, 104970, 131744],
                    itemStyle:{

                        normal:{
                            color:'#00ABF7',
                            barBorderRadius: 20
                        }
                    },
                },


            ]
        };
        myChart.hideLoading();
        myChart.setOption(option, true);
    }
    
    loadyys1();
    
})