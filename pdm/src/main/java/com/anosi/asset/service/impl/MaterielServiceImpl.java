package com.anosi.asset.service.impl;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.MaterielDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Materiel;
import com.anosi.asset.model.jpa.QMateriel;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.MaterielService;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.ExcelUtil;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

@Service("materielService")
@Transactional
public class MaterielServiceImpl extends BaseJPAServiceImpl<Materiel> implements MaterielService {

    @Autowired
    private MaterielDao materielDao;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private EntityManager entityManager;

    @Override
    public BaseJPADao<Materiel> getRepository() {
        return materielDao;
    }

    @Override
    public Page<Materiel> findBySearchContent(String searchContent, String deviceSN, Pageable pageable) {
        return materielDao.findBySearchContent(entityManager, searchContent, pageable, deviceSN);
    }

    @Override
    public void batchSave(InputStream is,Long deviceId) throws Exception {
        Table<Integer, String, Object> table = ExcelUtil.readExcel(is, 0);
        chekcMulitName(table);
        Map<Integer, Map<String, Object>> rows = table.rowMap();
        List<Materiel> materiels = new ArrayList<>();
        rows.forEach((rowNum, cells) -> {
            Materiel materiel = parseExcel(rowNum, cells,deviceId);
            //materiel.setDevice(deviceService.findOne(deviceId));
            materiel.needRemind();
            materiels.add(materiel);
        });
        materielDao.save(materiels);
        saveLog(i18nComponent.getMessage("materiel.predictive.maintenance"), Oplog.Operation.BATCH_ADD, materiels.size() + "条");
    }



    /**
     * 判断当前excel是否有重复且相同设备序列号的name
     *
     * @param table
     */
    private void chekcMulitName(Table<Integer, String, Object> table) {
        Map<Integer, Object> numberMap = table.columnMap().get("物料编码");
        //Map<Integer, Object> serialNoMap = table.columnMap().get("所属设备(序列号)");
        int size = table.rowKeySet().size();
        for (int i = 0; i < size; i++) {
            Object nameI = numberMap.get(i);
            //Object serialNoI = serialNoMap.get(i);
            for (int j = i + 1; j < size; j++) {
                Object nameJ = numberMap.get(j);
                //if (Objects.equals(nameI, nameJ) && Objects.equals(serialNoI, serialNoMap.get(j))) {
                if (Objects.equals(nameI, nameJ) ) {
                    throw new CustomRunTimeException(
                            MessageFormat.format(i18nComponent.getMessage("materiel.exist.excel"), i + 1, j + 1));
                }
            }
        }
    }

