package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.DailyMaintenance;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DailyMaintenanceService;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.util.DateFormatUtil;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * 日常维护controller
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.controller
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 15:22
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 15:22
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@RestController
public class DailyMaintenanceController extends BaseController<DailyMaintenance> {

    private static final Logger logger = LoggerFactory.getLogger(DailyMaintenanceController.class);

    @Autowired
    private DailyMaintenanceService dailyMaintenanceService;

    @Autowired
    private DeviceService deviceService;

    /***
     * 进入查看日常维护表的页面
     *
     * @return
     */
    @RequestMapping(value = "/dailyMaintenance/management/view", method = RequestMethod.GET)
    public ModelAndView toViewDailyMaintenanceManage(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("view dailyMaintenance manage");
        return new ModelAndView("dailyMaintenance/dailyMaintenanceManage").addObject("deviceId",deviceId);
    }

    /***
     * 获取日常维护表
     *
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dailyMaintenance/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findDailyMaintenanceManageData(@PathVariable ShowType showType,
                                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                             @QuerydslPredicate(root = DailyMaintenance.class) Predicate predicate,
                                             @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                             @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId
                                            ) throws Exception {
        logger.info("find dailyMaintenance");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);
        return parseToJson(dailyMaintenanceService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }

    /****
     * 在执行update前，先获取持久化的materiel对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getDailyMaintenance(@RequestParam(value = "dailyMaintenanceId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("dailyMaintenance", dailyMaintenanceService.getOne(id));
        }
    }

    /***
     * save/update dailyMaintenance
     *
     * @param dailyMaintenance
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dailyMaintenance/save", method = RequestMethod.POST)
    @Transactional
    public JSONObject saveDailyMaintenance(@ModelAttribute("dailyMaintenance") DailyMaintenance dailyMaintenance,@RequestParam(value = "deviceId",required = false) Long deviceId) throws Exception {
        logger.debug("saveOrUpdate dailyMaintenance");
        Oplog.Operation operation = dailyMaintenance.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        dailyMaintenance.needCheckRemind();
        dailyMaintenance.needExchangeRemind();
        Device device = deviceService.findOne(deviceId);
        dailyMaintenance.setDevice(device);
        dailyMaintenanceService.save(dailyMaintenance);
        saveLog(i18nComponent.getMessage("dailyMaintenance"), operation, "id: " + dailyMaintenance.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 进入批量导入页面
     *
     * @return
     */
    @RequestMapping(value = "/dailyMaintenance/save/batch/view", method = RequestMethod.GET)
    public ModelAndView dailyMaintenanceDocumentUpload(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("dailyMaintenance batch save view");
        return new ModelAndView("dailyMaintenance/batchUpload").addObject("deviceId",deviceId);
    }

