package com.anosi.asset.service.impl;


import com.anosi.asset.model.mongo.Oplog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DepartmentDao;
import com.anosi.asset.model.jpa.Department;
import com.anosi.asset.service.DepartmentService;

@Service("departmentService")
@Transactional
public class DepartmentServiceImpl extends BaseJPAServiceImpl<Department> implements DepartmentService{

	@Autowired
	private DepartmentDao departmentDao;

	@Override
	public Department findByCode(String code) {
		return departmentDao.findByCode(code);
	}

    @Override
	public BaseJPADao<Department> getRepository() {
		return departmentDao;
	}


	/**
	 * 根据id删除部门
	 * @since  2018/6/14 16:15
	 * @author 倪文骅
	 * @param id
	 * @return com.alibaba.fastjson.JSONObject
	 */
	@Override
	public void deleteDepartmentById(String id) {
		String[] ids = id.split("-");
		for (int i=0;i<ids.length;i++){
			departmentDao.delete(Long.parseLong(ids[i]));
		}
		saveLog(i18nComponent.getMessage("account.role.group.department"), Oplog.Operation.DEL, ids.length + "个");
	}
}
