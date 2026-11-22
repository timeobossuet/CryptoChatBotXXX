package org.cryptochatbotxxx;

import org.cryptochatbotxxx.config.ConfigReader;
import org.cryptochatbotxxx.config.NetworkConfig;
import org.cryptochatbotxxx.config.ViewConfig;
import org.cryptochatbotxxx.network.NetworkManager;
import org.cryptochatbotxxx.ui.UserView;
import org.cryptochatbotxxx.ui.cli.CliView;
import org.cryptochatbotxxx.ui.gui.GuiView;

/**
- Application entry point initializing configuration, networking, and the selected UI.
 */
public class App 
{
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final String DEFAULT_USERNAME = "+unknown";

    public static void main(String[] args) 
    {
        // reads the config
        ConfigReader reader = new ConfigReader(DEFAULT_CONFIG_FILE);
        // applies the config
        ViewConfig viewConfig = new ViewConfig(reader, args); // args >> config

        NetworkManager network = new NetworkManager(DEFAULT_USERNAME, new NetworkConfig(reader));

        UserView view = switch (viewConfig.getUiMode()) {
            case CLI -> new CliView(network);
            case GUI -> new GuiView(network, args);
        };

        startApplication(network, view);
    }

    private static void startApplication(NetworkManager network, UserView view) 
    {
        network.setView(view);
        network.start();
        view.run();
    }
}