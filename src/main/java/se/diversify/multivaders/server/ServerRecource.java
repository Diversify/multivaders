package se.diversify.multivaders.server;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Hanterar properties i systemets
 *
 * @author dvalfrid (daniel@valfridsson.net)
 * @version 1.0
 */
public class ServerRecource {

    private ResourceBundle resources;

    public ServerRecource(String enviroment) {
        try {
            if ("develop".equalsIgnoreCase(enviroment)) {
                resources = new PropertyResourceBundle(ClassLoader.getSystemResourceAsStream("develop.properties"));
            } else {
                resources = new PropertyResourceBundle(ClassLoader.getSystemResourceAsStream("server.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoot() {
        return resources.getString(Key.ROOT.getValue());
    }

    public int getSocketPort() {
        return Integer.parseInt(resources.getString(Key.SOCKETS_PORT.getValue()));
    }

    public int getStaticPort() {
        return Integer.parseInt(resources.getString(Key.STATIC_PORT.getValue()));
    }


    private enum Key {
        ROOT("server.root"),
        STATIC_PORT("static.port"),
        SOCKETS_PORT("socket.port");

        private String value;

        private Key(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

}
