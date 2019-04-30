package com.anosi.asset.service.impl;


import com.anosi.asset.model.mongo.Oplog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DepGroupDao;
import com.anosi.asset.model.jpa.DepGroup;
import com.anosi.asset.service.DepGroupService;

@Service("depGroupService")
@Transactional
public class DepGroupServiceImpl extends BaseJPAServiceImpl<DepGroup> implements DepGroupService{

	@Autowired
	private DepGroupDao depGroupDao;

	@Override
	public DepGroup findByCode(String code) {
		return depGroupDao.findByCode(code);
	}

    @Override
	public BaseJPADao<DepGroup> getRepository() {
		return depGroupDao;
	}


	/**
	 * 根据id删除组
	 * @since  2018/6/14 16:15
	 * @author 倪文骅
	 * @param id
	 * @return com.alibaba.fastjson.JSONObject
	 */
	@Override
	public void deleteDepGroupById(String id) {
		String[] ids = id.split("-");
		for (int i=0;i<ids.length;i++){
			depGroupDao.delete(Long.parseLong(ids[i]));
		}
		saveLog(i18nComponent.getMessage("account.role.group"), Oplog.Operation.DEL, ids.length + "个");
	}
}
