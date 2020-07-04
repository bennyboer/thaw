package de.be.thaw.font.system;

import de.be.thaw.core.util.OperatingSystem;
import de.be.thaw.core.util.exception.CouldNotDetermineOperatingSystemException;
import de.be.thaw.font.util.exception.CouldNotGetFontsException;
import de.be.thaw.font.util.file.FontCollectionFile;
import de.be.thaw.font.util.file.FontFile;
import de.be.thaw.font.util.file.SingleFontFile;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager providing access to fonts installed to the current system.
 */
public class SystemFontManager {

    /**
     * Available system fonts as mapping from the font name to its description.
     */
    private static FontFile[] SYSTEM_FONTS;

    /**
     * Get all available fonts in the system.
     *
     * @return available fonts
     * @throws CouldNotGetFontsException in case fonts could not be retrieved from the system
     */
    public static FontFile[] getAvailableFonts() throws CouldNotGetFontsException {
        if (SYSTEM_FONTS == null) {
            List<FontFile> descriptorList = new ArrayList<>();

            try {
                for (String location : SystemFontLocations.getLocations(OperatingSystem.current())) {
                    File fontsFolder = new File(location);

                    if (fontsFolder.isDirectory()) {
                        // Fetch all true type font files
                        File[] trueTypeFontFiles = fontsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));
                        for (File file : trueTypeFontFiles) {
                            descriptorList.add(new SingleFontFile(file.getAbsolutePath()));
                        }

                        // Fetch all true type collection files
                        File[] trueTypeCollectionFiles = fontsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttc"));
                        for (File file : trueTypeCollectionFiles) {
                            descriptorList.add(new FontCollectionFile(file.getAbsolutePath()));
                        }
                    }
                }
            } catch (CouldNotDetermineOperatingSystemException | FontFormatException | IOException e) {
                throw new CouldNotGetFontsException(e);
            }

            SYSTEM_FONTS = descriptorList.toArray(new FontFile[0]);
        }

        return SYSTEM_FONTS;
    }

}
