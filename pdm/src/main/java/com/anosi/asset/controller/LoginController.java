package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.LoginComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.component.WebSocketComponent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.SystemInfo;
import com.anosi.asset.service.AccountService;
import com.anosi.asset.service.SystemInfoService;
import com.anosi.asset.util.CodeUtil;
import com.anosi.asset.util.StringUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.UUID;

@RestController
public class LoginController extends BaseController<Account> {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginComponent loginComponent;
    @Autowired
    private WebSocketComponent webSocketComponent;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SystemInfoService systemInfoService;
    @Value("${local.domainName}")
    private String domainName;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginForm() {
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        return new ModelAndView("login", "uuid", UUID.randomUUID().toString()).addObject("systemInfo", systemInfo);
    }

    /***
     * 登录提交地址和shiro配置文件中配置的loginurl一致
     *
     * @param request
     * @param account
     * @return
     * @throws Exception
     *
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, Account account, RedirectAttributes redirectAttributes,
                              boolean rememberMe) throws Exception {
        logger.debug("...login...");
        Account user = accountService.findByLoginId(account.getLoginId());
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        if (!Objects.equals("0", user.getStatus())) {
            String result = loginComponent.login(account, rememberMe);
            if (result == "success") {
                return new ModelAndView("redirect:/index").addObject("systemInfo", systemInfo);
            } else {
                redirectAttributes.addFlashAttribute("message", result);
                return new ModelAndView("redirect:/login").addObject("systemInfo", systemInfo);
            }
        } else {
            String result = "您的账号已停用，请联系管理员";
            redirectAttributes.addFlashAttribute("message", result);
            return new ModelAndView("redirect:/login").addObject("systemInfo", systemInfo);
        }
    }

    /***
     * 移动端登录
     *
     * @param request
     * @param account
     * @param showAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/remote", method = RequestMethod.POST)
    public JSONObject loginRemote(HttpServletRequest request, Account account,
                                  @RequestParam(value = "showAttributes", required = false) String showAttributes) throws Exception {
        logger.debug("...remote login...");
        JSONObject jsonObject = new JSONObject();
        Account user = accountService.findByLoginId(account.getLoginId());
        //状态为0表示账户被禁用了,为1是启用
        if(!Objects.equals("0",user.getStatus())){
            String result = loginComponent.login(account, false);
            if (result == "success") {
                jsonObject = jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
                        accountService.findByLoginId(account.getLoginId()));
                jsonObject.put("JSESSIONID", SessionComponent.getSession().getId());
            } else {
                jsonObject.put("result", "error");
                jsonObject.put("message", result);
            }
        }else {
            String result = "账号已停用，请联系管理员";
            jsonObject.put("result", "error");
            jsonObject.put("message", result);
        }
        return jsonObject;
    }

    /***
     * 获取登录二维码
     *
     * @param uuid
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/login/QRCode/{uuid}", method = RequestMethod.GET)
    public void fileDownload(@PathVariable String uuid, HttpServletResponse response) throws Exception {
        BufferedImage image = CodeUtil.getRQ(domainName + "/login/remote/" + uuid, 200);
        // image转inputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        response.setHeader("Content-disposition",
                "attachment;filename=" + new String(UUID.randomUUID().toString().getBytes("gbk"), "iso-8859-1"));
        response.setContentType("application/force-download;charset=utf-8");
        // 获取文件流
        try (BufferedInputStream bis = new BufferedInputStream(is);
             OutputStream bos = new BufferedOutputStream(response.getOutputStream());) {
            IOUtils.copy(bis, bos);
        }

    }

    /***
     * 发送给浏览器，让js发送登录的请求
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/remote/QRCode/{uuid}", method = RequestMethod.POST)
    public void sendLoginWithQRCode(@PathVariable String uuid) throws Exception {
        logger.debug("...QRCode login...");

        Session session = SessionComponent.getSession();
        Object loginId = session.getAttribute("loginId");
        if (loginId != null) {
            // 向订阅这个topic的页面发送sessionId
            webSocketComponent.sendByBroadcast("/topic/login/" + uuid,
                    new JSONObject(
                            ImmutableMap.of("sessionId", session.getId().toString(), "loginId", loginId.toString()))
                            .toString());
        }
    }

    /***
     * 扫描二维码登录
     *
     * @param loginId
     * @param sessionId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/remote/QRCode", method = RequestMethod.POST)
    public ModelAndView loginWithQRCode(@RequestParam(value = "loginId") String loginId,
                                        @RequestParam(value = "sessionId") String sessionId) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginId, sessionId, false);
        // 登录验证
        currentUser.login(token);
        loginComponent.setSession(loginId);
        return new ModelAndView("redirect:/index");
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        ModelAndView mv = new ModelAndView("dashboard").addObject("systemInfo", systemInfo);
        return mv;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        return new ModelAndView("redirect:/login").addObject("systemInfo", systemInfo);
    }

    /***
     * rememberMe 以后的默认路径
     *
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index() {
        SystemInfo systemInfo = this.systemInfoService.getSystemInfo();
        return new ModelAndView("redirect:/index").addObject("systemInfo", systemInfo);
    }

}
