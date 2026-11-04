package org.cryptochatbotxxx.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

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

                String received = new String(inPacket.getData(), 0, inPacket.getLength(), StandardCharsets.UTF_8);

                if(received.equals("end")){
                    this.running = false;
                    continue;
                }

                if(received.startsWith("NEWUSER:")) {
                    String username = received.substring("NEWUSER: ".length());
                    if(username.equalsIgnoreCase(this.network.getUsername()) ) {
                        this.network.addOnlineUser(username, inPacket.getAddress());
                    }
                } else if (received.startsWith("OK_ME:")) {
                    String username = received.substring("OK_ME: ".length());
                    if(username.equalsIgnoreCase(this.network.getUsername()) ) {
                        this.network.addOnlineUser(username, inPacket.getAddress());
                    }
                } else {
                    System.out.println("[" + this.getClass().getName() + "] Packet: " + received);
                }
            }
        } catch (IOException e) {
            this.stop();
            throw new RuntimeException("[" + this.getClass().getName() + "] " + e);
        }
    }

    public void stop() 
    {
        this.running = false;
        if(this.socket != null) {
            this.socket.disconnect();
        }
    }
}
