package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Project;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.ProjectService;
import com.aspose.cad.internal.Exceptions.Exception;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProjectController extends BaseController<Project> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    /***
     * 进入查看项目的页面
     *
     * @return
     */
    @RequestMapping(value = "/project/management/view", method = RequestMethod.GET)
    public ModelAndView toViewProjectManage() {
        logger.debug("view project manage");
        return new ModelAndView("project/projectManage");
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
    @RequestMapping(value = "/project/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findProjectManageData(@PathVariable ShowType showType,
                                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable,
                                            @QuerydslPredicate(root = Project.class) Predicate predicate,
                                            @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                            @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws java.lang.Exception {
        logger.info("find project");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);
        Page<Project> projectPage = projectService.findAll(predicate, pageable);
        if(showType.equals(ShowType.REMOTE)){
            List<Project> list = projectPage.getContent();
            if(!CollectionUtils.isEmpty(list)){
                for(Project project:list){
                    JSONObject jsonObject = getPosition(project.getLocation());
                    if(!JSONUtils.isNull(jsonObject)){
                        if(jsonObject.get("lat") != null) {
                            project.setBaiduLatitude(Double.parseDouble(jsonObject.get("lat").toString()));
                        }
                        if(jsonObject.get("lng") != null) {
                            project.setBaiduLongitude(Double.parseDouble(jsonObject.get("lng").toString()));
                        }
                    }
                }
            }
        }
        return parseToJson(projectPage, rowId, showAttributes, showType);
    }

    public  JSONObject getPosition(String address) {
        BufferedReader in = null;
        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL("http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=5CszUV7dPeeTfhUi2OR8hXncqKYz2WqW");
            connection = url.openConnection();
            connection.setDoOutput(true);
            in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            StringBuilder text = new StringBuilder("");
            while ((line = in.readLine()) != null) {
                text.append(line.trim());
            }
            JSONObject result = JSONObject.parseObject(text.toString());
            if (result != null && result.getIntValue("status") == 0) {
                return result.getJSONObject("result").getJSONObject("location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /****
     * 在执行update前，先获取持久化的project对象
     *
     * @param number
     * @param model
     *
     */
    @ModelAttribute
    public void getAccount(@RequestParam(value = "number", required = false) String number, Model model) {
        if (StringUtils.isNoneBlank(number)) {
            model.addAttribute("project",
                    projectService.findByNumber(number) == null ? new Project() : projectService.findByNumber(number));
        }
    }

    /***
     * 进入save/update project的页面
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/save", method = RequestMethod.GET)
    public ModelAndView toSaveProjectPage(@RequestParam(value = "id", required = false) Long id) throws Exception {
        Project project;
        ModelAndView mv = new ModelAndView("project/save");
        if (id == null) {
            project = new Project();
        } else {
            project = projectService.getOne(id);
        }
        return mv.addObject("project", project);
    }


    /***
     * save or update project
     *
     * @param project
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/save", method = RequestMethod.POST)
    public JSONObject save(@ModelAttribute("project") Project project) throws Exception {
        logger.debug("saveOrUpdate project");
        Oplog.Operation operation = project.getId() == null ? Oplog.Operation.ADD : Oplog.Operation.EDIT;
        projectService.save(project);
        saveLog("项目", operation, project.getName());
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * batch save or update project
     *
     * @param projectVO
     * @return
     * @throws Exception
     */
    @Transactional
    @RequestMapping(value = "/project/save/batch", method = RequestMethod.POST)
    public JSONObject saveBatch(@ModelAttribute("projectVO") ProjectVO projectVO) throws Exception {
        logger.debug("batch saveOrUpdate project");
        List<Project> projects = projectVO.getProjects().parallelStream().map(project -> {
            Project existProject = projectService.findByNumber(project.getNumber());
            if (existProject == null) {
                return project;
            } else {
                // 直接赋值id，因为project不被hibernate管理，可以统一save到数据库
                // 如果为existProject赋值的话，那么会在查询的时候，因为hibernate的dirtyFlush而直接更新到数据库
                // 这样每次查询都会发生一次更新，效率太低
                project.setId(existProject.getId());
                return project;
            }
        }).collect(Collectors.toList());
        projectService.save(projects);
        saveLog("项目", Oplog.Operation.BATCH_ADD, projects.size() + "个");
        return new JSONObject(ImmutableMap.of("result", "success"));
    }

    /***
     * 删除项目
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/delete", method = RequestMethod.POST)
    @Transactional
    public JSONObject deleteProject(@RequestParam(value = "id") Long id) throws Exception {
        logger.debug("delete project");
        String projectName = projectService.findOne(id).getName();
        projectService.delete(id);
        saveLog("项目", Oplog.Operation.DEL, projectName);
        return new JSONObject(ImmutableMap.of("result", "success"));
    }


    /***
     * 获取根据number模糊搜索的project
     *
     * @param number
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/autocomplete", method = RequestMethod.GET)
    public JSONArray autocomplete(@RequestParam(value = "number") String number,
                                  @RequestParam(value = "remote", required = false) boolean remote) throws Exception {
        JSONArray jsonArray = new JSONArray();
        List<Project> projects = projectService.findByNumberStartsWith(number);
        for (Project project : projects) {
            JSONObject jsonObject = new JSONObject();
            if (remote) {
                jsonObject.put("id", project.getId());
                jsonObject.put("number", project.getNumber());
                jsonObject.put("name", project.getName());
                jsonObject.put("location", project.getLocation());
            } else {
                jsonObject.put("label", project.getNumber() + "-" + project.getName() + "-" + project.getLocation());
                jsonObject.put("value", project.getId());
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 按照project某些属性判断是否存在
     *
     * @param predicate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/project/checkExist", method = RequestMethod.GET)
    public JSONObject checkExist(@QuerydslPredicate(root = Project.class) Predicate predicate) throws Exception {
        return new JSONObject(ImmutableMap.of("result", projectService.exists(predicate)));
    }

    public static class ProjectVO {

        private List<Project> projects;

        public List<Project> getProjects() {
            return projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = projects;
        }

    }

}
