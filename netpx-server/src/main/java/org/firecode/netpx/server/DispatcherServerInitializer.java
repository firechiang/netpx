package org.firecode.netpx.server;

import org.firecode.netpx.server.config.ServerProperties;
import org.firecode.netpx.server.http.DispatcherHttpHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

/**
 * @author ChiangFire
 */
public class DispatcherServerInitializer extends ChannelInitializer<SocketChannel> {
	
	private static final int MAX_CONTENT_LENGTH = 1024 * 1024;

    private final SslContext sslCtx;
    
    public DispatcherServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
    	String remoteHostName = ch.remoteAddress().getHostName();
    	ServerProperties serverProperties = ServerProperties.getInstance();
    	ChannelPipeline channelPipeline = ch.pipeline();
    	if(serverProperties.getAdminDomain().equals(remoteHostName)) {
    		if(serverProperties.getAdminEnabled()) {
                // process https
                if (sslCtx != null) {
                	channelPipeline.addLast(sslCtx.newHandler(ch.alloc()));
                }
                channelPipeline.addLast(new HttpServerCodec());
                channelPipeline.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                channelPipeline.addLast(new HttpServerExpectContinueHandler());
                channelPipeline.addLast(new DispatcherHttpHandler());
                return;
    		}
    	}
    	ch.close();
    }
}
