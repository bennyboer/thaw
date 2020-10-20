package de.be.thaw.code.syntax.impl;

import de.be.thaw.code.syntax.SyntaxHighlighter;
import de.be.thaw.code.syntax.exception.HighlightException;
import de.be.thaw.util.debug.Debug;
import de.be.thaw.util.os.OperatingSystem;
import de.be.thaw.util.os.exception.CouldNotDetermineOperatingSystemException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Syntax highlighter outputting RTF (Rich-Text-Format).
 */
public class RTFSyntaxHighlighter implements SyntaxHighlighter {

    /**
     * Logger of the class.
     */
    private static final Logger LOGGER = Logger.getLogger(RTFSyntaxHighlighter.class.getSimpleName());

    /**
     * Aliases the user is able to specify as language when they want to syntax highlight Thaw document text format code.
     */
    private static final Set<String> THAW_TEXT_FORMAT_LANGUAGE_ALIASES = Set.of("thaw", "tdt");

    /**
     * Path of the python thaw lexer script for Pygments in the resources.
     */
    private static final String THAW_LEXER_PATH = "/pygments/lexer/thaw_lexer.py";

    /**
     * Class name of the python thaw lexer script for Pygments.
     */
    private static final String THAW_LEXER_CLASS_NAME = "ThawLexer";

    /**
     * Name of the python tool.
     */
    private static final String PYTHON_CMD_NAME = "python3";

    /**
     * Name of the pygmentize tool.
     */
    private static final String PYGMENTS_CMD_NAME = "pygmentize";

    /**
     * The maximum wait time for a cmd line process (in seconds).
     */
    private static final int MAX_WAIT_TIME = 30;

    /**
     * How many spaces are to be a tab.
     */
    private static final int SPACES_PER_TAB = 4;

    /**
     * Message printed when Python is not available on the command line.
     */
    private static final String PYTHON_NOT_INSTALLED_MESSAGE = "In order to syntax highlight code blocks you need to install Python on your machine (https://www.python.org/).\n" +
            "Afterwards you will need to install Pygments using `pip install pygments`.";

    /**
     * Message printed when Pygments is not available on the command line.
     */
    private static final String PYGMENTS_NOT_INSTALLED_MESSAGE = "In order to syntax highlight code blocks you need to install Pygments using `pip install pygments`.\n" +
            "Afterwards please try again.";

    /**
     * Working directory to execute commands in.
     */
    private File workingDirectory = new File(System.getProperty("user.home"));

