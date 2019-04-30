package com.anosi.asset.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.controller.remote.SensorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名 SensorServiceImpl
 * sensor实现层
 * 作者 倪文骅
 * 日期 2018-7-20 16:23:02
 */
@Service("sensorService")
@Transactional
public class SensorServiceImpl implements SensorService {

    /**
     * 把json格式数据转换成jqGrid格式
     * @since  2018/7/20 16:18
     * @author 倪文骅
     * @param jsonObject
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject parseIotxJsonDataToGrid(JSONObject jsonObject) {
        JSONArray content = jsonObject.getJSONArray("content");
        String totalPages = jsonObject.getString("totalPages");
        String totalElements = jsonObject.getString("totalElements");
        String pageNumber = jsonObject.getString("number");
        JSONObject newJsonObject = new JSONObject();
        newJsonObject.put("total", totalPages);
        newJsonObject.put("records", totalElements);
        newJsonObject.put("page", pageNumber);
        newJsonObject.put("rows", content);
        return newJsonObject;
    }
}
