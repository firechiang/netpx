package org.firecode.netpx.server.http.domain;

import java.util.Date;

/**
 * @author ChiangFire
 */
public class User {
	/**
	 * User name
	 */
	private String username;
	
	/**
	 * User access key
	 */
	private String accessKey;
	
	/**
	 * User secondary domain name
	 */
	private String secondaryDomain;
	
	/**
	 * User local service port
	 */
	private Integer localPort;
	
	/**
	 * User is disable
	 */
	private Boolean disable;
	
	/**
	 * User add date time
	 */
	private Date createTime;
	/**
	 * User remark
	 */
	private String remark;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecondaryDomain() {
		return secondaryDomain;
	}

	public void setSecondaryDomain(String secondaryDomain) {
		this.secondaryDomain = secondaryDomain;
	}

	public Integer getLocalPort() {
		return localPort;
	}

	public void setLocalPort(Integer localPort) {
		this.localPort = localPort;
	}

	public Boolean getDisable() {
		return disable;
	}

	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
