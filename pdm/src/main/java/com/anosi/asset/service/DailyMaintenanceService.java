package com.anosi.asset.service;

import com.anosi.asset.model.jpa.DailyMaintenance;

import java.io.InputStream;

/**
 * 日常维护service
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 15:17
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 15:17
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public interface DailyMaintenanceService extends BaseJPAService<DailyMaintenance>{
    /**
     * 根据id删除DailyMaintenance
     * @since  2018/6/6 14:06
     * @author 倪文骅
     * @param id
     * @return
     */
     void deleteDailyMaintenanceByIds(String id);

     /**
     * 导入Excel中数据并绑定deviceId
     * @since  2018/6/22 8:30
     * @author 倪文骅 
     * @param inputStream
     * @param deviceId  
     * @return void
     */
    void batchSave(InputStream inputStream, Long deviceId) throws Exception;
}
