package org.firecode.netpx.server.http.support;

import io.netty.handler.codec.http.FullHttpRequest;
/**
 * @author ChiangFire
 */
public interface RequestMappingHandler<T> {
	
    T handler(FullHttpRequest req) throws Exception;
}
