package se.diversify.multivaders.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import se.diversify.multivaders.server.http.HttpStaticFileServer;
import se.diversify.multivaders.server.websocket.WebSocketListener;
import se.diversify.multivaders.server.websocket.WebSocketServer;

/**
 * Preliminary setup for a HTTP static file server and WebSocket server.
 * <p/>
 * Note:<br/>
 * This class should be fixed!
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class Server {

    public static void main(String[] args) {
        File rootDir = new File(System.getProperty("user.home"));
        HttpStaticFileServer httpStaticFileServer = new HttpStaticFileServer(rootDir);

        final WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onMessage(String message, Channel channel, WebSocketServer server) {
                server.broadcastMessage("I got: " + message);
            }
        };

        WebSocketServer webSocketServer = new WebSocketServer(listener);
        try {
            httpStaticFileServer.start(new InetSocketAddress(4444));
            webSocketServer.start(new InetSocketAddress(4445));

            System.out.println("Press enter to shut the server down...");
            System.in.read();

            webSocketServer.shutdown();
            httpStaticFileServer.shutdown();
        } catch (IOException e) {
            System.err.println("Houston we have a problem!");
            e.printStackTrace();
        }

    }

}