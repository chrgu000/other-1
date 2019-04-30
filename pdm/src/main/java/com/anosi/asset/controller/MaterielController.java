package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Materiel;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.MaterielService;
import com.anosi.asset.util.DateFormatUtil;
import com.aspose.cad.internal.Exceptions.Exception;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

@RestController
public class MaterielController extends BaseController<Materiel> {

    private static final Logger logger = LoggerFactory.getLogger(MaterielController.class);

    @Autowired
    private MaterielService materielService;
    @Autowired
    private DeviceService deviceService;

    /***
     * 进入查看物料表的页面
     *
     * @return
     */
    @RequestMapping(value = "/materiel/management/view", method = RequestMethod.GET)
    public ModelAndView toViewMaterielManage(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("view materiel manage");
        Device device = deviceService.findOne(deviceId);
        return new ModelAndView("materiel/materielManage") .addObject("deviceId", deviceId).addObject("deviceSN", device.getSerialNo());
    }

    /***
     * 获取物料数据
     *
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findMaterielManageData(@PathVariable ShowType showType,
                                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                             @QuerydslPredicate(root = Materiel.class) Predicate predicate,
                                             @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                             @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
                                             @RequestParam(value = "searchContent", required = false) String searchContent,
                                             @RequestParam(value = "deviceSN", required = false) String deviceSN,
                                             @RequestParam(value = "beginTime", required = false) Date beginTime,
                                             @RequestParam(value = "endTime", required = false) Date endTime) throws java.lang.Exception {
        logger.info("find materiel");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        Page<Materiel> materiels;
        if (StringUtils.isNoneBlank(searchContent) && StringUtils.isNoneBlank(deviceSN)) {
            materiels = materielService.findBySearchContent(searchContent, deviceSN, pageable);
        } else {
            materiels = materielService.findAll(predicate, pageable);
        }
        /*if(StringUtils.isNoneBlank(searchContent)){
            materiels = materielService.findBySearchContent(searchContent, deviceSN, pageable);
        }else {
            materiels = materielService.findMaterielByDeviceId(deviceId,pageable);
        }*/

        return parseToJson(materiels, rowId, showAttributes, showType);
    }

    /***
     * 进入save/update device的页面
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/save", method = RequestMethod.GET)
    public ModelAndView toSaveMaterielPage(@RequestParam(value = "id", required = false) Long id,
                                           @RequestParam(value = "deviceId") Long deviceId) throws Exception {
        Materiel materiel;
        if (id == null) {
            materiel = new Materiel();
        } else {
            materiel = materielService.getOne(id);
        }
        return new ModelAndView("materiel/save").addObject("device", deviceService.getOne(deviceId))
                .addObject("materiel", materiel).addObject("deviceId",deviceId);
    }

    /****
     * 在执行update前，先获取持久化的materiel对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getMateriel(@RequestParam(value = "materielId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("materiel", materielService.getOne(id));
        }
    }

    /***
     * save/update materiel
     *
     * @param materiel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/save", method = RequestMethod.POST)
    @Transactional
    public JSONObject saveMateriel(@ModelAttribute("materiel") Materiel materiel,@RequestParam(value = "deviceId",required = false) Long deviceId) throws Exception {
        logger.debug("saveOrUpdate materiel");
        Oplog.Operation operation = materiel.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        materiel.needRemind();
        Device device = deviceService.findOne(deviceId);
        materiel.setDevice(device);
        materielService.save(materiel);
        saveLog(i18nComponent.getMessage("materiel.predictive.maintenance"), operation, "id: " + materiel.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 删除物料
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/delete", method = RequestMethod.POST)
    public JSONObject deleteMateriel(@RequestParam(value = "id") String id) throws Exception {
        logger.debug("delete materiel");
        materielService.deleteMaterielById(id);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 进入批量导入页面
     *
     * @return
     */
    @RequestMapping(value = "/materiel/save/batch/view", method = RequestMethod.GET)
    public ModelAndView deviceDocumentUpload(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("materiel batch save view");
        return new ModelAndView("materiel/upload").addObject("deviceId",deviceId);
    }

    /***
     * bat save materiel
     *
     * @param excel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/save/batch", method = RequestMethod.POST)
    @Transactional
    public JSONObject batSaveMateriel(@RequestParam(value = "excel") MultipartFile excel,@RequestParam(value = "deviceId") Long deviceId ) throws java.lang.Exception {
        logger.debug("bat save materiel");
        materielService.batchSave(excel.getInputStream(),deviceId);
        return new JSONObject(ImmutableMap.of("result", "success"));
        /*ImportParams importParams = new ImportParams();
        importParams.setImportFields(new String[]{"物料编码","物料名称","所属设备(序列号)","投运时间","检测周期-年","检测周期-月","检测周期-日","预警周期-年","预警周期-月","预警周期-日"});
        List<Materiel> materielList = ExcelImportUtil.importExcel(
                excel.getInputStream(),
                Materiel.class, importParams);
        Device device = deviceService.getOne(deviceId);
        for (int i = 0; i < materielList.size(); i++) {
            //materielList.get(i).needRemind();
            materielList.get(i).setDevice(device);
            materielService.save(materielList);
        }
        return new JSONObject(ImmutableMap.of("result", "success"));*/
    }

    /**
     * 按照materiel某些属性判断是否存在
     *
     * @param predicate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/materiel/checkExist", method = RequestMethod.GET)
    public JSONObject checkExist(@QuerydslPredicate(root = Materiel.class) Predicate predicate) throws Exception {
        return new JSONObject(ImmutableMap.of("result", materielService.exists(predicate)));
    }

    /**
     * 跳转到更新时间界面
     * @param id
     * @return
     */
    @RequestMapping(value = "/materiel/gotoEditTimePage", method = RequestMethod.GET)
    public ModelAndView gotoEditTimePage(@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        ModelAndView mv = new ModelAndView("materiel/editTime");
        mv.addObject("id", id);
        mv.addObject("date", DateFormatUtil.getFormateDate(date));
        return mv;
    }

    /**
     * 更新时间
     * @param id
     * @param date
     * @return
     */
    @RequestMapping(value = "/materiel/editTime", method = RequestMethod.POST)
    @Transactional
    public JSONObject editTime(@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        logger.debug("edit materiel date");
        Materiel materiel = materielService.findOne(id);
        materiel.setLastCheckTime(date);
        materiel.needRemind();
        materielService.save(materiel);
        saveLog(i18nComponent.getMessage("materiel.predictive.maintenance"), Oplog.Operation.EDIT, "id: " + materiel.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

}
