package org.cryptochatbotxxx.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigReaderTest 
{
    private ConfigReader reader;

    // Loading the test config
    @BeforeEach
    void setUp() 
    {
        reader = new ConfigReader("test-config.properties");
    }

    // --- getString ---

    @Test
    void trimmedValueWhenKeyExists() {
        assertEquals("CryptoChatBotTest", reader.getString("app.name", "fallback"));
        assertEquals("valeur_avec_espaces", reader.getString("app.padded", "fallback"));
        assertEquals("", reader.getString("app.empty", "fallback"));
    }

    @Test
    void defaultWhenKeyIsMissingWithGetString() {
        assertEquals("fallback", reader.getString("unknown.key", "fallback"));
        assertNull(reader.getString("unknown.key", null));
    }

    // --- getInt ---

    @Test
    void parseValidInteger() {
        assertEquals(8080, reader.getInt("network.port", 9000));
    }

    @Test
    void defaultWhenKeyMissingOrNotANumber() {
        assertEquals(9000, reader.getInt("network.invalid_port", 9000));
    }

    // --- getBoolean ---

    @Test
    void parseBooleanValues() {
        assertTrue(reader.getBoolean("feature.enabled", false));
        assertFalse(reader.getBoolean("feature.disabled", true));
    }

    @Test
    void defaultWhenKeyIsMissingWithGetBoolean() {
        assertTrue(reader.getBoolean("unknown.flag", true));
        assertFalse(reader.getBoolean("unknown.flag", false));
    }
}