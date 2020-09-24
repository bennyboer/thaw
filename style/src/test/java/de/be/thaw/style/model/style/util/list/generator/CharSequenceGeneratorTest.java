package de.be.thaw.style.model.style.util.list.generator;

import de.be.thaw.style.model.style.util.list.ListStyleGenerator;
import de.be.thaw.style.model.style.util.list.ListStyleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharSequenceGeneratorTest {

    @Test
    public void upperLatinTestSmallestNumber() {
        ListStyleGenerator generator = ListStyleType.UPPER_LATIN.getSymbolGenerator();

        Assertions.assertEquals("A", generator.generate(1));
    }

    @Test
    public void upperLatinTestBiggestOneCharacterNumber() {
        ListStyleGenerator generator = ListStyleType.UPPER_LATIN.getSymbolGenerator();

        Assertions.assertEquals("Z", generator.generate(26));
    }

    @Test
    public void upperLatinTestBigNumber1() {
        ListStyleGenerator generator = ListStyleType.UPPER_LATIN.getSymbolGenerator();

        Assertions.assertEquals("AA", generator.generate(27));
    }

    @Test
    public void upperLatinTestBigNumber2() {
        ListStyleGenerator generator = ListStyleType.UPPER_LATIN.getSymbolGenerator();

        Assertions.assertEquals("BA", generator.generate(53));
    }

}
