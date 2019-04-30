package com.anosi.asset.model.jpa;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.anosi.asset.util.DateFormatUtil;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;

/**
 * 仪表校验表
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.model.jpa
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 13:57
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 13:57
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Entity
@Table(name = "instrument")
public class Instrument extends BaseEntity{

    @Excel(name = "仪表")
    private String name;

    @Excel(name = "物料编码")
    private String code;

    @Excel(name = "代号")
    private String mark;

    @Excel(name = "生产日期")
    private Date produceTime;
    //Excel中存放的是produceTime，最终转换Date格式后用produceDate接收
    private Date produceDate;

//    @Excel(name = "校验周期")
//    private String checkOutCycle;
    @Excel(name = "校验周期-年")
    private int checkYear = 0;

    @Excel(name = "校验周期-月")
    private int checkMonth = 0;

    @Excel(name = "校验周期-日")
    private int  checkDay = 0;

    @Excel(name = "上次校验时间")
    private Date lastCheckOutTime;
    //Excel中存放的是lastCheckOutTime，最终转换Date格式后用lastCheckOutDate接收
    private Date lastCheckOutDate;

//    @Excel(name = "下次校验时间")
//    private String nextCheckOutDate;

    @Excel(name = "预警周期-年")
    private int remindCheckYear = 0;

    @Excel(name = "预警周期-月")
    private int remindCheckMonth = 0;

    @Excel(name = "预警周期-日")
    private int remindCheckDay = 0;// 提前预警年数,月数,天数

//    @Excel(name = "提醒状态")
//    private String remind;

    @Excel(name = "备注")
    private String note;

    private int remainderCheckDay = 0;// 距离检测时间还剩下的天数，每日更新

    private CheckRemindStatus checkRemindStatus = CheckRemindStatus.NORMAL;

//    @IndexedEmbedded
    private Device device;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public Date getLastCheckOutDate() {
        return lastCheckOutDate;
    }

    public void setLastCheckOutDate(Date lastCheckOutDate) {
        this.lastCheckOutDate = lastCheckOutDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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

    public CheckRemindStatus getCheckRemindStatus() {
        return checkRemindStatus;
    }

    public void setCheckRemindStatus(CheckRemindStatus checkRemindStatus) {
        this.checkRemindStatus = checkRemindStatus;
    }

    public Date getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(Date produceTime) {
        this.produceTime = produceTime;
    }

    public Date getLastCheckOutTime() {
        return lastCheckOutTime;
    }

    public void setLastCheckOutTime(Date lastCheckOutTime) {
        this.lastCheckOutTime = lastCheckOutTime;
    }

    public Instrument() {
    }

    public Instrument(String name, String code, String mark,
                      Date produceDate, int checkYear, int checkMonth, int checkDay,
                      Date lastCheckOutDate, int remindCheckYear, int remindCheckMonth, int remindCheckDay, String note, Device device) {
        this.name = name;
        this.code = code;
        this.mark = mark;
        this.produceDate = produceDate;
        this.checkYear = checkYear;
        this.checkMonth = checkMonth;
        this.checkDay = checkDay;
        this.lastCheckOutDate = lastCheckOutDate;
        this.remindCheckYear = remindCheckYear;
        this.remindCheckMonth = remindCheckMonth;
        this.remindCheckDay = remindCheckDay;
        this.note = note;
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
            Date cycleDate =  checkCycle(lastCheckOutDate  == null ? produceDate : lastCheckOutDate);
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

    public static enum CheckRemindStatus {
        NORMAL, REMIND, OVER
    }
}
