package de.be.thaw.cli;

import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.parser.TextParser;
import de.be.thaw.text.parser.exception.ParseException;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * CLI entry point for Thaw.
 */
@CommandLine.Command(
        name = "thaw",
        mixinStandardHelpOptions = true,
        version = "v0.1.0",
        description = "Command line interface for the Thaw project"
)
public class CLI implements Callable<Integer> {

    /**
     * Path to the folder with the root thaw document info file in it (the *.tdi file).
     */
    @CommandLine.Option(names = {"-r", "--root-folder"}, description = "Path to the folder containing the root *.tdi file")
    private File rootInfoFolderPath;

    /**
     * Name of the charset the files to process are encoded in.
     */
    @CommandLine.Option(names = {"-c", "--charset"}, description = "Name of the charset the files to process are encoded in. " +
            "If not specified the systems default charset will be used.")
    private String charsetName;

    /**
     * Entry point of the CLI application.
     *
     * @param args passed arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CLI()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Get the root info folder path.
     *
     * @return root info folder path
     */
    private File getRootInfoFolderPath() {
        if (rootInfoFolderPath == null) {
            return new File(System.getProperty("user.dir"));
        }

        return rootInfoFolderPath;
    }

    /**
     * Get the charset the files to process are encoded in.
     *
     * @return charset
     */
    private Charset getCharset() {
        if (charsetName == null) {
            return Charset.defaultCharset();
        }

        return Charset.forName(charsetName);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("########################\n" +
                "### Thaw Project CLI ###\n" +
                "########################\n");

        System.out.println("### Configuration ###");

        Charset charset = getCharset();
        System.out.println(String.format("Charset: '%s'", charset.displayName(Locale.ENGLISH)));

        System.out.println();
        System.out.println("### Processing ###");

        File root = getRootInfoFolderPath();
        System.out.println(String.format("Searching for Thaw files within folder at '%s'...", root.getAbsolutePath()));

        System.out.println();

        // Search text file *.tdt
        String[] textFiles = root.list((dir, name) -> name.endsWith(".tdt"));

        if (textFiles.length == 0) {
            System.err.println(String.format("There is no Thaw text file (ending with *.tdt) in the folder at '%s'", root.getAbsolutePath()));
            return ErrorResult.MISSING_TEXT_FILE.getCode();
        } else if (textFiles.length > 1) {
            System.err.println(String.format("There are more than one Thaw text file (ending with *.tdt) in the folder at '%s'", root.getAbsolutePath()));
            return ErrorResult.MORE_THAN_ONE_TEXT_FILE.getCode();
        } else {
            System.out.println(String.format("Processing Thaw text file '%s'...", textFiles[0]));
        }

        File textFile = new File(root, textFiles[0]);

        TextParser textParser = new TextParser();
        TextModel textModel;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)))) {
            textModel = textParser.parse(br);
        } catch (ParseException e) {
            System.err.println(String.format(
                    "An exception occurred while trying to parse the provided text file at '%s'.\n" +
                            "The exception message is: '%s'",
                    textFile.getAbsolutePath(),
                    e.getMessage()
            ));

            return ErrorResult.TEXT_FILE_PARSING_ERROR.getCode();
        }

        System.out.println(textModel.getRoot().toString());

        return ErrorResult.OK.getCode();
    }

}
