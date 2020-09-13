package de.be.thaw.info.parser;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.parser.exception.ParseException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Reader;

/**
 * Parser used to parse the Thaw document info format file.
 */
public interface InfoParser {

    /**
     * Parse the Thaw document info format using the passed reader.
     *
     * @param reader           to use
     * @param workingDirectory we are currently in
     * @return the parsed document info
     * @throws ParseException in case the info could not be parsed
     */
    ThawInfo parse(Reader reader, @Nullable File workingDirectory) throws ParseException;

}
