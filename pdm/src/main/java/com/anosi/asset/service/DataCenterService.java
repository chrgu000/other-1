package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpSession;
import java.io.IOException;

public interface DataCenterService {

    /**
     * 获取Iotx类型，用来判断是传感器还是PLC
     * @return
     */
        public JSONObject getIotxType(String iotx, String url, HttpSession session) throws IOException;

    /**
     * 分页获取PLC采集到的数据
     * @param pageSize
     * @param pageNumber
     * @param sort
     * @param url
     * @return
     */
        public JSONObject getPlcList(String iotx, int pageSize, int pageNumber, String sort, String url) throws IOException;

    /**
     * 获取数据监控采集数据
     * @param seria 微尘地址
     * @param url
     * @return
     */
        public JSONArray getMonitorData(String seria, String url) throws IOException;

    /**
     * 获取历史数据（用于绘制曲线图，默认100条实时数据）
     * @param startTime
     * @param endTime
     * @param seriaNo
     * @param url
     * @return
     */
        public JSONArray getChartData(String startTime, String endTime, String seriaNo, String url) throws IOException;

    /**
     * 分页获取所有历史数据
     * @param seriaNo
     * @param pageSize
     * @param pageNumber
     * @param sort
     * @param url
     * @return
     */
        public JSONObject getHistoryTableData(String seriaNo, int pageSize, int pageNumber, String sort, String url) throws IOException;

     /**
     * 根据id和name获取网元信息
     * @since  2018/7/3 15:29
     * @author 倪文骅 
     * @param httpUrl
     * @return com.alibaba.fastjson.JSONObject
     */   
    JSONObject findIotxByNameAndId(String httpUrl) throws IOException;

    /**
    * 根据iotSerialNo，pointUid，日期获取数据
    * @since  2018/7/3 16:20
    * @author 倪文骅 
    * @return com.alibaba.fastjson.JSONObject
    */
    JSONArray getIotxDataInfoByUidAndDate(String httpUrl) throws IOException;

    /**
    * 获取点位名称
    * @since  2018/7/23 17:43
    * @author 倪文骅
    * @param uid
    * @return java.lang.String
    */
    String getTypeName(String uid);
}
