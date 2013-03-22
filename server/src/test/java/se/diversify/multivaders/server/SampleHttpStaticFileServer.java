package se.diversify.multivaders.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import se.diversify.multivaders.server.http.HttpStaticFileServer;

/**
 * Just to show how to do HTTP static file serving.
 * <p/>
 * Root directory will be set to @{code System.getProperty("user.home")},
 * and the port used will be {@code 4444}.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class SampleHttpStaticFileServer {

    public static void main(String[] args) {
        File rootDir = new File(System.getProperty("user.home"));
        HttpStaticFileServer staticFileServer = new HttpStaticFileServer(rootDir);
        try {
            staticFileServer.start(new InetSocketAddress(4444));

            System.out.println("Press enter to stop the server...");
            System.in.read();

            staticFileServer.shutdown();
        } catch (IOException e) {
            System.err.println("Could not start server...");
            e.printStackTrace();
        }
    }

}
