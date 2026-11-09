package org.cryptochatbotxxx.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class UDPSender 
{
    private final NetworkManager network;

    public UDPSender(NetworkManager network) 
    {
        this.network = network;
    }

    /**
     * Envoi une demande de découverte sur le réseau local via UDP pour savoir qui est connecté.
     */
    public void broadcastPresence() 
    {
        this.sendBroadcastMessage("NEWUSER: " + this.network.getUser().getUsername() + "+" + Instant.now().toString());
    }

    /**
     * Envoi une réponse.
     */
    public void respondPresence(String address) 
    {
        this.sendMessage(address, "OK_ME: " + this.network.getUser().getUsername() + "+" + Instant.now().toString());
    }

    public void broadcastTryUsername() 
    {
        this.sendBroadcastMessage("TRY_USERNAME: " + this.network.getUser().getUsername() + "+" + Instant.now().toString());
    }

    public void respondBadUsername(String address) 
    {
        this.sendMessage(address, "BAD_USERNAME: " + this.network.getUser().getUsername() + "+" + Instant.now().toString());
    }

    private void sendBroadcastMessage(String message) 
    {
        this.sendMessage("255.255.255.255", message);
    }

    /**
     * Envoi un message.
     */
    private void sendMessage(String address, String message) {
        byte[] buf = message.getBytes(StandardCharsets.UTF_8); // Allocation du buffer
        try (DatagramSocket socket = new DatagramSocket()) { // garantit la fermeture du socket même en cas d'erreur
            socket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(
                buf, buf.length, 
                broadcastAddress, this.network.getConfig().getUdpPort()
            );
            socket.send(packet);
        }
        catch (Exception e) {
            throw new RuntimeException("[" + this.getClass().getName() + "] Erreur inattendue lors du broadcast", e);
        }
    }
}
