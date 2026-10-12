package org.cryptochatbotxxx.network;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.net.DatagramPacket;

public class UDPListener {

    public final int PORT = 19132;

    public static class presenceListener {
        try{
            DatagramSocket socket = new DatagramSocket(PORT); // On which port is the server listening? ;
            boolean running = true;
            byte[] buf = new byte[256]; // What is the maximum size of a received message?

            while (running) {
                // Which lines are responsible for receiving messages?
                DatagramPacket inPacket  = new DatagramPacket(buf, buf.length);
                socket.receive(inPacket); // Bocking operations
                String received = new String(inPacket.getData(), 0, inPacket.getLength());

                // What does the server do when a message is received?
                System.out.println("Greetings, " + received);

                if(received.equals("end")){
                    running = false;
                    continue;
                }

                socket.close();
            }
        }
        catch (Exception e) {
                throw new RuntimeException(e);
        }

    }

}
