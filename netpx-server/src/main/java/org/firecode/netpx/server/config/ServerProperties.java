package org.firecode.netpx.server.config;

import java.io.File;

import org.firecode.netpx.common.ConfigPropertyResolver;

/**
 * @author ChiangFire
 */
public class ServerProperties extends ConfigPropertyResolver {
	
	private static final ServerProperties serverProperties = new ServerProperties("server.properties");
	
	/**
	 * Server build port（netpx 绑定的端口）
	 */
	private Integer buildPort;
	
	/**
	 * Server build ip（netpx 绑定的ip）
	 */
	private String buildHost;
	
	/**
	 * Server build domain name（netpx 绑定的域名）
	 */
	private String buildName;
	
	/**
	 * Enabled admin manager（是否开启后台管理服务）
	 */
	private Boolean adminEnabled;
	/**
	 * Admin server domain name（后台管理服务域名）
	 */
	private String adminDomain;
	/**
	 * Admin server SSL enabled（后台管理服务是否启用SSL）
	 */
	private Boolean adminSSLEnabled;
	/**
	 * Admin server SSL certificate（后台管理服务SSL证书）
	 */
	private File adminSSLCert;
	/**
	 * Admin server SSL key（后台管理服务SSL Key）
	 */
	private File adminSSLKey;
	
	public Integer getBuildPort() {
		return buildPort;
	}
	
	public String getBuildHost() {
		return buildHost;
	}

	public String getBuildName() {
		return buildName;
	}

	public Boolean getAdminEnabled() {
		return adminEnabled;
	}
	
	public String getAdminDomain() {
		return adminDomain;
	}

	public Boolean getAdminSSLEnabled() {
		return adminSSLEnabled;
	}
	
	public File getAdminSSLCert() {
		return adminSSLCert;
	}

	public File getAdminSSLKey() {
		return adminSSLKey;
	}

	private ServerProperties(String configFileName) {
		super(configFileName);
		adminEnabled = getBoolean("server.admin.enabled");
		buildPort = getInteger("server.build.port");
		buildHost = getString("server.build.host");
		buildName = getString("server.build.name");
		adminDomain = getString("server.admin.build.name");
		adminSSLEnabled = getBoolean("server.admin.ssl.enabled");
		if(adminSSLEnabled) {
			adminSSLCert  = new File(getString("server.admin.ssl.cert"));
			adminSSLKey = new File(getString("server.admin.ssl.key"));
		}
	}
	
	public static final ServerProperties getInstance() {
		return serverProperties;
	}
}
