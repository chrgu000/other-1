package com.anosi.asset.controller;

import com.anosi.asset.model.mongo.FileMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 巡检记录
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.controller
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/13 13:38
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/13 13:38
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@RestController
public class InspectionRecordController extends BaseController<FileMetaData>{

    private static final Logger logger = LoggerFactory.getLogger(InspectionRecordController.class);

    /**
    * 进入巡检记录页面
    * @since  2018/9/28 11:08
    * @author 倪文骅
    * @param deviceId   设备id
    * @param deviceSN   设备序列号
    * @return org.springframework.web.servlet.ModelAndView
    */
//    @RequestMapping(value = "/inspectionRecord/management/{identification}", method = RequestMethod.GET)
//    public ModelAndView toViewInspectionRecordManage(@PathVariable String identification) {
    @RequestMapping(value = "/inspectionRecord/management/view", method = RequestMethod.GET)
    public ModelAndView toViewInspectionRecordManage(@RequestParam(value = "deviceId",required = false)String deviceId,
                                                     @RequestParam(value = "deviceSN",required = false)String deviceSN) {
        logger.debug("view inspectionRecord manage");
        return new ModelAndView("inspectionRecord/inspectionRecordManage")
                .addObject("deviceId",deviceId)
                .addObject("deviceSN",deviceSN);
    }

    /***
     * 进入上传页面
     *
     * @return
     */
    @RequestMapping(value = "/inspectionRecord/upload/view", method = RequestMethod.GET)
    public ModelAndView toViewUpload(@RequestParam(value = "deviceId") Long deviceId ,@RequestParam(value = "deviceSN")String deviceSN) {
        logger.debug("view upload");
        return new ModelAndView("inspectionRecord/upload")
                .addObject("deviceId",deviceId)
                .addObject("deviceSN",deviceSN);
    }


}
