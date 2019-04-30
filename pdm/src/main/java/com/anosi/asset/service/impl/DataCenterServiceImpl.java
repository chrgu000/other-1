package com.anosi.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.exception.DataCenterException;
import com.anosi.asset.service.DataCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service("dataCenterService")
@Transactional
public class DataCenterServiceImpl implements DataCenterService {

    @Autowired
    private ServerCallComponent serverCallComponent;

    @Override
    public JSONObject getIotxType(String iotx, String url, HttpSession session) throws IOException {

        String serialNo = null;
        try {
            if (iotx != null && iotx != "") {
                String result = serverCallComponent.executeGet(url);
                JSONObject jsonObject = JSON.parseObject(result);
                JSONObject advanced = jsonObject.getJSONObject("advanced");
                JSONObject iotxConfig = advanced.getJSONObject("iotxConfig");
                String dtype = iotxConfig.getString("dtype");
                System.out.println("测试1：" + dtype);
                if (dtype != null && dtype != "") {
                    JSONObject dtypeObj = new JSONObject();
                    dtypeObj.put("dtype", dtype);
                    return dtypeObj;
                } else {
                    String dustList = iotxConfig.getString("dustList");
                    JSONArray jsonArray = JSON.parseArray(dustList);
                    String o = jsonArray.getString(0);
                    JSONObject jsonObject1 = JSON.parseObject(o);
                    serialNo = jsonObject1.getString("serialNo");
                    session.setAttribute("serialNo", serialNo);
                    System.out.println("serialNo:" + serialNo);
                }
            }
        } catch (DataCenterException e) {
            serialNo = null;
        }
        return null;
    }

    @Override
    public JSONObject getPlcList(String iotx, int pageSize, int pageNumber, String sort, String url) throws IOException {
        try {
            if (iotx != null && iotx != "") {
                String result = serverCallComponent.executeGet(url);
                JSONObject jsonObject = JSON.parseObject(result);
                JSONArray content = jsonObject.getJSONArray("content");
                String totalPages = jsonObject.getString("totalPages");
                String totalElements = jsonObject.getString("totalElements");
                JSONArray jsonArray1 = new JSONArray();
                for (int i = 0; i < content.size(); i++) {
                    JSONObject jsonObject2 = new JSONObject();
                    JSONObject contentObj = content.getJSONObject(i);
                   // JSONObject plcConfig = contentObj.getJSONObject("plcConfig");
                   // String plcInterface = plcConfig.getString("plcInterface");//通讯协议
                    //String address = plcConfig.getString("address");//通讯地址
                    String type = contentObj.getString("type");//数据类型
                    String name = contentObj.getString("name");//点位名称
                    String uid = contentObj.getString("uid");//点位id
                    String iotSerialNo = contentObj.getString("iotSerialNo");//网元号
                    String pointAddress = contentObj.getString("pointAddress");//读取地址
                    String lastVal = contentObj.getString("lastVal");//实时数据
                    String collectTime = contentObj.getString("collectTime");//采集时间
                    String unit = contentObj.getString("unit");
                    //jsonObject2.put("plcInterface", plcInterface);
                   // jsonObject2.put("address", address);
                    jsonObject2.put("type", type);
                    jsonObject2.put("name", name);
                    jsonObject2.put("uid", uid);
                    jsonObject2.put("iotSerialNo", iotSerialNo);
                    jsonObject2.put("pointAddress", pointAddress);
                    jsonObject2.put("lastVal", lastVal);
                    jsonObject2.put("collectTime", collectTime);
                    jsonObject2.put("unit", unit);
                    jsonArray1.add(jsonObject2);
                }
                JSONObject resultJson1 = new JSONObject();
                resultJson1.put("total", totalPages);
                resultJson1.put("records", totalElements);
                resultJson1.put("page", pageNumber);
                resultJson1.put("rows", jsonArray1);
                return resultJson1;
            }
        } catch (DataCenterException e) {

        }
        return null;
    }

