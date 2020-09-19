package de.be.thaw.style.model.selector;

import java.util.List;
import java.util.Optional;

/**
 * Selector for a subset of styles.
 */
public interface StyleSelector {

    /**
     * Get the name of the target to select.
     *
     * @return target name
     */
    Optional<String> targetName();

    /**
     * Get the name of the class to select.
     *
     * @return class name
     */
    Optional<String> className();

    /**
     * Get the name of the pseudo class to select.
     *
     * @return pseudo class name
     */
    Optional<String> pseudoClassName();

    /**
     * Get the pseudo class settings to require for the selected styles.
     * This will only be used when you specify a pseudo class name as well.
     *
     * @return pseudo class settings
     */
    Optional<List<String>> pseudoClassSettings();

}
