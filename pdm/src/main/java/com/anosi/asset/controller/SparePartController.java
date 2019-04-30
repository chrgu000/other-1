package com.anosi.asset.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.SparePart;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.SparePartService;
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

import java.util.List;

/**
 * java类简单作用描述
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.controller
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 11:16
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 11:16
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@RestController
public class SparePartController extends BaseController<SparePart> {

    private static final Logger logger = LoggerFactory.getLogger(SparePartController.class);

    @Autowired
    private SparePartService sparePartService;
    @Autowired
    private DeviceService deviceService;

    /***
     * 进入查看备品备件的页面
     *
     * @return
     */
    @RequestMapping(value = "/sparePart/management/view", method = RequestMethod.GET)
    public ModelAndView toViewSparePartManage(@RequestParam(value = "deviceId", required = false) Long deviceId ) {
        logger.debug("view sparePart manage");
        return new ModelAndView("sparePart/sparePartManage").addObject("deviceId",deviceId);
    }

    /***
     * 获取备品备件
     *
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sparePart/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findSparePartManageData(@PathVariable ShowType showType,
                                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                             @QuerydslPredicate(root = SparePart.class) Predicate predicate,
                                             @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                             @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
        logger.info("find sparePart");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        return parseToJson(sparePartService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }

    /****
     * 在执行update前，先获取持久化的materiel对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getSparePart(@RequestParam(value = "sparePartId", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("sparePart", sparePartService.getOne(id));
        }
    }

    /***
     * save/update sparePart
     *
     * @param sparePart
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sparePart/save", method = RequestMethod.POST)
    @Transactional
    public JSONObject saveSparePart(@ModelAttribute("sparePart") SparePart sparePart,
                                    @RequestParam(value = "deviceId",required = false) Long deviceId) {
                                    //@RequestParam(value = "id",required = false)int id) throws Exception {
        logger.debug("saveOrUpdate sparePart");
//        if(StringUtils.isNotBlank(String.valueOf(id))){
////            SparePart part = sparePartService.getOne(new Long((long) id));
//            sparePart.setId(new Long((long) id));
//        }
        if (deviceId != null) {
            Device device = deviceService.getOne(deviceId);
            sparePart.setDevice(device);
        }
        Oplog.Operation operation = sparePart.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        sparePartService.save(sparePart);
        saveLog(i18nComponent.getMessage("sparePart"), operation, sparePart.getCode());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 进入批量导入页面
     *
     * @return
     */
    @RequestMapping(value = "/sparePart/save/batch/view", method = RequestMethod.GET)
    public ModelAndView sparePartDocumentUpload(@RequestParam(value = "deviceId", required = false) Long deviceId ) {
        logger.debug("sparePart batch save view");
        return new ModelAndView("sparePart/batchUpload").addObject("deviceId",deviceId);
    }

    /***
     * 批量导入
     * @param excel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sparePart/save/batch", method = RequestMethod.POST)
    @Transactional
    public JSONObject batchUpload(@RequestParam(value = "excel") MultipartFile excel, @RequestParam(value = "deviceId", required = false) Long deviceId) throws Exception {
        ImportParams importParams = new ImportParams();
//        importParams.setImportFields(new String[]{"存货编码","规格","存货名称","存货数量","去年消耗量","建议库存","需补充量","提醒状态"});
        importParams.setImportFields(new String[]{"存货编码","规格","存货名称","存货数量","去年消耗量","建议库存","需补充量"});
        List<SparePart> spareParts = ExcelImportUtil.importExcel(
                excel.getInputStream(),
                SparePart.class, importParams);
        Device device = deviceId == null ? null : deviceService.getOne(deviceId);
        for (int i = 0; i < spareParts.size(); i++) {
            if (device != null) {
                spareParts.get(i).setDevice(device);
            }
            sparePartService.save(spareParts);
        }
        saveLog(i18nComponent.getMessage("sparePart"), Oplog.Operation.BATCH_ADD, spareParts.size()+"个");
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /**
    * 根据id删除备品备件
    * @since  2018/6/8 15:17
    * @author 倪文骅
    * @param id
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/sparePart/delete", method = RequestMethod.POST)
    public JSONObject deleteMaintenance(@RequestParam(value = "id") String id){
        logger.debug("delete Maintenance");
        sparePartService.deleteSparePartByIds(id);
        return  new JSONObject(ImmutableMap.of("result","success"));
    }


    /**
    * 跳转到备品备件修改页面
    * @since  2018/6/19 13:36
    * @author 倪文骅
    * @param id
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping(value = "/sparePart/gotoEidtSparePartPage")
    public ModelAndView gotoEidtSparePartPage(@RequestParam(value = "id",required = false) Long id){
        ModelAndView mv = new ModelAndView("sparePart/save");
        SparePart sparePart;
        if (id == null){
            sparePart = new SparePart();
        }else {
            sparePart = sparePartService.getOne(id);
            mv.addObject("sparePart",sparePart);
        }
        return  mv;
    }

    /**
    * 跳转到备品备件新增页面
    * @since  2018/6/19 13:55
    * @author 倪文骅
    * @param deviceId
    * @return org.springframework.web.servlet.ModelAndView
    */
    @RequestMapping(value = "/sparePart/gotoAddSparePartPage",method = RequestMethod.GET)
    public ModelAndView gotoAddSparePartPage(@RequestParam(value = "deviceId",required = false) Long deviceId){
        ModelAndView mv = new ModelAndView("sparePart/save");
        mv.addObject("deviceId",deviceId);
        mv.addObject("sparePart",new SparePart());
        return mv;
    }

}
