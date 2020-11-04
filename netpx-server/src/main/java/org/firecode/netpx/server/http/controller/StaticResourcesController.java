package org.firecode.netpx.server.http.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.firecode.netpx.server.Server;
import org.firecode.netpx.server.http.PathMatchingResourceClassResolver;
import org.firecode.netpx.server.http.support.MediaType;
import org.firecode.netpx.server.http.support.RequestMapping;
import org.firecode.netpx.server.http.support.RequestMappingHandler;
import org.firecode.netpx.server.http.support.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Static resources controller（静态资源控制器）
 * @author ChiangFire
 */
@RequestMapping(urls={StaticResourcesController.ROOT_STATIC_RESOURCES,PathMatchingResourceClassResolver.DEFAULT_PATH_SEPARATOR},method = RequestMethod.GET,produce = MediaType.STATIC_RESOURCES)
public class StaticResourcesController implements RequestMappingHandler<byte[]> {
	
	private static final Logger LOG = LoggerFactory.getLogger(StaticResourcesController.class);
	
	public static final String ROOT_STATIC_RESOURCES = "/static";
	
	private static final String STATIC_RESOURCES_DIR = "webapp";
	
	private static final String INDEX_HTML_URI = STATIC_RESOURCES_DIR + "/index.html";
	
	private static final ClassLoader SERVER_CLASS_LOADER = Server.class.getClassLoader();
	
	private static final LoadingCache<String, byte[]> RESOURCES_CACHE = CacheBuilder.newBuilder().maximumSize(150).expireAfterWrite(0, TimeUnit.MINUTES).build(new CacheLoader<String,byte[]>() {
        public byte[] load(String uri) throws Exception {
			try (InputStream resourceInputStream = SERVER_CLASS_LOADER.getResourceAsStream(uri.replaceFirst("[\\?].*", ""));){
    			if(null != resourceInputStream) {
    				try(ByteArrayOutputStream bais = new ByteArrayOutputStream();) {
    					byte[] bytes = new byte[1024];
    					int readLength = 0;
    					while ((readLength = resourceInputStream.read(bytes)) != -1) {
    						bais.write(bytes,0,readLength);
    					}
    					return bais.toByteArray();
    				}
    			}
    		} catch(Exception e) {
    			LOG.error("Static resources '"+uri+"' load fail", e);
    		}
    		return new byte[0];
        }
    });
	
	@Override
	public byte[] handler(FullHttpRequest req) throws Exception {
		String uri = req.uri().replace(ROOT_STATIC_RESOURCES,STATIC_RESOURCES_DIR);
		if(PathMatchingResourceClassResolver.DEFAULT_PATH_SEPARATOR.equals(uri) || STATIC_RESOURCES_DIR.equals(uri)) {
			uri = INDEX_HTML_URI;
		}
		return RESOURCES_CACHE.get(uri);
	}
}
