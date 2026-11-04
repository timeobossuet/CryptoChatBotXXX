package org.cryptochatbotxxx;

import java.util.Random;

import org.cryptochatbotxxx.network.NetworkManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Random random = new Random();
        String username = args.length > 0 ? args[0] : "User_" + random.nextInt();
        System.out.println("[App] Bienvenu : " + username);
        
        NetworkManager network = new NetworkManager(2, username);
        network.start();

    }
}
