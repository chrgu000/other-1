package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.CategoryType;
import com.anosi.asset.model.jpa.DevCategory;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.CategoryTypeService;
import com.anosi.asset.service.DevCategoryService;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
public class CategoryTypeController extends BaseController<CategoryType>{

	@Autowired
	private DevCategoryService devCategoryService;
	@Autowired
	private CategoryTypeService categoryTypeService;

	/****
	 * 在执行update前，先获取持久化的对象
	 *
	 * @param id
	 * @param model
	 *
	 */
	@ModelAttribute
	public void getRole(@RequestParam(value = "categoryId", required = false) Long id, Model model) {
		if (id != null) {
			model.addAttribute("categoryType", categoryTypeService.getOne(id));
		}
	}

	/**
	 * 跳转到分类页面
	 * @since  2018/6/11 23:35
	 * @author 倪文骅
	 * @param
	 * @return org.springframework.web.servlet.ModelAndView
	 */
	@RequestMapping(value = "/category/management/view")
	public ModelAndView gotoCategoryManageList(){
		return new ModelAndView("category/categoryManage");
	}

	/***
	 * 获得分类数据
	 *
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findRoleManageData(@PathVariable ShowType showType,
										 @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
										 @QuerydslPredicate(root = CategoryType.class) Predicate predicate,
										 @RequestParam(value = "showAttributes", required = false) String showAttributes,
										 @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		log.info("find category");
		log.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		log.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(categoryTypeService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

	/**
	 * 保存/修改用户角色
	 * @since  2018/6/11 23:10
	 * @author 倪文骅
	 * @param categoryType
	 * @return void
	 */
	@RequestMapping(value = "/category/save",method=RequestMethod.POST)
	@Transactional
	public JSONObject save(@ModelAttribute("categoryType") CategoryType categoryType){
		boolean isNew = categoryType.getId() == null;
		categoryTypeService.save(categoryType);
		if (isNew) {
			DevCategory devCategory = new DevCategory();
			devCategory.setCategoryType(categoryType);
			devCategoryService.save(devCategory);
		}
		saveLog("设备型号", isNew ? Oplog.Operation.ADD : Oplog.Operation.EDIT, categoryType.getName());
		return new JSONObject(ImmutableMap.of("result", "success"));
	}


	/**
	 * 按照role某些属性判断是否已存在
	 *
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@QuerydslPredicate(root = CategoryType.class) Predicate predicate) {
		return new JSONObject(ImmutableMap.of("result", categoryTypeService.exists(predicate)));
	}




	/**
	 * 跳转到角色添加/修改页面
	 * @since  2018/6/11 10:56
	 * @author 倪文骅
	 * @param
	 * @return org.springframework.web.servlet.ModelAndView
	 */
	@RequestMapping(value = "/category/save/view",method = RequestMethod.GET)
	public ModelAndView toSave(@RequestParam(value = "id",required = false) Long id){
		log.debug("view categoryChange");
		CategoryType categoryType ;
		ModelAndView mv = new ModelAndView("category/save");
		if(id!=null){
			categoryType=categoryTypeService.findOne(id);
		}else {
			categoryType = new CategoryType();
		}
		return mv.addObject("categoryType",categoryType);
	}



	/**
	 * 根据id删除角色
	 * @since  2018/6/14 16:15
	 * @author 倪文骅
	 * @param id
	 * @return com.alibaba.fastjson.JSONObject
	 */
	@RequestMapping(value = "/category/delete", method = RequestMethod.POST)
	public JSONObject delete(@RequestParam(value = "id") Long id) {
		log.debug("delete category");
		String name = categoryTypeService.findOne(id).getName();
		categoryTypeService.delete(id);
		saveLog("设备型号", Oplog.Operation.DEL, name);
		return new JSONObject(ImmutableMap.of("result", "success"));
	}

}
