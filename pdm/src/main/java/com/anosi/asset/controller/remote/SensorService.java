package com.anosi.asset.controller.remote;

import com.alibaba.fastjson.JSONObject;

/**
* sensor接口
* @since  2018/7/20 16:21
* @author 倪文骅
*/
public interface SensorService {

    /**
     * 把json格式数据转换成jqGrid格式
     * @since  2018/7/20 16:18
     * @author 倪文骅
     * @param jsonObject
     * @return com.alibaba.fastjson.JSONObject
     */
    JSONObject parseIotxJsonDataToGrid(JSONObject jsonObject);
}
