package org.cryptochatbotxxx.network;

import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cryptochatbotxxx.config.NetworkConfig;
import org.cryptochatbotxxx.network.utils.NetworkUtils;
import org.cryptochatbotxxx.network.utils.User;

public class NetworkManager {

    private final ExecutorService threadPool;
    private final NetworkConfig config;

    private final UDPListener udpListener;
    private final UDPSender udpSender;

    private User user;
    private final HashMap<InetAddress, User> onlineUsers = new HashMap<>();

    private volatile boolean usernameCanChange = false;
    private volatile boolean usernameNeedToChange = false;

    public NetworkManager(int n, String username, NetworkConfig config) 
    {
        this.user = new User(username, NetworkUtils.getLocalIPAddress(), Instant.now());
        this.threadPool = Executors.newFixedThreadPool(n);
        this.config = config;
        this.udpListener = new UDPListener(this);
        this.udpSender = new UDPSender(this);
    }

    public NetworkManager(int n, String username) 
    {
        this(n, username, new NetworkConfig());
    }

    public void start() 
    {
        this.threadPool.submit(this.udpListener::handleListener);
    }

    public void stop() 
    {
        this.udpListener.stop();
        this.threadPool.shutdownNow();
    }

    public void setUsername(String username, Instant dateCreation) 
    {
        this.user = new User(username, this.getUser().getAddress(), dateCreation);
    }

    public void addOnlineUser(User user) 
    {
        this.onlineUsers.put(user.getAddress(), user);
        System.out.println(
            "[" + this.getClass().getName() + "] Utilisateur en ligne détecté : " 
            + user.getUsername() + " (" + user.getAddress().getHostAddress() + ")"
        );
    }

    public void removeOnlineUser(InetAddress address) 
    {
        if(this.onlineUsers.containsKey(address)) {
            this.onlineUsers.remove(address);
        }
    }

    public Optional<String> existsUsername(String username) 
    {
        return this.onlineUsers.values().stream().map(User::getUsername).filter(k -> username.equalsIgnoreCase(k)).findFirst();
    }

    public void setUsernameCanChange() 
    {
        this.usernameCanChange = true;

        this.threadPool.submit(() -> {
            try {
                Thread.sleep(1000);
                this.setUsernameCantChange();
            } catch (InterruptedException e) {
            }
        });
    }

    public void setUsernameCantChange()
    {
        this.usernameCanChange = false;
    }

    public void setUsernameNeedToChange() 
    {
        this.usernameNeedToChange = true;
    }

    public void setUsernameDoesntNeedToChange() 
    {
        this.usernameNeedToChange = false;
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
    public User getUser() 
    {
        return user;
    }

    public HashMap<InetAddress, User> getOnlineUsers() 
    { 
        return onlineUsers;
    }

    public boolean getUsernameCanChange()
    {
        return this.usernameCanChange;
    }

    public boolean getUsernameNeedToChange() 
    {
        return this.usernameNeedToChange;
    }
}