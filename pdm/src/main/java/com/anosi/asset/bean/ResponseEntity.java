package com.anosi.asset.bean;


import lombok.Data;

/**
* 相应bean
* @since  2018/7/13 16:34
* @author 倪文骅
* @param
* @return
*/
@Data
public class ResponseEntity {

    private int status;// 0是成功，1失败，2表示401

    private String msg;// 错误信息

    private Object data;// 返回数据

    private Object describe;// 错误描述

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getDescribe() {
        return describe;
    }

    public void setDescribe(Object describe) {
        this.describe = describe;
    }
}
