package org.cryptochatbotxxx.network.protocols.udp;

import java.time.Instant;
import java.net.InetAddress;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.network.protocols.UDPProtocol;

public class UsernameAlreadyTakenProtocol extends UDPProtocol
{

    public void send(NetworkManager network, InetAddress address)
    {
        this.sendPacket(
            network, 
            address.getHostAddress(),
            getClass().getSimpleName() + ": " + 
            network.getUser().getUsername() + "+" + Instant.now()
        );
    }

    public void handle(NetworkManager network, InetAddress address, String message)
    {
        if(network.getUsernameCanChange()) {
            network.setUsernameCantChange();
            network.setUsernameNeedToChange();
        }
    }
}