    /***
     * 批量导入
     * @param excel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dailyMaintenance/save/batch", method = RequestMethod.POST)
    @Transactional
    public JSONObject batchUpload(@RequestParam(value = "excel") MultipartFile excel,
                                  @RequestParam(value = "deviceId") Long deviceId ) throws Exception {
//        ImportParams importParams = new ImportParams();
//        importParams.setImportFields(new String[]{"主类别","细分类别","物料编码","代号","生产日期","检测周期-年","检测周期-月","检测周期-日","更换周期-年","更换周期-月","更换周期-日","预警周期-年","预警周期-月","预警周期-日","上次检查维护时间","上次更换维护时间","维护人"});
        dailyMaintenanceService.batchSave(excel.getInputStream(),deviceId);
        return new JSONObject(ImmutableMap.of("result", "success"));
        /*ImportParams importParams = new ImportParams();
        importParams.setImportFields(new String[]{"主类别","细分类别","物料编码","代号","生产日期","检测周期-年","检测周期-月","检测周期-日","更换周期-年","更换周期-月","更换周期-日","预警周期-年","预警周期-月","预警周期-日","上次检查维护时间","上次更换维护时间","维护人"});
        List<DailyMaintenance> dailyMaintenances = ExcelImportUtil.importExcel(
                excel.getInputStream(),
                DailyMaintenance.class, importParams);
        Device device = deviceService.getOne(deviceId);
        for (int i = 0; i < dailyMaintenances.size(); i++) {
            //dailyMaintenances.get(i).needExchangeRemind();
            dailyMaintenances.get(i).setDevice(device);
            dailyMaintenanceService.save(dailyMaintenances);
        }
        return new JSONObject(ImmutableMap.of("result", "success"));
        return new JSONObject(ImmutableMap.of("result", "success"));*/
    }

    /**
    * 根据id删除日常维护表中的数据
    * @since  2018/6/6 13:10
    * @author 倪文骅
    * @param id
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/dailyMaintenance/delete", method = RequestMethod.POST)
    public JSONObject deleteMaintenance(@RequestParam(value = "id") String id){
        logger.debug("delete Maintenance");
        dailyMaintenanceService.deleteDailyMaintenanceByIds(id);
        return  new JSONObject(ImmutableMap.of("result","success"));
    }

    /**
    * 跳转到日常维护的修改页面
    * @since  2018/6/15 16:18
    * @author 倪文骅
    * @param id 是url传来的参数表示修改的那行数据的索引
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping(value = "/dailyMaintenance/gotoEditDailyMaintenancePage", method = RequestMethod.GET)
    public ModelAndView gotoSaveDailyMaintenancePage(@RequestParam(value = "id",required = false) Long id){
        DailyMaintenance dailyMaintenance;
        ModelAndView mv = new ModelAndView("dailyMaintenance/save");
        if(id == null){
            dailyMaintenance = new DailyMaintenance();
        }else {
            dailyMaintenance = dailyMaintenanceService.getOne(id);
            mv.addObject("dailyMaintenance",dailyMaintenance);
        }
        return mv;
    }

    /**
    * 跳转到日常维护的新增页面
    * @since  2018/6/15 16:18
    * @author 倪文骅
    * @param deviceId 是url传来的需要关联的设备id
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping(value = "/dailyMaintenance/gotoAddDailyMaintenancePage", method = RequestMethod.GET)
    public ModelAndView gotoAddDailyMaintenancePage(@RequestParam(value = "deviceId",required = false) Long deviceId){
        ModelAndView mv = new ModelAndView("dailyMaintenance/save");
        mv.addObject("deviceId",deviceId);
        mv.addObject("dailyMaintenance",new DailyMaintenance());
        return mv;
    }

    /**
     * 跳转到更新时间界面
     * @param type
     * @param id
     * @return
     */
    @RequestMapping(value = "/dailyMaintenance/gotoEditTimePage", method = RequestMethod.GET)
    public ModelAndView gotoEditTimePage(@RequestParam(value = "type") Long type,@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        ModelAndView mv = new ModelAndView("dailyMaintenance/editTime");
        // type: 0表示修改上次维护时间,1表示修改上次替换时间
        mv.addObject("type",type);
        mv.addObject("id", id);
        mv.addObject("date", DateFormatUtil.getFormateDate(date));
        return mv;
    }

    /**
     * 更新时间
     * @param type
     * @param id
     * @param date
     * @return
     */
    @RequestMapping(value = "/dailyMaintenance/editTime", method = RequestMethod.POST)
    @Transactional
    public JSONObject editTime(@RequestParam(value = "type") Long type,@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        logger.debug("edit dailyMaintenance date");
        DailyMaintenance dailyMaintenance = dailyMaintenanceService.findOne(id);
        if (type == 0) {
            dailyMaintenance.setLastCheckMaintainDate(date);
        } else if (type == 1) {
            dailyMaintenance.setLastExchangeMaintainDate(date);
        }
        dailyMaintenance.needCheckRemind();
        dailyMaintenance.needExchangeRemind();
        dailyMaintenanceService.save(dailyMaintenance);
        saveLog(i18nComponent.getMessage("dailyMaintenance"), Oplog.Operation.EDIT, "id: " + dailyMaintenance.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }
}
