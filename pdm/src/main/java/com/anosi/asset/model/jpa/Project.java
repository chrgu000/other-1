package com.anosi.asset.model.jpa;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.search.annotations.*;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/***
 * 项目
 * 
 * @author jinyao
 *
 */
@Entity
@Table(name = "project")
@Indexed
@Analyzer(impl = IKAnalyzer.class)
public class Project extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8182240180903969356L;

	@Transient
	// 为了在百度地图上显示点，还需要存储百度坐标
	private Double baiduLongitude;// 经度


	@Transient
	private Double baiduLatitude;// 纬度

	@Field
	private String name;// 企业名称

	@Field(analyze = Analyze.NO)
	private String number;// 项目编号

	@Field
	private String location;// 企业地址

	@JSONField(serialize=false)  
	@ContainedIn
	private List<StartDetail> startDetailList = new ArrayList<>();

	@JSONField(serialize=false)  
	@ContainedIn
	private List<Device> deviceList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(unique = true, nullable = false)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "project")
	public List<StartDetail> getStartDetailList() {
		return startDetailList;
	}

	public void setStartDetailList(List<StartDetail> startDetailList) {
		this.startDetailList = startDetailList;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "project", targetEntity = Device.class)
	public List<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

	public Double getBaiduLongitude() {
		return baiduLongitude;
	}

	public void setBaiduLongitude(Double baiduLongitude) {
		this.baiduLongitude = baiduLongitude;
	}

	public Double getBaiduLatitude() {
		return baiduLatitude;
	}

	public void setBaiduLatitude(Double baiduLatitude) {
		this.baiduLatitude = baiduLatitude;
	}
}
