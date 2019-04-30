package com.anosi.asset.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DeviceDao;
import com.anosi.asset.dao.jpa.IotxSaveDao;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.service.IotxSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import java.util.List;

@Service("iotxSaveService")
@Transactional
public class IotxSaveServiceImpl extends BaseJPAServiceImpl<Iotx> implements IotxSaveService {
    @Autowired
    private IotxSaveDao iotxSaveDao;
    @Autowired
    private DeviceDao deviceDao;


    @Override
    public BaseJPADao<Iotx> getRepository() {
        return iotxSaveDao;
    }


    /**
    * 根据设备id获取设备关联的网元信息
    * @since  2018/7/18 10:59
    * @author 倪文骅
    * @param deviceId
    * @return com.alibaba.fastjson.JSONArray
    */
    @Override
    public JSONArray findByDevice(Long deviceId) {
        //List<String> findByDevice = iotxSaveDao.findByDevice(deviceId);
        List<Iotx> iotxList = deviceDao.findById(deviceId).getIotxList();
        JSONArray jsonArray = new JSONArray();
        int i=1;
        if(!ListUtils.isEmpty(iotxList)){
            for(Iotx iotx : iotxList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("orderNum",i);
                jsonObject.put("serialNo",iotx.getSerial_no());
                jsonObject.put("groupId",iotx.getGroup_id());
                jsonArray.add(jsonObject);
                i++;
            }
        }
        return jsonArray;
    }

    @Override
    public Iotx findBySerialNo(String serialNo) {
        return iotxSaveDao.findBySerialNo(serialNo);
    }


    /**
     * 根据组获取iotx信息
     * @since  2018/7/26 14:21
     * @author 倪文骅
     * @param groupId
     * @return com.anosi.asset.model.jpa.Iotx
     */
    @Override
    public Iotx findByGroupId(String groupId) {
        return iotxSaveDao.findByGroupId(groupId);
    }

    @Override
    public List<Iotx> findByDeviceId(Long deviceId) {
        return iotxSaveDao.findByDeviceId(deviceId);
    }


}
