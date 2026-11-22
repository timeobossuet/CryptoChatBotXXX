package org.cryptochatbotxxx.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cryptochatbotxxx.network.protocols.udp.AckPresenceProtocol;
import org.cryptochatbotxxx.network.protocols.udp.DisconnectionProtocol;
import org.cryptochatbotxxx.network.protocols.udp.PresenceProtocol;
import org.cryptochatbotxxx.network.protocols.udp.TryUsernameProtocol;
import org.cryptochatbotxxx.network.protocols.udp.UsernameAlreadyTakenProtocol;
import org.cryptochatbotxxx.network.protocols.UDPProtocol;

public class UDPListener 
{

    private final NetworkManager network;
    private volatile AtomicBoolean running = new AtomicBoolean(true);
    private DatagramSocket socket;

    HashMap<String, UDPProtocol> protocols = new HashMap<>();

    public UDPListener(NetworkManager network) 
    {
        this.network = network;

        this.protocols.put("AckPresenceProtocol", new AckPresenceProtocol());
        this.protocols.put("DisconnectionProtocol", new DisconnectionProtocol());
        this.protocols.put("UsernameAlreadyTakenProtocol", new UsernameAlreadyTakenProtocol());
        this.protocols.put("TryUsernameProtocol", new TryUsernameProtocol());
        this.protocols.put("PresenceProtocol", new PresenceProtocol());
    }

    public void handleListener() 
    {
        try {
            this.socket = new DatagramSocket(this.network.getConfig().getUdpPort());
            byte[] buf = new byte[this.network.getConfig().getBufferSize()]; 

            while (this.running.get()) {
                DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
                socket.receive(inPacket);

                if(this.isLocalPacket(inPacket.getAddress())) 
                    continue;

                String received = new String(inPacket.getData(), 0, inPacket.getLength(), StandardCharsets.UTF_8);

                UDPProtocol protocol = this.protocols.get(received.split(":")[0]);
                if (protocol != null) {
                    protocol.handle(this.network, inPacket.getAddress(), received);
                } else {
                    System.out.println("[" + this.getClass().getName() + "] Unknown protocol: " + received);
                }
            }
        } catch (IOException e) {
            if(!this.running.get()) 
                return;

            this.stop();
            throw new RuntimeException("[" + this.getClass().getName() + "] " + e);
        }
    }

    public void stop() 
    {
        this.running.compareAndSet(true, false);

        if(this.socket != null) 
            this.socket.close();
    }

    public HashMap<String, UDPProtocol> getProtocols() { return this.protocols; }

    private boolean isLocalPacket(InetAddress address) 
    {
        try {
            return address.isLoopbackAddress() || NetworkInterface.getByInetAddress(address) != null;
        } catch (SocketException e) {
            return false;
        }  
    } 
}