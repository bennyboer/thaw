package de.be.thaw.style.parser;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.BackgroundStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
import de.be.thaw.style.parser.exception.ParseException;
import de.be.thaw.style.parser.impl.DefaultStyleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class StyleParserTest {

    @Test
    public void simpleParserTest() throws ParseException {
        String source = "{\n" +
                "\t\"DOCUMENT\": {\n" +
                "\t\t\"size\": {\n" +
                "\t\t\t\"width\": 210,\n" +
                "\t\t\t\"height\": 297\n" +
                "\t\t},\n" +
                "\t\t\"insets\": {\n" +
                "\t\t\t\"top\": 10,\n" +
                "\t\t\t\"bottom\": 10,\n" +
                "\t\t\t\"left\": 20,\n" +
                "\t\t\t\"right\": 20\n" +
                "\t\t},\n" +
                "\t\t\"background\": {\n" +
                "\t\t\t\"color\": {\n" +
                "\t\t\t\t\"red\": 1.0,\n" +
                "\t\t\t\t\"green\": 1.0,\n" +
                "\t\t\t\t\"blue\": 1.0,\n" +
                "\t\t\t\t\"alpha\": 1.0\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t\"font\": {\n" +
                "\t\t\t\"family\": \"Cambria\",\n" +
                "\t\t\t\"size\": 12,\n" +
                "\t\t\t\"color\": {\n" +
                "\t\t\t\t\"red\": 0.9,\n" +
                "\t\t\t\t\"green\": 0.9,\n" +
                "\t\t\t\t\"blue\": 0.9\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"H1\": {\n" +
                "\t\t\"font\": {\n" +
                "\t\t\t\"size\": 24\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        StyleParser parser = new DefaultStyleParser();

        StyleModel model = parser.parse(new StringReader(source));

        StyleBlock document = model.getBlock("DOCUMENT").orElseThrow();
        StyleBlock h1 = model.getBlock("H1").orElseThrow();

        Assertions.assertEquals("DOCUMENT", document.getName());
        Assertions.assertEquals("H1", h1.getName());

        Assertions.assertEquals(4, document.getStyles().size());

        SizeStyle sizeStyle = ((SizeStyle) document.getStyles().get(StyleType.SIZE));
        Assertions.assertEquals(210, sizeStyle.getWidth());
        Assertions.assertEquals(297, sizeStyle.getHeight());

        InsetsStyle insetsStyle = ((InsetsStyle) document.getStyles().get(StyleType.INSETS));
        Assertions.assertEquals(10, insetsStyle.getTop());
        Assertions.assertEquals(20, insetsStyle.getLeft());
        Assertions.assertEquals(10, insetsStyle.getBottom());
        Assertions.assertEquals(20, insetsStyle.getRight());

        BackgroundStyle backgroundStyle = ((BackgroundStyle) document.getStyles().get(StyleType.BACKGROUND));
        Assertions.assertEquals(1.0, backgroundStyle.getColor().getRed());
        Assertions.assertEquals(1.0, backgroundStyle.getColor().getGreen());
        Assertions.assertEquals(1.0, backgroundStyle.getColor().getBlue());
        Assertions.assertEquals(1.0, backgroundStyle.getColor().getAlpha());

        FontStyle fontStyle = ((FontStyle) document.getStyles().get(StyleType.FONT));
        Assertions.assertEquals("Cambria", fontStyle.getFamily());
        Assertions.assertNull(fontStyle.getVariant());
        Assertions.assertEquals(12.0, fontStyle.getSize());
        Assertions.assertEquals(0.9, fontStyle.getColor().getRed());
        Assertions.assertEquals(0.9, fontStyle.getColor().getGreen());
        Assertions.assertEquals(0.9, fontStyle.getColor().getBlue());
        Assertions.assertEquals(1.0, fontStyle.getColor().getAlpha());

        fontStyle = ((FontStyle) h1.getStyles().get(StyleType.FONT));
        Assertions.assertNull(fontStyle.getFamily());
        Assertions.assertNull(fontStyle.getVariant());
        Assertions.assertEquals(24.0, fontStyle.getSize());
        Assertions.assertNull(fontStyle.getColor());
    }

}
