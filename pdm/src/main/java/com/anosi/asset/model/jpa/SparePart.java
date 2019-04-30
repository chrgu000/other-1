package com.anosi.asset.model.jpa;

import cn.afterturn.easypoi.excel.annotation.Excel;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 备品备件
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.model.jpa
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2018/4/8 10:48
 * @UpdateUser: jinyao
 * @UpdateDate: 2018/4/8 10:48
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Entity
@Table(name = "sparePart")
public class SparePart extends BaseEntity {

    @Excel(name = "存货编码")
    private String code;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "存货名称")
    private String name;

    @Excel(name = "存货数量")
    private Integer num;

    @Excel(name = "去年消耗量")
    private Integer lastYearUsed;

    @Excel(name = "建议库存")
    private Integer suggest;

    @Excel(name = "需补充量")
    private Integer needSupplement;

    /*@Excel(name = "提醒状态")
    private String remind;*/

//    @IndexedEmbedded
    private Device device;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getLastYearUsed() {
        return lastYearUsed;
    }

    public void setLastYearUsed(Integer lastYearUsed) {
        this.lastYearUsed = lastYearUsed;
    }

    public Integer getSuggest() {
        return suggest;
    }

    public void setSuggest(Integer suggest) {
        this.suggest = suggest;
    }

    public Integer getNeedSupplement() {
        return needSupplement;
    }

    public void setNeedSupplement(Integer needSupplement) {
        this.needSupplement = needSupplement;
    }

    /*public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }*/


    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "SparePart{" +
                "code='" + code + '\'' +
                ", specification='" + specification + '\'' +
                ", name='" + name + '\'' +
                ", num=" + num +
                ", lastYearUsed=" + lastYearUsed +
                ", suggest=" + suggest +
                ", needSupplement=" + needSupplement +
//                ", remind='" + remind + '\'' +
                '}';
    }
}
