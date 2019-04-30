package com.anosi.asset.service.impl;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DailyMaintenanceDao;
import com.anosi.asset.dao.jpa.DeviceDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.DailyMaintenance;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.QDailyMaintenance;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DailyMaintenanceService;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.ExcelUtil;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

/**
 * 日常维护service实现类
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service.impl
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 15:17
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 15:17
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Service("dailyMaintenanceService")
public class DailyMaintenanceServiceImpl extends BaseJPAServiceImpl<DailyMaintenance> implements DailyMaintenanceService{

    @Autowired
    private DailyMaintenanceDao dailyMaintenanceDao;

    @Autowired
    private DeviceDao deviceDao;

    @Override
    public BaseJPADao<DailyMaintenance> getRepository() {
        return dailyMaintenanceDao;
    }

    /**
    * 根据id删除DailyMaintenance
    * @since  2018/6/6 14:06
    * @author 倪文骅
    * @param id
    * @return
    */
    @Override
    public void deleteDailyMaintenanceByIds(String id) {
        String[] ids = id.split("-");
        for (int i=0;i<ids.length;i++){
            dailyMaintenanceDao.delete(Long.parseLong(ids[i]));
        }
        saveLog(i18nComponent.getMessage("dailyMaintenance"), Oplog.Operation.DEL, ids.length + "条");
    }

    /**
     * 导入Excel中数据并绑定deviceId
     * @since  2018/6/22 8:30
     * @author 倪文骅
     * @param inputStream
     * @param deviceId
     * @return void
     */
    @Override
    public void batchSave(InputStream inputStream, Long deviceId) throws Exception {
        Table<Integer,String,Object> table = ExcelUtil.readExcel(inputStream,0);
        checkMulitName(table);
        Map<Integer,Map<String,Object>> rows = table.rowMap();
        List<DailyMaintenance> dailyMaintenanceList = new ArrayList<>();
        rows.forEach((rowNum,cells)->{
            DailyMaintenance dailyMaintenance = parseExcel(rowNum, cells,deviceId);
            dailyMaintenance.needCheckRemind();
            dailyMaintenance.needExchangeRemind();
            dailyMaintenanceList.add(dailyMaintenance);
        });
        dailyMaintenanceDao.save(dailyMaintenanceList);
        saveLog(i18nComponent.getMessage("dailyMaintenance"), Oplog.Operation.BATCH_ADD, dailyMaintenanceList.size() + "条");
    }



    /**
    * 检查表格中主类别细分类别物料编码3者是否重复
    * @since  2018/6/22 9:26
    * @author 倪文骅
    * @param table
    * @return void
    */
    private void checkMulitName(Table<Integer,String,Object> table){
        Map<Integer,Object> mainCategoryMap = table.columnMap().get("主类别");//主分类
        Map<Integer,Object> subCategoryMap = table.columnMap().get("细分类别");
        Map<Integer,Object> codeMap = table.columnMap().get("物料编码");
        int size = table.rowKeySet().size();
        for(int i = 0;i < size;i++){
            Object mainCategoryI = mainCategoryMap.get(i);
            Object subCategoryI = subCategoryMap.get(i);
            Object codeMapI = codeMap.get(i);
            for(int j = i+1;j < size;j++){
                Object mainCategoryJ = mainCategoryMap.get(j);
                Object subCategoryJ = subCategoryMap.get(j);
                Object codeMapJ = codeMap.get(j);
                if(Objects.equals(mainCategoryI,mainCategoryJ) && Objects.equals(subCategoryI,subCategoryJ) && Objects.equals(codeMapI,codeMapJ)){
                    throw new CustomRunTimeException(MessageFormat.format(i18nComponent.getMessage("dailyMaintenance.exist.excel"),i+1,j+1));
                }
            }
        }
    }


    /**
    * 解析Excel数据
    * @since  2018/6/22 11:23
    * @author 倪文骅
    * @param rowNum
    * @param cells
    * @param deviceId
    * @return com.anosi.asset.model.jpa.DailyMaintenance
    */
    private DailyMaintenance parseExcel(Integer rowNum, Map<String,Object> cells, Long deviceId) {
        Device device = deviceDao.getOne(deviceId);
        String mainCategory = cells.get("主类别").toString();
        String subCategory = cells.get("细分类别").toString();
        String code = cells.get("物料编码").toString();
        String mark = cells.get("代号").toString();
        String maintainer = "";
        if(cells.get("维护人")!=null){
            maintainer = cells.get("维护人").toString();
        }
        //检查数据库中主类别细分类别物料编码3者是否重复
        if(dailyMaintenanceDao.exists(
                QDailyMaintenance.dailyMaintenance.mainCategory.eq(mainCategory)
                .and(QDailyMaintenance.dailyMaintenance.device.id.eq(deviceId))
                .and(QDailyMaintenance.dailyMaintenance.subCategory.eq(subCategory)
                .and(QDailyMaintenance.dailyMaintenance.code.eq(code)))))
        {
            throw new CustomRunTimeException("rowNum:"+(rowNum+1)+","+MessageFormat.format(i18nComponent.getMessage("dailyMaintenance.exist"),mainCategory,subCategory,code,deviceId));
        }
        int checkYear = Integer.parseInt(cells.get("检测周期-年").toString());
        int checkMonth = Integer.parseInt(cells.get("检测周期-月").toString());
        int checkDay = Integer.parseInt(cells.get("检测周期-日").toString());
        int exchangeYear = Integer.parseInt(cells.get("更换周期-年").toString());
        int exchangeMonth = Integer.parseInt(cells.get("更换周期-月").toString());
        int exchangeDay = Integer.parseInt(cells.get("更换周期-日").toString());
        int remindCheckYear = Integer.parseInt(cells.get("检查预警周期-年").toString());
        int remindCheckMonth = Integer.parseInt(cells.get("检查预警周期-月").toString());
        int remindCheckDay = Integer.parseInt(cells.get("检查预警周期-日").toString());
        int remindExchangeYear = Integer.parseInt(cells.get("更换预警周期-年").toString());
        int remindExchangeMonth = Integer.parseInt(cells.get("更换预警周期-月").toString());
        int remindExchangeDay = Integer.parseInt(cells.get("更换预警周期-日").toString());
        if(checkYear != 0 || checkMonth != 0 || checkDay != 0 ||
           exchangeYear != 0 || exchangeMonth != 0 || exchangeDay != 0 ||
           remindCheckYear != 0 || remindCheckMonth != 0 || remindCheckDay != 0 ||
           remindExchangeYear != 0 || remindExchangeMonth != 0 || remindExchangeDay != 0){
            int checkTotal = checkYear * 365 + checkMonth * 30 + checkDay;
            int exchangeTotal = exchangeYear * 365 + exchangeMonth * 30 + exchangeDay;
            int remindCheckTotal = remindCheckYear * 365 + remindCheckMonth * 30 + remindCheckDay;
            int remindExchangeTotal = remindExchangeYear * 365 + remindExchangeMonth * 30 + remindExchangeDay;
            if (checkTotal < remindCheckTotal || exchangeTotal < remindExchangeTotal){
                throw new CustomRunTimeException("rowNum:"+(rowNum+1)+","+i18nComponent.getMessage("dailyMaintenance.remindCannotGreaterThanCheck"));
            }
        }
        String produceTime = cells.get("生产日期").toString();
        String lastCheckMaintainTime = cells.get("上次检查维护时间").toString();
        String lastExchangeMaintainTime = cells.get("上次更换维护时间").toString();

        try {
            Date produceDate = DateFormatUtil.getDateByParttern(produceTime,"yyyy-MM-dd");
            Date lastCheckMaintainDate = DateFormatUtil.getDateByParttern(lastCheckMaintainTime,"yyyy-MM-dd");
            Date lastExchangeMaintainDate = DateFormatUtil.getDateByParttern(lastExchangeMaintainTime,"yyyy-MM-dd");
            DailyMaintenance dailyMaintenance = new DailyMaintenance(mainCategory,subCategory,code,mark,produceDate,lastCheckMaintainDate,lastExchangeMaintainDate,maintainer,
                    checkYear,checkMonth,checkDay,
                    exchangeYear,exchangeMonth,exchangeDay,
                    remindCheckYear,remindCheckMonth,remindCheckDay,
                    remindExchangeYear,remindExchangeMonth,remindExchangeDay,device
                    );
            return dailyMaintenance;
        }catch (ParseException e){
            throw new CustomRunTimeException("rowNum:"+(rowNum+1)+","+MessageFormat.format(i18nComponent.getMessage("dailyMaintenance.parseError"),"yyyy-MM-dd"));
        }



    }
}
