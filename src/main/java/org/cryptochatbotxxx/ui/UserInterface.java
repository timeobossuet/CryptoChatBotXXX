package org.cryptochatbotxxx.ui;

import org.cryptochatbotxxx.network.utils.User;

public interface UserInterface {
    void start();
    void onMessageReceived(User sender, String message);
    void onUserConnected(User user);
    void onUserDisconnected(User user);
    void onError(String errorMessage);
}