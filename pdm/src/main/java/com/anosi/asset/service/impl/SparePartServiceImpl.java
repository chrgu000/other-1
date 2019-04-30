package com.anosi.asset.service.impl;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SparePartDao;
import com.anosi.asset.model.jpa.SparePart;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.OplogService;
import com.anosi.asset.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 备品备件service实现类
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service.impl
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 11:14
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 11:14
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Service("sparePartService")
public class SparePartServiceImpl extends BaseJPAServiceImpl<SparePart> implements SparePartService{

    @Autowired
    private SparePartDao sparePartDao;
    @Autowired
    private OplogService oplogService;

    @Override
    public BaseJPADao<SparePart> getRepository() {
        return sparePartDao;
    }


    /**
     * 根据id删除备品备件
     * @since  2018/6/8 15:17
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    @Transactional
    public void deleteSparePartByIds(String id) {
        String[] ids = id.split("-");
        for (int i=0;i<ids.length;i++){
            sparePartDao.delete(Long.parseLong(ids[i]));
        }
        saveLog(i18nComponent.getMessage("sparePart"), Oplog.Operation.DEL, ids.length+"个");
    }
}
