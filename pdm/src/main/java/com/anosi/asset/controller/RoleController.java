package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.DepGroup;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DepGroupService;
import com.anosi.asset.service.RoleService;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RoleController extends BaseController<Role> {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	private RoleService roleService;

	@Autowired
	private DepGroupService depGroupService;


	/**
	* 跳转到角色管理页面
	* @since  2018/6/11 23:35
	* @author 倪文骅
	* @param
	* @return org.springframework.web.servlet.ModelAndView
	*/
	@RequestMapping(value = "/roleDepGroupManage/gotoRoleManageList")
	public ModelAndView gotoRoleManageList(){
		return new ModelAndView("roleDepGroupManage/roleManage");
	}



	/***
	 * 获得角色数据
	 *
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/role/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findRoleManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@QuerydslPredicate(root = Role.class) Predicate predicate,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("find roleFunctionGroup");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(roleService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}



	/****
	 * 在执行update前，先获取持久化的role对象
	 *
	 * @param id
	 * @param model
	 *
	 */
	@ModelAttribute
	public void getRole(@RequestParam(value = "roleId", required = false) Long id, Model model) {
		if (id != null) {
			model.addAttribute("role", roleService.getOne(id));
		}
	}

	/**
	* 保存/修改用户角色
	* @since  2018/6/11 23:10
	* @author 倪文骅
	* @param role
	* @return void
	*/
	@RequestMapping(value = "/roleDepGroupManage/save",method=RequestMethod.POST)
	@Transactional
	public JSONObject saveRole(@ModelAttribute("role") Role role , @RequestParam(value = "depGroup",required = false)DepGroup depGroups){
		Oplog.Operation operation = role.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
		role.setDepGroup(depGroups);
		roleService.save(role);
		saveLog(i18nComponent.getMessage("account.role"), operation, role.getName());
		return new JSONObject(ImmutableMap.of("result", "success"));
	}


	/**
	 * 按照role某些属性判断是否已存在
	 *
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/roleDepGroupManage/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@QuerydslPredicate(root = Role.class) Predicate predicate) {
		return new JSONObject(ImmutableMap.of("result", roleService.exists(predicate)));
	}




	/**
	 * 跳转到角色添加/修改页面
	 * @since  2018/6/11 10:56
	 * @author 倪文骅
	 * @param
	 * @return org.springframework.web.servlet.ModelAndView
	 */
	@RequestMapping(value = "/roleDepGroupManage/saveRole/view",method = RequestMethod.GET)
	public ModelAndView saveRole(@RequestParam(value = "id",required = false) Long id){
		logger.debug("view roleChange");
		Role role ;
		ModelAndView mv = new ModelAndView("roleDepGroupManage/saveRole");
		if(id!=null){
			role=roleService.findOne(id);
			//1个角色对应1个组
			mv.addObject("depGroupId",role.getDepGroup().getId());
		}else {
			role = new Role();
		}
		return mv
				.addObject("role",role).addObject("depGroups",depGroupService.findAll());
	}



	/**
	* 根据id删除角色
	* @since  2018/6/14 16:15
	* @author 倪文骅
	* @param id
	* @return com.alibaba.fastjson.JSONObject
	*/
	@RequestMapping(value = "/roleDepGroupManage/deleteRole", method = RequestMethod.POST)
	public JSONObject deleteRole(@RequestParam(value = "id") String id) {
		logger.debug("delete deleteRole");
		roleService.deleteRoleById(id);
		return new JSONObject(ImmutableMap.of("result", "success"));
	}


}
