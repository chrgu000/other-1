package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.DepGroup;
import com.anosi.asset.model.jpa.Department;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DepGroupService;
import com.anosi.asset.service.DepartmentService;
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

/**
 * 类名 DepartmentController
 * 类型描述
 * 作者 20180329
 * 日期
 */
@RestController
public class DepGroupController extends BaseController<DepGroup> {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private DepGroupService depGroupService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 跳转到组添加/修改页面
     * @since  2018/6/12 15:12
     * @author 倪文骅
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value = "/roleDepGroupManage/saveDepGroup/view",method = RequestMethod.GET)
    public ModelAndView saveDepGroup(@RequestParam(value = "id",required = false) Long id){
        logger.debug("view saveDepGroup");
        DepGroup depGroup ;
        ModelAndView mv = new ModelAndView("roleDepGroupManage/saveDepGroup");
        if(id!=null){
            depGroup=depGroupService.findOne(id);
            //获取所有角色roleid(多个角色)
            //mv.addObject("roleId",depGroup.getRoleList().stream().map(Role::getId).collect(Collectors.toList()));
            //部门是单个
            mv.addObject("departmentId",depGroup.getDepartment().getId());

        }else {
            depGroup = new DepGroup();
        }
        return mv
                .addObject("depGroup",depGroup)
                .addObject("roles",roleService.findAll())
                .addObject("departments",departmentService.findAll());
    }

    /**
     * 跳转组管理页面
     * @since  2018/6/12 0:06
     * @author 倪文骅
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value = "/roleDepGroupManage/gotoDepGroupList")
    private ModelAndView gotoDepGroupList(){
        return new ModelAndView("roleDepGroupManage/depGroupManage");
    }

    /**
     * 保存/修改组
     * @since  2018/6/12 15:20
     * @author 倪文骅
     * @param depGroup
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/saveDepGroup",method=RequestMethod.POST)
    @Transactional
    public JSONObject saveDepGroup(@ModelAttribute("depGroup") DepGroup depGroup, @RequestParam("department") Department departments){
        Oplog.Operation operation = depGroup.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        depGroup.setDepartment(departments);
        depGroupService.save(depGroup);
        saveLog(i18nComponent.getMessage("account.role.group"), operation, depGroup.getName());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /**
     * 获取组数据
     * @since  2018/6/12 15:10
     * @author 倪文骅
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/depGroup/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findDepGroupManageData(@PathVariable ShowType showType,
                                             @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                             @QuerydslPredicate(root = DepGroup.class) Predicate predicate,
                                             @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                             @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
        logger.info("find DepGroup");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        return parseToJson(depGroupService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }

    /**
     * 判断组是否已存在
     * @since  2018/6/12 15:25
     * @author 倪文骅
     * @param predicate
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/checkDepGroupExist", method = RequestMethod.GET)
    public JSONObject checkDepGroupExist(@QuerydslPredicate(root = DepGroup.class) Predicate predicate) {
        return new JSONObject(ImmutableMap.of("result", depGroupService.exists(predicate)));
    }

    /****
     * 在执行update前，先获取持久化的role对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getRole(@RequestParam(value = "depGroupId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("depGroup", depGroupService.getOne(id));
        }
    }

    /**
     * 根据id删除组
     * @since  2018/6/14 16:15
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/roleDepGroupManage/deleteDepGroup", method = RequestMethod.POST)
    public JSONObject deleteDepGroup(@RequestParam(value = "id") String id) {
        logger.debug("delete deleteDepGroup");
        depGroupService.deleteDepGroupById(id);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }
}
