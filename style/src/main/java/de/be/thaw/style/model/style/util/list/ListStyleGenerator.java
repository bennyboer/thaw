package de.be.thaw.style.model.style.util.list;

/**
 * Generator for list item styles.
 */
public interface ListStyleGenerator {

    /**
     * Generate the item style string.
     *
     * @param sequence number of the item (first item, second item, ...)
     * @return the item style string
     */
    String generate(int sequence);

}
