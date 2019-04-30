package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Department;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DepGroupService;
import com.anosi.asset.service.DepartmentService;
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

/**
 * 类名 DepartmentController
 * 类型描述
 * 作者 20180329
 * 日期
 */
@RestController
public class DepartmentController extends BaseController<Department> {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepGroupService depGroupService;


    /**
     * 跳转部门管理页面
     * @since  2018/6/12 0:06
     * @author 倪文骅
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value = "/roleDepGroupManage/gotoDepartmentList",method = RequestMethod.GET)
    public ModelAndView gotoDepartmentList(){
        logger.debug("gotoDepartmentList");
        return new ModelAndView("roleDepGroupManage/departmentManage");
    }

    /**
     * 获取部门数据
     * @since  2018/6/12 15:08
     * @author 倪文骅
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/department/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findDepartmentManageData(@PathVariable ShowType showType,
                                               @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                               @QuerydslPredicate(root = Department.class) Predicate predicate,
                                               @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                               @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
        logger.info("find Department");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);
        return parseToJson(departmentService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }

    /**
     * 跳转到部门添加/修改页面
     * @since  2018/6/12 15:12
     * @author 倪文骅
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value = "/roleDepGroupManage/saveDepartment/view",method = RequestMethod.GET)
    public ModelAndView saveDepartment(@RequestParam(value = "id",required = false) Long id){
        logger.debug("view saveDepartment");
        Department department;
        ModelAndView mv = new ModelAndView("roleDepGroupManage/saveDepartment");
        if(id!=null){
            department=departmentService.findOne(id);
            //一个部门下有多个组,获取组id
            //mv.addObject("depGroupId",department.getDepGroupList().stream().map(DepGroup::getId).collect(Collectors.toList()));
        }else {
            department = new Department();
        }
        return mv.addObject("department",department)
                .addObject("depGroups",depGroupService.findAll());
    }

    /**
     * 保存/修改部门
     * @since  2018/6/12 15:20
     * @author 倪文骅
     * @param department
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/saveDepartment",method=RequestMethod.POST)
    @Transactional
    public JSONObject saveDepartment(@ModelAttribute("department") Department department){
        Oplog.Operation operation = department.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        departmentService.save(department);
        /*if (null !=depGroups && depGroups.length !=0){
            for(String group : depGroups){
//                DepGroup depGroup = new DepGroup();
                DepGroup depGroupData = depGroupService.findOne(Long.parseLong(group));
//                depGroup.setName(depGroupData.getName());
//                depGroup.setCode(depGroupData.getCode());
                department.getDepGroupList().add(depGroupData);
            }
        }*/
        saveLog(i18nComponent.getMessage("account.role.group.department"), operation, department.getName());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /**
     * 判断部门是否已存在
     * @since  2018/6/12 15:25
     * @author 倪文骅
     * @param predicate
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/checkDepartmentExist", method = RequestMethod.GET)
    public JSONObject checkDepartmentExist(@QuerydslPredicate(root = Department.class) Predicate predicate) {
        return new JSONObject(ImmutableMap.of("result", departmentService.exists(predicate)));
    }

    /****
     * 在执行update前，先获取持久化的department对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getRole(@RequestParam(value = "departmentId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("department", departmentService.getOne(id));
        }
    }

    /**
     * 根据id删除部门
     * @since  2018/6/14 16:15
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/deleteDepartment", method = RequestMethod.POST)
    public JSONObject deleteDepartment(@RequestParam(value = "id") String id) {
        logger.debug("delete deleteDepartment");
        departmentService.deleteDepartmentById(id);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }
}
