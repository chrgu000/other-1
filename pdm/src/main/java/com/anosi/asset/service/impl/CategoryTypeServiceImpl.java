package com.anosi.asset.service.impl;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.CategoryTypeDao;
import com.anosi.asset.model.jpa.CategoryType;
import com.anosi.asset.service.CategoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:
 * @Author: zxgang
 * @Date: 2018/12/27 17:23
 */
@Service
@Transactional
public class CategoryTypeServiceImpl extends BaseJPAServiceImpl<CategoryType> implements CategoryTypeService {
    @Autowired
    private CategoryTypeDao categoryTypeDao;
    @Override
    public BaseJPADao<CategoryType> getRepository() {
        return categoryTypeDao;
    }
}
