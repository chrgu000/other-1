package com.anosi.asset.service.impl;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DeviceDao;
import com.anosi.asset.dao.jpa.InstrumentDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Instrument;
import com.anosi.asset.model.jpa.QInstrument;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.InstrumentService;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.ExcelUtil;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

/**
 * 仪器service实现类
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.service.impl
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 14:20
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 14:20
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Service("instrumentService")
public class InstrumentServiceImpl extends BaseJPAServiceImpl<Instrument> implements InstrumentService{

    @Autowired
    private InstrumentDao instrumentDao;

    @Autowired
    private DeviceDao deviceDao;

    @Override
    public BaseJPADao<Instrument> getRepository() {
        return instrumentDao;
    }

    /**
    * 根据id删除仪表检验数据
    * @since  2018/6/22 11:31
    * @author 倪文骅
    * @param id
    * @return void
    */
    @Override
    public void deleteInstrumentById(String id) {
        String[] ids = id.split("-");
        for (int i=0;i<ids.length;i++){
            instrumentDao.delete(Long.parseLong(ids[i]));
        }
        saveLog(i18nComponent.getMessage("inspectionRecord"), Oplog.Operation.DEL, ids.length + "条");
    }


    /**
     * 导入Excel数据
     * @since  2018/6/22 11:31
     * @author 倪文骅
     * @param inputStream
     * @param deviceId
     * @return void
     */
    @Override
    @Transactional
    public void batchSave(InputStream inputStream, Long deviceId) throws Exception {
        Table<Integer,String,Object> table = ExcelUtil.readExcel(inputStream,0);
        //checkMulitName(table);
        Map<Integer,Map<String,Object>> rows =  table.rowMap();
        List<Instrument> instrumentList = new ArrayList<>();
        rows.forEach((rowNum,cells)->{
            Instrument instrument = parseExcel(rowNum, cells,deviceId);
            instrument.needCheckRemind();
            instrumentList.add(instrument);
        });
        instrumentDao.save(instrumentList);
        saveLog(i18nComponent.getMessage("inspectionRecord"), Oplog.Operation.BATCH_ADD, instrumentList.size() + "条");
    }



    /**
    * 判断表格中name是否有重复的
    * @since  2018/6/22 11:36
    * @author 倪文骅
    * @param table
    * @return void
    */
    private void checkMulitName(Table<Integer,String,Object> table) {
        Map<Integer,Object> nameMap = table.columnMap().get("仪表");
        Map<Integer,Object> codeMap = table.columnMap().get("物料编码");
        int size = table.rowKeySet().size();
        for(int i = 0;i < size;i++){
            Object nameMapI = nameMap.get(i);
            Object codeMapI = codeMap.get(i);
            for(int j = i+1;j < size;j++){
                Object nameMapJ = nameMap.get(j);
                Object codeMapJ = codeMap.get(j);
                if(Objects.equals(nameMapI,nameMapJ) && Objects.equals(codeMapI,codeMapJ)){
                    throw new CustomRunTimeException(MessageFormat.format(i18nComponent.getMessage("instrument.exist.excel"),i+1,j+1));
                }
            }
        }
    }


    private Instrument parseExcel(Integer rowNum, Map<String,Object> cells, Long deviceId) {
        Device device = deviceDao.getOne(deviceId);
        //字符串处理
        String name = cells.get("仪表").toString();
        String code = cells.get("物料编码").toString();
        String mark = cells.get("代号").toString();
        String note = "";
        if(cells.get("备注") !=null){
            note = cells.get("备注").toString();
        }
        if (instrumentDao.exists(
                QInstrument.instrument.name.eq(name)
                .and(QInstrument.instrument.code.eq(code))
                .and(QInstrument.instrument.device.id.eq(deviceId))
                 )
            ){
            throw new CustomRunTimeException(MessageFormat.format(i18nComponent.getMessage("instrument.exist"),name,code,deviceId));
        }
        //维护和预警周期处理
        int checkYear = Integer.parseInt(cells.get("校验周期-年").toString());
        int checkMonth = Integer.parseInt(cells.get("校验周期-月").toString());
        int checkDay = Integer.parseInt(cells.get("校验周期-日").toString());
        int remindCheckYear = Integer.parseInt(cells.get("预警周期-年").toString());
        int remindCheckMonth = Integer.parseInt(cells.get("预警周期-月").toString());
        int remindCheckDay = 0;
        if(cells.get("预警周期-日")!=null){
            remindCheckDay = Integer.parseInt(cells.get("预警周期-日").toString());
        }

        int checkTotal = checkYear * 365 + checkMonth * 30 + checkDay;
        int remindCheckTotal = remindCheckYear * 365 + remindCheckMonth * 30 + remindCheckDay;
        if (checkYear != 0 || checkMonth != 0 || checkDay != 0 || remindCheckYear != 0 || remindCheckMonth != 0 || remindCheckDay != 0){
            if (checkTotal < remindCheckTotal){
                throw new CustomRunTimeException("rowNum:"+(rowNum+1)+","+i18nComponent.getMessage("instrument.remindCannotGreaterThanCheck"));
            }
        }
        //日期处理
        String produceTime = cells.get("生产日期").toString();
        String lastCheckOutTime = cells.get("上次校验时间").toString();

        //将Excel中数据实例化到实体
        try {
            Date produceDate = DateFormatUtil.getDateByParttern(produceTime,"yyyy-MM-dd");
            Date lastCheckOutDate = DateFormatUtil.getDateByParttern(lastCheckOutTime,"yyyy-MM-dd");
            Instrument instrument = new Instrument( name,  code,  mark, produceDate,  checkYear,  checkMonth,  checkDay,
                                                    lastCheckOutDate,  remindCheckYear,  remindCheckMonth,  remindCheckDay,
                                                    note,  device);
            return instrument;
        }catch (ParseException e){
            throw new CustomRunTimeException("rowNum:"+(rowNum+1)+","+MessageFormat.format(i18nComponent.getMessage("instrument.parseError"),"yyyy-MM-dd"));
        }





    }
}
