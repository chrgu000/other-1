package com.anosi.asset.service;

import java.io.InputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Materiel;

public interface MaterielService extends BaseJPAService<Materiel>{

	Page<Materiel> findBySearchContent(String searchContent, String deviceSN, Pageable pageable);

	void batchSave(InputStream inputStream,Long deviceId) throws Exception;


	/**
	* 根据设备id查询物料数据
	* @since  2018/6/6 21:45
	* @author 倪文骅 
	* @param deviceId	设备id
	* @param pageable  分页
	* @return org.springframework.data.domain.Page<com.anosi.asset.model.jpa.Materiel>
	*/
	Page<Materiel> findMaterielByDeviceId(String deviceId, Pageable pageable);

	/**
	* 根据行号删除物料
	* @since  2018/6/8 13:40
	* @author 倪文骅 
	* @param id  
	* @return void
	*/
    void deleteMaterielById(String id);
}
