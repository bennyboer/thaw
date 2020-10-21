package de.be.thaw.util.cache;

import de.be.thaw.util.cache.exception.CouldNotGetProjectCacheDirectoryException;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Utility class for dealing with caching.
 */
public class CacheUtil {

    /**
     * Name of the root caching directory for Thaw.
     */
    private static final String ROOT_DIR_NAME = ".thaw";

    /**
     * Get the root caching directory.
     * It is guaranteed that the directory exists.
     *
     * @return root caching directory
     */
    public static File getCacheRootDir() {
        File dir = Path.of(System.getProperty("user.home"), ROOT_DIR_NAME).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    /**
     * Clean the root caching directory.
     * Tries 3 times in case of IOException.
     *
     * @throws IOException  in case directory access failed or other errors related to IO
     */
    public static void cleanCacheRootDir() throws IOException {
        try{
            FileUtils.cleanDirectory(getCacheRootDir());
        }
        catch(IOException e){
            throw e;
        }
        System.out.println("Root cache cleaned successfully");
    }

    /**
     * Get a project specific caching directory.
     *
     * @param rootProjectDir root directory of the project
     * @return a project-specific caching directory
     */
    public static File getProjectSpecificCacheDir(File rootProjectDir) throws CouldNotGetProjectCacheDirectoryException {
        // First and foremost create hash from the file path to represent the project-specific folder name
        String hexHash;
        try {
            hexHash = generateHexHash(new ByteArrayInputStream(rootProjectDir.getAbsolutePath().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new CouldNotGetProjectCacheDirectoryException("Could not generate hash to represent the project-specific caching folder name", e);
        }

        // Get or create folder (if it does not exist yet).
        File result = new File(getCacheRootDir(), hexHash);
        if (!result.exists()) {
            result.mkdir();
        }

        return result;
    }

    /**
     * Generate a hash in hex form for the passed input stream.
     *
     * @param is to generate hash for
     * @return the generated hash in hex form
     * @throws NoSuchAlgorithmException in case the hash algorithm could not be instantiated
     * @throws IOException              in case the stream reading failed
     */
    public static String generateHexHash(InputStream is) throws NoSuchAlgorithmException, IOException {
        try (Formatter formatter = new Formatter()) {
            for (byte b : generateHash(is)) {
                formatter.format("%02X", b);
            }
            return formatter.toString();
        }
    }

    /**
     * Generate a hash for the passed input stream.
     *
     * @param is to generate hash for
     * @return the generated hash
     * @throws NoSuchAlgorithmException in case the hash algorithm could not be instantiated
     * @throws IOException              in case the stream reading failed
     */
    public static byte[] generateHash(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (DigestInputStream in = new DigestInputStream(is, md)) {
            in.readAllBytes();
        }

        return md.digest();
    }

}
