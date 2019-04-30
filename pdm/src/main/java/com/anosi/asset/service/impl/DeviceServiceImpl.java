package com.anosi.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.MapComponent;
import com.anosi.asset.component.ServerCallComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.dao.hibernateSearch.SupplyQuery;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DeviceDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.*;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.pojo.GroupOfA5;
import com.anosi.asset.pojo.IotxInfo;
import com.anosi.asset.service.*;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.ExcelUtil;
import com.anosi.asset.util.MapUtil;
import com.google.common.collect.Table;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.query.dsl.MustJunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

@Service("deviceService")
@Transactional
public class DeviceServiceImpl extends BaseJPAServiceImpl<Device> implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private MapComponent mapComponent;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private DevCategoryService devCategoryService;
    @Autowired
    private DistrictService districtService;
    @Value("${data-center.url}")
    private String url;

    @Value("${data-center.username}")
    private String username;

    @Autowired
    private ServerCallComponent serverCallComponent;

    @Autowired
    private IotxSaveService iotxSaveService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public BaseJPADao<Device> getRepository() {
        return deviceDao;
    }

    @Override
    public Device findBySerialNo(String serialNo) {
        return deviceDao.findBySerialNo(serialNo);
    }

    @Override
    public Device setDeviceDistrict(Device device) {
        Double longitude = device.getBaiduLongitude();
        Double latitude = device.getBaiduLatitude();
        if (longitude == null) {
            throw new RuntimeException(i18nComponent.getMessage("device.longitude.cannot.null"));
        }
        if (latitude == null) {
            throw new RuntimeException(i18nComponent.getMessage("device.latitude.cannot.null"));
        }
        JSONObject addressComponent = MapUtil.getAddressComponent(String.valueOf(longitude), String.valueOf(latitude));
        District district = mapComponent.createMap(addressComponent);
        device.setDistrict(district);
        // 获取转换的百度坐标
//        JSONObject convertLocation = MapUtil.convertLocation(String.valueOf(longitude), String.valueOf(latitude));
//        device.setBaiduLongitude(
//                Double.parseDouble(new String(Base64.getDecoder().decode(convertLocation.getString("x")))));
//        device.setBaiduLatitude(
//                Double.parseDouble(new String(Base64.getDecoder().decode(convertLocation.getString("y")))));
        return device;
    }

    /***
     * 基本思路是count groupBy province,city 查看结果是否为1,为1表示都是同一个行政区划的,那么找下一级
     *
     * @param predicate
     */
    @Override
    public JSONArray ascertainArea(Predicate predicate) {
        QDevice qDevice = QDevice.device;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Tuple> iotxTuples = null;

        QDevice qDeviceCustom = createQDeviceCustom();
        long countCountry = queryFactory.from(qDeviceCustom).where(predicate)
                .groupBy(qDeviceCustom.district.city.province.country).fetchCount();
        if (countCountry == 1) {
            // 如果都是一个国家,那么查看是否都是一个省
            long countProvince = queryFactory.from(qDeviceCustom).where(predicate)
                    .groupBy(qDeviceCustom.district.city.province).fetchCount();
            if (countProvince == 1) {
                // 如果都是一个省，那么查看是否都是一个市
                long countCity = queryFactory.from(qDevice).where(predicate).groupBy(qDevice.district.city)
                        .fetchCount();
                if (countCity == 1) {
                    // 如果都是一个市，那么就按照区来统计数据
                    iotxTuples = queryFactory.select(qDevice.district.name, qDevice.count()).from(qDevice)
                            .where(predicate).groupBy(qDevice.district).fetch();
                } else {
                    // 如果是多个市，就按照市统计
                    iotxTuples = queryFactory.select(qDevice.district.city.name, qDevice.count()).from(qDevice)
                            .where(predicate).groupBy(qDevice.district.city).fetch();
                }
            } else {
                // 如果是多个省，就按照省统计
                iotxTuples = queryFactory.select(qDeviceCustom.district.city.province.name, qDevice.count())
                        .from(qDevice).where(predicate).groupBy(qDeviceCustom.district.city.province).fetch();
            }
        } else {
            iotxTuples = queryFactory.select(qDeviceCustom.district.city.province.country.name, qDevice.count())
                    .from(qDevice).where(predicate).groupBy(qDeviceCustom.district.city.province.country).fetch();
        }

        JSONArray jsonArray = new JSONArray();
        for (Tuple tuple : iotxTuples) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", tuple.get(0, String.class));
            jsonObject.put("amount", tuple.get(1, Long.class));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /***
     * 主要在于构造PathInits 需要有如下结构: map:{country:{district:{city:{province:.....}}}}
     */
    private QDevice createQDeviceCustom() {
        PathInits inits = new PathInits("district.city.province.country");
        QDevice qDevice = new QDevice(Device.class, forVariable("device"), inits);
        return qDevice;
    }

    @Override
    public Page<Device> findByContentSearch(String searchContent, Pageable pageable) {
        logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
        if (SessionComponent.isClient()) {
            SupplyQuery supplyQuery = (queryBuilder) -> {
                MustJunction mustJunction = queryBuilder.bool()
                        .must(queryBuilder.keyword()
                                .onFields("project.name",
                                        "project.number", "project.location", "productName", "productNo", "productSpecifications", "serialNo",
                                        "rfid")
                                .matching(searchContent).createQuery())
                        .must(new TermQuery(new Term("ownerList.loginId", sessionComponent.getCurrentUser().getLoginId())));
                return mustJunction.createQuery();
            };
            return deviceDao.findBySearchContent(entityManager, searchContent, pageable, Device.class, supplyQuery, "");
        } else {
            return deviceDao.findBySearchContent(entityManager, searchContent, pageable);
        }
    }

    @Override
    public Device findByRfid(String rfid) {
        return deviceDao.findByRfid(rfid);
    }

    @Override
    public List<Device> findIdAndSN() {
        return deviceDao.findIdAndSN();
    }

    @Override
    public void batchSave(InputStream is) throws Exception {
        Table<Integer, String, Object> table = ExcelUtil.readExcel(is, 0);
        checkMulitSN(table);
        Map<Integer, Map<String, Object>> rows = table.rowMap();
        List<Device> devices = new ArrayList<>();
        rows.forEach((rowNum, cells) -> {
            Device device = parseExcel(rowNum, cells);
            deviceDao.save(device);
            devices.add(device);
        });
//        deviceDao.save(devices);
        saveLog("设备", Oplog.Operation.BATCH_ADD, devices.size() + "个");
    }




    /***
     * 解析excel
     * @param rowNum
     * @param cells
     * @return
     */
    private Device parseExcel(Integer rowNum, Map<String, Object> cells) {
        String serialNo = cells.get("序列号").toString();
        if (StringUtils.isBlank(serialNo)) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("device.serialNo.null"));
        }
        Device device = deviceDao.findBySerialNo(serialNo);
        if (device != null) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("device.serialNo.exist"), serialNo));
        }

        String projectNo = cells.get("企业编号").toString();
        if (StringUtils.isBlank(projectNo)) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("device.projectNo.null"));
        }
        Project project = projectService.findByNumber(projectNo);
        if (project == null) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + MessageFormat.format(i18nComponent.getMessage("device.projectNo.notExist"), projectNo));
        }

        DevCategory devCategory;
        try {
            String categoryType = cells.get("设备种类").toString();
            devCategory = devCategoryService.findByCategoryTypeCode(categoryType);
        } catch (Exception e) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("device.devcategory.illegal"));
        }

        device = new Device();
        device.setSerialNo(serialNo);
        device.setProject(project);
        device.setProductNo(cells.get("产品编号").toString());
        device.setProductName(cells.get("产品名称").toString());
        device.setProductSpecifications(cells.get("产品规格").toString());

        Iotx iotx ;
        List<Iotx> iotxes = new ArrayList<>();
        String testUrl = null;
        String result = null;
        JSONObject jsonObject = null;
        Boolean aBoolean = null;

        String netSerialNo = cells.get("网元序列号").toString();
        testUrl = url+"/iotx/initNetElement/exist?serialNo="+netSerialNo+"&account="+sessionComponent.getCurrentUser().getLoginId();
        try {
            result = serverCallComponent.executeGet(testUrl);
        }catch (Exception e){
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("iotx.serial.no.error"));
        }
        jsonObject = JSON.parseObject(result);
        aBoolean = jsonObject.getBoolean("result");
        if(!aBoolean){
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("iotx.serial.no.error"));
        } else {
            iotx = new Iotx();
            iotx.setSerial_no(netSerialNo);
            //网元设备是否被绑定
            Iotx iot = iotxSaveService.findBySerialNo(netSerialNo);
            if(iot != null){
                logger.info("parseExcel 网元被设备绑定");
                throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                        + i18nComponent.getMessage("iotx.serial.exits.error"));
            }else {
                try {
                    List<GroupOfA5> groupOfA5List = getNetAll();
                    if (!ListUtils.isEmpty(groupOfA5List)) {
                        Long groupId = findNetGroupId(netSerialNo, groupOfA5List);
                        if (groupId == null) {
                            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                                    + i18nComponent.getMessage("iotx.serial.no.error"));
                        } else {
                            iotx.setGroup_id(groupId.toString());
                        }
                    }
                } catch (Exception e) {
                    throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                            + i18nComponent.getMessage("iotx.serial.no.error"));
                }
            }
            iotxSaveService.save(iotx);
            iotx.setDevice(device);
            iotxes.add(iotx);
        }

        device.setIotxList(iotxes);

        try {
            if(cells.get("投运时间")!=null){
                device.setCommissioningTime(DateFormatUtil.getDateByParttern(cells.get("投运时间").toString(), "yyyy-MM-dd"));
            }
        } catch (ParseException e) {
            throw new CustomRunTimeException("rowNum:" + (rowNum + 1) + ","
                    + i18nComponent.getMessage("device.commissioningTime.illegal"));
        }
        device.setDevCategory(devCategory);
        return device;
    }

    private Long findNetGroupId(String serialNo,  List<GroupOfA5> groupOfA5List){
        for(GroupOfA5 groupOfA5:groupOfA5List){
            List<IotxInfo> list = groupOfA5.getIotxList();
            Long count = list.stream().filter(e->e.getSerialNo().equals(serialNo)).count();
            if(count>0){
                return groupOfA5.getId();
            }
        }
        return null;
    }

    //查找账号所有的网元
    private List<GroupOfA5> getNetAll() throws Exception{
        logger.info("查找账号下面所有的网元");

        List<GroupOfA5> groupOfA5List = new ArrayList<>();
        Object redisResult = redisTemplate.opsForValue().get(username);
        if(redisResult != null){
            logger.info("在redis中查找到所有的网元");
            groupOfA5List = JSON.parseArray(redisResult.toString(), GroupOfA5.class);
            return groupOfA5List;
        }

        //A5 接口
        String findGroupUrl = url + "/iotx/netElement/combination/findAll";
        //获取所有的分组数据
        String groupAll = serverCallComponent.executeGet(findGroupUrl);
        JSONObject jsonGroup = JSON.parseObject(groupAll);
        String groupOfCompany = "";
        String groupByUsername = "";
        if (username.equals("admin")) {
            //辉浩
            groupByUsername = jsonGroup.getString("INDIVIDUAL");
            groupOfCompany = JSON.parseObject(groupByUsername).getString("and003");
        } else {
            //根据不同账号获取组信息，admin用于测试服
            groupByUsername = jsonGroup.getString("ENTERPRISE");
            //获取账号的网元名称组信息
            groupOfCompany = JSON.parseObject(groupByUsername).getString(username);
        }

        groupOfA5List = JSON.parseArray(groupOfCompany, GroupOfA5.class);

        // 获取到账号的所有网元,存储到redis
        //添加一个 key
        logger.info("存储网元到redis");
        redisTemplate.opsForValue().set(username, groupOfCompany, 2, TimeUnit.MINUTES);
        return groupOfA5List;
    }

    /***
     * 判断同一个excel中是否有重复的序列号
     * @param table
     */
    private void checkMulitSN(Table<Integer, String, Object> table) {
        Map<Integer, Object> serialNoMap = table.columnMap().get("序列号");
        int size = table.rowKeySet().size();
        for (int i = 0; i < size; i++) {
            Object serialNoI = serialNoMap.get(i);
            for (int j = i + 1; j < size; j++) {
                Object serialNoJ = serialNoMap.get(j);
                if (Objects.equals(serialNoI, serialNoJ)) {
                    throw new CustomRunTimeException(
                            MessageFormat.format(i18nComponent.getMessage("device.serialNo.exist.excel"), i + 1, j + 1));
                }
            }
        }
    }


    /**
    * 根据id删除device
    * @since  2018/6/6 15:35
    * @author 倪文骅
    * @param id
    * @return void
    */
    @Override
    public boolean deleteDevice(String id) {
        if (StringUtils.isNotBlank(id)) {
            String[] ids = id.split("-");
            for (int i = 0; i < ids.length; i++) {
                //获取device对象
            Device device = deviceDao.findOne(Long.parseLong(ids[i]));
//                Long devCategoryId = device.getDevCategory().getId();
//                Long projectId = device.getProject().getId();
//                //Long districtId = device.getDistrict().getId();
//                if(devCategoryId !=null){
//                    //DevCategory对象删除
//                    devCategoryService.delete(devCategoryId);
//                }
//                if(projectId !=null){
//                    //获取Project对象并删除
//                    projectService.delete(projectId);
//                }
                if(device != null && !CollectionUtils.isEmpty(device.getRepairDetailList())){
                    return false;
                }
                //最后再删除device
                deviceDao.delete(Long.parseLong(ids[i]));
            }
            saveLog("设备", Oplog.Operation.DEL, ids.length + "个");
        }
        return true;
    }

    /**
     * 根据设备种类获取设备信息
     * @since  2018/7/5 14:28
     * @author 倪文骅
     * @param devCategoryId 设备种类
     * @return com.alibaba.fastjson.JSONArray
     */
    @Override
    public List<Device> findByDevCategoryId(Long devCategoryId) {
        return deviceDao.findByDevCategoryId(devCategoryId);
    }
}
