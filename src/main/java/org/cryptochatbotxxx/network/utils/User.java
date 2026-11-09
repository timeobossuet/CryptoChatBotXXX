package org.cryptochatbotxxx.network.utils;

import java.net.InetAddress;
import java.time.Instant;

public class User 
{
    private final String username;
    private final InetAddress address;
    private final Instant dateCreation;

    public User(String username, InetAddress address, Instant dateCreation) 
    {
        this.username = username;
        this.address = address;
        this.dateCreation = dateCreation;
    }

    public String getUsername() 
    {
        return username;
    }

    public InetAddress getAddress() 
    {
        return address;
    }

    public Instant getDateCreation() 
    {
        return dateCreation;
    }
}
