package com.anosi.asset.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 类名 GroupOfA5
 * 用来转换Json对象数据存储和获取的组接口对象
 * 作者 倪文骅
 * 日期 2018-07-18 2018-7-18 15:11:04
 */
public class GroupOfA5 implements Serializable {
    private Long id;//组id
    private String name;//组的名字
    private List<IotxInfo> iotxList;//网元list
    private String account;//账户号
    private String role;// ENTERPRISE INDIVIDUAL ADMIN
    private int onLineNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IotxInfo> getIotxList() {
        return iotxList;
    }

    public void setIotxList(List<IotxInfo> iotxList) {
        this.iotxList = iotxList;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getOnLineNum() {
        return onLineNum;
    }

    public void setOnLineNum(int onLineNum) {
        this.onLineNum = onLineNum;
    }
}
