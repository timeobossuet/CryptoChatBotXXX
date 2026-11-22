package org.cryptochatbotxxx.network;

import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cryptochatbotxxx.config.NetworkConfig;
import org.cryptochatbotxxx.network.utils.NetworkUtils;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.ui.UserView;

public class NetworkManager 
{
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,20}$";

    private final ScheduledExecutorService threadPool;
    private final NetworkConfig config;
    private final UDPListener udpListener;
    private final HashMap<InetAddress, User> onlineUsers = new HashMap<>();

    private User user;
    private UserView view;

    private volatile AtomicBoolean usernameCanChange = new AtomicBoolean(false);
    private volatile AtomicBoolean usernameNeedToChange = new AtomicBoolean(false);

    public NetworkManager(String username, NetworkConfig config) 
    {
        this.config = config;
        this.user = new User(username, NetworkUtils.getLocalIPAddress(), Instant.now());
        this.threadPool = Executors.newScheduledThreadPool(config.getThreadPoolSize());
        this.udpListener = new UDPListener(this);
    }

    public void setView(UserView view) { this.view = view; }

    public void start() { this.threadPool.execute(this.udpListener::handleListener); }
    public void stop() 
    {
        this.udpListener.getProtocols().get("DisconnectionProtocol").send(this, this.getUser().getAddress());
        this.udpListener.stop();
        this.threadPool.shutdownNow();
    }

    public boolean isValidUsernameFormat(String username) 
    { 
        return username != null && username.matches(USERNAME_REGEX); 
    }

    public void requestUsername(String newUsername, UsernameCallback callback) 
    {
        if (!isValidUsernameFormat(newUsername)) {
            callback.onFailure("Format invalide (3-20 caractères alphanumériques, _ ou -).");
            return;
        }

        User old = this.user;
        this.setUsername(newUsername, Instant.now());
        this.setUsernameDoesntNeedToChange();
        this.setUsernameCanChange();
        this.udpListener.getProtocols().get("TryUsernameProtocol").send(this, this.getUser().getAddress());

        this.threadPool.schedule(() -> {
            if (this.getUsernameNeedToChange()) {
                this.setUsername(old.getUsername(), old.getDateCreation());
                callback.onFailure("Pseudo déjà pris, change le.");
            } else {
                this.udpListener.getProtocols().get("PresenceProtocol").send(this, this.getUser().getAddress());
                callback.onSuccess(newUsername);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void setUsername(String username, Instant dateCreation) 
    {
        this.user = new User(username, this.getUser().getAddress(), dateCreation);
    }

    public void addOnlineUser(User user) 
    {
        this.onlineUsers.put(user.getAddress(), user);
        if (this.view != null) {
            this.view.onUserConnected(user);
        }
    }

    public void removeOnlineUser(InetAddress address) 
    {
        User removed = this.onlineUsers.remove(address);
        if (removed != null && this.view != null) {
            this.view.onUserDisconnected(removed);
        }
    }

    public Optional<String> existsUsername(String username) 
    {
        return this.onlineUsers.values().stream()
                .map(User::getUsername)
                .filter(k -> username.equalsIgnoreCase(k))
                .findFirst();
    }

    public void setUsernameCanChange() 
    {
        this.usernameCanChange.set(true);
        this.threadPool.schedule(() -> {
            this.setUsernameCantChange();
        }, 1, TimeUnit.SECONDS);
    }

    public void setUsernameCantChange() { this.usernameCanChange.set(false); }
    public void setUsernameNeedToChange() { this.usernameNeedToChange.set(true); }
    public void setUsernameDoesntNeedToChange() { this.usernameNeedToChange.set(false); }

    public ScheduledExecutorService getThreadPool() { return this.threadPool; }
    public UDPListener getUDPListener() { return this.udpListener; }
    public NetworkConfig getConfig() { return this.config; }
    public User getUser() { return this.user; }
    public HashMap<InetAddress, User> getOnlineUsers() { return this.onlineUsers; }
    public boolean getUsernameCanChange() { return this.usernameCanChange.get(); }
    public boolean getUsernameNeedToChange() { return this.usernameNeedToChange.get(); }
}