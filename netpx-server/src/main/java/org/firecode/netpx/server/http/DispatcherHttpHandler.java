package org.firecode.netpx.server.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.firecode.netpx.server.Server;
import org.firecode.netpx.server.http.controller.StaticResourcesController;
import org.firecode.netpx.server.http.support.MediaType;
import org.firecode.netpx.server.http.support.RequestMapping;
import org.firecode.netpx.server.http.support.RequestMappingHandler;
import org.firecode.netpx.server.http.support.RequestMappingHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author ChiangFire
 */
public class DispatcherHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private static final String KEEP_ALIVE_NAME = "Keep-Alive";
	
	private static final String KEEP_ALIVE_TIME_OUT = "timeout=30";
	
	private static final Logger LOG = LoggerFactory.getLogger(DispatcherHttpHandler.class);
	
    private static final Map<String,RequestMappingHandlerAdapter> REQUEST_MAPPING_MAP = new ConcurrentHashMap<>();
    
    static {
		PathMatchingResourceClassResolver p = new PathMatchingResourceClassResolver();
		Set<Class<?>> clazzs = p.doFindMatchingFileClasses(Server.getPackageName());
		for(Class<?> clazz : clazzs) {
			if(RequestMappingHandler.class.isAssignableFrom(clazz) && !RequestMappingHandler.class.equals(clazz)) {
				try {
				    RequestMappingHandler<?> requestMappingHandler = (RequestMappingHandler<?>)clazz.getConstructor().newInstance();
					registerRequestMappingHandler(requestMappingHandler);
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				    LOG.error("Initialization failed "+clazz.getCanonicalName(),e);
				}
			}
		}
    }
	
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
    	String uri = req.uri();
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER));
        }
        FullHttpResponse response;
        // process static resources
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = REQUEST_MAPPING_MAP.get(uri);
        if(null != requestMappingHandlerAdapter) {
        	response = requestMappingHandlerAdapter.handler(ctx, req);
        } else {
            if(uri.startsWith(StaticResourcesController.ROOT_STATIC_RESOURCES)) {
            	uri = StaticResourcesController.ROOT_STATIC_RESOURCES;
            }
            requestMappingHandlerAdapter = REQUEST_MAPPING_MAP.get(uri);
            if(null != requestMappingHandlerAdapter) {
            	response = requestMappingHandlerAdapter.handler(ctx, req);
            // process 404	
            } else {
            	response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.EMPTY_BUFFER);
                response.headers().set(CONTENT_TYPE,MediaType.TEXT_PLAIN_UTF8.value());
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            }
        }
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (keepAlive) {
            if (req.protocolVersion().equals(HTTP_1_0)) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }
            response.headers().set(KEEP_ALIVE_NAME, KEEP_ALIVE_TIME_OUT);
            ctx.write(response);
        } else {
            response.headers().set(CONNECTION, CLOSE);
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	LOG.error("", cause);
        ctx.close();
    }
    
    /**
     * Register requestMappingHandler（注册Http请求处理器）
     * @param requestMappingHandler
     */
    private static final void registerRequestMappingHandler(RequestMappingHandler<?> requestMappingHandler) {
    	RequestMapping requestMapping = requestMappingHandler.getClass().getAnnotation(RequestMapping.class);
    	if(null != requestMapping) {
    		String[] urls = requestMapping.urls();
    		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter(requestMapping.method(), requestMapping.produce(), requestMappingHandler);
    		for(String url : urls) {
        		REQUEST_MAPPING_MAP.put(url,adapter);
    		}
    	}
    }
}
