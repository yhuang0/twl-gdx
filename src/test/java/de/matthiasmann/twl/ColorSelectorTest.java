package de.matthiasmann.twl;

import static org.junit.Assert.*;

import de.matthiasmann.twl.model.ColorSpaceHSL;
import org.junit.Test;

public class ColorSelectorTest {

    @Test
    public void updateHexEditField_givenColor_setsCorrectHexText() {
        ColorSelector selector = new ColorSelector(new ColorSpaceHSL());
        selector.setShowHexEditField(true);
        selector.layout();
        // selector.setColor tampers with color 0x44FFAA88 becomes 0x44FEA988
        selector.currentColor = new Color(0x44FFAA88).toARGB();
        selector.updateHexEditField();
        assertEquals("44FFAA88", selector.hexColorEditField.getText());
    }
}
