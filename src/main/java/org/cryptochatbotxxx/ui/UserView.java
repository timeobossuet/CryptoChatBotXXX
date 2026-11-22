package org.cryptochatbotxxx.ui;

import org.cryptochatbotxxx.network.User;

/**
 * Contract defining how the network layer interacts with a user interface.
 */
public interface UserView
{
    /**
     * Starts the user interface lifecycle 
     * blocking until the user exits.
     */
    public void run();

    public void onMessageReceived(User sender, String message);

    public void onUserConnected(User user);

    public void onUserDisconnected(User user);

    public void onError(String errorMessage);
}