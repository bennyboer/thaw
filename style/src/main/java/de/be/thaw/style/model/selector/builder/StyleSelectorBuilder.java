package de.be.thaw.style.model.selector.builder;

import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.selector.impl.ImmutableStyleSelector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Builder for style selectors.
 */
public class StyleSelectorBuilder {

    /**
     * The target name to select.
     */
    @Nullable
    private String targetName;

    /**
     * The class name to select.
     */
    @Nullable
    private String className;

    /**
     * The pseudo class name to select.
     */
    @Nullable
    private String pseudoClassName;

    /**
     * Pseudo class settings to require for the selected styles.
     */
    @Nullable
    private List<String> pseudoClassSettings;

    @Nullable
    public String getTargetName() {
        return targetName;
    }

    public StyleSelectorBuilder setTargetName(@Nullable String targetName) {
        this.targetName = targetName;
        return this;
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public StyleSelectorBuilder setClassName(@Nullable String className) {
        this.className = className;
        return this;
    }

    @Nullable
    public String getPseudoClassName() {
        return pseudoClassName;
    }

    public StyleSelectorBuilder setPseudoClassName(@Nullable String pseudoClassName) {
        this.pseudoClassName = pseudoClassName;
        return this;
    }

    @Nullable
    public List<String> getPseudoClassSettings() {
        return pseudoClassSettings;
    }

    public StyleSelectorBuilder setPseudoClassSettings(@Nullable List<String> pseudoClassSettings) {
        this.pseudoClassSettings = pseudoClassSettings;
        return this;
    }

    /**
     * Build a style selector from the current builder configuration.
     *
     * @return style selector
     */
    public StyleSelector build() {
        return new ImmutableStyleSelector(
                targetName,
                className,
                pseudoClassName,
                pseudoClassSettings
        );
    }

}
