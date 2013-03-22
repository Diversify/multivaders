package se.diversify.multivaders.server.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket server.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class WebSocketServer {

    /** Logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The state for the server. I.e. if it's running or not. */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /** A WebSocket listener. */
    private final WebSocketListener listener;

    /** All channels. */
    private ChannelGroup channelGroup;

    /** Our Screen*/
    private Channel screen;



    /** For starting and stopping the server etc. */
    private ServerBootstrap bootstrap;

    /**
     * Create a new instance.
     *
     * @param listener WebSocket listener.
     */
    public WebSocketServer(WebSocketListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener is null!");

        this.listener = listener;
    }

    /**
     * Send a message to all connected WebSocket clients (i.e. channels).
     *
     * @param message The message to send.
     * @return {@code true} if the message was sent (i.e. the server is running), or {@code false} if not running.
     */
    public boolean broadcastMessage(String message) {
        if (running.get()) {
            channelGroup.write(new TextWebSocketFrame(message));
            return true;
        }

        return false;
    }

    /**
     * Send a message to the screen
     * @param message The message to send
     * @return {@code true} if the message was sent (i.e. the server is running), or {@code false} if not running.
     */
    public boolean sendToScreen(String message) {
        if(running.get() && screen != null){
            screen.write(new TextWebSocketFrame(message));
            return true;
        }
        return false;
    }

    /**
     * Sets the screen.
     * @param screen The screen to write to
     */
    public void setScreen(Channel screen) {
        if(screen != null){
            this.screen = screen;
        }
    }

    /**
     * Start the server at the specified host and port.
     *
     * @param address Host and port information.
     * @return {@code true} if the server was started, or {@code false} if already running.
     * @throws java.io.IOException On errors starting the server.
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

            logger.info("Started server at {}", address);
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
            screen = null;

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
                pipeline.addLast("handler", new WebSocketServerHandler(
                        WebSocketServer.this, listener, channelGroup));
                return pipeline;
            }
        });
    }

}
