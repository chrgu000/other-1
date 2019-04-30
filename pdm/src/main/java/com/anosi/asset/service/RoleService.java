package com.anosi.asset.service;

import com.anosi.asset.model.jpa.Role;

public interface RoleService extends BaseJPAService<Role>{

	public Role findByCode(String code);

    /**
     * 根据id删除角色
     * @since  2018/6/14 16:15
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    void deleteRoleById(String id);
}
