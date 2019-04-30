package com.anosi.asset.service.impl;

import com.anosi.asset.dao.mongo.BaseMongoDao;
import com.anosi.asset.dao.mongo.OplogDao;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.OplogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 操作日志service实现类
 * @Author: zxgang
 * @Date: 2019/1/14 11:00
 */
@Service
public class OplogServiceImpl extends BaseMongoServiceImpl<Oplog> implements OplogService {

    private static final Logger logger = LoggerFactory.getLogger(OplogServiceImpl.class);

    @Autowired
    private OplogDao oplogDao;

    @Override
    public BaseMongoDao<Oplog> getRepository() {
        return oplogDao;
    }

}
