package org.firecode.netpx.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author ChiangFire
 */
public abstract class ConfigPropertyResolver {
	
	public static final String CONFIG_FOLDER = "config";
	
	private Properties properties;
	
	public ConfigPropertyResolver(String configFileName) {
		String filePath = String.join(File.separator,CONFIG_FOLDER,configFileName);
		try (InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);){
			if(propertiesInputStream == null) {
				throw new FileNotFoundException(String.join(configFileName,"config file "," does not exist."));
			}
			this.properties = new Properties();
			this.properties.load(propertiesInputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getString(String key) {
		return properties.getProperty(key);
	}
	
	public Integer getInteger(String key) {
		return Integer.parseInt(getString(key));
	}
	
	public Long getLong(String key) {
		return Long.parseLong(getString(key));
	}
	
	public Boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}
}
