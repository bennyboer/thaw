package de.be.thaw.export.pdf.element;

import de.be.thaw.export.pdf.element.impl.ImageElementExporter;
import de.be.thaw.export.pdf.element.impl.LineElementExporter;
import de.be.thaw.export.pdf.element.impl.MathElementExporter;
import de.be.thaw.export.pdf.element.impl.TextElementExporter;
import de.be.thaw.typeset.page.ElementType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of available element exporters.
 */
public class ElementExporters {

    /**
     * Lookup of available element exporters by their supported types.
     */
    private static final Map<ElementType, ElementExporter> EXPORTER_LOOKUP = new HashMap<>();

    static {
        initExporter(new TextElementExporter());
        initExporter(new ImageElementExporter());
        initExporter(new MathElementExporter());
        initExporter(new LineElementExporter());

        // ADDITIONAL SUPPORTED EXPORTERS ARE TO BE DEFINED HERE!
    }

    /**
     * Initialize the passed element exporter.
     *
     * @param exporter to initialize
     */
    private static void initExporter(ElementExporter exporter) {
        for (ElementType type : exporter.supportedElementTypes()) {
            EXPORTER_LOOKUP.put(type, exporter);
        }
    }

    /**
     * Get an element exporter for the passed type.
     *
     * @param type to get exporter for
     * @return element exporter
     */
    public static Optional<ElementExporter> getForType(ElementType type) {
        return Optional.ofNullable(EXPORTER_LOOKUP.get(type));
    }

}
