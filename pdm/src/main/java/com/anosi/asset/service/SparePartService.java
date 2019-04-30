package com.anosi.asset.service;

import com.anosi.asset.model.jpa.SparePart;

/**
 * 备品备件service
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 11:10
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 11:10
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public interface SparePartService extends BaseJPAService<SparePart>{
    /**
     * 根据id删除备品备件
     * @since  2018/6/8 15:17
     * @author 倪文骅
     * @param id
     * @return com.alibaba.fastjson.JSONObject
     */
    void deleteSparePartByIds(String id);
}
