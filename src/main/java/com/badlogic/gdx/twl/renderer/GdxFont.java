/*
 * Copyright (c) 2008-2010, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution. * Neither the name of Matthias Mann nor
 * the names of its contributors may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.badlogic.gdx.twl.renderer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.math.MathUtils;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.renderer.FontParameter;
import de.matthiasmann.twl.utils.StateSelect;

/**
 * @author Yifu Huang
 * @author Nathan Sweet
 * @author Matthias Mann
 * @author Kurtis Kopf
 */
public class GdxFont implements Font {

    public static final FontParameter.Parameter<Integer> FONTPARAM_OFFSET_X = FontParameter.newParameter("offsetX", 0);
    public static final FontParameter.Parameter<Integer> FONTPARAM_OFFSET_Y = FontParameter.newParameter("offsetY", 0);
    public static final FontParameter.Parameter<Integer> FONTPARAM_UNDERLINE_OFFSET = FontParameter.newParameter("underlineOffset", 0);

    static private final HAlignment[] gdxAlignment = HAlignment.values();

    static final int STYLE_UNDERLINE   = 1;
    static final int STYLE_LINETHROUGH = 2;

    final GdxRenderer renderer;
    final BitmapFont bitmapFont;
    private final StateSelect stateSelect;
    private final FontState[] fontStates;
    private final float yOffset;

    private Boolean proportional = null;

    public GdxFont(GdxRenderer renderer, BitmapFont bitmapFont, StateSelect select, FontParameter... parameterList) {
        this.bitmapFont = bitmapFont;
        this.renderer = renderer;
        this.stateSelect = select;
        this.fontStates = new FontState[parameterList.length];
        for(int i=0 ; i<parameterList.length ; i++) {
            fontStates[i] = new FontState(parameterList[i]);
        }
        yOffset = -bitmapFont.getAscent();
    }

    @Override
    public int drawText (AnimationState as, int x, int y, CharSequence str) {
        return drawText(as, x, y, str, 0, str.length());
    }

    @Override
    public int drawText (AnimationState as, int x, int y, CharSequence str, int start, int end) {
        FontState fontState = evalFontState(as);
        x += fontState.offsetX;
        y += fontState.offsetY + yOffset;
        bitmapFont.setColor(fontState.color.getRedFloat(), fontState.color.getGreenFloat(), fontState.color.getBlueFloat(), fontState.color.getAlphaFloat());
        int width = MathUtils.ceilPositive(bitmapFont.draw(renderer.batch, str, x, y, start, end).width);
        drawLine(fontState, x, y, width, fontState.color);
        return width;
    }

    void drawLine(FontState fontState, int x, int y, int width, Color color) {
        if((fontState.style & STYLE_UNDERLINE) != 0) {
            drawLine(x, y + (int)(bitmapFont.getAscent() + bitmapFont.getCapHeight()) + fontState.underlineOffset, x + width, color);
        }
        if((fontState.style & STYLE_LINETHROUGH) != 0) {
            drawLine(x, y + (int)(bitmapFont.getLineHeight()/2), x + width, color);
        }
    }

    @Override
    public int drawMultiLineText (AnimationState as, int x, int y, CharSequence str, int width, de.matthiasmann.twl.HAlignment align) {
        FontState fontState = evalFontState(as);
        x += fontState.offsetX;
        y += fontState.offsetY + yOffset;
        bitmapFont.setColor(fontState.color.getRedFloat(), fontState.color.getGreenFloat(), fontState.color.getBlueFloat(), fontState.color.getAlphaFloat());
        return MathUtils.ceilPositive(bitmapFont.drawMultiLine(renderer.batch, str, x, y, width, gdxAlignment[align.ordinal()]).width);
    }

    @Override
    public FontCache cacheText (FontCache cache, CharSequence str) {
        return cacheText(cache, str, 0, str.length());
    }

    @Override
    public FontCache cacheText (FontCache cache, CharSequence str, int start, int end) {
        if (cache == null) {
            cache = new GdxFontCache();
        }
        GdxFontCache bitmapCache = (GdxFontCache)cache;
        bitmapFont.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        bitmapCache.setText(str, 0, yOffset, start, end);
        return cache;
    }

