package com.anosi.asset.service.impl;


import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SystemInfoDao;
import com.anosi.asset.model.jpa.SystemInfo;
import com.anosi.asset.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("systemInfoService")
@Transactional
public class SystemInfoServiceImpl extends BaseJPAServiceImpl<SystemInfo> implements SystemInfoService {
	@Autowired
	private SystemInfoDao systemInfoDao;

	@Override
	public BaseJPADao<SystemInfo> getRepository() {
		return systemInfoDao;
	}

	@Override
	public SystemInfo getSystemInfo() {
		SystemInfo systemInfo = new SystemInfo();
		List<SystemInfo> list = this.systemInfoDao.findAll();
		if (list != null && list.size() > 0) {
			systemInfo = list.get(0);
		}
		return systemInfo;
	}
}
