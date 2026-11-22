package org.cryptochatbotxxx.network;

public interface UsernameCallback 
{
    void onSuccess(String username);
    void onFailure(String reason);
}