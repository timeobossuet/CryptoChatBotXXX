package org.cryptochatbotxxx.config;

import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkConfig 
{

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final int DEFAULT_PORT = 19132;
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private static final int DEFAULT_THREAD_POOL_SIZE = 2;

    private final int udpPort;
    private final int bufferSize;
    private final int threadPoolSize;

    // creates a config reader if the user forgot to
    public NetworkConfig() 
    {
        this(new ConfigReader(DEFAULT_CONFIG_FILE));
    }

    public NetworkConfig(ConfigReader reader) 
    {
        int loadedPort = reader.getInt("network.udpPort", DEFAULT_PORT);
        int loadedBuffer = reader.getInt("network.bufferSize", DEFAULT_BUFFER_SIZE);
        int loadedThreads = reader.getInt("network.threadPoolSize", DEFAULT_THREAD_POOL_SIZE);

        // Skip privileged ports (0-1024) to avoid permission issues
        this.udpPort = (loadedPort > 1024 && loadedPort <= 65535) ? loadedPort : DEFAULT_PORT;
        // Buffer needs to have a positive size
        this.bufferSize = loadedBuffer > 0 ? loadedBuffer : DEFAULT_BUFFER_SIZE;
        this.threadPoolSize = loadedThreads >= 2 ? loadedThreads : DEFAULT_THREAD_POOL_SIZE;
    }

    public boolean isPortAvailable(int port) 
    {
        // Opening a socket, tests availability and immediately closes it
        try (DatagramSocket socket = new DatagramSocket(port)) {
            return true;
        } catch (SocketException e) {
            return false;
        }
    }

    public int getUdpPort() { return udpPort; }
    public int getBufferSize() { return bufferSize; }
    public int getThreadPoolSize() { return threadPoolSize; }
}