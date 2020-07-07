package de.be.thaw.font.system;

import de.be.thaw.font.util.OperatingSystem;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Standard system font locations.
 */
public class SystemFontLocations {

    /**
     * Common locations for fonts for individual operating systems.
     */
    private static final Map<OperatingSystem, Set<String>> LOCATIONS = new HashMap<>();

    /**
     * Get common font locations for the passed operating system.
     *
     * @param os to get common font locations for
     * @return common font locations
     */
    public static Set<String> getLocations(OperatingSystem os) {
        if (LOCATIONS.isEmpty()) {
            initialize();
        }

        Set<String> locations = LOCATIONS.get(os);
        if (locations == null) {
            return Collections.emptySet();
        }

        return locations;
    }

    /**
     * Initialize the locations.
     */
    private static void initialize() {
        initForWindows();
        initForLinux();
        initForMacOS();
    }

    /**
     * Initialize common font locations for Windows.
     */
    private static void initForWindows() {
        String windowsFolderPath = System.getenv("WINDIR");

        LOCATIONS.put(OperatingSystem.WINDOWS, Set.of(
                Path.of(windowsFolderPath, "Fonts").toString()
        ));
    }

    /**
     * Initialize common font locations for Linux.
     */
    private static void initForLinux() {
        LOCATIONS.put(OperatingSystem.LINUX, Set.of(
                "/usr/share/fonts",
                "/usr/local/share/fonts",
                "~/.fonts"
        ));
    }

    /**
     * Initialize common font locations for Mac OS.
     */
    private static void initForMacOS() {
        LOCATIONS.put(OperatingSystem.MAC_OS, Set.of(
                "/System/Library/Fonts",
                "/Library/Fonts"
        ));
    }

}
