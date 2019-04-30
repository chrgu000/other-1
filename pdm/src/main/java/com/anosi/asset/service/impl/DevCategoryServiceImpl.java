package com.anosi.asset.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DevCategoryDao;
import com.anosi.asset.model.jpa.DevCategory;
import com.anosi.asset.service.DevCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("devCategoryService")
@Transactional
public class DevCategoryServiceImpl extends BaseJPAServiceImpl<DevCategory> implements DevCategoryService {

	@Autowired
	private DevCategoryDao devCategoryDao;

	@Override
	public BaseJPADao<DevCategory> getRepository() {
		return devCategoryDao;
	}

	@Override
	public JSONArray countByDevCategory() {
		List<DevCategory> devCategories = devCategoryDao.findAll();

		JSONArray jsonArray = new JSONArray();
		for (DevCategory category : devCategories) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", category.getCategoryType().getName());
			jsonObject.put("code", category.getCategoryType().getCode());
			jsonObject.put("count", category.getDeviceList().size());
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

	@Override
	public DevCategory findByCategoryTypeCode(String categoryType){
		return devCategoryDao.findByCategoryType_CodeEquals(categoryType);
	}

}
