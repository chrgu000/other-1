package com.anosi.asset.model.jpa;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 设备种类
 * @Author: zxgang
 * @Date: 2018/12/27 16:57
 */
@Entity
@Table
public class CategoryType extends BaseEntity {

    private String name;

    private String code;

    @JSONField(serialize=false)
    private DevCategory devCategory;

    @JSONField(serialize=false)
    private List<StartDetail> startDetailList = new ArrayList<>();

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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "categoryType")
    public DevCategory getDevCategory() {
        return devCategory;
    }

    public void setDevCategory(DevCategory devCategory) {
        this.devCategory = devCategory;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categoryType")
    public List<StartDetail> getStartDetailList() {
        return startDetailList;
    }

    public void setStartDetailList(List<StartDetail> startDetailList) {
        this.startDetailList = startDetailList;
    }
}
