package se.diversify.multivaders.server.http;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP static file server.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class HttpStaticFileServer {

    /** Logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The state for the server. I.e. if it's running or not. */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /** Root directory for file serving. */
    private final File rootDir;

    /** All channels. */
    private ChannelGroup channelGroup;

    /** For starting and stopping the server etc. */
    private ServerBootstrap bootstrap;

    /**
     * Create a new instance.
     *
     * @param rootDir Root directory for file serving.
     */
    public HttpStaticFileServer(File rootDir) {
        if (rootDir == null)
            throw new IllegalArgumentException("rootDir is null!");

        this.rootDir = rootDir;
    }

    /**
     * Start the server at the specified host and port.
     *
     * @param address Host and port information.
     * @return {@code true} if the server was started, or {@code false} if already running.
     * @throws IOException On errors starting the server.
     */
    public boolean start(InetSocketAddress address) throws IOException {
        if (address == null)
            throw new IllegalArgumentException("address is null!");

        if (running.compareAndSet(false, true)) {
            channelGroup = new DefaultChannelGroup();

            bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory());
            bootstrap.setOption("child.tcpNoDelay", true);
            bootstrap.setOption("child.keepAlive", true);
            bootstrap.setOption("reuseAddress", true);

            setPipelineFactory();

            channelGroup.add(bootstrap.bind(address));

            logger.info("Started server at {} serving files from {}", address, rootDir);
            return true;
        }

        return false;
    }

    /**
     * Shutdown the server.
     *
     * @return {@code true} if the server was shut down, or {@code false} if not running.
     */
    public boolean shutdown() {
        if (running.compareAndSet(true, false)) {
            channelGroup.close().awaitUninterruptibly();
            bootstrap.releaseExternalResources();

            channelGroup = null;
            bootstrap = null;

            logger.info("The server has been shut down...");
            return true;
        }

        return false;
    }

    /**
     * Sets the pipeline factory on {@link #bootstrap}.
     */
    private void setPipelineFactory() {
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler", new HttpStaticFileServerHandler(rootDir, channelGroup));
                return pipeline;
            }
        });
    }

}
