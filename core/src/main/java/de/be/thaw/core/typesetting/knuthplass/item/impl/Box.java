package de.be.thaw.core.typesetting.knuthplass.item.impl;

import de.be.thaw.core.typesetting.knuthplass.item.AbstractItem;
import de.be.thaw.core.typesetting.knuthplass.item.ItemType;

/**
 * A box of the Knuth-Plass line breaking model.
 * It refers to something that is to be typeset.
 */
public abstract class Box extends AbstractItem {

    @Override
    public ItemType getType() {
        return ItemType.BOX;
    }

}
