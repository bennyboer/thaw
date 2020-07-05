package de.be.thaw.info.parser;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.parser.exception.ParseException;

import java.io.Reader;

/**
 * Parser used to parse the Thaw document info format file.
 */
public interface InfoParser {

    /**
     * Parse the Thaw document info format using the passed reader.
     *
     * @param reader to use
     * @return the parsed document info
     * @throws ParseException in case the info could not be parsed
     */
    ThawInfo parse(Reader reader) throws ParseException;

}
