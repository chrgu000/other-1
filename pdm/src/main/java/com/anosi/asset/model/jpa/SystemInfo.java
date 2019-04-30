package com.anosi.asset.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "systemInfo")
public class SystemInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7555992276120785280L;
	
	private String name;//系统名称
	private String version;//系统版本号
	private String loginImg_url;//登录页login图片url
	private String indexImg_url;//首页logo图片url

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginImg_url() {
		return loginImg_url;
	}

	public void setLoginImg_url(String loginImg_url) {
		this.loginImg_url = loginImg_url;
	}

	public String getIndexImg_url() {
		return indexImg_url;
	}

	public void setIndexImg_url(String indexImg_url) {
		this.indexImg_url = indexImg_url;
	}
}
