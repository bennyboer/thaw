package de.be.thaw.reference.citation.styles.apa;

import java.util.Properties;

/**
 * Settings for the APA citation style.
 */
public class APASettings {

    /**
     * The properties for translations.
     */
    private final Properties properties;

    public APASettings(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

}
