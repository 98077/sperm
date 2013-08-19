package com.dirlt.java.veritas;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: dirlt
 * Date: 8/13/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class VeritasServer {
    public static class Logger {
        public boolean xdebug = true;
        public boolean xinfo = true;
        public boolean xwarn = true;

        public void debug(String s) {
            if (xdebug) {
                System.out.println("[DEBUG]" + s);
            }
        }

        public void info(String s) {
            if (xinfo) {
                System.out.println("[INFO]" + s);
            }
        }

        public void warn(String s) {
            if (xwarn) {
                System.out.println("[WARN]" + s);
            }
        }
    }

    public static Logger logger = new Logger();

    public static void runHttpServer(final Configuration configuration) {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                configuration.getAcceptIOThreadNumber(),
                Executors.newCachedThreadPool(),
                configuration.getIoThreadNumber());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        final Timer timer = new HashedWheelTimer(); // use default configuration.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(1024 * 1024 * 8));
                pipeline.addLast("encoder", new HttpResponseEncoder());
//                pipeline.addLast("rto_handler", new ReadTimeoutHandler(timer, configuration.getReadTimeout(), TimeUnit.MILLISECONDS));
//                pipeline.addLast("wto_handler", new WriteTimeoutHandler(timer, configuration.getWriteTimeout(), TimeUnit.MILLISECONDS));
                pipeline.addLast("handler", new VeritasHandler(configuration));
                return pipeline;
            }
        });
        // TODO(dirlt): add more options.
        bootstrap.setOption("backlog", configuration.getBacklog());
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
//        bootstrap.setOption("child.receiveBufferSize",16 * 1024);
//        bootstrap.setOption("child.sendBufferSize",16 * 1024);
        bootstrap.bind(new InetSocketAddress(configuration.getIp(), configuration.getPort()));
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        if (!configuration.parse(args)) {
            Configuration.usage();
            return;
        }
        // easy way!!
        if (!configuration.isDebug()) {
            logger.xdebug = false;
        }
        System.out.print(configuration);
        StatStore.init(configuration);
        ProxyConnector.init(configuration);
        CpuWorkerPool.init(configuration);
        runHttpServer(configuration);
    }
}
