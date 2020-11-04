package org.firecode.netpx.server.http.support;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT_RANGES;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Objects;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author ChiangFire
 */
public class RequestMappingHandlerAdapter {
	
	private static final String SUFFIX_HTML = ".html";
	
	private static final String SUFFIX_JS = ".js";
	
	private static final String SUFFIX_CSS = ".css";
	
	private static final String SUFFIX_WOFF = ".woff";
	
	private static final String SUFFIX_TTF = ".ttf";
	
	private MediaType mediaType;
	
	private RequestMethod requestMethod;
	
	private RequestMappingHandler<?> requestMappingHandler;
	
	public RequestMappingHandlerAdapter(RequestMethod requestMethod,MediaType mediaType,RequestMappingHandler<?> requestMappingHandler) {
		this.requestMethod = requestMethod;
		this.mediaType = mediaType;
		this.requestMappingHandler = requestMappingHandler;
	}
	
	/**
	 * @param ctx
	 * @param req
	 */
	public FullHttpResponse handler(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		ByteBuf content = ctx.alloc().buffer();
		if(req.method().equals(requestMethod.value())) {
			Object res = this.requestMappingHandler.handler(req);
			if(!Objects.isNull(res)) {
				if(res instanceof byte[]) {
					content.writeBytes((byte[])res);
				} else {
					if(MediaType.STATIC_RESOURCES.equals(mediaType)) {
						ByteBufUtil.writeUtf8(content, res.toString());
					}
					if(MediaType.APPLICATION_JSON_UTF8.equals(mediaType)) {
						ByteBufUtil.writeUtf8(content,JSON.toJSONString(res));
					}
				}
			}
		}
//		if(content == null) {
//			content = Unpooled.EMPTY_BUFFER;
//		}
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
        response.headers().set(CONTENT_TYPE,mediaType.value());
        if(MediaType.STATIC_RESOURCES.equals(mediaType)) {
            String uri = req.uri();
            if(uri.indexOf(SUFFIX_HTML) != -1) {
            	response.headers().set(ACCEPT_RANGES,MediaType.BYTES.value());
            	response.headers().set(CONTENT_TYPE,MediaType.TEXT_HTML_UTF8.value());
            }
            if(uri.indexOf(SUFFIX_JS) != -1) {
            	response.headers().set(ACCEPT_RANGES,MediaType.BYTES.value());
            	response.headers().set(CONTENT_TYPE,MediaType.APPLICATION_JAVASCRIPT_UTF8.value());
            }
            if(uri.indexOf(SUFFIX_CSS) != -1) {
            	response.headers().set(ACCEPT_RANGES,MediaType.BYTES.value());
            	response.headers().set(CONTENT_TYPE,MediaType.TEXT_CSS_UTF8.value());
            }
            if(uri.indexOf(SUFFIX_WOFF) !=  -1) {
            	response.headers().set(ACCEPT_RANGES,MediaType.BYTES.value());
            	response.headers().set(CONTENT_TYPE,MediaType.APPLICATION_FONT_WOFF.value());
            }
            if(uri.indexOf(SUFFIX_TTF) !=  -1) {
            	response.headers().set(ACCEPT_RANGES,MediaType.BYTES.value());
            	response.headers().set(CONTENT_TYPE,MediaType.APPLICATION_FONT_TTF.value());
            }
        }
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
	}
}
