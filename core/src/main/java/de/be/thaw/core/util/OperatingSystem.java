package de.be.thaw.core.util;

import de.be.thaw.core.util.exception.CouldNotDetermineOperatingSystemException;

/**
 * Enumeration of supported operating systems.
 */
public enum OperatingSystem {

    WINDOWS("Windows".toLowerCase()),
    LINUX("Linux".toLowerCase()),
    MAC_OS("Mac".toLowerCase());

    /**
     * System property key that should contain the operating system name.
     */
    private static final String OS_NAME_SYSTEM_PROPERTY_KEY = "os.name";

    /**
     * The current operating system.
     */
    private static OperatingSystem CURRENT = null;

    /**
     * Get the current operating system.
     *
     * @return current operating system
     * @throws CouldNotDetermineOperatingSystemException in case the current os could not be determined
     */
    public static OperatingSystem current() throws CouldNotDetermineOperatingSystemException {
        if (CURRENT == null) {
            String osName = System.getProperty(OS_NAME_SYSTEM_PROPERTY_KEY);
            if (osName == null) {
                throw new CouldNotDetermineOperatingSystemException("There is no system property value given that could be used to determine the operating system");
            }

            osName = osName.toLowerCase();

            if (osName.startsWith(WINDOWS.getPrefix())) {
                CURRENT = WINDOWS;
            } else if (osName.startsWith(LINUX.getPrefix())) {
                CURRENT = LINUX;
            } else if (osName.startsWith(MAC_OS.getPrefix())) {
                CURRENT = MAC_OS;
            } else {
                throw new CouldNotDetermineOperatingSystemException(String.format("There is no operating system known by the name '%s'", osName));
            }
        }

        return CURRENT;
    }

    /**
     * Prefix of the OS name.
     */
    private final String prefix;

    OperatingSystem(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get the prefix of the operating system name.
     *
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

}
