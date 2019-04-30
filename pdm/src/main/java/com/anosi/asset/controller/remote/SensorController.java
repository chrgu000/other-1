package com.anosi.asset.controller.remote;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.bean.SensorBean;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.service.DataCenterService;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.ExcelUtil;
import com.anosi.asset.util.URLConncetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* 传感器控制层
* @since  2018/7/18 22:23
* @author
*/
@RestController
public class SensorController extends BaseRemoteController {

    private static final Logger logger = LoggerFactory.getLogger(SensorController.class);

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SensorService sensorService;

    @Value("${data-center.url}")
    private String url;

    @Autowired
    private DataCenterService dataCenterService;

    @Autowired
    private ServerCallComponent serverCallComponent;

    /***
     * 进入sensor管理页面
     *
     * @return
     */
    @RequestMapping(value = "/sensor/management/view", method = RequestMethod.GET)
    public ModelAndView toViewSensorManageTable() {
        logger.info("view sensor management");
        return new ModelAndView("sensor/sensorMgr");
    }

    /***
     * 获取sensor管理的数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/sensor/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findSensorManageData(HttpServletRequest request) throws Exception {
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /***
     * 点进传感器查看详情的页面
     *
     * @param serialNo 点位uid
     * @param iotSerialNo 网元号
     * @param deviceSN 设备序列号
     * @return
     */
    @RequestMapping(value = "/sensor/management/detail/{serialNo}/view", method = RequestMethod.GET)
    public ModelAndView toViewSensorManageTable(@PathVariable String serialNo,
                                                @RequestParam(value = "iotSerialNo") String iotSerialNo,
                                                @RequestParam(value = "deviceSN")String deviceSN,
                                                @RequestParam(value = "deviceId")String deviceId,
                                                @RequestParam(value = "name")String name
                                                ) {
        logger.info("view sensor management detail");
        return new ModelAndView("sensor/managementDetail")
                .addObject("serialNo", serialNo)
                .addObject("deviceId", deviceId)
                .addObject("iotSerialNo",iotSerialNo)
                .addObject("deviceSN",deviceSN)
                .addObject("name",name);
    }

    /***
     * 根据条件查询某个sensor
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sensor/management/data/one", method = RequestMethod.GET)
    public JSONObject findSensorManageDataOne(HttpServletRequest request) throws Exception {
        logger.info("find sensor one");
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /***
     * 获取autocomplete的source
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sensor/autocomplete", method = RequestMethod.GET)
    public JSONArray autocomplete(HttpServletRequest request) throws Exception {
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseArray(result);
    }

    /***
     * 获取需要展示的技术参数
     * @param deviceSN
     * @return
     */
    @RequestMapping(value = "/device/parameters/get", method = RequestMethod.GET)
    public JSONObject getShowParameters(
            @RequestParam(value = "deviceSN",required = false) String deviceSN,
            @RequestParam(value = "serial_no",required = false)String serial_no
            ) throws Exception {

        JSONObject resultJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray content = new JSONArray();
        Device device = deviceService.findBySerialNo(deviceSN);
        String[] showParameters = device.getShowParameters();
        if (showParameters != null && showParameters.length != 0) {
            for (String showParameter : showParameters) {
//                String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath("/sensor/management/data/one"),
//                        "serialNo=" + showParameter + "&showAttributes=serialNo,name,parameterDescribe", remoteComponent.getHearders());
//                String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath("/iotx/getPlcPointData"),
//                        "serial_no=" + showParameter + "&showAttributes=serialNo,name,parameterDescribe", remoteComponent.getHearders());
                JSONObject jsonObj = new JSONObject();
                String plcPointDataUrl = url+"/iotx/netElement/iotx/plcPointData/findPage?uid="+showParameter;
                String jsonResult = serverCallComponent.executeGet(plcPointDataUrl);
                JSONObject jsonObject = JSON.parseObject(jsonResult);
                content = jsonObject.getJSONArray("content");
                String totalPages = jsonObject.getString("totalPages");
                String totalElements = jsonObject.getString("totalElements");
                if(content.size()>0){
                    JSONObject contentObj = content.getJSONObject(0);
                    String type = contentObj.getString("type");//数据类型
                    String name = contentObj.getString("name");//点位名称
                    String uid = contentObj.getString("uid");//点位id
                    String iotSerialNo = contentObj.getString("iotSerialNo");//网元号
                    String pointAddress = contentObj.getString("pointAddress");//读取地址
                    String lastVal = contentObj.getString("lastVal");//实时数据
                    String collectTime = contentObj.getString("collectTime");//采集时间
                    jsonObj.put("type", type);
                    jsonObj.put("name", name);
                    jsonObj.put("uid", uid);
                    jsonObj.put("iotSerialNo", iotSerialNo);
                    jsonObj.put("pointAddress", pointAddress);
                    jsonObj.put("lastVal", "");
                    jsonObj.put("collectTime", collectTime);
                    jsonArray.add(jsonObj);
                }
            }
            resultJson.put("rows", jsonArray);
        }
        return resultJson;
    }


