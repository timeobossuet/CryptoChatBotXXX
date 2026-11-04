package org.cryptochatbotxxx.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {

	private final ExecutorService threadPool;
    private final NetworkConfig config;

    private final UDPListener udpListener;
    private final UDPSender udpSender;

    private final String username;
    private final HashMap<String, InetAddress> onlineUsers = new HashMap<>();

    public NetworkManager(int n, String username) 
    {
        this.username = username;
        this.threadPool = Executors.newFixedThreadPool(n);
        this.config = new NetworkConfig();
        this.udpListener = new UDPListener(this);
        this.udpSender = new UDPSender(this);
    }

    public void start() 
	{
        this.threadPool.submit(this.udpListener::handleListener);
        this.udpSender.broadcastPresence();
    }

    public void stop() 
    {
        this.threadPool.shutdown();
    }

    public void addOnlineUser(String userName, InetAddress address) 
	{
        this.onlineUsers.put(userName, address);
        System.out.println(
			"[" + this.getClass().getName() + "] Utilisateur en ligne détecté : " 
			+ userName + " (" + address.getHostAddress() + ")"
		);
    }

	public void removeOnlineUser(String userName) 
	{
		if(this.onlineUsers.containsKey(userName)) {
			this.onlineUsers.remove(userName);
		}
	}

    public ExecutorService getThreadPool() 
    { 
        return threadPool;
    }
    public UDPListener getUDPListener() 
    { 
        return udpListener;
    }
    public UDPSender getUDPSender() 
    {
        return udpSender;
    }
    public NetworkConfig getConfig() 
    { 
        return config; 
    }
    public String getUsername() 
    {
        return username;
    }

    public HashMap<String, InetAddress> getOnlineUsers() 
    { 
        return onlineUsers;
    }
}