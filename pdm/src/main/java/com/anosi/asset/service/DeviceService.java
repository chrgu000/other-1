package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Device;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

public interface DeviceService extends BaseJPAService<Device>{

	public Device findBySerialNo(String serialNo);

	/***
	 * 为设备设置经纬度
	 * 
	 * @param device
	 * @return
	 */
	Device setDeviceDistrict(Device device);

	/***
	 * 获取设备的分布
	 * 
	 * @param predicate
	 * @return
	 */
	public JSONArray ascertainArea(Predicate predicate);

	/****
	 * 模糊搜索
	 * 
	 * @param searchContent
	 * @param pageable
	 * @return
	 */
	public Page<Device> findByContentSearch(String searchContent, Pageable pageable);
	
	public Device findByRfid(String rfid);
	
	public List<Device> findIdAndSN();

	/***
	 * 批量导入devices
	 * @param inputStream
	 * @throws Exception
	 */
    void batchSave(InputStream inputStream) throws Exception;
	
    /**
    * 根据id删除devices
    * @since  2018/6/6 15:28 
    * @author 倪文骅 
    * @param id  
    * @return
    */
    boolean deleteDevice(String id);

	/**
	 * 根据设备种类获取设备信息
	 * @since  2018/7/5 14:28
	 * @author 倪文骅
	 * @param categoryId 设备种类
	 * @return com.alibaba.fastjson.JSONArray
	 */
	List<Device> findByDevCategoryId(Long categoryId);
}
