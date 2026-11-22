package org.cryptochatbotxxx.network.protocols.udp;

import java.time.Instant;
import java.net.InetAddress;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.network.protocols.UDPProtocol;

public class DisconnectionProtocol extends UDPProtocol
{

    public void send(NetworkManager network, InetAddress address)
    {
        this.sendBroadcastPacket(
            network, 
            getClass().getSimpleName() + ": " + 
            network.getUser().getUsername() + "+" + Instant.now()
        );
    }

    public void handle(NetworkManager network, InetAddress address, String message)
    {
        String usernameAndTimestamp = message.substring((this.getClass().getSimpleName() + ": ").length());
        String username = usernameAndTimestamp.substring(0, usernameAndTimestamp.indexOf("+"));
        Instant dateCreation = Instant.parse(usernameAndTimestamp.substring(usernameAndTimestamp.indexOf("+")+1));

        network.removeOnlineUser(address);
    }
}