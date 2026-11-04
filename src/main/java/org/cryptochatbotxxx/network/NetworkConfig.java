package org.cryptochatbotxxx.network;

import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkConfig 
{
    private static final int DEFAULT_PORT = 19132;
    private static final int BUFFER_SIZE = 256;

    private final int udpPort;

    public NetworkConfig() 
    {
        this.udpPort = loadPortFromConfig();
    }

    private int loadPortFromConfig() 
    {
        //this.getClass().getClassLoader().getResourceAsStream("config.json");

        return DEFAULT_PORT;
    }

    public boolean isPortAvailable(int port) {
        try (DatagramSocket useless = new DatagramSocket(port)) {
            useless.disconnect();
            return true;
        } catch (SocketException e) {
            return false;
        }
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getBufferSize() {
        return BUFFER_SIZE;
    }
}
