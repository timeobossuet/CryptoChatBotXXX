package org.cryptochatbotxxx.config;

public class AppConfig {

    public enum UiMode {
        GUI, CLI
    }

    private final UiMode uiMode;

    public AppConfig(ConfigReader reader, String[] args) {
        // Extraction depuis les settings
        String configMode = reader.getString("app.uiMode", "gui").trim().toUpperCase();
        UiMode resolvedMode = configMode.equals("CLI") ? UiMode.CLI : UiMode.GUI;

        // cli >> gui
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--cli")) {
                resolvedMode = UiMode.CLI;
            } else if (arg.equalsIgnoreCase("--gui")) {
                resolvedMode = UiMode.GUI;
            }
        }

        this.uiMode = resolvedMode;
    }

    public UiMode getUiMode() {
        return uiMode;
    }
}