    /***
     * 将excel解析成bean
     *
     * @param rowNum
     * @param cells
     * @return
     */
    private Materiel parseExcel(int rowNum, Map<String, Object> cells,Long deviceId) {
       /* String serialNo = cells.get("所属设备(序列号)").toString();
        Device device = deviceService.findBySerialNo(serialNo);
        if (device == null) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("device.notExist.withSN"), serialNo));
        }*/
        Device device = deviceService.getOne(deviceId);
        String name = cells.get("物料名称").toString();
        String number = cells.get("物料编码").toString();
        /*if (materielDao.exists(QMateriel.materiel.name.eq(name).and(QMateriel.materiel.device.serialNo.eq(serialNo)))) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("materiel.exist"), name, serialNo));
        }*/
        //物料名称或者物料编码相同就判定为重复了
        if (materielDao.exists(
                QMateriel.materiel.name.eq(name)
                .and(QMateriel.materiel.number.eq(number))
                .and(QMateriel.materiel.device.id.eq(deviceId))
                )
            ) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("materiel.exist"), name, deviceId));
        }

        int checkYear = Integer.parseInt(cells.get("检测周期-年").toString());
        int checkMonth = Integer.parseInt(cells.get("检测周期-月").toString());
        int checkDay = Integer.parseInt(cells.get("检测周期-日").toString());
        int remindYear = Integer.parseInt(cells.get("预警周期-年").toString());
        int remindMonth = Integer.parseInt(cells.get("预警周期-月").toString());
        int remindDay = Integer.parseInt(cells.get("预警周期-日").toString());

        if (checkYear != 0 || checkMonth != 0 || checkDay != 0 || remindYear != 0 || remindMonth != 0
                || remindDay != 0) {
            int checkTotal = checkYear * 365 + checkMonth * 30 + checkDay;
            int remindTotal = remindYear * 365 + remindMonth * 30 + remindDay;
            if (checkTotal < remindTotal) {
                throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                        + i18nComponent.getMessage("materiel.remindCannotGreaterThanCheck"));
            }
        }


        String beginDate = cells.get("投运时间").toString();
        // 判断日期格式

        //Date beginDate = null;
        try {
            Date beginTime = DateFormatUtil.getDateByParttern(beginDate, "yyyy-MM-dd");
            Date productionTime = DateFormatUtil.getDateByParttern(cells.get("生产时间").toString(), "yyyy-MM-dd");
            Date lastCheckTime = DateFormatUtil.getDateByParttern(cells.get("上一次维护时间").toString(), "yyyy-MM-dd");
            Materiel materiel = new Materiel(number, name, device, beginTime, checkYear, checkMonth, checkDay, remindYear,
                    remindMonth, remindDay);
            materiel.setProductionTime(productionTime);
            materiel.setLastCheckTime(lastCheckTime);
            return materiel;
        } catch (ParseException e) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("materiel.beginDate.parseError"), "yyyy-MM-dd"));
        }


    }


    /**
     * 根据设备id查询设物料数据
     * @since  2018/6/6 21:45
     * @author 倪文骅
     * @param deviceId	设备id
     * @param pageable  分页
     * @return org.springframework.data.domain.Page<com.anosi.asset.model.jpa.Materiel>
     */
    @Override
    public Page<Materiel> findMaterielByDeviceId(String deviceId, Pageable pageable) {
        StringBuffer countsql = new StringBuffer();
        countsql.append("select count(id) from Materiel m where 1=1 ");

        StringBuffer selectsql = new StringBuffer();
        selectsql.append("from Materiel m where 1=1");

        StringBuffer wheresql = new StringBuffer();
        Map<String,Object> params = new HashMap<>();
        if (StringUtils.isNoneBlank(deviceId)){
            wheresql.append(" and device_id =:deviceId");
            params.put("deviceId",deviceId);
        }
        Query countQuery = this.entityManager.createQuery(countsql.append(wheresql).toString(),Long.class);
        this.setParameters(countQuery,params);
        Long count = (Long) countQuery.getSingleResult();

        Query query = this.entityManager.createQuery(selectsql.append(wheresql).append(" order by m.id ").toString(),Materiel.class);
        this.setParameters(query,params);

        // 分页
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        List<Materiel> materielList = query.getResultList();

        return new PageImpl<>(materielList, pageable, count);
    }




    /**
     * 给sql设置参数值
     * @since  2018/6/7 9:30
     * @author 倪文骅
     * @param query
     * @param params
     * @return void
     */
    public void setParameters(Query query, Map<String,Object> params){
        for(Map.Entry<String,Object> entry :params.entrySet()){
            query.setParameter(entry.getKey(),entry.getValue());
        }
    }

    /**
     * 根据行号删除物料
     * @since  2018/6/8 13:40
     * @author 倪文骅
     * @param id
     * @return void
     */
    @Override
    public void deleteMaterielById(String id) {
        String[] ids = id.split("-");
        for (int i=0;i<ids.length;i++){
            materielDao.delete(Long.parseLong(ids[i]));
        }
        saveLog(i18nComponent.getMessage("materiel.predictive.maintenance"), Oplog.Operation.DEL, ids.length + "条");
    }
}
