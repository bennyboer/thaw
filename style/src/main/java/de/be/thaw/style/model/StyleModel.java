package de.be.thaw.style.model;

import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

import java.util.Map;

/**
 * Representation of the Thaw document style model.
 */
public interface StyleModel {

    /**
     * Select a subset of styles from the model.
     *
     * @param selector to select styles with
     * @return styles that apply on the passed settings in the selector
     */
    Map<StyleType, Style> select(StyleSelector selector);

}
