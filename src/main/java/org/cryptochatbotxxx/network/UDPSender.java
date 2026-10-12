package org.cryptochatbotxxx.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {
    public final int PORT = 19132;
    public final int BUF_SIZE = 256;

    /**
     * Envoi une demande de découverte sur le réseau local via UDP pour savoir qui est connecté.
     */
    public void broadcastPresence(String name) {
        byte[] buf = name.getBytes(StandardCharsets.UTF_8); // Allocation du buffer
        try (DatagramSocket socket = new DatagramSocket()) { // garantit la fermeture du socket même en cas d'erreur
            socket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buf, BUF_SIZE, broadcastAddress, PORT);
            socket.send(packet);
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors du broadcast", e);
        }
    }


}
