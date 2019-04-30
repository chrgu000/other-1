package com.anosi.asset.service.impl;

import com.anosi.asset.model.mongo.Oplog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.RoleDao;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.service.RoleService;

@Service("roleService")
@Transactional
public class RoleServiceImpl extends BaseJPAServiceImpl<Role> implements RoleService{

	@Autowired
	private RoleDao roleDao;

	@Override
	public Role findByCode(String code) {
		return roleDao.findByCodeEquals(code);
	}

    @Override
	public BaseJPADao<Role> getRepository() {
		return roleDao;
	}


	/**
	 * 根据id删除角色
	 * @since  2018/6/14 16:15
	 * @author 倪文骅
	 * @param id
	 * @return com.alibaba.fastjson.JSONObject
	 */
	@Override
	public void deleteRoleById(String id) {
		String[] ids = id.split("-");
		for (int i=0;i<ids.length;i++){
			roleDao.delete(Long.parseLong(ids[i]));
		}
		saveLog(i18nComponent.getMessage("account.role"), Oplog.Operation.DEL, ids.length + "个");
	}
}