    /**
    * 根据网元号和点位id获取网元数据
    * @since  2018/7/19 11:49
    * @author 倪文骅
    * @param pointUid       点位uid
    * @param iotxId    网元号
    * @param pageable       分页
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/sensor/getIotxDataByUidAndSerialNo",method = RequestMethod.GET)
    public JSONObject getIotxDataByUidAndSerialNo(
            @RequestParam(value = "pointUid") String pointUid,
            @RequestParam(value = "iotxId") String iotxId,
            @PageableDefault(sort = {"collectTime"},direction = Sort.Direction.DESC,page = 0,size = 20)Pageable pageable) throws IOException {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
//        Sort sort = pageable.getSort();
//        String[] splitCollectTime = sort.toString().split(": ");
        String l3Url = url+"/iotx/netElement/iotx/data/plc/page?iotxId="+iotxId+"&pointUid="+pointUid+"&page="+pageNumber+"&size="+pageSize+"&sort=collectTime,asc";
        String jsonResult = serverCallComponent.executePost(l3Url);
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        return jsonObject;
    }

    /**
     * 根据网元号和点位id获取历史数据并格式成jqGrid需要的格式
     * @since  2018/7/19 11:49
     * @author 倪文骅
     * @param pointUid       点位uid
     * @param iotxId    网元号
     * @param pageable       分页
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping(value = "/sensor/getIotxDataByUidAndSerialNoFormatGrid",method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public JSONObject getIotxDataByUidAndSerialNoFormatGrid(HttpServletRequest request,
            @RequestParam(value = "pointUid") String pointUid,
            @RequestParam(value = "iotxId") String iotxId,
            @PageableDefault(sort = {"collectTime"},direction = Sort.Direction.ASC,page = 0,size = 20)Pageable pageable) throws IOException {
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String[] start;
        String[] end;
        int pageSize = pageable.getPageSize();
        int page = pageable.getPageNumber();
//        Sort sortObj = pageable.getSort();
//        String[] sortArr = sortObj.toString().trim().split(":");
//        String sort = sortArr[0]+","+sortArr[1];
        JSONObject jqGridJsonObject = new JSONObject();
        String L3Url = url+"/iotx/netElement/iotx/data/plc/page?";
        if(StringUtils.isNotBlank(iotxId)){
            L3Url+= "iotxId="+iotxId+"&size="+pageSize+"&sort=collectTime,asc"+"&page="+page;
        }

        if(startTime!=null&&endTime!=null){
            start = startTime.split(" ");
            end = endTime.split(" ");
            L3Url+="&startTime="+start[0]+"+"+start[1]+"&endTime="+end[0]+"+"+end[1];
        }else{//给个默认开始结束时间
            startTime = DateFormatUtil.getFormateDate(DateFormatUtil.initDateOfToday());
            endTime = DateFormatUtil.getFormateDate(new Date());
            start = startTime.split(" ");
            end = endTime.split(" ");
            L3Url+="&startTime="+start[0]+"+"+start[1]+"&endTime="+end[0]+"+"+end[1];
        }
        if (pointUid!=null){
            L3Url+="&pointUid="+pointUid;
        }

        String result = serverCallComponent.executePost(L3Url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray content = jsonObject.getJSONArray("content");
        String totalPages = jsonObject.getString("totalPages");
        String totalElements = jsonObject.getString("totalElements");
        String pageNumber = jsonObject.getString("number");
       /* JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < content.size(); i++) {
            JSONObject data = content.getJSONObject(i);
            JSONObject newObj = new JSONObject();
            String uid = data.getString("pointUid");
            String typeName = dataCenterService.getTypeName(uid);
            newObj.put("name",typeName);
            newObj.put("val",data.getString("val"));
            newObj.put("collectTime",data.getString("collectTime"));
            jsonArray.add(newObj);
        }*/
        jqGridJsonObject.put("rows", content);
        jqGridJsonObject.put("total", totalPages);
        jqGridJsonObject.put("records", totalElements);
        jqGridJsonObject.put("page", pageNumber);
        return jqGridJsonObject;
    }


    /**
    * L3 分页获取PLC数据（历史曲线图表数据）
    * @since  2018/7/23 21:38
    * @author 倪文骅
    * @param request
    * @param showType   类型
    * @param startTime  开始时间
    * @param endTime    结束时间
    * @param iotxId     网元号
    * @param pointUid   点位id
    * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping(value = "/sensor/getIotxData/{showType}", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public JSONObject plcChart(HttpServletRequest request,
                               @PathVariable String showType,
                               @RequestParam(value = "startTime")String startTime,
                               @RequestParam(value = "endTime")String endTime,
                               @RequestParam(value = "iotxId") String iotxId,
                               @RequestParam(value = "pointUid")String pointUid,
                               @PageableDefault(sort = {"collectTime"}, direction = Sort.Direction.ASC,page = 0,size = 20)Pageable pageable)throws IOException{
//        tokenComponent.login();
        String L3Url = url+"/iotx/netElement/iotx/data/plc/page?";

        if(StringUtils.isNotBlank(iotxId)){
            L3Url+= "iotxId="+iotxId+"&size=600"+"&sort=collectTime,asc";
        }

        if(startTime!=null&&endTime!=null){
            String[] start = startTime.split(" ");
            String[] end = endTime.split(" ");
            L3Url+="&startTime="+start[0]+"+"+start[1]+"&endTime="+end[0]+"+"+end[1];
        }
        if (pointUid!=null){
            L3Url+="&pointUid="+pointUid;
        }

        if("REMOTE".equals(showType)){
            String s = serverCallComponent.executePost(L3Url, showType);
            JSONObject jsonObject = JSON.parseObject(s);
            return jsonObject;
        }else {
            String result = serverCallComponent.executePost(L3Url);
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray content = jsonObject.getJSONArray("content");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < content.size(); i++) {
                JSONObject data = content.getJSONObject(i);
                JSONObject newObj = new JSONObject();
                String uid = data.getString("pointUid");
                String typeName = dataCenterService.getTypeName(uid);
                newObj.put("name",typeName);
                newObj.put("val",data.getString("val"));
                newObj.put("collectTime",data.getString("collectTime"));
                jsonArray.add(newObj);
            }
            JSONObject resultJson = new JSONObject();
            resultJson.put("data", jsonArray);
            return resultJson;
        }

    }

    /**
     * 导出传感器历史数据,默认1000条
     * @param request
     * @param response
     * @param startTime
     * @param endTime
     * @param iotxId
     * @param pointUid
     * @param pageable
     * @throws IOException
     */
    @RequestMapping(value = "/sensor/export", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public void plcChart(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value = "startTime")String startTime,
                               @RequestParam(value = "endTime")String endTime,
                               @RequestParam(value = "iotxId") String iotxId,
                               @RequestParam(value = "pointUid")String pointUid,
                               @PageableDefault(sort = {"collectTime"}, direction = Sort.Direction.ASC,page = 0,size = 20)Pageable pageable)throws IOException{
//        tokenComponent.login();
        String L3Url = url+"/iotx/netElement/iotx/data/plc/page?";

        if(StringUtils.isNotBlank(iotxId)){
            L3Url+= "iotxId="+iotxId+"&size=1000"+"&sort=collectTime,asc";
        }

        if(startTime!=null&&endTime!=null){
            String[] start = startTime.split(" ");
            String[] end = endTime.split(" ");
            L3Url+="&startTime="+start[0]+"+"+start[1]+"&endTime="+end[0]+"+"+end[1];
        }
        if (pointUid!=null){
            L3Url+="&pointUid="+pointUid;
        }

        String result = serverCallComponent.executePost(L3Url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray content = jsonObject.getJSONArray("content");
        List<SensorBean> beanList = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            JSONObject data = content.getJSONObject(i);
            SensorBean sensorBean = new SensorBean();
            sensorBean.setCollectTime(data.getString("collectTime"));
            sensorBean.setValue(data.getString("val"));
            beanList.add(sensorBean);
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("历史数据",null), SensorBean.class, beanList);
        ExcelUtil.downLoad("历史数据", response, workbook, request);
    }
}
