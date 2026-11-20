package org.cryptochatbotxxx.network.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.Enumeration;

public class NetworkUtils 
{
    public static InetAddress getLocalIPAddress() 
    {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if(!i.isUp() || i.isLoopback()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = i.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if(address.isLoopbackAddress() || address.isLinkLocalAddress()) {
                        continue;
                    }

                    return address;
                } 
            } 
        } catch (Exception e) {}

        return InetAddress.getLoopbackAddress();
    } 
}
