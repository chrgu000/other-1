package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Instrument;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.InstrumentService;
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
 * 仪器controller
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.controller
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 14:23
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 14:23
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@RestController
public class InstrumentController extends BaseController<Instrument> {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private DeviceService deviceService;

    /***
     * 进入查看仪表校验表的页面
     *
     * @return
     */
    @RequestMapping(value = "/instrument/management/view", method = RequestMethod.GET)
    public ModelAndView toViewInstrumentManage(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("view instrument manage");
        return new ModelAndView("Instrument/instrumentManage").addObject("deviceId",deviceId);
    }

    /***
     * 获取仪器校验表
     *
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/instrument/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findInstrumentManageData(@PathVariable ShowType showType,
                                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                             @QuerydslPredicate(root = Instrument.class) Predicate predicate,
                                             @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                             @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
        logger.info("find instrument");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        return parseToJson(instrumentService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }

    /****
     * 在执行update前，先获取持久化的materiel对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getInstrument(@RequestParam(value = "instrumentId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("instrument", instrumentService.getOne(id));
        }
    }

    /***
     * save/update instrument
     *
     * @param instrument
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/instrument/save", method = RequestMethod.POST)
    @Transactional
    public JSONObject saveInstrument(@ModelAttribute("instrument") Instrument instrument,@RequestParam(value = "deviceId",required = false) Long deviceId) throws Exception {
        logger.debug("saveOrUpdate instrument");
        Oplog.Operation operation = instrument.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        instrument.needCheckRemind();
        Device device = deviceService.getOne(deviceId);
        instrument.setDevice(device);
        instrumentService.save(instrument);
        saveLog(i18nComponent.getMessage("inspectionRecord"), operation, "id: " + instrument.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 进入批量导入页面
     *
     * @return
     */
    @RequestMapping(value = "/instrument/save/batch/view", method = RequestMethod.GET)
    public ModelAndView instrumentDocumentUpload(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("instrument batch save view");
        return new ModelAndView("Instrument/batchUpload").addObject("deviceId",deviceId);
    }

    /***
     * 批量导入
     * @param excel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/instrument/save/batch", method = RequestMethod.POST)
    @Transactional
    public JSONObject batchUpload(@RequestParam(value = "excel") MultipartFile excel,
                                  @RequestParam(value = "deviceId") Long deviceId) throws Exception {
        instrumentService.batchSave(excel.getInputStream(),deviceId);
        return new JSONObject(ImmutableMap.of("result", "success"));
        /*ImportParams importParams = new ImportParams();
        importParams.setImportFields(new String[]{"仪表","物料编码","代号","生产日期","上次校验时间","校验周期-年","校验周期-月","校验周期-日","预警周期-年","预警周期-月","预警周期-日","备注"});
        List<Instrument> instruments = ExcelImportUtil.importExcel(
                excel.getInputStream(),
                Instrument.class, importParams);
        Device device = deviceService.getOne(deviceId);
        for (int i = 0; i < instruments.size(); i++) {
            instruments.get(i).needCheckRemind();
            instruments.get(i).setDevice(device);
        }
        instrumentService.save(instruments);
        return new JSONObject(ImmutableMap.of("result", "success"));*/
    }

    /**
    * 根据行号id删除仪表检验数据
    * @since  2018/6/8 13:10
    * @author 倪文骅
    * @param id  行号
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/instrument/delete")
    public JSONObject deleteInstrument(@RequestParam(value = "id") String id ){
        logger.debug("delete Instrument");
        instrumentService.deleteInstrumentById(id);
        return new JSONObject(ImmutableMap.of("result","success"));
    }

    /**
    * 跳转到仪表检验修改页面
    * @since  2018/6/19 11:53
    * @author 倪文骅
    * @param id
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping(value = "/instrument/gotoEditInstrumentPage",method = RequestMethod.GET)
    public ModelAndView gotoEditInstrumentPage(@RequestParam(value = "id",required = false) Long id){
        Instrument instrument;
        ModelAndView mv = new ModelAndView("Instrument/save");
        if (id == null){
            instrument = new Instrument();
        }else {
            instrument = instrumentService.getOne(id);
            mv.addObject("instrument",instrument);
        }
        return mv;
    }

    /**
    * 跳转到仪表检验新增页面
    * @since  2018/6/19 11:57
    * @author 倪文骅
    * @param
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping("/instrument/gotoAddInstrumentPage")
    public ModelAndView gotoAddInstrumentPage(@RequestParam(value = "deviceId",required = false) Long deviceId){
        ModelAndView mv = new ModelAndView("Instrument/save");
        mv.addObject("deviceId",deviceId);
        mv.addObject("instrument",new Instrument());
        return  mv;
    }

    /**
     * 跳转到更新时间界面
     * @param id
     * @return
     */
    @RequestMapping(value = "/instrument/gotoEditTimePage", method = RequestMethod.GET)
    public ModelAndView gotoEditTimePage(@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        ModelAndView mv = new ModelAndView("instrument/editTime");
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
    @RequestMapping(value = "/instrument/editTime", method = RequestMethod.POST)
    @Transactional
    public JSONObject editTime(@RequestParam(value = "id") Long id,@RequestParam(value = "date") Date date){
        logger.debug("edit instrument date");
        Instrument instrument = instrumentService.findOne(id);
        instrument.setLastCheckOutDate(date);
        instrument.needCheckRemind();
        instrumentService.save(instrument);
        saveLog(i18nComponent.getMessage("inspectionRecord"), Oplog.Operation.EDIT, "id: " + instrument.getId());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }
}
