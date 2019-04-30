package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Iotx;

import java.util.List;


public interface IotxSaveService extends BaseJPAService<Iotx> {

    JSONArray findByDevice(Long deviceId);

    Iotx findBySerialNo(String serialNo);


    /**
    * 根据组获取iotx信息
    * @since  2018/7/26 14:21
    * @author 倪文骅
    * @param groupId
    * @return com.anosi.asset.model.jpa.Iotx
    */
    Iotx findByGroupId(String groupId);

    /**
    * 根据设备id获取网元信息
    * @since  2018/9/27 9:56
    * @author 倪文骅 
    * @param deviceId
    * @return com.anosi.asset.model.jpa.Iotx
    */
    List<Iotx> findByDeviceId(Long deviceId);
}
