package com.anosi.asset.service.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.AdvertisementDao;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.model.jpa.Advertisement;
import com.anosi.asset.service.AdvertisementService;

@Transactional
@Service("advertisementService")
public class AdvertisementServiceImpl extends BaseJPAServiceImpl<Advertisement> implements AdvertisementService {

	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private EntityManager entityManager;

	@Override
	public BaseJPADao<Advertisement> getRepository() {
		return advertisementDao;
	}

	@Override
	public Page<Advertisement> findByContentSearch(String searchContent, Pageable pageable) {
		return advertisementDao.findBySearchContent(entityManager, searchContent, pageable);
	}


	/**
	 * 根据id删除广告
	 * @since  2018/6/14 14:00
	 * @author 倪文骅
	 * @param id
	 * @return void
	 */
    @Override
    public void deleteAdvertisement(String id) {
		String[] ids = id.split("-");
		for (int i=0;i<ids.length;i++){
			advertisementDao.delete(Long.parseLong(ids[i]));
		}
    }

}
