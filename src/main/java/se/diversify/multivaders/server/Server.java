package se.diversify.multivaders.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import se.diversify.multivaders.DecisionMaker;
import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.server.http.HttpStaticFileServer;
import se.diversify.multivaders.server.websocket.WebSocketListener;
import se.diversify.multivaders.server.websocket.WebSocketServer;
import se.diversify.multivaders.strategy.SumStrategy;

/**
 * Preliminary setup for a HTTP static file server and WebSocket server.
 * <p/>
 * Note:<br/>
 * This class should be fixed!
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class Server implements DecisionMaker.EventCallback {

    private final ServerRecource resource;
    private WebSocketServer webSocketServer;

    public static void main(String[] args) {
        ServerRecource resource = new ServerRecource(args.length > 0 ? args[0] : null);

        final Server server = new Server(resource);
        server.run();
    }

    private Server(ServerRecource serverRecource) {
        resource = serverRecource;
    }

    private void run() {
        File rootDir = new File(resource.getRoot());
        HttpStaticFileServer httpStaticFileServer = new HttpStaticFileServer(rootDir);

        final DecisionMaker decisionMaker = new DecisionMaker(new SumStrategy(), this);

        final WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onMessage(String message, Channel channel, WebSocketServer server) {
                KeyEvent event = new KeyEvent(message);
                decisionMaker.process(event);
            }
        };

        webSocketServer = new WebSocketServer(listener);
        try {
            httpStaticFileServer.start(new InetSocketAddress(resource.getStaticPort()));
            webSocketServer.start(new InetSocketAddress(resource.getSocketPort()));

            System.out.println("Press enter to shut the server down...");
            System.in.read();

            webSocketServer.shutdown();
            httpStaticFileServer.shutdown();
        } catch (IOException e) {
            System.err.println("Houston we have a problem!");
            e.printStackTrace();
        }

    }

    @Override
    public void sendEvent(KeyEvent event) {
        webSocketServer.broadcastMessage(event.toJs());
    }
}
