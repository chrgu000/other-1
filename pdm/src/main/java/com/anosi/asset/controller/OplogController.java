package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.model.mongo.QOplog;
import com.anosi.asset.service.OplogService;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Description: 操作日志controller
 * @Author: zxgang
 * @Date: 2019/1/14 11:10
 */
@RestController
public class OplogController extends BaseController<Oplog> {

    private static final Logger logger = LoggerFactory.getLogger(OplogController.class);

    @Autowired
    private OplogService oplogService;

    /**
     * 进入操作日志页面
     * @return
     */
    @RequestMapping(value = "/oplog/management/view", method = RequestMethod.GET)
    public ModelAndView toViewReport() {
        logger.debug("view oplog");
        return new ModelAndView("oplog/oplogManage");
    }

    /**
     * 获取操作日志数据
     * @param showType
     * @param pageable
     * @param predicate
     * @param showAttributes
     * @param rowId
     * @param searchContent
     * @param beginTime
     * @param endTime
     * @return
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/oplog/management/data/{showType}", method = RequestMethod.GET)
    public JSONObject findDeviceManageData(@PathVariable ShowType showType,
                                           @PageableDefault(sort = {"opTime"}, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
                                           @QuerydslPredicate(root = Oplog.class) Predicate predicate,
                                           @RequestParam(value = "showAttributes", required = false) String showAttributes,
                                           @RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
                                           @RequestParam(value = "searchContent", required = false) String searchContent,
                                           @RequestParam(value = "beginTime", required = false) Date beginTime,
                                           @RequestParam(value = "endTime", required = false) Date endTime) throws java.lang.Exception {
        logger.info("find oplog");
        logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

        Page<Oplog> oplogs;
        QOplog qOplog = QOplog.oplog;
        if (beginTime != null) {
            predicate = qOplog.opTime.after(beginTime).and(predicate);
        }
        if (endTime != null) {
            predicate = qOplog.opTime.before(endTime).and(predicate);
        }
        if (StringUtils.isNoneBlank(searchContent)) {
            Oplog.Operation op = Oplog.Operation.getByName(searchContent);
            if (op != null) {
                predicate = qOplog.operator.contains(searchContent)
                        .or(qOplog.target.contains(searchContent))
                        .or(qOplog.operation.eq(op))
                        .or(qOplog.content.contains(searchContent))
                        .and(predicate);
            } else {
                predicate = qOplog.operator.contains(searchContent)
                        .or(qOplog.target.contains(searchContent))
                        .or(qOplog.content.contains(searchContent))
                        .and(predicate);
            }
        }
        oplogs = oplogService.findAll(predicate, pageable);
        return parseToJson(oplogs, rowId, showAttributes, showType);
    }
}
