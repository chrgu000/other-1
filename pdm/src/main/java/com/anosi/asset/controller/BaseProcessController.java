package com.anosi.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.anosi.asset.model.jpa.BaseProcess;
import com.anosi.asset.model.jpa.BaseProcess.FinishType;
import com.anosi.asset.service.BaseProcessService;
import com.anosi.asset.util.JsonUtil;
import com.anosi.asset.util.StringUtil;
import com.google.common.collect.ImmutableMap;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public abstract class BaseProcessController<T extends BaseProcess> extends BaseController<T> {

	private static final Logger logger = LoggerFactory.getLogger(BaseProcessController.class);

	@Autowired
	public TaskService taskService;

	public String definitionKey;

	public abstract BaseProcessService<T> getProcessService();

	/***
	 * 进入查看当前account<b>发起的流程</b>的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/startedProcess/view", method = RequestMethod.GET)
	public ModelAndView toViewStartedProcess() {
		logger.debug("view startedProcess");
		return new ModelAndView("process/" + definitionKey + "/startedProcess");
	}

	/***
	 * 获取由当前account<b>发起的流程</b>的数据
	 * 
	 * @param pageable
	 * @param showAttributes
	 * @param rowId
	 * @param searchContent
	 *            模糊搜索的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/startedProcess/data/{showType}", method = RequestMethod.GET)
	public JSONObject findStartedProcess(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent,
			@RequestParam(value = "timeType", required = false, defaultValue = "start") String timeType,
			@RequestParam(value = "beginTime", required = false) Date beginTime,
			@RequestParam(value = "endTime", required = false) Date endTime,
			@RequestParam(value = "finishType", required = false) FinishType finishType) throws Exception {
		logger.info("find startedProcess");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(getProcessService().findStartedProcess(pageable, searchContent, timeType, beginTime, endTime,
				finishType), rowId, showAttributes, showType);
	}

	/****
	 * 在执行update前，先获取持久化的process对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getAccount(@RequestParam(value = "processId", required = false) Long id, Model model) {
		if (id != null) {
			model.addAttribute("process", getProcessService().getOne(id));
		}
	}

	/***
	 * 进入<b>发起流程</b>的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/startProcess/form/view", method = RequestMethod.GET)
	public ModelAndView toViewStartProcessForm(@ModelAttribute("process") T process) throws Exception {
		logger.debug("view startProcess form");
		return new ModelAndView("process/" + definitionKey + "/startProcessForm", "process", process)
				.addAllObjects(getStartProcessObjects());
	}

	/***
	 * 获取发起流程需要的数据，json格式
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/startProcess/prepare/datas", method = RequestMethod.GET)
	public JSONObject startProcessObjectsRemote() throws Exception {
		return getStartProcessObjectsRemote();
	}

	/***
	 * 发起流程所需的数据
	 * 
	 * @return
	 */
	public abstract Map<String, Object> getStartProcessObjects();

	/***
	 * 发起流程所需的数据,json格式
	 * 
	 * @return
	 */
	public abstract JSONObject getStartProcessObjectsRemote();

	/***
	 * 进入查看当前account<b>待办的任务</b>的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/runtimeTask/view", method = RequestMethod.GET)
	public ModelAndView toViewRuntimeTasks() {
		logger.debug("view runtime tasks");
		return new ModelAndView("process/" + definitionKey + "/runtimeTask");
	}

	/**
	 * 查询当前account<b>待办的任务</b>的数据
	 * 
	 * @param pageable
	 *            分页
	 * @param showAttributes
	 *            展示的列
	 * @param rowId
	 *            jqgrid每行的id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/runtimeTask/data/{showType}", method = RequestMethod.GET)
	public JSONObject findRuntimeTaskDatas(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent,
			@RequestParam(value = "beginTime", required = false) Date beginTime,
			@RequestParam(value = "endTime", required = false) Date endTime) throws Exception {
		logger.info("find runtimeTask");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(getProcessService().findTasksToDo(pageable, searchContent, beginTime, endTime), rowId,
				showAttributes, showType);
	}

	/***
	 * 进入办理页面 会根据task
	 * 
	 * @param process
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/runtimeTask/form/view", method = RequestMethod.GET)
	public ModelAndView toViewRunTimeTaskForm(@RequestParam String taskId) throws Exception {
		logger.debug("view startProcess form");
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return new ModelAndView("process/" + definitionKey + "/runtimeTask-" + task.getTaskDefinitionKey())
				.addObject("task", task)
				.addAllObjects(getRunTimeTaskObjects(task.getTaskDefinitionKey(), task.getProcessInstanceId()));
	}

	/***
	 * 获取办理需要的数据，json格式
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/runtimeTask/prepare/datas", method = RequestMethod.GET)
	public JSONObject runTimeTasksObjectsRemote(@RequestParam String taskId) throws Exception {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return getRunTimeTaskObjectsRemote(task.getTaskDefinitionKey(), task.getProcessInstanceId());
	}

	/***
	 * 发起办理所需的数据
	 * 
	 * @param taskDefinitionKey
	 *            task定义的key
	 * @param processInstanceId
	 * @return
	 */
	public abstract Map<String, Object> getRunTimeTaskObjects(String taskDefinitionKey, String processInstanceId);

	/***
	 * 发起办理所需的数据,json格式
	 * 
	 * @param taskDefinitionKey
	 *            task定义的key
	 * @param processInstanceId
	 * @return
	 */
	public abstract JSONObject getRunTimeTaskObjectsRemote(String taskDefinitionKey, String processInstanceId);

	/***
	 * 进入查看当前account<b>办理过的任务</b>的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/historicTasks/view", method = RequestMethod.GET)
	public ModelAndView toViewHistoricTasks() {
		logger.debug("view historic tasks");
		return new ModelAndView("process/" + definitionKey + "/historicTasks");
	}

	/***
	 * 查询当前account<b>办理过的任务</b>的数据
	 * 
	 * @param pageable
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/historicTasks/data/{showType}", method = RequestMethod.GET)
	public JSONObject findHistoricTasks(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent,
			@RequestParam(value = "timeType", required = false, defaultValue = "start") String timeType,
			@RequestParam(value = "beginTime", required = false) Date beginTime,
			@RequestParam(value = "endTime", required = false) Date endTime,
			@RequestParam(value = "finishType", required = false) FinishType finishType) throws Exception {
		logger.info("find historicTasks");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(getProcessService().findHistoricTasks(pageable, searchContent, timeType, beginTime, endTime,
				finishType), rowId, showAttributes, showType);
	}

	/***
	 * 进入查看<b>所有发起任务</b>的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/allProcesses/view", method = RequestMethod.GET)
	public ModelAndView toViewAllProcesses() {
		logger.debug("view allProcesses");
		return new ModelAndView("process/" + definitionKey + "/allProcesses");
	}

	/***
	 * 获取所有发起的流程
	 * 
	 * @param showType
	 * @param pageable
	 * @param showAttributes
	 * @param rowId
	 * @param searchContent
	 * @param timeType
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/allProcesses/data/{showType}", method = RequestMethod.GET)
	public JSONObject findAllProcesses(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent,
			@RequestParam(value = "timeType", required = false, defaultValue = "start") String timeType,
			@RequestParam(value = "beginTime", required = false) Date beginTime,
			@RequestParam(value = "endTime", required = false) Date endTime,
			@RequestParam(value = "finishType", required = false) FinishType finishType) throws Exception {
		logger.info("find allProcesses");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(
				getProcessService().findAllProcesses(pageable, searchContent, timeType, beginTime, endTime, finishType),
				rowId, showAttributes, showType);
	}

	/***
	 * 进入查看<b>详情</b>的页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/process/detail/view", method = RequestMethod.GET)
	public ModelAndView toViewDetail(@RequestParam(value = "id") Long id) {
		logger.debug("view detail");
		return new ModelAndView("process/" + definitionKey + "/detail", "id", id);
	}

	/****
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/process/detail/{id}", method = RequestMethod.GET)
	public JSONObject findById(@PathVariable Long id,
			@RequestParam(value = "showAttributes", required = false) String showAttributes) throws Exception {
		T t = getProcessService().getOne(id);
		JSONObject jsonObject;
		if (StringUtils.isBlank(showAttributes)) {
			t = getProcessService().getDetail(t);
			jsonObject = JSON.parseObject(JSON.toJSONStringWithDateFormat(t, "yyyy-MM-dd HH:mm:ss",
					SerializerFeature.DisableCircularReferenceDetect));
			if (t.getTask() != null) {
				jsonObject.put("task", new JSONObject(ImmutableMap.of("id", t.getTask().getId(), "name",
						t.getTask().getName(), "taskDefinitionKey", t.getTask().getTaskDefinitionKey())));
			}
		} else {
			jsonObject = jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes), t);
		}
		return jsonObject;
	}

	/***
	 * 获取这个流程所有按照key区分的最近一次任务的相关信息
	 * 
	 * @param id
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/process/detail/taskDatas/{id}", method = RequestMethod.GET)
	public JSONArray getTaskDatas(@PathVariable Long id,
			@RequestParam(value = "showAttributes", required = false) String showAttributes) throws Exception {
		JSONArray jsonArray = new JSONArray();
		List<HistoricTaskInstance> taskDatas = getProcessService().getTaskDatas(getProcessService().getOne(id));
		JsonUtil<HistoricTaskInstance> historicJsonUtil = new JsonUtil<>();
		for (HistoricTaskInstance historicTaskInstance : taskDatas) {
			JSONObject jsonObject = historicJsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
					historicTaskInstance);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

}
