package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Advertisement;

public interface AdvertisementService extends BaseJPAService<Advertisement>{

	Page<Advertisement> findByContentSearch(String searchContent, Pageable pageable);

	/**
	* 根据id删除广告
	* @since  2018/6/14 14:00 
	* @author 倪文骅
	* @param id  
	* @return void
	*/
    void deleteAdvertisement(String id);
}
