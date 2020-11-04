package org.firecode.netpx.server;

import static io.netty.handler.codec.http2.Http2SecurityUtil.CIPHERS;
import static io.netty.handler.ssl.ApplicationProtocolConfig.Protocol.ALPN;
import static io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
import static io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE;
import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.netty.handler.ssl.SupportedCipherSuiteFilter.INSTANCE;

import java.io.File;

import org.firecode.netpx.common.ConfigPropertyResolver;
import org.firecode.netpx.server.config.ServerProperties;
import org.firecode.netpx.server.http.PathMatchingResourceClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * @author ChiangFire
 */
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	public static void main(String[] args) throws Exception {
		ServerProperties serverProperties = ServerProperties.getInstance();
		loadLogbackConfig();
		// Configure SSL.
		SslContext sslCtx = null;
		if (serverProperties.getAdminSSLEnabled()) {
			ApplicationProtocolConfig applicationProtocolConfig = new ApplicationProtocolConfig(ALPN,NO_ADVERTISE,ACCEPT,HTTP_1_1);
            sslCtx = SslContextBuilder.forServer(serverProperties.getAdminSSLCert(), serverProperties.getAdminSSLKey()).ciphers(CIPHERS,INSTANCE).applicationProtocolConfig(applicationProtocolConfig).build();
		}
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new DispatcherServerInitializer(sslCtx));
			Channel ch = b.bind(serverProperties.getBuildHost(),serverProperties.getBuildPort()).sync().channel();
			LOG.info("Proxy server started on domain \"{}\" port(s) {}",serverProperties.getBuildName(),serverProperties.getBuildPort());
			if(serverProperties.getAdminEnabled()) {
				LOG.info("Admin server started on domain \"{}\" port(s) {}",serverProperties.getAdminDomain(),serverProperties.getBuildPort());
			}
			ch.closeFuture().sync();
		} catch(Exception e) {
			LOG.error("Server start fail",e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * load config logback.xml
	 * 
	 * @throws JoranException
	 */
	private static void loadLogbackConfig() throws JoranException {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator loggerConfigurator = new JoranConfigurator();
		loggerConfigurator.setContext(loggerContext);
		loggerContext.reset();
		loggerConfigurator.doConfigure(Thread.currentThread().getContextClassLoader().getResource(String.join(File.separator, ConfigPropertyResolver.CONFIG_FOLDER, "logback.xml")));
	}
	
	/**
	 * Get current class package name
	 * @param fqClassName
	 * @return
	 */
	public static String getPackageName() {
		String fqClassName = Server.class.getCanonicalName();
		int lastDotIndex = fqClassName.lastIndexOf(PathMatchingResourceClassResolver.PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
	}
}
