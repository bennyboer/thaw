package de.be.thaw.font.system;

import de.be.thaw.font.util.exception.CouldNotGetFontsException;
import de.be.thaw.font.util.file.FontCollectionFile;
import de.be.thaw.font.util.file.FontFile;
import de.be.thaw.font.util.file.SingleFontFile;
import de.be.thaw.util.os.OperatingSystem;
import de.be.thaw.util.os.exception.CouldNotDetermineOperatingSystemException;

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
                        findFontsInFolder(fontsFolder, descriptorList);
                    }
                }
            } catch (CouldNotDetermineOperatingSystemException | FontFormatException | IOException e) {
                throw new CouldNotGetFontsException(e);
            }

            SYSTEM_FONTS = descriptorList.toArray(new FontFile[0]);
        }

        return SYSTEM_FONTS;
    }

    /**
     * Find fonts in the passed folder file.
     *
     * @param folder to search for fonts in
     * @param result the result list to add found fonts to
     * @throws IOException         in case a font could not be read
     * @throws FontFormatException in case a font format is incorrect
     */
    private static void findFontsInFolder(File folder, List<FontFile> result) throws IOException, FontFormatException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                findFontsInFolder(file, result);
            } else if (file.getName().toLowerCase().endsWith(".ttf")) {
                result.add(new SingleFontFile(file.getAbsolutePath()));
            } else if (file.getName().toLowerCase().endsWith(".ttc")) {
                result.add(new FontCollectionFile(file.getAbsolutePath()));
            }
        }
    }

}
