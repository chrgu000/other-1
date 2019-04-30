package com.anosi.asset.controller.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.component.WebSocketComponent;
import com.anosi.asset.service.DataCenterService;
import com.anosi.asset.util.URLConncetUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AlarmDataController extends BaseRemoteController {

    private static final Logger logger = LoggerFactory.getLogger(AlarmDataController.class);

    @Autowired
    private WebSocketComponent webSocketComponent;

    @Value("${data-center.url}")
    private String url;

    @Autowired
    private DataCenterService dataCenterService;

    @Autowired
    private ServerCallComponent serverCallComponent;

    /***
     * 获取告警数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alarmData/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findAlarmDataManageData(HttpServletRequest request) throws Exception {
        logger.debug("get iotxData");
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /***
     * 发生告警时调用
     *
     * @param alarmData
     * @throws Exception
     */
    @RequestMapping(value = "/alarmData/alarm/occur", method = RequestMethod.GET)
    public void alarmOccur(@RequestParam(value = "alarmData") String alarmData) throws Exception {
        logger.debug("alarm occur");
        webSocketComponent.sendByBroadcast("/topic/broadcast/alarmData", alarmData);
    }


    /***
     * 修改alarmData
     *
     * @param sensor
     * @param result
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/alarmData/save", method = RequestMethod.POST)
    public JSONObject save(HttpServletRequest request) throws Exception {
        logger.debug("saveOrUpdate alarmData");
        String result = URLConncetUtil.sendPostString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /**
     * 获取告警数据
     *
     * @param pageable
     * @param request
     * @param showType
     * @param iotSerialNo   网元号
     * @param uid            网元点位id
     * @return com.alibaba.fastjson.JSONObject
     * @author 倪文骅
     * @since 2018/7/24 15:54
     */
    @RequestMapping(value = "/alarmData/getAlarmData/{showType}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public JSONObject getAlarmData( HttpServletRequest request,
                                    @PathVariable String showType,
                                    @RequestParam(value = "iotSerialNo") String iotSerialNo,
                                    @RequestParam(value = "uid", required = false) String uid,
                                    @PageableDefault(sort = {"collectTime"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) throws IOException {
        if (StringUtils.isBlank(iotSerialNo)) {
            return null;
        }
//        tokenComponent.login();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        //N3接口
        String n3AlarmRecordUrl = url + "/alarm/alarmRecord/findPage?page=" + pageNumber + "&size=" + pageSize;
        Map<String, String> map = new HashMap<>();
        map.put("iotSerialNo", iotSerialNo);
        if (StringUtils.isNotBlank(uid)) {
            n3AlarmRecordUrl += ("&uid=" + uid);
        }
        String result = serverCallComponent.executePost(n3AlarmRecordUrl, serverCallComponent.convertEntity(map));
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray contentArray = jsonObject.getJSONArray("content");
        String totalPages = jsonObject.getString("totalPages");
        String totalElements =jsonObject.getString("totalElements");
        JSONObject resultJsonObj = new JSONObject();
        resultJsonObj.put("total",totalPages);
        resultJsonObj.put("records",totalElements);
        resultJsonObj.put("page",pageNumber);
        resultJsonObj.put("rows",contentArray);
        return resultJsonObj;
    }

    /***
     * 进入查看告警记录表的页面
     *
     * @return
     */
    @RequestMapping(value = "/alarmRecord/management/view", method = RequestMethod.GET)
    public ModelAndView toViewRepairRecordManage(@RequestParam(value = "deviceId") Long deviceId ) {
        logger.debug("view alarmRecord manage");
        return new ModelAndView("device/alarmRecord").addObject("deviceId",deviceId);
    }
}
