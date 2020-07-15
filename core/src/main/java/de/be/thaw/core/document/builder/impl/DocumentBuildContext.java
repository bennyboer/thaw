package de.be.thaw.core.document.builder.impl;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.text.model.TextModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context used during building of the document.
 */
public class DocumentBuildContext {

    /**
     * List of potential references.
     */
    private final List<PotentialInternalReference> potentialReferences = new ArrayList<>();

    /**
     * Lookup from labels to their node ID.
     */
    private final Map<String, String> labelToNodeID = new HashMap<>();

    /**
     * Counter for headline numbering.
     */
    private final List<Integer> headlineCounter = new ArrayList<>();

    /**
     * The document info.
     */
    private final ThawInfo info;

    /**
     * Text model of the document.
     */
    private final TextModel textModel;

    /**
     * Reference model of the document.
     */
    private final ReferenceModel referenceModel;

    /**
     * Style model of the document.
     */
    private final StyleModel styleModel;

    public DocumentBuildContext(ThawInfo info, TextModel textModel, ReferenceModel referenceModel, StyleModel styleModel) {
        this.info = info;
        this.textModel = textModel;
        this.referenceModel = referenceModel;
        this.styleModel = styleModel;
    }

    public List<PotentialInternalReference> getPotentialReferences() {
        return potentialReferences;
    }

    public Map<String, String> getLabelToNodeID() {
        return labelToNodeID;
    }

    public List<Integer> getHeadlineCounter() {
        return headlineCounter;
    }

    public ThawInfo getInfo() {
        return info;
    }

    public TextModel getTextModel() {
        return textModel;
    }

    public ReferenceModel getReferenceModel() {
        return referenceModel;
    }

    public StyleModel getStyleModel() {
        return styleModel;
    }

    /**
     * A potential internal reference.
     */
    public static class PotentialInternalReference {

        /**
         * ID of the source node.
         */
        private final String sourceID;

        /**
         * Label of the target.
         */
        private final String targetLabel;

        /**
         * Prefix to prefix the reference number with.
         */
        private final String prefix;

        /**
         * Name of the counter to use.
         */
        private final String counterName;

        public PotentialInternalReference(String sourceID, String targetLabel, String prefix, String counterName) {
            this.sourceID = sourceID;
            this.targetLabel = targetLabel;
            this.prefix = prefix;
            this.counterName = counterName;
        }

        public String getSourceID() {
            return sourceID;
        }

        public String getTargetLabel() {
            return targetLabel;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getCounterName() {
            return counterName;
        }

    }

}
