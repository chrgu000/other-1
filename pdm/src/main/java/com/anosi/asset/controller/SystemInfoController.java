package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.model.jpa.SystemInfo;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.FileMetaDataService;
import com.anosi.asset.service.SystemInfoService;
import com.google.common.collect.ImmutableMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class SystemInfoController extends BaseController<SystemInfo> {
    private static final Logger logger = LoggerFactory.getLogger(SystemInfoController.class);

    @Autowired
    private SystemInfoService systemInfoService;
    @Autowired
    private FileMetaDataService fileMetaDataService;

    /***
     * 进入查看<b>用户信息管理</b>的页面
     *
     * @return
     */
    @RequestMapping(value = "/systemInfo/management/view", method = RequestMethod.GET)
    public ModelAndView toViewSystemInfoManage() {
        logger.debug("view systemInfo manage");
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        return new ModelAndView("systemInfo/systemInfoMgr").addObject("systemInfo", systemInfo);
    }


    /****
     * 在执行update前，先获取持久化的systemInfo对象
     *
     * @param id
     * @param model
     *
     */
    @ModelAttribute
    public void getSystemInfo(HttpServletRequest request,
                          @RequestParam(value = "systemInfoId", required = false) Long id, Model model) {
        if (id != null) {
            SystemInfo systemInfo = systemInfoService.getOne(id);
            model.addAttribute("systemInfo", systemInfo);
        }
    }

    /***
     * uploadLoginImage
     *
     * @return
     * @throws Exception
     */
    @RequiresPermissions({"systemInfoMgr:edit"})
    @RequestMapping(value = "/systemInfo/updateSystemInfo", method = RequestMethod.POST)
    @Transactional
    public JSONObject updateSystemInfo(@ModelAttribute("systemInfo") SystemInfo systemInfo,
                                 @RequestParam(value = "uploadLoginImage", required = false) MultipartFile uploadLoginImage,
                                       @RequestParam(value = "uploadIndexImage", required = false) MultipartFile uploadIndexImage) throws Exception {
        logger.debug("updateSystemInfo");
        if (uploadLoginImage != null) {
            // 登录页logo只保留最新的一张
            // 先查询再存储
            List<FileMetaData> fileMetas = fileMetaDataService.findByIdentification("systemInfo_loginImage_" + systemInfo.getId());
            for (FileMetaData fileMetaData : fileMetas) {
                // 删除多余的
                fileMetaDataService.deleteFile(fileMetaData);
            }
            fileMetaDataService.saveFile("systemInfo_loginImage_" + systemInfo.getId(), uploadLoginImage.getName(), uploadLoginImage.getInputStream(), uploadLoginImage.getSize());
            List<FileMetaData> newFileMetas = fileMetaDataService.findByIdentification("systemInfo_loginImage_" + systemInfo.getId());
            systemInfo.setLoginImg_url("/fileDownload/"+newFileMetas.get(0).getStringObjectId());
        }
        if (uploadIndexImage != null) {
            // 首页logo只保留最新的一张
            // 先查询再存储
            List<FileMetaData> fileMetas = fileMetaDataService.findByIdentification("systemInfo_indexImage_" + systemInfo.getId());
            for (FileMetaData fileMetaData : fileMetas) {
                // 删除多余的
                fileMetaDataService.deleteFile(fileMetaData);
            }
            fileMetaDataService.saveFile("systemInfo_indexImage_" + systemInfo.getId(), uploadIndexImage.getName(), uploadIndexImage.getInputStream(), uploadIndexImage.getSize());
            List<FileMetaData> newFileMetas = fileMetaDataService.findByIdentification("systemInfo_indexImage_" + systemInfo.getId());
            systemInfo.setIndexImg_url("/fileDownload/"+newFileMetas.get(0).getStringObjectId());
        }
        this.systemInfoService.save(systemInfo);
        Session session = SessionComponent.getSession();
        session.setAttribute("systemInfo", systemInfo);
        saveLog("系统信息", Oplog.Operation.EDIT, systemInfo.getName());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }
}
