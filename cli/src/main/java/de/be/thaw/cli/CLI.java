package de.be.thaw.cli;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.impl.DefaultDocumentBuilder;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.PdfExporter;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.parser.InfoParser;
import de.be.thaw.info.parser.impl.DefaultInfoParser;
import de.be.thaw.reference.citation.CitationManagerFactory;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.parser.StyleFormatParser;
import de.be.thaw.style.parser.StyleFormatParserFactory;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.parser.TextParser;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.util.cache.CacheUtil;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.io.IOException;

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
    private String rootInfoFolderPath;

    /**
     * Path where to save the resulting file to.
     */
    @CommandLine.Option(names = {"-o", "--output"}, description = "Path where to save the resulting file to")
    private String outputPath;

    /**
     * Name of the charset the files to process are encoded in.
     */
    @CommandLine.Option(names = {"-c", "--charset"}, description = "Name of the charset the files to process are encoded in. " +
            "If not specified the systems default charset will be used.")
    private String charsetName;

    /**
     * Subcommand to clean the cache folder
     */
    @CommandLine.Command(name="clean", description="Empty the root cache folder.")
    int clean(){
        try{
            CacheUtil.cleanCacheRootDir();
        }
        catch(IOException e){
            System.err.println("An exception has occurred. Root cache clean is not successful. Try again later.");
            return ErrorResult.ROOT_CACHE_CLEANING_ERROR.getCode();
        }
        return ErrorResult.OK.getCode();
    }

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
    private Path getRootInfoFolderPath() {
        if (rootInfoFolderPath == null) {
            return Path.of(System.getProperty("user.dir"));
        }

        return Path.of(rootInfoFolderPath);
    }

    /**
     * Get the path to save the resulting file to.
     *
     * @return path to save the resulting file to
     */
    private Path getOutputPath() {
        if (outputPath == null) {
            return Path.of(System.getProperty("user.dir"), "out.pdf");
        }

        return Path.of(outputPath);
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

        File root = getRootInfoFolderPath().toFile();
        ThawContext.getInstance().setRootFolder(root);
        ThawContext.getInstance().setCurrentFolder(root);
        System.out.println(String.format("Searching for Thaw files within folder at '%s'...", root.getAbsolutePath()));

        System.out.println();

        String[] infoFiles = root.list((dir, name) -> name.endsWith(".tdi"));
        if (infoFiles.length == 0) {
            System.out.println("[WARNING] Found not info file (*.tdi). It is recommended to have one.");
        } else if (infoFiles.length > 1) {
            System.err.println(String.format("There are more than one Thaw info file (ending with *.tdi) in the folder at '%s'", root.getAbsolutePath()));
            return ErrorResult.MORE_THAN_ONE_INFO_FILE.getCode();
        } else {
            System.out.println(String.format("Processing Thaw info file '%s'...", infoFiles[0]));
        }

        File infoFile = new File(root, infoFiles[0]);

        InfoParser infoParser = new DefaultInfoParser();
        ThawContext.getInstance().setInfoParser(infoParser);
        ThawInfo info;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infoFile), charset))) {
            info = infoParser.parse(br, ThawContext.getInstance().getCurrentFolder());
        } catch (de.be.thaw.info.parser.exception.ParseException e) {
            System.err.println(String.format(
                    "An exception occurred while trying to parse the provided info file at '%s'.\n" +
                            "The exception message is: '%s'",
                    infoFile.getAbsolutePath(),
                    e.getMessage()
            ));

            return ErrorResult.INFO_FILE_PARSING_ERROR.getCode();
        }
        ThawContext.getInstance().setEncoding(info.getEncoding());
        ThawContext.getInstance().setLanguage(info.getLanguage());

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
        ThawContext.getInstance().setTextParser(textParser);
        TextModel textModel;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), info.getEncoding()))) {
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

        String[] styleFiles = root.list((dir, name) -> name.endsWith(".tds"));

        StyleFormatParser styleFormatParser = StyleFormatParserFactory.getInstance().getParser();
        ThawContext.getInstance().setStyleParser(styleFormatParser);
        StyleModel styleModel;
        if (styleFiles.length > 1) {
            System.err.println(String.format("There are more than one Thaw style files (ending with *.tds) in the folder at '%s'", root.getAbsolutePath()));
            return ErrorResult.MORE_THAN_ONE_STYLE_FILE.getCode();
        } else if (styleFiles.length == 1) {
            System.out.println(String.format("Processing Thaw style file '%s'...", styleFiles[0]));

            File styleFile = new File(root, styleFiles[0]);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(styleFile), info.getEncoding()))) {
                styleModel = styleFormatParser.parse(br, ThawContext.getInstance().getRootFolder());
                styleModel = styleModel.merge(DefaultStyleModel.defaultModel());
            } catch (StyleModelParseException e) {
                System.err.println(String.format(
                        "An exception occurred while trying to parse the provided style file at '%s'.\n" +
                                "The exception message is: '%s'",
                        styleFile.getAbsolutePath(),
                        e.getMessage()
                ));

                return ErrorResult.STYLE_FILE_PARSING_ERROR.getCode();
            }
        } else {
            styleModel = DefaultStyleModel.defaultModel();
        }

        Document document = new DefaultDocumentBuilder().build(new DocumentBuildSource(
                info,
                textModel,
                styleModel,
                CitationManagerFactory.getInstance().create(
                        info.getBibliographyFile().orElse(null),
                        info.getBibliographyStyle(),
                        info.getLanguage()
                )
        ));

        Exporter exporter = new PdfExporter();
        try {
            exporter.export(document, getOutputPath());
        } catch (ExportException e) {
            System.err.println(String.format(
                    "An exception occurred while trying to export the resulting document.\n" +
                            "The exception message is: '%s'",
                    e.getMessage()
            ));
            e.printStackTrace();
            return ErrorResult.EXPORT_ERROR.getCode();
        }

        return ErrorResult.OK.getCode();
    }

}
