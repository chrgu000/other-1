package com.anosi.asset.service;

import com.anosi.asset.model.jpa.Department;

public interface DepartmentService extends BaseJPAService<Department>{

	public Department findByCode(String code);

    /**
     * 根据id删除部门
     * @since  2018/6/14 16:15
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    void deleteDepartmentById(String id);
}
