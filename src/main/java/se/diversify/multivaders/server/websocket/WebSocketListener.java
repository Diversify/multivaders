package se.diversify.multivaders.server.websocket;

import org.jboss.netty.channel.Channel;

/**
 * A listener for WebSocket messages.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public interface WebSocketListener {

    /**
     * Invoked on a new WebSocket message.
     *
     * @param message The message received.
     * @param channel The channel that sent the message.
     * @param server The WebSocket server that the channel belong to.
     */
    public void onMessage(String message, Channel channel, WebSocketServer server);

}
