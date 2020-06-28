package de.be.thaw.cli;

import picocli.CommandLine;

import java.io.File;
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

    @Override
    public Integer call() throws Exception {
        System.out.println(getRootInfoFolderPath());

        return 0;
    }

}