    @Override
    public FontCache cacheMultiLineText (FontCache cache, CharSequence str, int width, de.matthiasmann.twl.HAlignment align) {
        if (cache == null) cache = new GdxFontCache();
        GdxFontCache bitmapCache = (GdxFontCache)cache;
        bitmapFont.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        bitmapCache.setMultiLineText(str, 0, yOffset, width, gdxAlignment[align.ordinal()]);
        return cache;
    }

    @Override
    public void destroy () {
        bitmapFont.dispose();
    }

    @Override
    public int getBaseLine () {
        return (int)bitmapFont.getCapHeight();
    }

    @Override
    public int getLineHeight () {
        return (int)bitmapFont.getLineHeight();
    }

    @Override
    public int getSpaceWidth () {
        return (int)bitmapFont.getSpaceWidth();
    }

    @Override
    public int getEM () {
        return (int)bitmapFont.getLineHeight();
    }

    @Override
    public int getEX () {
        return (int)bitmapFont.getXHeight();
    }

    @Override
    public int computeMultiLineTextWidth (CharSequence str) {
        return MathUtils.ceilPositive(bitmapFont.getMultiLineBounds(str).width);
    }

    @Override
    public int computeTextWidth (CharSequence str) {
        return MathUtils.ceilPositive(bitmapFont.getBounds(str).width);
    }

    @Override
    public int computeTextWidth (CharSequence str, int start, int end) {
        return MathUtils.ceilPositive(bitmapFont.getBounds(str, start, end).width);
    }

    @Override
    public int computeVisibleGlpyhs (CharSequence str, int start, int end, int width) {
        return bitmapFont.computeVisibleGlyphs(str, start, end, width);
    }

    FontState evalFontState(AnimationState as) {
        return fontStates[stateSelect.evaluate(as)];
    }

    static class FontState {
        final Color color;
        final int offsetX;
        final int offsetY;
        final int style;
        final int underlineOffset;

        FontState(FontParameter fontParam) {
            int lineStyle = 0;
            if(fontParam.get(FontParameter.UNDERLINE)) {
                lineStyle |= STYLE_UNDERLINE;
            }
            if(fontParam.get(FontParameter.LINETHROUGH)) {
                lineStyle |= STYLE_LINETHROUGH;
            }

            this.color = fontParam.get(FontParameter.COLOR);
            this.offsetX = fontParam.get(FONTPARAM_OFFSET_X);
            this.offsetY = fontParam.get(FONTPARAM_OFFSET_Y);
            this.style = lineStyle;
            this.underlineOffset = fontParam.get(FONTPARAM_UNDERLINE_OFFSET);
        }
    }

    private class GdxFontCache extends BitmapFontCache implements FontCache {

        public GdxFontCache () {
            super(bitmapFont);
        }

        public void draw (AnimationState as, int x, int y) {
            GdxFont.FontState fontState = evalFontState(as);
            setColors(fontState.color.getRedFloat(), fontState.color.getGreenFloat(), fontState.color.getBlueFloat(), fontState.color.getAlphaFloat());
            setPosition(x + fontState.offsetX, y + fontState.offsetY);
            draw(renderer.batch);
        }

        public int getWidth () {
            return MathUtils.ceilPositive(getBounds().width);
        }

        public int getHeight () {
            return MathUtils.ceilPositive(getBounds().height);
        }

        public void destroy () {
        }
    }

    @Override
    public boolean isProportional () {
        if (proportional == null) {
            try {
                int iBound = (int)bitmapFont.getBounds("i").width;
                int mBound = (int)bitmapFont.getBounds("m").width;
                proportional = iBound != 0 && mBound != 0 && iBound != mBound;
            } catch (Exception e) {
                proportional = false;
            }
        }
        return proportional;
    }

    private float[] lineVertices = new float[4];
    private void drawLine(int x0, int y, int x1, Color color) {
        lineVertices[0] = x0;
        lineVertices[1] = y;
        lineVertices[2] = x1;
        lineVertices[3] = y;
        renderer.drawLine(lineVertices, 2, 1, color, false);
    }

}
