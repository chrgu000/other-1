package com.anosi.asset.bean;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 传感器数据导出bean
 * @Author: zxgang
 * @Date: 2018/12/26 16:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorBean {
    @Excel(name = "采集时间")
    private String collectTime;
    @Excel(name = "数值")
    private String value;
}
