package org.cryptochatbotxxx.network.protocols.udp;

import java.time.Instant;
import java.net.InetAddress;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.network.protocols.UDPProtocol;

public class PresenceProtocol extends UDPProtocol
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

        User user = new User(username, address, dateCreation);
        network.addOnlineUser(user);
        
        UDPProtocol protocol = network.getUDPListener().getProtocols().get("Ack" + this.getClass().getSimpleName());
        if (protocol != null) {
            try {
                protocol.send(network, address);
            } catch (Exception e) {
                System.err.println("[" + this.getClass().getName() + "] Error sending acknowledgment: " + e.getMessage());
            }
        } else {
            System.out.println("[" + this.getClass().getName() + "] Acknowledgment protocol not found for: " + this.getClass().getSimpleName());
        }
    }
}