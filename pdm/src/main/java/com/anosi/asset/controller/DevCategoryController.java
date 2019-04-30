package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.DevCategory;
import com.anosi.asset.service.DevCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DevCategoryController extends BaseController<DevCategory>{

	@Autowired
	private DevCategoryService devCategoryService;

	/***
	 * 获取各种类设备数量的统计结果，设备种类名称有中英文
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/devCategory/count", method = RequestMethod.GET)
	public JSONArray countByDevCategory() throws Exception {
		return devCategoryService.countByDevCategory();
	}

}
