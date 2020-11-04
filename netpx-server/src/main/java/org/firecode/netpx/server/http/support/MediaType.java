package org.firecode.netpx.server.http.support;
/**
 * @author ChiangFire
 */
public enum MediaType {
	
	TEXT_PLAIN_UTF8("text/plain; charset=UTF-8"),
	
	TEXT_HTML_UTF8("text/html; charset=UTF-8"),
	
	APPLICATION_JAVASCRIPT_UTF8("application/javascript; charset=UTF-8"),
	
	TEXT_CSS_UTF8("text/css; charset=UTF-8"),
	
	APPLICATION_JSON_UTF8("application/json; charset=UTF-8"),
	
	APPLICATION_FONT_WOFF("application/font-woff; charset=UTF-8"),
	
	APPLICATION_FONT_TTF("application/font-ttf; charset=UTF-8"),
	
	BYTES("bytes"),
	// 静态资源
	STATIC_RESOURCES("");
	
	private String value;
	
	MediaType(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}
