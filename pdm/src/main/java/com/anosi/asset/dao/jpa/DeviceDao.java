package com.anosi.asset.dao.jpa;

import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.QDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import javax.persistence.EntityManager;
import java.util.List;

public interface DeviceDao extends BaseJPADao<Device>, QuerydslBinderCustomizer<QDevice> {

	@Override
	default public void customize(QuerydslBindings bindings, QDevice qDevice) {
		bindings.bind(qDevice.serialNo).first((path, value) -> {
			if (value.startsWith("contain$")) {
				value = value.replace("contain$", "");
				return path.contains(value);
			} else if (value.startsWith("start$")) {
				value = value.replace("start$", "");
				return path.startsWithIgnoreCase(value);
			} else if (value.startsWith("end$")) {
				value = value.replace("end$", "");
				return path.endsWithIgnoreCase(value);
			} else {
				return path.eq(value);
			}
		});
	}

	public Device findBySerialNo(String serialNo);
	
	public Device findByRfid(String rfid);

	default public Page<Device> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable) {
		return findBySearchContent(entityManager, searchContent, pageable, Device.class, "project.name",
				"project.number", "project.location", "productName", "productNo", "productSpecifications", "serialNo",
				"rfid");
	}
	
	@Query(value="SELECT new Device(d.id,d.serialNo) FROM Device d")
	public List<Device> findIdAndSN();

	/**
	 * 根据设备种类获取设备信息
	 * @since  2018/7/5 14:28
	 * @author 倪文骅
	 * @param devCategoryId 设备种类
	 * @return com.alibaba.fastjson.JSONArray
	 */
	List<Device> findByDevCategoryId(Long devCategoryId);

	/**
	* 根据设备id获取设备信息
	* @since  2018/7/18 10:54 
	* @author 倪文骅 
	* @param deviceId
	* @return java.util.List<java.lang.String>
	*/
	Device findById(Long deviceId);
}
