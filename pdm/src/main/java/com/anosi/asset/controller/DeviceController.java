package com.anosi.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.component.TokenComponent;
import com.anosi.asset.model.jpa.*;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.pojo.GroupOfA5;
import com.anosi.asset.service.*;
import com.anosi.asset.util.StringUtil;
import com.aspose.cad.internal.Exceptions.Exception;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ListUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class DeviceController extends BaseController<Device> {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DevCategoryService devCategorySerivce;
    @Autowired
    private DocumentTypeService documentTypeService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileMetaDataService fileMetaDataService;
    @Autowired
    private TokenComponent tokenComponent;
    @Autowired
    private ServerCallComponent serverCallComponent;
    @Autowired
    private IotxSaveService iotxSaveService;
    @Value("${data-center.url}")
    private String url;
    @Value("${data-center.username}")
    private String username;

    /***
     * 进入查看<b>所有设备信息</b>的页面
     *
     * @return
     */
    @RequestMapping(value = "/device/management/view", method = RequestMethod.GET)
    public ModelAndView toViewDeviceManage(@RequestParam(value = "categoryType", required = false) String categoryType, @RequestParam(value = "searchContent", required = false) String searchContent) {
        logger.debug("view device manage");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("device/deviceManage");
        modelAndView.addObject("companyName", searchContent);
        modelAndView.addObject("categoryType", categoryType);
        return modelAndView;
    }

    /***
     * 根据条件查询某个device
     *
     * @param predicate
     * @param showAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/management/data/one", method = RequestMethod.GET)
    public JSONObject findDeviceManageDataOne(@QuerydslPredicate(root = Device.class) Predicate predicate,
                                              @RequestParam(value = "showAttributes", required = false) String showAttributes) throws java.lang.Exception {
        logger.info("find device one");
        return jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
                deviceService.findOne(predicate));
    }

    /***
     * 获取设备数据
     *
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findDeviceManageData(@PathVariable ShowType showType,
                                           @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable,
                                           @QuerydslPredicate(root = Device.class) Predicate predicate,
                                           @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                           @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
                                           @RequestParam(value = "searchContent", required = false) String searchContent,
                                           @RequestParam(value = "beginTime", required = false) Date beginTime,
                                           @RequestParam(value = "endTime", required = false) Date endTime) throws java.lang.Exception {
        logger.info("find device");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        Page<Device> devices;
        if (beginTime != null) {
            predicate = QDevice.device.commissioningTime.after(beginTime).and(predicate);
        }
        if (endTime != null) {
            predicate = QDevice.device.commissioningTime.before(endTime).and(predicate);
        }
        if (StringUtils.isNoneBlank(searchContent)) {
            devices = deviceService.findByContentSearch(searchContent, pageable);
        } else {
            if (SessionComponent.isClient()) {
                predicate = QDevice.device.ownerList.contains(sessionComponent.getCurrentUser()).and(predicate);
            }
            devices = deviceService.findAll(predicate, pageable);
        }

        return parseToJson(devices, rowId, showAttributes, showType);
    }

    /**
     * 获取公司的设备
     * **/
    @RequestMapping(value = "/device/company/data/{showType}", method = RequestMethod.GET)
    public JSONObject getCompanyDeviceManageData(@PathVariable ShowType showType,
                                           @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable,
                                                 @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                                 @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
                                                 @QuerydslPredicate(root = Device.class) Predicate predicate) throws java.lang.Exception {

        return  parseToJson(deviceService.findAll(predicate, pageable), rowId, showAttributes, showType);
    }
    /**
     * 进入新增/修改设备的页面
     *
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @author 倪文骅
     * @since 2018/7/16 9:59
     */
    @RequestMapping(value = "/device/save", method = RequestMethod.GET)
    public ModelAndView toSaveDevicePage(@RequestParam(value = "id", required = false) Long id) throws Exception, IOException {
        Device device;
        ModelAndView mv = new ModelAndView("device/save");
        //数据中心登录验证 测试服使用时先注释掉
        tokenComponent.login();
        //A5 接口
        String findGroupUrl = url + "/iotx/netElement/combination/findAll";
        //获取所有的分组数据
        String groupAll = serverCallComponent.executeGet(findGroupUrl);
        JSONObject jsonGroup = JSON.parseObject(groupAll);
        String groupOfCompany = "";
        String groupByUsername = "";
        if (username.equals("admin")) {
            //辉浩
            groupByUsername = jsonGroup.getString("INDIVIDUAL");
            groupOfCompany = JSON.parseObject(groupByUsername).getString("and003");
        } else {
            //根据不同账号获取组信息，admin用于测试服
            groupByUsername = jsonGroup.getString("ENTERPRISE");
            //获取账号的网元名称组信息
            groupOfCompany = JSON.parseObject(groupByUsername).getString(username);
        }
        // groupByUsername =jsonGroup.getString("ENTERPRISE");
//        String groupOfCompany = JSON.parseObject(groupByUsername).getString("wanginin");
        List<GroupOfA5> groupOfA5List = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(groupOfCompany);
        groupOfA5List = JSON.parseArray(groupOfCompany, GroupOfA5.class);
        mv.addObject("jsonArray", jsonArray);
        mv.addObject("groupOfA5List", groupOfA5List);
        if (id == null) {
            device = new Device();
        } else {
            //根据id获取设备信息
            device = deviceService.getOne(id);
            //获取所有的网元id
//            List<String> iotxIdList = device.getIotxList().stream().map(Iotx::getSerial_no).collect(Collectors.toList());
            List<Iotx> iotxIdList = device.getIotxList();
            String iotxIds = JSON.toJSONString(iotxIdList);
            //获取所有预警维护收件人
            List<Long> receiverIds = device.getRemindReceiverList().stream().map(Account::getId).collect(Collectors.toList());
            mv.addObject("receiverIds", receiverIds);
            //if(!ListUtils.isEmpty(iotxIdList)){

            mv.addObject("iotxIds", iotxIds);
            //}
            mv.addObject("ownerIds", device.getOwnerList().stream().map(Account::getId).collect(Collectors.toList()));
        }
        return mv.addObject("device", device)
                .addObject("devCategorys", devCategorySerivce.findAll())
                .addObject("receivers", accountService.findAll())
                .addObject("owners", accountService.findAll(QAccount.account.roleList.contains(roleService.findByCode("client"))));
    }

    /****
     * 在执行update前，先获取持久化的device对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getDevice(HttpServletRequest request,
                          @RequestParam(value = "deviceId", required = false) Long id, Model model) {
        if (id != null) {
            Device device = deviceService.getOne(id);
            if ("/device/save".equalsIgnoreCase(request.getServletPath())) {
                device.setDevCategory(null);
                device.setProject(null);
            }
            model.addAttribute("device", device);
        }
    }

    /***
     * 保存/修改设备信息
     *
     * @param device
     * @param receivers
     *            预测性维护提醒人
     * @param owners
     *            所属用户
     * @return
     * @throws Exception
     */
    @RequiresPermissions({"deviceInfo:add", "deviceInfo:edit"})
    @RequestMapping(value = "/device/save", method = RequestMethod.POST)
    @Transactional
    public JSONObject saveDevice(@ModelAttribute("device") Device device,
                                 @RequestParam(name = "serial_no",required=false) String serial_no,
                                 @RequestParam(name = "deviceId",required=false) Long deviceId,
                                 @RequestParam(name = "groupId",required=false) String groupId,
                                 @RequestParam(name = "remindReceivers") Long[] receivers,
                                 @RequestParam(name = "owners", required = false) Long[] owners,
                                 @RequestParam(value = "fileUpLoad", required = false) MultipartFile[] pictures,
                                 @RequestParam(value = "topology", required = false) MultipartFile topology) throws java.lang.Exception {
        logger.debug("saveOrUpdate device");
        //设备id不为空--编辑修改功能
//        if(deviceId!=null){
//            //根据设备id获取设备信息
//            Device deviceInfo = deviceService.getOne(deviceId);

//        }else{//设备id为空--新增设备功能
//            Iotx iotx = new Iotx();
//            iotx.setSerial_no(serial_no);
//            iotx.setGroup_id(groupId);
//            iotx.setDevice(device);
//            iotxSaveService.save(iotx);
//            device.getIotxList().add(iotx);
//        }
        Oplog.Operation operation = device.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        device.getRemindReceiverList().clear();
        for (Long receiver : receivers) {
            device.getRemindReceiverList().add(accountService.getOne(receiver));
        }
        device.getOwnerList().clear();
        if (owners != null && owners.length != 0) {
            for (Long owner : owners) {
                device.getOwnerList().add(accountService.getOne(owner));
            }
        }
        if (!ArrayUtils.isEmpty(pictures)) {
            for (MultipartFile picture : pictures) {
                fileMetaDataService.saveFile("device_img_" + device.getSerialNo(), picture.getName(), picture.getInputStream(), picture.getSize());
            }
        }
        if (topology != null) {
            // 拓扑图只保留最新的一张
            // 先查询再存储
            List<FileMetaData> topologys = fileMetaDataService.findByIdentification("device_topology_" + device.getSerialNo());
            fileMetaDataService.saveFile("device_topology_" + device.getSerialNo(), topology.getName(), topology.getInputStream(), topology.getSize());
            for (FileMetaData fileMetaData : topologys) {
                // 删除多余的拓扑图
                fileMetaDataService.deleteFile(fileMetaData);
            }
        }

       /* //根据网元号获取网元信息
        Iotx iotx = iotxSaveService.findBySerialNo(serial_no);
        if(iotx !=null){//修改更新
            iotx.setSerial_no(serial_no);
            iotx.setGroup_id(groupId);
            iotx.setDevice(device);
            device.getIotxList().add(iotx);
            //iotxSaveService.save(iotx);
        }else{//新增
            Iotx iotxInfo = new Iotx();
            iotxInfo.setSerial_no("");
            iotxInfo.setGroup_id(groupId);
            iotxInfo.setDevice(device);
            iotxSaveService.save(iotxInfo);
            device.getIotxList().add(iotxInfo);
        }*/
       List<Iotx> iotxList = iotxSaveService.findByDeviceId(deviceId);
       if(ListUtils.isEmpty(iotxList)){
           Iotx iotxInfo = new Iotx();
           iotxInfo.setSerial_no(serial_no);
           iotxInfo.setGroup_id(groupId);
           iotxInfo.setDevice(device);
           iotxSaveService.save(iotxInfo);
           device.getIotxList().add(iotxInfo);
       }else { //不为空 修改更新
           for(Iotx iotx : iotxList){
               iotx.setSerial_no(serial_no);
               iotx.setGroup_id(groupId);
               iotx.setDevice(device);
               device.getIotxList().add(iotx);
           }
       }

        if (device.getBaiduLongitude() != null && device.getBaiduLatitude() != null) {
            deviceService.setDeviceDistrict(device);
        }
        deviceService.save(device);
        saveLog("设备", operation, device.getSerialNo());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 删除设备
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequiresPermissions({"deviceInfo:delete"})
    @RequestMapping(value = "/device/delete", method = RequestMethod.POST)
    public JSONObject deleteDevice(@RequestParam(value = "id") String id) throws Exception {
        logger.debug("delete device");
        if(!deviceService.deleteDevice(id)){
            return new JSONObject(ImmutableMap.of("result", "error", "message",
                    MessageFormat.format(i18nComponent.getMessage("device.using"), "设备在使用中")));
        }
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /**
     * 为设备添加坐标
     *
     * @param deviceSN
     * @param longitude
     * @param latitude
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/setDistrict", method = RequestMethod.POST)
    public JSONObject setDeviceDistrict(@RequestParam(name = "deviceSN") String deviceSN,
                                        @RequestParam(name = "longitude") Double longitude, @RequestParam(name = "latitude") Double latitude)
            throws Exception {
        Device device = deviceService.findBySerialNo(deviceSN);
        if (device == null) {
            return new JSONObject(ImmutableMap.of("result", "error", "message",
                    MessageFormat.format(i18nComponent.getMessage("device.notExist.withSN"), deviceSN)));
        } else {
            device.setLongitude(longitude);
            device.setLatitude(latitude);
            deviceService.setDeviceDistrict(device);
        }
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 获取设备的分布
     *
     * @param predicate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/distribute/data", method = RequestMethod.GET)
    public JSONArray deviceDistribute(@QuerydslPredicate(root = Device.class) Predicate predicate) throws Exception {
        return deviceService.ascertainArea(predicate);
    }

    /***
     * 获取autocomplete的source
     *
     * @param predicate
     * @param label
     * @param value
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/autocomplete", method = RequestMethod.GET)
    public JSONArray autocomplete(@QuerydslPredicate(root = Device.class) Predicate predicate,
                                  @RequestParam(value = "label") String label, String value) throws java.lang.Exception {
        return jsonUtil.parseAttributesToAutocomplete(label, value, deviceService.findAll(predicate));
    }

    /**
     * 按照device某些属性判断是否存在
     *
     * @param predicate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/checkExist", method = RequestMethod.GET)
    public JSONObject checkExist(@QuerydslPredicate(root = Device.class) Predicate predicate) throws Exception {
        return new JSONObject(ImmutableMap.of("result", deviceService.exists(predicate)));
    }

    /***
     * 为设备绑定rifd
     *
     * @param serialNo
     * @param rfid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/rfid/binding", method = RequestMethod.POST)
    @Transactional
    public JSONObject bindingRfid(@RequestParam(value = "serialNo") String serialNo,
                                  @RequestParam(value = "rfid") String rfid) throws Exception {
        Device device = deviceService.findBySerialNo(serialNo);
        if (device == null) {
            return new JSONObject(ImmutableMap.of("result", "error", "message",
                    MessageFormat.format(i18nComponent.getMessage("device.rfid.exist"), serialNo)));
        } else if (StringUtils.isNoneBlank(device.getRfid())) {
            return new JSONObject(
                    ImmutableMap.of("result", "error", "message", i18nComponent.getMessage("device.rfid.exist")));
        } else if (StringUtils.isBlank(rfid)) {
            return new JSONObject(
                    ImmutableMap.of("result", "error", "message", i18nComponent.getMessage("rfid.cannot.null")));
        } else if (deviceService.findByRfid(rfid) != null) {
            return new JSONObject(
                    ImmutableMap.of("result", "error", "message", i18nComponent.getMessage("rfid.repeat")));
        } else {
            device.setRfid(rfid);
        }
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 为设备解绑rifd
     *
     * @param serialNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/rfid/unBinding", method = RequestMethod.POST)
    @Transactional
    public JSONObject unBindingRfid(@RequestParam(value = "serialNo") String serialNo) throws Exception {
        deviceService.findBySerialNo(serialNo).setRfid(null);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 点击device详情进入的页面
     *
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/device/management/detail/{deviceId}/view", method = RequestMethod.GET)
    public ModelAndView toViewDeviceManageTable(@PathVariable Long deviceId) throws Exception {
        logger.info("view device management detail");
        Device device = deviceService.getOne(deviceId);
        List<Iotx> iotxList = device.getIotxList();
        String iotxListJsonStr = JSON.toJSONString(iotxList);
        List<FileMetaData> fileMetaDatas = fileMetaDataService.findByIdentification("device_img_" + device.getSerialNo());
        List<FileMetaData> topologys = fileMetaDataService.findByIdentification("device_topology_" + device.getSerialNo());
        ModelAndView modelAndView = new ModelAndView("device/detail");
        if (!CollectionUtils.isEmpty(topologys)) {
            modelAndView.addObject("topology", topologys.get(0));
        }
        return modelAndView.addObject("deviceId", deviceId)
                .addObject("serialNo",device.getSerialNo())
                .addObject("fileMetaDatas", fileMetaDatas)
                .addObject("device", device)
                .addObject("iotxListJsonStr",iotxListJsonStr);
    }

    /***
     * 进入文档上传页面
     *
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/device/document/upload/{deviceId}/view", method = RequestMethod.GET)
    public ModelAndView deviceDocumentUpload(@PathVariable Long deviceId) {
        logger.debug("device document upload");
        return new ModelAndView("device/upload").addObject("device", deviceService.getOne(deviceId));
    }

    /***
     * 进入设备技术文档页面
     *
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/device/technologyDocument/manage/{deviceId}/view", method = RequestMethod.GET)
    public ModelAndView toTechnologyDocumentManage(@PathVariable Long deviceId) {
        logger.debug("technologyDocument manage");
        return new ModelAndView("document/documentManage").addObject("types", documentTypeService.findAll())
                .addObject("uploaders", accountService.findByIsUploadDocument(true))
                .addObject("device", deviceService.getOne(deviceId)).addObject("isDevice", true);
    }

    /***
     * 进入批量导入页面
     *
     * @return
     */
    @RequestMapping(value = "/device/save/batch/view", method = RequestMethod.GET)
    public ModelAndView deviceDocumentUpload() {
        logger.debug("device batch save view");
        return new ModelAndView("device/batchUpload");
    }

    /***
     * bat save device
     *
     * @param excel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/device/save/batch", method = RequestMethod.POST)
    @Transactional
    public JSONObject batSaveMateriel(@RequestParam(value = "excel") MultipartFile excel) throws java.lang.Exception {
        logger.debug("bat save device");
        deviceService.batchSave(excel.getInputStream());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 进入查看设备所有数据的页面
     *
     * @param deviceSN
     * @return
     */
    @RequestMapping(value = "/device/allData/{deviceSN}/view", method = RequestMethod.GET)
    public ModelAndView toViewAllData(@PathVariable String deviceSN) {
        logger.debug("device all datas");
        return new ModelAndView("device/allData").addObject("deviceSN", deviceSN);
    }

    /***
     * 进入查看设备所有技术参数的页面
     *
     * @param deviceSN 设备序列号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/device/parameters/{deviceSN}/view", method = RequestMethod.GET)
    public ModelAndView toViewAllParameters(@PathVariable String deviceSN, @RequestParam(value = "deviceId") String deviceId) {
        logger.debug("device all parameters");
        Device device = deviceService.findBySerialNo(deviceSN);
        String iotxs = JSON.toJSONString(device.getIotxList());
        return new ModelAndView("device/technologyPameters")
                .addObject("deviceSN", deviceSN)
                .addObject("iotxs", iotxs)
                .addObject("deviceId", deviceId)
                .addObject("devCategorys", devCategorySerivce.findAll());
    }

    /***
     * 进入选择技术参数的页面
     *
     * @param deviceSN
     * @return
     */
    @RequestMapping(value = "/device/parameters/select/view", method = RequestMethod.GET)
    public ModelAndView selectShowParametersView(
            @RequestParam(value = "deviceSN") String deviceSN,
            @RequestParam(value = "deviceId") String deviceId) {
        logger.debug("select parameters");
        Device device = deviceService.findBySerialNo(deviceSN);
        String iotxListJsonStr = JSON.toJSONString(device.getIotxList());
        return new ModelAndView("device/selectParameters")
                .addObject("deviceSN", deviceSN)
                .addObject("iotxListJsonStr",iotxListJsonStr);
    }

    /***
     * 选择需要展示的技术参数
     * @param deviceSN
     * @param showParameter
     * @return
     */
    @RequestMapping(value = "/device/parameters/select/add", method = RequestMethod.POST)
    @Transactional
    public JSONObject addShowParameters(@RequestParam(value = "deviceSN") String deviceSN, @RequestParam(value = "showParameter") String showParameter) {
        Device device = deviceService.findBySerialNo(deviceSN);
        String showTechnologyParameters = device.getShowTechnologyParameters();
        if(StringUtils.isNotBlank(showTechnologyParameters)){
            String[] technologyParametersArr = showTechnologyParameters.split("\t");
            if(technologyParametersArr.length>=5){
                return new JSONObject(ImmutableMap.of("result", "over"));
            }
            if (showTechnologyParameters.contains(showParameter)){
                return new JSONObject(ImmutableMap.of("result", "exist"));
                //showTechnologyParameters.replace(showParameter+"\t","");
            }
        }

        device.setShowTechnologyParameters((showTechnologyParameters == null ? "" : showTechnologyParameters) + showParameter + "\t");
        device.setShowParameter(showParameter);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 移除要展示的技术参数
     * @param deviceSN
     * @param showParameter
     * @return
     */
    @RequestMapping(value = "/device/parameters/select/remove", method = RequestMethod.POST)
    @Transactional
    public JSONObject removeShowParameters(@RequestParam(value = "deviceSN") String deviceSN, @RequestParam(value = "showParameter") String showParameter) {
        Device device = deviceService.findBySerialNo(deviceSN);
        String[] showParameters = device.getShowParameters();
        if (showParameters != null && showParameters.length != 0) {
            ArrayList<String> showParameterList = Lists.newArrayList(showParameters);
            // 移除showParameter
            showParameterList.remove(showParameter);

            // 重新拼接
            device.setShowTechnologyParameters(null);
            StringBuilder showParameterBuilder = new StringBuilder();
            for (String sp : showParameterList) {
                showParameterBuilder.append(sp + "\t");
            }
            device.setShowTechnologyParameters(showParameterBuilder.toString());
        }
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /**
     * 判断网元号是否已被绑定
     *
     * @param serial_no     网元号
     * @return com.alibaba.fastjson.JSONObject
     * @author 倪文骅
     * @since 2018/7/26 10:36
     */
    @RequestMapping(value = "/device/checkSerial_noExist", method = RequestMethod.GET)
    public JSONObject checkSerial_noExist(@RequestParam(value = "serial_no") String serial_no,
                                          @RequestParam(value = "deviceId") String deviceId) throws Exception {
        //根据网元号查找网元信息
        Iotx iot = iotxSaveService.findBySerialNo(serial_no);
        //设备id不为空-修改
        if(StringUtils.isNotBlank(deviceId)){
            //已存在
            if (iot != null) {
                //属于本设备-返回false 表示可以继续
                if(Objects.equals(String.valueOf(iot.getDevice().getId()),deviceId)){
                    return new JSONObject(ImmutableMap.of("result", false));
                }
                else{
                    return new JSONObject(ImmutableMap.of("result", true));
                }
            }else{
                return new JSONObject(ImmutableMap.of("result", false));
            }

        }else{//新增
            if(iot!=null){
                return new JSONObject(ImmutableMap.of("result", true));
            }else{
                return new JSONObject(ImmutableMap.of("result", false));
            }
        }
    }


    /**
    * 取消关联网元
    * @since  2018/7/26 17:09
    * @author 倪文骅
    * @param deviceId   设备id
    * @param serial_no  网元号
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/device/cancelConnect", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public JSONObject cancelConnect(@RequestParam(value = "deviceId") String deviceId,
                                 @RequestParam(value = "serial_no") String serial_no){
        Device device = deviceService.getOne(Long.parseLong(deviceId));
        Iotx iotx = iotxSaveService.findBySerialNo(serial_no);
        if(device.getIotxList().contains(iotx)){
            device.getIotxList().remove(iotx);
           iotxSaveService.delete(iotx);
            return new JSONObject(ImmutableMap.of("result", "success"));
        }else {
            //iotxSaveService.deletebyserialNo(serialNo);
            return null;
        }
    }

    /***
     * 进入查看维修记录表的页面
     *
     * @return
     */
    @RequestMapping(value = "/repairRecord/management/view", method = RequestMethod.GET)
    public ModelAndView toViewRepairRecordManage(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("view repairRecord manage");
        return new ModelAndView("device/repairRecord").addObject("deviceId",deviceId);
    }

}
