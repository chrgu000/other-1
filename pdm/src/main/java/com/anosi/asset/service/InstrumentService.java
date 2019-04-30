package com.anosi.asset.service;

import com.anosi.asset.model.jpa.Instrument;

import java.io.InputStream;

/**
 * 仪器service
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 14:20
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 14:20
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public interface InstrumentService extends BaseJPAService<Instrument>{
    /**
     * 根据行号id删除仪表检验数据
     * @since  2018/6/8 13:10
     * @author 倪文骅
     * @param id  行号
     * @return com.alibaba.fastjson.JSONObject
     */
    void deleteInstrumentById(String id);

    /**
    * 导入Excel数据
    * @since  2018/6/22 11:31 
    * @author 倪文骅 
    * @param inputStream
    * @param deviceId  
    * @return void
    */
    void batchSave(InputStream inputStream, Long deviceId) throws Exception;
}
