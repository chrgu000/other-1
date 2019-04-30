package com.anosi.asset.dao.jpa;

import com.anosi.asset.model.jpa.Iotx;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IotxSaveDao extends BaseJPADao<Iotx>{
    @Query(value = "select d.serial_no  from  iotx d,device_iotx_list t  where d.id = t.iotx_list_id and t.device_list_id = ?1", nativeQuery = true)
    List<String> findByDevice(String deviceId);

    @Query(value = "select d.* from  iotx d where d.serial_no = ?1", nativeQuery = true)
    Iotx findBySerialNo(String serialNo);


    /**
     * 根据设备种类获取设备信息
     * @since  2018/7/5 14:28
     * @author 倪文骅
     * @param categoryType
     * @return com.alibaba.fastjson.JSONArray
     */
//    @Query(value = "select d.id from DevCategory d where d.category_type = ?1",nativeQuery = true)
//    List<DevCategory> findDeviceByDvCategory(String categoryType);

    /**
     * 根据组获取iotx信息
     * @since  2018/7/26 14:21
     * @author 倪文骅
     * @param groupId
     * @return com.anosi.asset.model.jpa.Iotx
     */
    @Query(value = "select d.* from iotx d where d.group_id = ?1",nativeQuery = true)
    Iotx findByGroupId(String groupId);

    @Query(value = "select i.* from  iotx i ,Device d where i.device_id = d.id and i.device_id = ?1", nativeQuery = true)
    List<Iotx> findByDeviceId(Long deviceId);
}
