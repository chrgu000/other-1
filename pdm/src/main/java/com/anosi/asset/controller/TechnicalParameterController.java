package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.TechnicalParameter;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.TechnicalParameterService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ListUtils;

import java.util.List;

@RestController
public class TechnicalParameterController extends BaseController<TechnicalParameter>{

	@Autowired
	private TechnicalParameterService technicalParameterService;
	@Autowired
	private DeviceService deviceService;
	
	/***
	 * 获取技术参数的元数据
	 * 可以通过控制predicate来完成某一设备的技术参数的查询
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/technicalParameter/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findTechnicalParameterData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@QuerydslPredicate(root = TechnicalParameter.class) Predicate predicate,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception{
		
		return parseToJson(technicalParameterService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

	/**
	 * 根据设备种类获取设备信息
	 * @since  2018/7/5 14:28
	 * @author 倪文骅
	 * @param categoryId 设备种类
	 * @return com.alibaba.fastjson.JSONArray
	 */
	@RequestMapping(value = "/report/findDeviceByDvCategory", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
	public JSONArray findDeviceByDvCategory(@RequestParam(value = "categoryId", required = false) Long categoryId){
		JSONArray jsonArray = new JSONArray();
//		String loginId = sessionComponent.getCurrentUser().getLoginId();
		//查询所有设备
			List<Device> deviceList = deviceService.findByDevCategoryId(categoryId);
			if(!ListUtils.isEmpty(deviceList)){
				for(Device device : deviceList){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("productName",device.getProductName());
					jsonObject.put("serialNo",device.getSerialNo());
					jsonObject.put("id",device.getId());
					jsonArray.add(jsonObject);
				}
			}
		return jsonArray;
	}
}
