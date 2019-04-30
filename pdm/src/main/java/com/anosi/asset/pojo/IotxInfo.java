package com.anosi.asset.pojo;

import java.io.Serializable;

/**
 * 类名 IotxInfo
 * 类型描述
 * 作者 20180329
 * 日期
 */
public class IotxInfo implements Serializable {
    private Long id;
    private String serialNo;
    private String account;
    private String iotStatusType;
    private boolean onLine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIotStatusType() {
        return iotStatusType;
    }

    public void setIotStatusType(String iotStatusType) {
        this.iotStatusType = iotStatusType;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
