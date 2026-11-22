package org.cryptochatbotxxx.config;

public class ViewConfig 
{

    public enum UiMode 
    {
        GUI, CLI
    }

    private static final String UI_MODE_KEY = "app.uiMode";
    private static final UiMode DEFAULT_UI_MODE = UiMode.GUI;

    private final UiMode uiMode;

    public ViewConfig(ConfigReader reader, String[] args) 
    {
        // loads the default mode from the config file
        String rawMode = reader.getString(UI_MODE_KEY, DEFAULT_UI_MODE.name()).trim().toUpperCase();
        UiMode mode = "CLI".equals(rawMode) ? UiMode.CLI : DEFAULT_UI_MODE; // remove invalid modes

        if (args != null) {
            for (String arg : args) {
                if (arg == null) {
                    continue;
                }
                // CLI flags >> properties file
                if ("--cli".equalsIgnoreCase(arg)) {
                    mode = UiMode.CLI;
                } else if ("--gui".equalsIgnoreCase(arg)) {
                    mode = UiMode.GUI;
                }
            }
        }

        this.uiMode = mode;
    }

    public UiMode getUiMode() { return uiMode; }
}