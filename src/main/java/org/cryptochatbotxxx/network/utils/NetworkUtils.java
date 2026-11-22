package org.cryptochatbotxxx.network.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.Enumeration;

public class NetworkUtils 
{
    /**
     * Gets the current local IP
     * Prefers : IPv4 over loopback or linklocal
     * If none : returns loop-back addr
     */
    public static InetAddress getLocalIPAddress() 
    {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // if no interface
            if (interfaces == null) {
                return InetAddress.getLoopbackAddress();
            }

            while(interfaces.hasMoreElements()) 
            {
                NetworkInterface i = interfaces.nextElement();

                // ignores inactive interfaces
                if(!i.isUp() || i.isLoopback()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = i.getInetAddresses();

                while(addresses.hasMoreElements()) 
                {
                    InetAddress address = addresses.nextElement();

                    // Prefer a routable IPv4 address over loopback or link-local (169.254.x.x)
                    if(!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address instanceof Inet4Address) {
                        return address;
                    }
                } 
            } 
        } catch (SocketException e) {
            System.err.println("Failed to inspect network interfaces: " + e.getMessage());
        }

        return InetAddress.getLoopbackAddress();
    } 
}
