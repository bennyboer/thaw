package de.be.thaw.style.model.selector;

import de.be.thaw.style.model.selector.impl.ImmutableStyleSelector;

/**
 * An abstract style selector.
 */
public abstract class AbstractStyleSelector implements StyleSelector {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        targetName().ifPresent(sb::append);

        className().ifPresent(n -> {
            sb.append('.');
            sb.append(n);
        });

        pseudoClassName().ifPresent(n -> {
            sb.append(':');
            sb.append(n);
        });

        pseudoClassSettings().ifPresent(settings -> {
            if (!settings.isEmpty()) {
                sb.append('(');
                sb.append(String.join(", ", settings));
                sb.append(')');
            }
        });

        return sb.toString();
    }

}
