package org.cryptochatbotxxx.network.protocols;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import org.cryptochatbotxxx.network.NetworkManager;

public abstract class UDPProtocol implements Protocol
{

    private static final String BROADCAST_ADDRESS = "255.255.255.255";

    /**
     * Broadcasts the packet.
     */
    protected final void sendBroadcastPacket(NetworkManager network, String message) 
    {
        sendPacket(network, BROADCAST_ADDRESS, message);
    }

    /**
     * Sends a regular packet. 
     */
    protected final void sendPacket(NetworkManager network, String address, String message) 
    {
        byte[] buf = message.getBytes(StandardCharsets.UTF_8); // Allocation du buffer
        try (DatagramSocket socket = new DatagramSocket()) { // garantit la fermeture du socket même en cas d'erreur

            socket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(
                buf, buf.length, 
                broadcastAddress, network.getConfig().getUdpPort()
            );
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException("[" + this.getClass().getName() + "] Error sending packet: " + e.getMessage(), e);
        }
    }
}