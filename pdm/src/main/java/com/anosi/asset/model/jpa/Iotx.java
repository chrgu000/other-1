package com.anosi.asset.model.jpa;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;

@Entity
@Table(name="iotx")
public class Iotx extends BaseEntity {

	/**
	 * 网元设备表，只记录网元序列号对应数据中心序列号
	 */
	private static final long serialVersionUID = 2865898828588906144L;

	private String serial_no;//序列号

	private String group_id;//组id

	@JSONField(serialize=false)
	private Device device;

	@Column(unique = true, nullable = false)
	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	@Column(unique = true,nullable = false)
	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	//	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "iotxList", targetEntity = Device.class)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Device.class)
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
}
