package se.diversify.multivaders.server.websocket;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.*;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * A handler for WebSocket communication.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class WebSocketServerHandler extends SimpleChannelUpstreamHandler {

    /** Just to make it more specific that we're doing WebSocket stuff. */
    public static final String WEBSOCKET_PATH = "/websocket";

    /** Logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The owning WebSocket server. */
    private final WebSocketServer server;

    /** WebSocket listener. */
    private final WebSocketListener listener;

    /** Channels handling. */
    private final ChannelGroup channelGroup;

    /** WebSocket handshaker. */
    private WebSocketServerHandshaker handshaker;

    /**
     * Create a new instance.
     *
     * @param server The owning WebSocket server.
     * @param listener A WebSocket listener.
     * @param channelGroup A channel group for handling all the channels.
     */
    public WebSocketServerHandler(WebSocketServer server, WebSocketListener listener, ChannelGroup channelGroup) {
        if (server == null)
            throw new IllegalArgumentException("server is null!");

        if (listener == null)
            throw new IllegalArgumentException("listener is null!");

        if (channelGroup == null)
            throw new IllegalArgumentException("channelGroup is null!");

        this.server = server;
        this.listener = listener;
        this.channelGroup = channelGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("Client {} connected...", e.getChannel());
        channelGroup.add(e.getChannel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.warn("Got exception!", e.getCause());

        // Let's toast this channel.
        e.getChannel().close();
    }

    /**
     * Handle a HTTP resuest.
     *
     * @param ctx Channel handler context.
     * @param req HTTP request.
     * @throws Exception On errors.
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        // Allow only GET methods.
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory =
                new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);

        handshaker = webSocketServerHandshakerFactory.newHandshaker(req);
        if (handshaker == null) {
            webSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
        } else {
            handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
        }
    }

    /**
     * Handle a WebSocket frame.
     *
     * @param ctx Channel handler context.
     * @param frame WebSocket frame.
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
            return;
        }

        if (frame instanceof PingWebSocketFrame) {
            ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
            return;
        }

        // We only handle text based frames. On other frames we close the client
        // for being ill behaved.
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.warn("Got something other than a text frame. Closing client...");

            ctx.getChannel().close();
            return;
        }

        String request = ((TextWebSocketFrame) frame).getText();
        logger.debug("Channel {} received {}", ctx.getChannel(), request);
        listener.onMessage(request, ctx.getChannel(), server);
    }

    /**
     * Send a HTTP response.
     *
     * @param ctx Channel handler context.
     * @param req HTTP request.
     * @param res HTTP response.
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        // Generate an error page if the response status code is not OK (200).
        if (res.getStatus().getCode() != 200) {
            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(res, res.getContent().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.getChannel().write(res);
        if (!isKeepAlive(req) || res.getStatus().getCode() != 200)
            f.addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Returns the WebSocket URI.
     *
     * @param req HTTP request
     * @return WebSocket URI
     */
    private static String getWebSocketLocation(HttpRequest req) {
        return "ws://" + req.getHeader(HOST) + WEBSOCKET_PATH;
    }

}
