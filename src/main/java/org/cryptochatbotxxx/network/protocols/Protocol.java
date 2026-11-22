package org.cryptochatbotxxx.network.protocols;

import org.cryptochatbotxxx.network.NetworkManager;
import java.net.InetAddress;

public interface Protocol
{
    public abstract void send(NetworkManager network, InetAddress address);

    public abstract void handle(NetworkManager network, InetAddress address, String message);
}