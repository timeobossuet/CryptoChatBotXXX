package org.cryptochatbotxxx.ui.cli;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.Collection;

import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.network.UsernameCallback;
import org.cryptochatbotxxx.network.User;
import org.cryptochatbotxxx.ui.UserView;

public class CliView implements UserView 
{
    private final NetworkManager network;
    private final Scanner scanner = new Scanner(System.in);
    private boolean connected = false;

    public CliView(NetworkManager network) 
    {
        this.network = network;
    }

    /**
     * Prompts for a username, blocks until the network validates it,
     * then starts the interactive command loop.
     */
    @Override
    public void run() 
    {
        System.out.println("=== CryptoChatBotXXX ===");

        while (!connected) 
        {
            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine().toLowerCase().trim();

            System.out.println("Vérification de la disponibilité...");

            handleUsernameRequest(pseudo, "Bienvenue ", "Erreur : ");
        }

        runCommandLoop();
    }

    private void runCommandLoop() 
    {
        System.out.println("Commandes disponibles : /rename [pseudo], /users, /quit");

        while (connected && scanner.hasNextLine()) 
        {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            switch (input.toLowerCase()) {
                case String r when r.startsWith("/rename"):
                    String[] args = r.split(" ", 2);
                    if (args.length < 2 || args[1].trim().isEmpty()) {
                        System.out.println("Usage : /rename [pseudo]");

                    } else {
                        String newUsername = args[1].trim();
                        handleUsernameRequest(
                            newUsername,
                            "Pseudo mis à jour avec succès : ",
                            "Erreur lors du changement de pseudo : "
                        );
                    }
                    break;
                case "/quit":
                    handleQuit();
                    break;
                case "/users":
                    printOnlineUsers();
                    break;
                default:
                    System.out.println("Commande inconnue. Commandes disponibles : /rename [pseudo], /users, /quit");
            }
        }
    }

    private void handleUsernameRequest(String username, String successMessage, String failureMessage) 
    {
        CountDownLatch latch = new CountDownLatch(1);

        network.requestUsername(username, new UsernameCallback() {
            @Override
            public void onSuccess(String acceptedUsername) {
                System.out.println(successMessage + acceptedUsername);
                connected = true;
                latch.countDown();
            }

            @Override
            public void onFailure(String reason) {
                System.out.println(failureMessage + reason);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    // command methods 
    private void handleQuit() 
    {
        network.stop();
        connected = false;
        System.out.println("Déconnexion...");
    }

    private void printOnlineUsers() 
    {
        Collection<User> users = network.getOnlineUsers().values();
        System.out.println("Utilisateurs en ligne (" + users.size() + ") :");
        for (User u : users) {
            System.out.println("  - " + u.getUsername() + " (" + u.getAddress().getHostAddress() + ")");
        }
    }

    // event methods
    @Override
    public void onMessageReceived(User sender, String message) 
    {
        System.out.println("[" + sender.getUsername() + "] : " + message);
    }

    public void handleRename()
    {

    }

    @Override
    public void onUserConnected(User user) 
    {
        System.out.println("[+] " + user.getUsername() + " est en ligne (" + user.getAddress().getHostAddress() + ")");
    }

    @Override
    public void onUserDisconnected(User user) 
    {
        System.out.println("[-] " + user.getUsername() + " s'est déconnecté.");
    }

    @Override
    public void onError(String errorMessage) 
    {
        System.err.println("[Erreur] " + errorMessage);
    }
}