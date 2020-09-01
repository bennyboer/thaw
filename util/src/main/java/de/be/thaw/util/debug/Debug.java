package de.be.thaw.util.debug;

/**
 * Collection of debug tools.
 */
public class Debug {

    /**
     * Name of the system property that determines whether Thaw is to be executed in debug mode.
     */
    private static final String DEBUG_MODE_SYSTEM_PROPERY = "thaw.debug";

    /**
     * Whether the application is in debug mode.
     */
    private static Boolean DEBUG_MODE;

    /**
     * Check if the application is running in debug mode.
     *
     * @return whether the application is running in debug mode
     */
    public static boolean isDebug() {
        if (DEBUG_MODE == null) {
            DEBUG_MODE = Boolean.parseBoolean(System.getenv(DEBUG_MODE_SYSTEM_PROPERY));
        }

        return DEBUG_MODE;
    }

}
