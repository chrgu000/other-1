package com.anosi.asset.controller.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.util.URLConncetUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Map;

@RestController
public class IotxDataController extends BaseRemoteController {

    private static final Logger logger = LoggerFactory.getLogger(IotxDataController.class);

    /***
     * 获取iotx采集的数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/iotxData/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findIotxDataManageData(HttpServletRequest request) throws Exception {
        logger.debug("get iotxData");
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /***
     * 获取线图数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/iotxData/dynamicData", method = RequestMethod.GET)
    public JSONObject dynamicData(HttpServletRequest request) throws Exception {
        logger.info("find dynamicData");
        String servletPath = request.getServletPath();//  /iotxData/dynamicData
        String fullPath = remoteComponent.getFullPath(servletPath);// http://127.0.0.1:8080//iotxData/dynamicData
        Map<String,String[]> parameterMap =  request.getParameterMap();// sensorSN : AWILZ180521010_-36488853_2   showAttributes : collectTime,val
        Map<String, String> hearders = remoteComponent.getHearders();// Cookie : JSESSIONID=7b8e0931-2d36-47dd-b830-768b062e30dc
        // http://127.0.0.1:8080//iotxData/dynamicData?sensorSN=AWILZ180521010_-36488853_2&showAttributes=collectTime%2Cval
        String result = URLConncetUtil.sendGetString(remoteComponent.getFullPath(request.getServletPath()),
                request.getParameterMap(), remoteComponent.getHearders());
        return JSON.parseObject(result);
    }

    /***
     * 导出所有数据
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/iotxData/export/allData", method = RequestMethod.GET)
    public void exportData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        URLConnection conn = URLConncetUtil.sendGet(remoteComponent.getFullPath(
                request.getServletPath()),
                URLConncetUtil.convertParams(request.getParameterMap(), true),
                remoteComponent.getHearders());
        try (InputStream is = conn.getInputStream();
             OutputStream os = new BufferedOutputStream(response.getOutputStream());) {
            String disposition = conn.getHeaderField("Content-disposition");
            response.setHeader("Content-disposition", disposition);
            response.setContentType("application/force-download;charset=utf-8");
            IOUtils.copy(is, os);
        }

    }

}
