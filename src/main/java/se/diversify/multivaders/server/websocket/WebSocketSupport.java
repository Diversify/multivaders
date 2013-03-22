package se.diversify.multivaders.server.websocket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Some utility methods for WebSockets.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class WebSocketSupport {

    /**
     * No need to instantiate me!
     */
    private WebSocketSupport() {
    }

    /**
     * Send a message to the specified channel.
     * <p/>
     * Note that the channel must belong to a WebSocket pipeline etc.
     *
     * @param message Message to send.
     * @param channel The channel to send the message to.
     */
    public static void sendWebSocketMessage(String message, Channel channel) {
        TextWebSocketFrame frame = new TextWebSocketFrame(message);
        channel.write(frame);
    }

}
