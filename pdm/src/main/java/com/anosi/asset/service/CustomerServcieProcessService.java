package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.AgreementStatus.Agreement;
import com.anosi.asset.model.jpa.CustomerServiceProcess;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public interface CustomerServcieProcessService extends BaseProcessService<CustomerServiceProcess> {

	/***
	 * 销售部/工程部/质检部发起 流程变量:${assignee}:发起人
	 * 
	 * @param process
	 * @param multipartFiles
	 */
	void startProcess(CustomerServiceProcess process, MultipartFile[] multipartFiles) throws Exception;

	/**
	 * 发起人完成发起的清单
	 * 
	 * @param taskId
	 * @param process
	 * @throws Exception
	 */
	void completeStartDetail(String taskId, CustomerServiceProcess process) throws Exception;

	/****
	 * 领导审批
	 * 
	 * @param taskId
	 * @param process
	 * @throws Exception
	 */
	void examine(String taskId, CustomerServiceProcess process) throws Exception;

	/***
	 * 工程部问题评估
	 * 
	 * @param taskId
	 * @throws Exception
	 */
	void evaluating(String taskId, CustomerServiceProcess process) throws Exception;

	/***
	 * 售后服务组派单
	 * 
	 * @param taskId
	 * @param process
	 * @throws Exception
	 */
	void distribute(String taskId, CustomerServiceProcess process) throws Exception;

	/***
	 * 工程师上门维修
	 * 
	 * @param taskId
	 * @param multipartFiles 
	 * @param fellows 
	 * @param devices 
	 * @param faultCategorys 
	 * @throws Exception
	 */
	void repair(String taskId, CustomerServiceProcess process, MultipartFile[] multipartFiles, Long[] devices, Long[] fellows, Long[] faultCategorys) throws Exception;
	
	/***
	 * 转派维修
	 * 
	 * @param taskId
	 * @param multipartFiles 
	 * @param fellows 
	 * @param devices 
	 * @param faultCategorys 
	 * @throws Exception
	 */
	void entrust(String taskId, CustomerServiceProcess process, MultipartFile[] multipartFiles, Long[] devices, Long[] fellows, Long[] faultCategorys) throws Exception;

	/***
	 * 委托
	 * 
	 * @param taskId
	 * @param mandatary
	 *            代理人
	 * @param process
	 * @deprecated 转派已经是一个节点
	 */
	@Deprecated
	void entrust(String taskId, Account mandatary, String reason, CustomerServiceProcess process);

	/***
	 * 获取任务的分布
	 * 
	 * @return
	 */
	JSONArray getTaskDistribute();

	/**
	 * 转换条件
	 * 
	 * @param predicate
	 * @param timeType
	 * @param beginTime
	 * @param endTime
	 * @param agreement
	 * @return
	 */
	Predicate convertPredicate(Predicate predicate, String timeType, Date beginTime, Date endTime, Agreement agreement);

	/**
	 * 模糊查询
	 * 
	 * @param searchContent
	 * @param pageable
	 * @return
	 */
	Page<CustomerServiceProcess> findbyContent(String searchContent, Pageable pageable);

	/***
	 * 获取每日工单发起数量
	 * @param predicate 
	 * 
	 * @param pageable
	 * @return
	 */
	JSONArray getDailyStartedProcess(Predicate predicate, Pageable pageable);
	
}