    @Override
    public JSONArray getMonitorData(String seria, String url) throws IOException {
        String result = serverCallComponent.executePost(url);
        JSONObject jsonObject = JSON.parseObject(result);
        String content = jsonObject.getString("content");
        JSONArray jsonArray = JSON.parseArray(content);
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String one = jsonArray.getString(i);
            JSONObject jsonObject1 = JSON.parseObject(one);
            String dustMac = jsonObject1.getString("dustMac");//mac地址
            String val = jsonObject1.getString("val");
            String[] split = val.split(",");
            String temperature = split[0];//温度
            String humidity = split[1];//湿度
            String collectTime = jsonObject1.getString("collectTime");//采集时间
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("dustMac", dustMac);
            jsonObject2.put("temperature", temperature + "℃");
            jsonObject2.put("humidity", humidity + "%");
            jsonObject2.put("collectTime", collectTime);
            System.out.println("mac地址：" + dustMac + " 温度：" + temperature + " 湿度：" + humidity + " 采集时间：" + collectTime);
            jsonArray1.add(jsonObject2);
        }
        return jsonArray1;
    }

    @Override
    public JSONArray getChartData(String startTime, String endTime, String seriaNo, String url) throws IOException {
        String result = serverCallComponent.executePost(url);
        JSONObject jsonObject = JSON.parseObject(result);
        String content = jsonObject.getString("content");
        JSONArray jsonArray = JSON.parseArray(content);
        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            String one = jsonArray.getString(i);
            JSONObject jsonObject1 = JSON.parseObject(one);
            String val = jsonObject1.getString("val");
            String[] split = val.split(",");
            String temperature = split[0];//温度
            String humidity = split[1];//湿度
            String collectTime = jsonObject1.getString("collectTime");//采集时间
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("temperature", temperature);
            jsonObject2.put("humidity", humidity);
            jsonObject2.put("collectTime", collectTime);
            jsonArray1.add(jsonObject2);
        }
        return jsonArray1;
    }

    @Override
    public JSONObject getHistoryTableData(String seriaNo, int pageSize, int pageNumber, String sort, String url) throws IOException {
        String result = serverCallComponent.executePost(url);
        JSONObject jsonObject = JSON.parseObject(result);
        String content = jsonObject.getString("content");
        String totalPages = jsonObject.getString("totalPages");
        String totalElements = jsonObject.getString("totalElements");
        JSONArray jsonArray = JSON.parseArray(content);

        System.out.println("总页数：" + totalPages + ", 总数据：" + totalElements);

        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String one = jsonArray.getString(i);
            JSONObject jsonObject1 = JSON.parseObject(one);
            String val = jsonObject1.getString("val");
            String[] split = val.split(",");
            String temperature = split[0];//温度
            String humidity = split[1];//湿度
            String collectTime = jsonObject1.getString("collectTime");//采集时间
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("temperature", temperature + "℃");
            jsonObject2.put("humidity", humidity + "%");
            jsonObject2.put("collectTime", collectTime);
            jsonArray1.add(jsonObject2);
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("total", totalPages);
        resultJson.put("records", totalElements);
        resultJson.put("page", pageNumber);
        resultJson.put("rows", jsonArray1);
        return resultJson;
    }

    /**
     * 根据id和name获取网元信息
     * @since  2018/7/3 15:29
     * @author 倪文骅
     * @param httpUrl
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject findIotxByNameAndId(String httpUrl) throws IOException {
        String result = serverCallComponent.executeGet(httpUrl);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }


    /**
     * 根据iotSerialNo，pointUid，日期获取温度
     * @since  2018/7/3 16:20
     * @author 倪文骅
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONArray getIotxDataInfoByUidAndDate(String httpUrl) throws IOException {
        String result = serverCallComponent.executePost(httpUrl);
        JSONObject jsonObject = JSON.parseObject(result);
        String content = jsonObject.getString("content");
        JSONArray jsonArray = JSON.parseArray(content);
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String objStr = jsonArray.getString(i);
            JSONObject jsonObject1 = JSON.parseObject(objStr);
            String val = jsonObject1.getString("val");
            String collectTime = jsonObject1.getString("collectTime");//采集时间
            String uid = jsonObject1.getString("pointUid");
            String[] split = uid.split("_");
            String s = split[2];
            String typeName = "";
            if(uid!=null){
                switch (s){
                    case "1008":typeName="粉尘浓度" ;
                        break;
                    case "1012":typeName="压力" ;
                        break;
                    case "1016":typeName="温度" ;
                        break;
                    case "1020":typeName="湿度" ;
                        break;
                    case "1024":typeName="风量" ;
                        break;
                    case "1028":typeName="压差" ;
                        break;
                }
            }
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("val", val);
            jsonObject2.put("collectTime", collectTime);
            jsonObject2.put("typeName", typeName);
            jsonArray1.add(jsonObject2);
        }
        return jsonArray1;
    }

    /**
     * 获取点位名称
     * @since  2018/7/23 17:43
     * @author 倪文骅
     * @param uid
     * @return java.lang.String
     */
    @Override
    public String getTypeName(String uid) {
//        String[] split = uid.split("_");
//        String s = split[2];
        String typeName = "";
        if(uid!=null){
            switch (uid){
//                case "1008":typeName="粉尘浓度" ;
                case "AWIGZ180709001_1343534396_0":typeName="蓄冷槽上端入口温度" ;
                    break;
//                case "1012":typeName="压力" ;
                case "AWIGZ180709001_1230278704_8":typeName="放冷端供水温度" ;
                    break;
//                case "1016":typeName="温度" ;
                case "AWIGZ180709001_1343534396_2":typeName="蓄冷槽下端入口温度" ;
                    break;
//                case "1020":typeName="湿度" ;
                case "AWIGZ180709001_1343537134_4":typeName="新增空调主机出口温度" ;
                    break;
//                case "1024":typeName="风量" ;
                case "AWIGZ180709001_1343534396_6":typeName="放冷板换入口温度" ;
                    break;
//                case "1028":typeName="压差" ;
                case "AWIGZ180709001_-1150370546_24":typeName="水槽第13层温度";
                    break;
                case "AWIGZ180709001_1343534396_38":typeName="环境温度" ;
                    break;
            }
        }
        return typeName;
    }
}
