package org.cryptochatbotxxx.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.cryptochatbotxxx.network.utils.User;

public class UDPListener 
{

    private final NetworkManager network;
    private volatile boolean running = true;
    private DatagramSocket socket;

    public UDPListener(NetworkManager network) 
    {
        this.network = network;
    }

    public void handleListener() 
    {
        try
        {
            this.socket = new DatagramSocket(this.network.getConfig().getUdpPort());
            byte[] buf = new byte[this.network.getConfig().getBufferSize()]; // What is the maximum size of a received message?

            while (this.running) {
                DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
                socket.receive(inPacket);

                if(this.isLocalPacket(inPacket.getAddress())) {
                    continue;
                }

                try { 
                    String received = new String(inPacket.getData(), 0, inPacket.getLength(), StandardCharsets.UTF_8);

                    String usernameAndTimestamp;
                    String username;
                    Instant dateCreation;
                    User user;

                    switch (received) {
                        case String r when r.equals("end"):
                                this.running = false;
                            break;
                        case String r when r.startsWith("NEWUSER:"):
                            usernameAndTimestamp = received.substring("NEWUSER: ".length());
                            username = usernameAndTimestamp.substring(0, usernameAndTimestamp.indexOf("+"));
                            dateCreation = Instant.parse(usernameAndTimestamp.substring(usernameAndTimestamp.indexOf("+")+1));

                            user = new User(username, inPacket.getAddress(), dateCreation);
                            this.network.addOnlineUser(user);
                            this.network.getUDPSender().respondPresence(inPacket.getAddress().getHostAddress());
                            break;
                        case String r when r.startsWith("OK_ME:"):
                            usernameAndTimestamp = received.substring("OK_ME: ".length());
                            username = usernameAndTimestamp.substring(0, usernameAndTimestamp.indexOf("+"));
                            dateCreation = Instant.parse(usernameAndTimestamp.substring(usernameAndTimestamp.indexOf("+")+1));

                            user = new User(username, inPacket.getAddress(), dateCreation);
                            this.network.addOnlineUser(user);
                            break;
                        case String r when r.startsWith("TRY_USERNAME:"):
                            usernameAndTimestamp = received.substring("TRY_USERNAME: ".length());
                            username = usernameAndTimestamp.substring(0, usernameAndTimestamp.indexOf("+"));
                            dateCreation = Instant.parse(usernameAndTimestamp.substring(usernameAndTimestamp.indexOf("+")+1));
                            if(username.equalsIgnoreCase(this.network.getUser().getUsername())) {
                                if(this.network.getUser().getDateCreation().isBefore(dateCreation)) {
                                    this.network.getUDPSender().respondBadUsername(inPacket.getAddress().getHostAddress());
                                    break;
                                } else {
                                    if(this.network.getUsernameCanChange()) {
                                        this.network.setUsernameCantChange();
                                        this.network.setUsernameNeedToChange();
                                    }

                                    // Limitation: Si le packet met trop de temps à parvenir, on rentre dans un problème de concurence de pseudo mais on va dire que ça arrive jamais.
                                }
                            }
                            break;
                        case String r when r.startsWith("BAD_USERNAME:"):
                            if(this.network.getUsernameCanChange()) {
                                this.network.setUsernameCantChange();
                                this.network.setUsernameNeedToChange();
                            }

                            // Limitation: Si le packet met trop de temps à parvenir, on rentre dans un problème de concurence de pseudo mais on va dire que ça arrive jamais.
                            break;
                        default:
                            System.out.println("[" + this.getClass().getName() + "] Packet: " + received);
                    }
                } catch (RuntimeException e) {
                    System.out.println("[" + this.getClass().getName() + "] Packet invalid: " + e);
                } 
            }
        } catch (IOException e) {
            if(!this.running) {
                return;
            }

            this.stop();
            throw new RuntimeException("[" + this.getClass().getName() + "] " + e);
        }
    }

    public void stop() 
    {
        this.running = false;
        if(this.socket != null) {
            this.socket.close();
        }
    }

    private boolean isLocalPacket(InetAddress address) 
    {
        try {
            return address.isLoopbackAddress() || NetworkInterface.getByInetAddress(address) != null;
        } catch (SocketException e) {
            return false;
        }  
    } 
}
