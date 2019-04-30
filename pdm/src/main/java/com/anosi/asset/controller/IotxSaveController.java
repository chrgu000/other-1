package com.anosi.asset.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.component.TokenComponent;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.service.DataCenterService;
import com.anosi.asset.service.DepartmentService;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.IotxSaveService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IotxSaveController extends BaseController<Iotx> {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private IotxSaveService iotxSaveService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DepartmentService departmentService;
    @Value("${data-center.url}")
    private String url;
    @Autowired
    private TokenComponent tokenComponent;
    @Autowired
    private ServerCallComponent serverCallComponent;
    @Autowired
    private DataCenterService dataCenterService;


    /**
    * 根据设备id获取网元数据
    * @since  2018/7/17 21:07
    * @author 倪文骅 
    * @param deviceId  
    * @return com.alibaba.fastjson.JSONArray
    */
    @RequestMapping(value = "/iotx/management/data", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public JSONArray findIotx(@RequestParam(value = "deviceId", required = false, defaultValue = "0") Long deviceId){
        JSONArray iotxList =  iotxSaveService.findByDevice(deviceId);
        return iotxList ;
    }


    /**
    * 根据网元分组id获取网元序列号
    * @since  2018/7/16 17:19 
    * @author 倪文骅 
    * @param id  
    * @return com.alibaba.fastjson.JSONArray
    */
    @RequestMapping(value = "/iotx/group/iotxList", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public JSONArray findIotxsBygroup(@RequestParam(value = "id", required = false, defaultValue = "0") String id)throws Exception{
        tokenComponent.login();
        String testUrl = url+"/iotx/netElement/combination/findIotx?id="+id;
        String iotxs = serverCallComponent.executeGet(testUrl);
        JSONArray jsonIotxs = JSON.parseArray(iotxs);
        return jsonIotxs;
    }

    /**
     * 获取持久化对象
     * @param request
     * @param serialNo
     * @param model
     */
    @ModelAttribute
    public void getDevice(HttpServletRequest request,
                          @RequestParam(value = "iotxList", required = false) String serialNo, Model model) {
        if (serialNo != null) {
            Iotx iotx = iotxSaveService.findBySerialNo(serialNo);
            model.addAttribute("iotx", iotx);
        }
    }
    /***
     * 保存网元
     * @param devId
     * @param serialNo
     * @return
     */
    @RequestMapping(value = "/device/iotx/save", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public JSONObject saveIotx(@RequestParam(value = "deviceId") String devId,
                               @RequestParam(value = "iotxList") String serialNo){
        Device device = deviceService.getOne(Long.parseLong(devId));
        Iotx iotx = iotxSaveService.findBySerialNo(serialNo);
        //判断是否已经绑定
        List<String> serialList =new ArrayList();
        List<Device> deviceList = deviceService.findAll();
        for(Device device1:deviceList){
            List<Iotx> iotxList = device1.getIotxList();
            for(Iotx iotx1:iotxList){
                String serial = iotx1.getSerial_no();
                serialList.add(serial);
            }
        }
        if(iotx!=null && serialList.contains(serialNo)){
            return new JSONObject(ImmutableMap.of("result", "exist"));
        }else{
            if(iotx == null){
                iotx = new Iotx();
            }
            iotx.setSerial_no(serialNo);
            device.getIotxList().add(iotx);
            iotxSaveService.save(iotx);
            return new JSONObject(ImmutableMap.of("result", "success"));
        }

    }

    @RequestMapping(value = "/device/iotx/delete", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public JSONObject deleteIotx(@RequestParam(value = "deviceId") String devId,
                               @RequestParam(value = "iotxList") String serialNo){
        Device device = deviceService.getOne(Long.parseLong(devId));
        Iotx iotx = iotxSaveService.findBySerialNo(serialNo);
        if(device.getIotxList().contains(iotx)){
            device.getIotxList().remove(iotx);
            iotx.setDevice(null);
            iotxSaveService.save(iotx);
            return new JSONObject(ImmutableMap.of("result", "success"));
        }else {
            //iotxSaveService.deletebyserialNo(serialNo);
            return null;
        }
    }


    /**
    * F36接口分页获取网元下的点位表和最后一次的采集数据
    * @since  2018/7/17 17:33 
    * @author 倪文骅 
    * @param request
    * @param pageable  
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/iotx/getPlcPointData", method = RequestMethod.GET)
    public JSONObject getPlcPointData(HttpServletRequest request,
                                      @PageableDefault(sort = { "collectTime" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                      @RequestParam(value = "serial_no",required = false) String serial_no) throws Exception {
//        String iotx = request.getParameter("iotx");
//        System.out.println("网元："+iotx);
        if(StringUtils.isNotBlank(serial_no)){
//        tokenComponent.login();
            int pageSize = pageable.getPageSize();
            int pageNumber = pageable.getPageNumber();
            Sort sort = pageable.getSort();
            String[] split1 = sort.toString().split(": ");
            String sortString = split1[0]+","+split1[1];
            String testUrl = url+"/iotx/netElement/iotx/plcPointData/findPage?iotSerialNo="+serial_no+"&page="+pageNumber+"&size="+pageSize;
            JSONObject plcList = dataCenterService.getPlcList(serial_no, pageSize, pageNumber, sortString, testUrl);
            return plcList;
        }else {
            return new JSONObject();
        }
    }
}
