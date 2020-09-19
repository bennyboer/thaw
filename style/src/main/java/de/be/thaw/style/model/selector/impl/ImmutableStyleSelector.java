package de.be.thaw.style.model.selector.impl;

import de.be.thaw.style.model.selector.StyleSelector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * An immutable style selector.
 */
public class ImmutableStyleSelector implements StyleSelector {

    /**
     * The target name to select.
     */
    @Nullable
    private final String targetName;

    /**
     * The class name to select.
     */
    @Nullable
    private final String className;

    /**
     * The pseudo class name to select.
     */
    @Nullable
    private final String pseudoClassName;

    /**
     * Pseudo class settings to require for the selected styles.
     */
    @Nullable
    private final List<String> pseudoClassSettings;

    public ImmutableStyleSelector(
            @Nullable String targetName,
            @Nullable String className,
            @Nullable String pseudoClassName,
            @Nullable List<String> pseudoClassSettings
    ) {
        this.targetName = targetName;
        this.className = className;
        this.pseudoClassName = pseudoClassName;
        this.pseudoClassSettings = pseudoClassSettings;
    }

    @Override
    public Optional<String> targetName() {
        return Optional.ofNullable(targetName);
    }

    @Override
    public Optional<String> className() {
        return Optional.ofNullable(className);
    }

    @Override
    public Optional<String> pseudoClassName() {
        return Optional.ofNullable(pseudoClassName);
    }

    @Override
    public Optional<List<String>> pseudoClassSettings() {
        return Optional.ofNullable(pseudoClassSettings);
    }

}
