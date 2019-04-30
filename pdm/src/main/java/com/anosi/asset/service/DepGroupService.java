package com.anosi.asset.service;

import com.anosi.asset.model.jpa.DepGroup;

public interface DepGroupService extends BaseJPAService<DepGroup>{

	public DepGroup findByCode(String code);

    /**
     * 根据id删除组
     * @since  2018/6/14 16:15
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    void deleteDepGroupById(String id);
}
