package org.cryptochatbotxxx.network;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.cryptochatbotxxx.config.ConfigReader;

public class NetworkConfig {

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final int DEFAULT_PORT = 19132;
    private static final int DEFAULT_BUFFER_SIZE = 256;

    private final int udpPort;
    private final int bufferSize;

    public NetworkConfig() {
        this(new ConfigReader(DEFAULT_CONFIG_FILE));
    }

    public NetworkConfig(ConfigReader reader) {
        int loadedPort = reader.getInt("network.udpPort", DEFAULT_PORT);
        int loadedBuffer = reader.getInt("network.bufferSize", DEFAULT_BUFFER_SIZE);

        this.udpPort = isValidPort(loadedPort) ? loadedPort : DEFAULT_PORT;
        this.bufferSize = (loadedBuffer > 0) ? loadedBuffer : DEFAULT_BUFFER_SIZE;
    }

    private boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }

    public boolean isPortAvailable(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            return true;
        } catch (SocketException e) {
            return false;
        }
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}