    @Override
    public String highlight(String code, String language, String style) throws HighlightException {
        checkIfToolsAvailable(); // Check if Python and Pygments tools are available on the command line

        // Replace tabs with whitespaces as they are not formatted by Pygments properly
        code = code.replaceAll("\t", " ".repeat(SPACES_PER_TAB));

        // Write code in a temporary file
        File tmpCodeFile;
        try {
            tmpCodeFile = writeInTmpFile(code, "thaw-", "-codesrc");
        } catch (IOException e) {
            throw new HighlightException("Could not create temporary file to store the code to syntax highlight later.", e);
        }

        // Create temporary file for Pygments to store the formatted output to
        File tmpResultFile;
        try {
            tmpResultFile = File.createTempFile("thaw-", "-formatted");
            tmpResultFile.deleteOnExit();
        } catch (IOException e) {
            throw new HighlightException("Could not create temporary file to store the syntax highlighted code to", e);
        }

        // Check if we have a custom lexer script file specified instead of a language alias
        boolean isCustomLexerScriptFile = language.contains(".py");

        // Check if the user specified the thaw document text format as language which will be handled specially
        if (!isCustomLexerScriptFile && THAW_TEXT_FORMAT_LANGUAGE_ALIASES.contains(language.toLowerCase())) {
            // Pygments does not have a lexer for the thaw document text format -> we will use a custom lexer for this
            // First step: Write lexer from resources to a file so that pygmentize can use it
            InputStream lexerStream = RTFSyntaxHighlighter.class.getResourceAsStream(THAW_LEXER_PATH);
            File tmpLexerFile;
            try {
                tmpLexerFile = File.createTempFile("thaw-", "-thaw-lexer.py");
                tmpLexerFile.deleteOnExit();
            } catch (IOException e) {
                throw new HighlightException("Could not create temporary file to contain the thaw lexer for Pygments", e);
            }

            try (FileOutputStream out = new FileOutputStream(tmpLexerFile)) {
                byte[] buffer = new byte[2048];
                int read;
                while ((read = lexerStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                throw new HighlightException("Could not copy thaw lexer for Pygments to a temporary file", e);
            }

            // Second step: Set language variable properly to point to the temporary file
            language = String.format("%s:%s", tmpLexerFile.getAbsolutePath(), THAW_LEXER_CLASS_NAME);
            isCustomLexerScriptFile = true;
        }

        // Let pygmentize process the temporary code file
        String cmd = String.format(
                "%s -f rtf -l \"%s\" -O style=%s -o \"%s\"%s \"%s\"",
                PYGMENTS_CMD_NAME,
                language,
                style,
                tmpResultFile.getAbsolutePath(),
                isCustomLexerScriptFile ? " -x" : "",
                tmpCodeFile.getAbsolutePath()
        );

        try {
            ProcessResult result = runOnCmd(cmd, cmd, MAX_WAIT_TIME, TimeUnit.SECONDS);

            if (Debug.isDebug()) {
                LOGGER.log(Level.INFO, String.format("Executed command to syntax highlight code: '%s'\n" +
                        "EXIT CODE: %s, OUTPUT: '%s'", cmd, result.getCode(), result.getOutput()));
            }

            if (result.getCode() != 0) {
                throw new HighlightException(String.format(
                        "Syntax highlighting failed. pygmentize (Pygments) returned with exit code %d, output '%s' and error output '%s'",
                        result.getCode(),
                        result.getOutput(),
                        result.getErrorOutput()
                ));
            }
        } catch (CouldNotDetermineOperatingSystemException | IOException | TimeoutException | InterruptedException e) {
            throw new HighlightException("Could not run pygmentize (Pygments) on the command line to syntax highlight the code.", e);
        }

        // Read in file the result has been written to by pygmentize.
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tmpResultFile), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);

                line = br.readLine();
                if (line != null) {
                    sb.append('\n');
                }
            }
        } catch (IOException e) {
            throw new HighlightException("Could not read the temporary result file pygmentize (Pygments) should have written the formatted code to.", e);
        }

        return sb.toString();
    }

    /**
     * Write the passed string in a temporary file.
     *
     * @param str    to write
     * @param prefix of the temporary file
     * @param suffix of the temporary file
     * @return the temporary file
     * @throws IOException in case the file could not be created or written
     */
    private File writeInTmpFile(String str, String prefix, String suffix) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        tmpFile.deleteOnExit();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)))) {
            pw.print(str);
        }

        return tmpFile;
    }

    /**
     * Check if the needed tools are available on the command line.
     */
    public void checkIfToolsAvailable() throws HighlightException {
        boolean pythonAvailable;
        try {
            pythonAvailable = isToolAvailable(PYTHON_CMD_NAME, "--version");
        } catch (InterruptedException | IOException | CouldNotDetermineOperatingSystemException | TimeoutException e) {
            throw new HighlightException("Could not determine whether Python is available on the command line.", e);
        }

        boolean pygmentsAvailable;
        try {
            pygmentsAvailable = isToolAvailable(PYGMENTS_CMD_NAME, "-V");
        } catch (InterruptedException | IOException | CouldNotDetermineOperatingSystemException | TimeoutException e) {
            throw new HighlightException("Could not determine whether Pygments is available on the command line.", e);
        }

        if (!pythonAvailable) {
            LOGGER.log(Level.SEVERE, PYTHON_NOT_INSTALLED_MESSAGE);
            throw new HighlightException("Python ('python') is not available on the command line but needed by the syntax highlighter.");
        }
        if (!pygmentsAvailable) {
            LOGGER.log(Level.SEVERE, PYGMENTS_NOT_INSTALLED_MESSAGE);
            throw new HighlightException("Pygments ('pygmentize') is not available on the command line but needed by the syntax highlighter.");
        }
    }

    /**
     * Check if the passed tool name is available on the command line.
     *
     * @param name    of the tool to check availability for
     * @param options like '--version' to check the availability with
     * @return whether the tool is available
     */
    private boolean isToolAvailable(String name, String options) throws InterruptedException, TimeoutException, CouldNotDetermineOperatingSystemException, IOException {
        String cmd = String.format("%s %s", name, options);

        ProcessResult result = runOnCmd(cmd, cmd, MAX_WAIT_TIME, TimeUnit.SECONDS);

        if (Debug.isDebug()) {
            LOGGER.log(Level.INFO, String.format(
                    "Checking tool availability for tool '%s' finished with exit code %d, output '%s' and error output '%s'",
                    name,
                    result.getCode(),
                    result.getOutput(),
                    result.getErrorOutput()
            ));
        }

        return result.getCode() == 0;
    }

    /**
     * Run the passed command(s) on the command line.
     *
     * @param windowsCmd   command to execute for windows machines
     * @param linuxCommand command to execute for linux-flavoured machines
     * @param timeout      timeout
     * @param timeUnit     of the timeout
     * @return the process result
     * @throws CouldNotDetermineOperatingSystemException in case the OS could not be determined
     * @throws IOException                               in case the process execution failed
     * @throws InterruptedException                      when the thread was interrupted
     * @throws TimeoutException                          when the execution timed out
     */
    private ProcessResult runOnCmd(String windowsCmd, String linuxCommand, long timeout, TimeUnit timeUnit) throws CouldNotDetermineOperatingSystemException, IOException, InterruptedException, TimeoutException {
        ProcessBuilder builder = new ProcessBuilder();

        OperatingSystem os = OperatingSystem.current();

        if (os == OperatingSystem.WINDOWS) {
            builder.command("cmd.exe", "/c", windowsCmd);
        } else {
            builder.command("sh", "-c", linuxCommand);
        }

        // Set the directory the command will be executed in.
        builder.directory(getWorkingDirectory());

        Process process = builder.start();

        String output = readStreamToString(process.getInputStream());
        String errorOutput = readStreamToString(process.getErrorStream());

        if (process.waitFor(timeout, timeUnit)) {
            // Process has finished in time
            return new ProcessResult(process.exitValue(), output, errorOutput);
        } else {
            // Process run into timeout!
            throw new TimeoutException(String.format("Cmd line process exceeded the maximum wait time of %d seconds", MAX_WAIT_TIME));
        }
    }

    /**
     * Read the passed stream to a string.
     *
     * @param is to read
     * @return the read string
     */
    private String readStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);

                line = reader.readLine();
                if (line != null) {
                    sb.append('\n');
                }
            }
        }

        return sb.toString();
    }

    /**
     * Get the working directory to execute commands in.
     *
     * @return working directory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Set the working directory to execute commands in.
     *
     * @param workingDirectory to set
     */
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Result of a cmd line process.
     */
    private static final class ProcessResult {

        /**
         * The exit code of a process.
         */
        private final int code;

        /**
         * The output of the process.
         */
        private final String output;

        /**
         * The error output of the process.
         */
        private final String errorOutput;

        public ProcessResult(int code, String output, String errorOutput) {
            this.code = code;
            this.output = output;
            this.errorOutput = errorOutput;
        }

        public int getCode() {
            return code;
        }

        public String getOutput() {
            return output;
        }

        public String getErrorOutput() {
            return errorOutput;
        }

    }

}
