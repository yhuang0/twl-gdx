package de.matthiasmann.twl;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorTest {

    @Test
    public void toString_givenNonAlphaColor_returnsValidHex() {
        assertEquals("#000000", Color.BLACK.toString());
    }

    @Test
    public void toString_givenTransparentBlackColor_returnsValidHex() {
        assertEquals("#00000000", Color.TRANSPARENT.toString());
    }

    @Test
    public void toString_givenTransparentColor_returnsValidHex() {
        assertEquals("#44FFAA88", new Color(0x44FFAA88).toString());
    }

    @Test
    public void toString_givenArgbColor_returnsValidHex() {
        Color c = new Color(0x44FFAA88);
        assertEquals("#44FFAA88", new Color(c.toARGB()).toString());
    }
}
