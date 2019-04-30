package com.anosi.asset.model.jpa;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.anosi.asset.util.DateFormatUtil;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.Date;

/**
 * 日常维护表
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.model.jpa
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 14:55
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 14:55
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Entity
@Table(name = "dailyMaintenance")
public class DailyMaintenance extends BaseEntity{

    @Excel(name = "主类别")
    private String mainCategory;

    @Excel(name = "细分类别")
    private String subCategory;

    @Excel(name = "物料编码")
    private String code;

    @Excel(name = "代号")
    private String mark;

    @Excel(name = "生产日期")
    private Date produceTime;

    //Excel中存放的是produceTime，最终转换Date格式后用produceDate接收
    private Date produceDate;

//    private String examineCycle;
//
//    private String changeCycle;

    //检查预警状态
    private String checkRemind;

    //更换预警状态
    private String exchangeRemind;

    @Excel(name = "上次检查维护时间")
    private Date lastCheckMaintainTime;

    //Excel中存放的是lastCheckMaintainTime，最终转换Date格式后用lastCheckMaintainDate接收
    private Date lastCheckMaintainDate;

    @Excel(name = "上次更换维护时间")
    private Date lastExchangeMaintainTime;

    //Excel中存放的是lastExchangeMaintainTime，最终转换Date格式后用lastExchangeMaintainDate接收
    private Date lastExchangeMaintainDate;

//    @Excel(name = "下次维护时间")
//    private String nextMaintainDate;

    @Excel(name = "维护人")
    private String maintainer;

    @Excel(name = "检测周期-年")
    private int checkYear = 0;

    @Excel(name = "检测周期-月")
    private int checkMonth = 0;

    @Excel(name = "检测周期-日")
    private int  checkDay = 0;// 设备检测周期

    @Excel(name = "更换周期-年")
    private int exchangeYear = 0;

    @Excel(name = "更换周期-月")
    private int exchangeMonth = 0;

    @Excel(name = "更换周期-日")
    private int  exchangeDay = 0;// 设备更换周期

    @Excel(name = "检查预警周期-年")
    private int remindCheckYear = 0;

    @Excel(name = "检查预警周期-月")
    private int remindCheckMonth = 0;

    @Excel(name = "检查预警周期-日")
    private int remindCheckDay = 0;// 提前预警年数,月数,天数

    @Excel(name = "更换预警周期-年")
    private int remindExchangeYear = 0;

    @Excel(name = "更换预警周期-月")
    private int remindExchangeMonth = 0;

    @Excel(name = "更换预警周期-日")
    private int remindExchangeDay = 0;// 更换预警年数,月数,天数

    private int remainderCheckDay = 0;// 距离检测时间还剩下的天数，每日更新

    private int remainderExchangeDay = 0;// 距离更换时间还剩下的天数，每日更新

    private CheckRemindStatus checkRemindStatus = CheckRemindStatus.NORMAL;

    private ExchangeRemindStatus exchangeRemindStatus = ExchangeRemindStatus.NORMAL;


//    @IndexedEmbedded
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    public Device getDevice(){return  device;}

    public void setDevice(Device device){this.device=device;}

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Date getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(Date produceDate) {
        this.produceDate = produceDate;
    }


    public Date getLastCheckMaintainDate() {
        return lastCheckMaintainDate;
    }

    public void setLastCheckMaintainDate(Date lastCheckMaintainDate) {
        this.lastCheckMaintainDate = lastCheckMaintainDate;
    }

    public Date getLastExchangeMaintainDate() {
        return lastExchangeMaintainDate;
    }

    public void setLastExchangeMaintainDate(Date lastExchangeMaintainDate) {
        this.lastExchangeMaintainDate = lastExchangeMaintainDate;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public int getCheckYear() {
        return checkYear;
    }

    public void setCheckYear(int checkYear) {
        this.checkYear = checkYear;
    }

    public int getCheckMonth() {
        return checkMonth;
    }

    public void setCheckMonth(int checkMonth) {
        this.checkMonth = checkMonth;
    }

    public int getCheckDay() {
        return checkDay;
    }

    public void setCheckDay(int checkDay) {
        this.checkDay = checkDay;
    }

    public int getExchangeYear() {
        return exchangeYear;
    }

    public void setExchangeYear(int exchangeYear) {
        this.exchangeYear = exchangeYear;
    }

    public int getExchangeMonth() {
        return exchangeMonth;
    }

    public void setExchangeMonth(int exchangeMonth) {
        this.exchangeMonth = exchangeMonth;
    }

    public int getExchangeDay() {
        return exchangeDay;
    }

    public void setExchangeDay(int exchangeDay) {
        this.exchangeDay = exchangeDay;
    }

    public int getRemindCheckYear() {
        return remindCheckYear;
    }

    public void setRemindCheckYear(int remindCheckYear) {
        this.remindCheckYear = remindCheckYear;
    }

    public int getRemindCheckMonth() {
        return remindCheckMonth;
    }

    public void setRemindCheckMonth(int remindCheckMonth) {
        this.remindCheckMonth = remindCheckMonth;
    }

    public int getRemindCheckDay() {
        return remindCheckDay;
    }

    public void setRemindCheckDay(int remindCheckDay) {
        this.remindCheckDay = remindCheckDay;
    }

    public int getRemainderCheckDay() {
        return remainderCheckDay;
    }

    public void setRemainderCheckDay(int remainderCheckDay) {
        this.remainderCheckDay = remainderCheckDay;
    }

    public int getRemindExchangeYear() {
        return remindExchangeYear;
    }

    public void setRemindExchangeYear(int remindExchangeYear) {
        this.remindExchangeYear = remindExchangeYear;
    }

    public int getRemindExchangeMonth() {
        return remindExchangeMonth;
    }

    public void setRemindExchangeMonth(int remindExchangeMonth) {
        this.remindExchangeMonth = remindExchangeMonth;
    }

    public int getRemindExchangeDay() {
        return remindExchangeDay;
    }

    public void setRemindExchangeDay(int remindExchangeDay) {
        this.remindExchangeDay = remindExchangeDay;
    }

    public int getRemainderExchangeDay() {
        return remainderExchangeDay;
    }

    public void setRemainderExchangeDay(int remainderExchangeDay) {
        this.remainderExchangeDay = remainderExchangeDay;
    }

    public String getCheckRemind() {
        return checkRemind;
    }

    public void setCheckRemind(String checkRemind) {
        this.checkRemind = checkRemind;
    }

    public String getExchangeRemind() {
        return exchangeRemind;
    }

    public void setExchangeRemind(String exchangeRemind) {
        this.exchangeRemind = exchangeRemind;
    }

    public CheckRemindStatus getCheckRemindStatus() {
        return checkRemindStatus;
    }

    public void setCheckRemindStatus(CheckRemindStatus checkRemindStatus) {
        this.checkRemindStatus = checkRemindStatus;
    }

    public ExchangeRemindStatus getExchangeRemindStatus() {
        return exchangeRemindStatus;
    }

    public void setExchangeRemindStatus(ExchangeRemindStatus exchangeRemindStatus) {
        this.exchangeRemindStatus = exchangeRemindStatus;
    }

    public Date getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(Date produceTime) {
        this.produceTime = produceTime;
    }

    public Date getLastCheckMaintainTime() {
        return lastCheckMaintainTime;
    }

    public void setLastCheckMaintainTime(Date lastCheckMaintainTime) {
        this.lastCheckMaintainTime = lastCheckMaintainTime;
    }

    public Date getLastExchangeMaintainTime() {
        return lastExchangeMaintainTime;
    }

    public void setLastExchangeMaintainTime(Date lastExchangeMaintainTime) {
        this.lastExchangeMaintainTime = lastExchangeMaintainTime;
    }


    public DailyMaintenance() {
    }

    public DailyMaintenance(String mainCategory, String subCategory, String code, String mark, Date produceDate,
                            Date lastCheckMaintainDate, Date lastExchangeMaintainDate, String maintainer,
                            int checkYear, int checkMonth, int checkDay,
                            int exchangeYear, int exchangeMonth, int exchangeDay,
                            int remindCheckYear, int remindCheckMonth, int remindCheckDay,
                            int remindExchangeYear, int remindExchangeMonth, int remindExchangeDay,
                            Device device) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.code = code;
        this.mark = mark;
        this.produceDate = produceDate;
        this.lastCheckMaintainDate = lastCheckMaintainDate;
        this.lastExchangeMaintainDate = lastExchangeMaintainDate;
        this.maintainer = maintainer;
        this.checkYear = checkYear;
        this.checkMonth = checkMonth;
        this.checkDay = checkDay;
        this.exchangeYear = exchangeYear;
        this.exchangeMonth = exchangeMonth;
        this.exchangeDay = exchangeDay;
        this.remindCheckYear = remindCheckYear;
        this.remindCheckMonth = remindCheckMonth;
        this.remindCheckDay = remindCheckDay;
        this.remindExchangeYear = remindExchangeYear;
        this.remindExchangeMonth = remindExchangeMonth;
        this.remindExchangeDay = remindExchangeDay;
        this.device = device;
    }

    /**
    * 判断是否需要检查预警
     * 先将生产时间上叠加周期，利用递归叠加到当前时间所处的周期，然后周期减去预警时间，再和当前时间比较
    * @since  2018/6/21 8:46
    * @author 倪文骅
    * @param
    * @return boolean
    */
    @Transient
    public boolean needCheckRemind() {
        if (checkYear != 0 || checkMonth != 0 || checkDay != 0) {
            // 获取当前日期
            Date nowDate = new Date();
            //根据用户上次维护时间以及用户填写的维护检查周期获取相应具体维护检查年月日
            Date cycleDate = checkCycle(lastCheckMaintainDate == null ? produceDate : lastCheckMaintainDate);
            //获取距离检查维护日期还剩多少天
            remainderCheckDay = DateFormatUtil.daysBetween(nowDate, cycleDate);

            //根据用户填写的提前预警年月日获取具体提醒的日期
            Date remindTime = DateFormatUtil.getBeforeDayTime(
                                DateFormatUtil.getBeforeMonthTime(
                                    DateFormatUtil.getBeforeYearTime(cycleDate, this.remindCheckYear), this.remindCheckMonth),this.remindCheckDay);

            // 将提醒日期和当前时间比较，显示是否预警
            if (nowDate.getTime() < remindTime.getTime()) {
                checkRemindStatus = CheckRemindStatus.NORMAL;//正常
            } else if (nowDate.getTime() > cycleDate.getTime()) {
                checkRemindStatus = CheckRemindStatus.OVER;// 超时
            } else {
                checkRemindStatus = CheckRemindStatus.REMIND;//提醒
            }

            // 如果提前预警的年月日和当前时间的年月日相等
            if (DateFormatUtil.compareYear(nowDate, remindTime) && DateFormatUtil.compareMonth(nowDate, remindTime)
                    && DateFormatUtil.compareDay(nowDate, remindTime)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否需要更换预警
     * 先将生产时间上叠加周期，利用递归叠加到当前时间所处的周期，然后周期减去预警时间，再和当前时间比较
     * @since  2018/6/21 8:46
     * @author 倪文骅
     * @param
     * @return boolean
     */
    @Transient
    public boolean needExchangeRemind() {
        if (exchangeYear != 0 || exchangeMonth != 0 || exchangeDay != 0) {
            // 获取当前日期
            Date nowDate = new Date();
            //根据用户上次更换时间以及用户填写的维护更换周期获取相应具体维护更换年月日
            Date cycleDate = exchangeCycle(lastExchangeMaintainDate == null ? produceDate : lastExchangeMaintainDate);
            //获取距离维护日期还剩多少天
            remainderExchangeDay = DateFormatUtil.daysBetween(nowDate, cycleDate);

            //根据用户填写的提前预警年月日获取具体提醒的日期
            Date remindTime = DateFormatUtil.getBeforeDayTime(
                    DateFormatUtil.getBeforeMonthTime(
                            DateFormatUtil.getBeforeYearTime(cycleDate, this.remindExchangeYear), this.remindExchangeMonth),this.remindExchangeDay);

            // 将提醒日期和当前时间比较，显示是否更换预警
            if (nowDate.getTime() < remindTime.getTime()) {
                exchangeRemindStatus = ExchangeRemindStatus.NORMAL;//正常
            } else if (nowDate.getTime() > cycleDate.getTime()) {
                exchangeRemindStatus = ExchangeRemindStatus.OVER;// 超时
                return true;
            } else {
                exchangeRemindStatus = ExchangeRemindStatus.REMIND;//提醒
                return true;
            }

            // 如果提前预警的年月日和当前时间的年月日相等
            if (DateFormatUtil.compareYear(nowDate, remindTime) && DateFormatUtil.compareMonth(nowDate, remindTime)
                    && DateFormatUtil.compareDay(nowDate, remindTime)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /***
     * 根据用户填写的维护检查周期年月日获取相应检查日期
     *
     * @param cycleDate
     * @return
     */
    @Transient
    private Date checkCycle(Date cycleDate) {
        cycleDate = DateFormatUtil.getLaterDayTime(DateFormatUtil.getLaterMonthTime(
                DateFormatUtil.getLaterYearTime(cycleDate, this.checkYear), this.checkMonth), this.checkDay);
        return cycleDate;
    }

    /**
    * 根据用户填写的更换周期年月日获取相应更换日期
    * @since  2018/6/21 9:07
    * @author 倪文骅
    * @param cycleDate
    * @return java.util.Date
    */
    @Transient
    private Date exchangeCycle(Date cycleDate){
        cycleDate = DateFormatUtil.getLaterDayTime(DateFormatUtil.getLaterMonthTime(DateFormatUtil.getLaterYearTime(cycleDate,this.exchangeYear),this.exchangeMonth),this.exchangeDay);
        return cycleDate;
    }

    /***
     * 获取检查维护周期
     *
     * @return
     */
    @Transient
    public String getExamineCycle() {
        StringBuilder sb = new StringBuilder();
        if (checkYear != 0) {
            sb.append(checkYear + "年");
        }
        if (checkMonth != 0) {
            sb.append(checkMonth + "个月");
        }
        if (checkDay != 0) {
            sb.append(checkDay + "日");
        }

        return sb.toString();
    }
    /***
     * 获取更换维护周期
     *
     * @return
     */
    @Transient
    public String getChangeCycle() {
        StringBuilder sb = new StringBuilder();
        if (exchangeYear != 0) {
            sb.append(exchangeYear + "年");
        }
        if (exchangeMonth != 0) {
            sb.append(exchangeMonth + "个月");
        }
        if (exchangeDay != 0) {
            sb.append(exchangeDay + "日");
        }

        return sb.toString();
    }

    /***
     * 获取维护检查预警周期
     *
     * @return
     */
    @Transient
    public String getCheckRemindDate() {
        StringBuilder sb = new StringBuilder();
        if (remindCheckYear != 0) {
            sb.append(remindCheckYear + "年");
        }
        if (remindCheckMonth != 0) {
            sb.append(remindCheckMonth + "个月");
        }
        if (remindCheckDay != 0) {
            sb.append(remindCheckDay + "日");
        }
        if (sb.length() != 0) {
            sb.insert(0, "提前");
        }
        return sb.toString();
    }
    /***
     * 获取维护更换预警周期
     *
     * @return
     */
    @Transient
    public String getExchangeRemindDate() {
        StringBuilder sb = new StringBuilder();
        if (remindExchangeYear != 0) {
            sb.append(remindExchangeYear + "年");
        }
        if (remindExchangeMonth != 0) {
            sb.append(remindExchangeMonth + "个月");
        }
        if (remindExchangeDay != 0) {
            sb.append(remindExchangeDay + "日");
        }
        if (sb.length() != 0) {
            sb.insert(0, "提前");
        }
        return sb.toString();
    }


    public static enum CheckRemindStatus {
        NORMAL, REMIND, OVER
    }

    public static enum ExchangeRemindStatus {
        NORMAL, REMIND, OVER
    }
}
