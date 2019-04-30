package com.anosi.asset.service;

import com.anosi.asset.model.jpa.SystemInfo;

public interface SystemInfoService extends BaseJPAService<SystemInfo> {


    SystemInfo getSystemInfo();
}
