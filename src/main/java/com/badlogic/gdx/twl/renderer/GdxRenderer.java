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

import java.io.IOException;
import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessorQueue;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.twl.input.GdxInput;
import com.badlogic.gdx.utils.BufferUtils;

import com.badlogic.gdx.utils.TimeUtils;
import de.matthiasmann.twl.Rect;
import de.matthiasmann.twl.input.Input;
import de.matthiasmann.twl.renderer.*;
import de.matthiasmann.twl.utils.ClipStack;
import de.matthiasmann.twl.utils.StateSelect;

// BOZO - Add cursors.

/**
 * @author Yifu Huang
 * @author Nathan Sweet
 * @author Matthias Mann
 * @author Kurtis Kopf
 */
public class GdxRenderer implements Renderer, LineRenderer {

    private int mouseX, mouseY;
    private boolean useSWMouseCursors;

    private GdxCacheContext cacheContext;
    private boolean hasScissor;
    private final TintStack tintStateRoot = new TintStack();
    private TintStack tintStack = tintStateRoot;
    private final Color tempColor = new Color(1, 1, 1, 1);
    private boolean rendering;
    private int width, height;
    private Input input;
    final SpriteBatch batch;

    private final ClipStack clipStack;
    protected final Rect clipRectTemp;

    private FontMapper fontMapper;
    private AssetManager assetManager;

    public GdxRenderer (SpriteBatch batch, AssetManager assetManager, InputProcessorQueue inputProcessor) {
        input = new GdxInput(inputProcessor);
        this.batch = batch;
        this.assetManager = assetManager;
        clipStack = new ClipStack();
        clipRectTemp = new Rect();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public GdxCacheContext createNewCacheContext () {
        return new GdxCacheContext(this);
    }

    @Override
    public GdxCacheContext getActiveCacheContext () {
        if (cacheContext == null) {
            setActiveCacheContext(createNewCacheContext());
        }
        return cacheContext;
    }

    @Override
    public void setActiveCacheContext (CacheContext cacheContext) throws IllegalStateException {
        if (cacheContext == null) throw new IllegalArgumentException("cacheContext cannot be null.");
        if (!cacheContext.isValid()) throw new IllegalStateException("cacheContext is invalid.");
        if (!(cacheContext instanceof GdxCacheContext))
            throw new IllegalArgumentException("cacheContext is not from this renderer.");
        if (((GdxCacheContext)cacheContext).renderer != this)
            throw new IllegalArgumentException("cacheContext is not from this renderer.");
        this.cacheContext = (GdxCacheContext)cacheContext;
    }

    @Override
    public long getTimeMillis () {
        return TimeUtils.nanoTime() / 1000000;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public boolean startRendering() {
        tintStack = tintStateRoot;
        batch.begin();
        rendering = true;
        return true;
    }

    @Override
    public void endRendering() {
        rendering = false;
        batch.end();
        if (hasScissor) {
            Gdx.gl.glDisable(Gdx.gl.GL_SCISSOR_TEST);
            hasScissor = false;
        }
    }

    public void setClipRect () {
        if (rendering) batch.flush();
        final Rect rect = clipRectTemp;
        if (clipStack.getClipRect(rect)) {
            Gdx.gl.glScissor(rect.getX(), Gdx.graphics.getHeight() - rect.getBottom(), rect.getWidth(), rect.getHeight());
            if (!hasScissor) {
                Gdx.gl.glEnable(Gdx.gl.GL_SCISSOR_TEST);
                hasScissor = true;
            }
        } else if (hasScissor) {
            Gdx.gl.glDisable(Gdx.gl.GL_SCISSOR_TEST);
            hasScissor = false;
        }
    }

    @Override
    public Font loadFont(FileHandle url, StateSelect select, FontParameter... parameterList) throws IOException {
        if(url == null) {
            throw new NullPointerException("url");
        }
        if(select == null) {
            throw new NullPointerException("select");
        }
        if(parameterList == null) {
            throw new NullPointerException("parameterList");
        }
        if(select.getNumExpressions() + 1 != parameterList.length) {
            throw new IllegalArgumentException("select.getNumExpressions() + 1 != parameterList.length");
        }
        BitmapFont bitmapFont = getActiveCacheContext().loadBitmapFont(url);
        return new GdxFont(this, bitmapFont, select, parameterList);
    }

    @Override
    public Texture loadTexture (FileHandle handle, String formatStr, String filterStr) throws IOException {
        if (handle == null) {
            throw new IllegalArgumentException("handle cannot be null.");
        }
        return getActiveCacheContext().loadTexture(handle);
    }

    @Override
    public int getWidth () {
        return width;
    }

    @Override
    public int getHeight () {
        return height;
    }

    public void setViewportSize(int width, int height) {
        this.width = width;
        this.height = height;
        //batch.getProjectionMatrix().setToOrtho(0, width, height, 0, 0, 1);
    }

    @Override
    public LineRenderer getLineRenderer () {
        return this;
    }

    @Override
    public DynamicImage createDynamicImage (int width, int height) {
        return null; // Unsupported.
    }

    @Override
    public Image createGradient(Gradient gradient) {
        //return new GradientImage(this, gradient);
        return null;
    }

    @Override
    public void setCursor (MouseCursor cursor) {
        // Unsupported
    }

    @Override
    public void setMouseButton (int arg0, boolean arg1) {
        // Unsupported
    }

    @Override
    public void setMousePosition (int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public void pushGlobalTintColor (float r, float g, float b, float a) {
        tintStack = tintStack.push(r, g, b, a);
    }

    @Override
    public void popGlobalTintColor () {
        tintStack = tintStack.previous;
    }

    public Color getColor (de.matthiasmann.twl.Color color) {
        Color tempColor = this.tempColor;
        TintStack tintStack = this.tintStack;
        tempColor.r = tintStack.r * (color.getR() & 255);
        tempColor.g = tintStack.g * (color.getG() & 255);
        tempColor.b = tintStack.b * (color.getB() & 255);
        tempColor.a = tintStack.a * (color.getA() & 255);
        return tempColor;
    }

    public void dispose () {
        if (cacheContext != null) {
            cacheContext.destroy();
            cacheContext = null;
        }
        batch.dispose();
    }

    static private class TintStack extends Color {
        final TintStack previous;

        public TintStack () {
            super(1 / 255f, 1 / 255f, 1 / 255f, 1 / 255f);
            this.previous = this;
        }

        private TintStack (TintStack prev) {
            super(prev.r, prev.g, prev.b, prev.a);
            this.previous = prev;
        }

        public TintStack push (float r, float g, float b, float a) {
            TintStack next = new TintStack(this);
            next.r = this.r * r;
            next.g = this.g * g;
            next.b = this.b * b;
            next.a = this.a * a;
            return next;
        }
    }

    @Override
    public void clipEnter (Rect rect) {
        clipStack.push(rect);
        setClipRect();
    }

    @Override
    public void clipEnter (int x, int y, int w, int h) {
        clipStack.push(x, y, w, h);
        setClipRect();
    }

    @Override
    public void clipLeave () {
        clipStack.pop();
        setClipRect();
    }

    @Override
    public boolean clipIsEmpty () {
        return clipStack.isClipEmpty();
    }

    @Override
    public OffscreenRenderer getOffscreenRenderer () {
        // this is the same as in LWJGLRenderer in the main TWL project
        return null;
    }

    @Override
    public FontMapper getFontMapper() {
        return fontMapper;
    }

    /**
     * Installs a font mapper. It is the responsibility of the font mapper to
     * manage the OpenGL state correctly so that normal rendering by LWJGLRenderer
     * is not disturbed.
     *
     * @param fontMapper the font mapper object - can be null.
     */
    public void setFontMapper(FontMapper fontMapper) {
        this.fontMapper = fontMapper;
    }

    @Override
    public void drawLine (float[] pts, int numPts, float width, de.matthiasmann.twl.Color color, boolean drawAsLoop) {
        if (numPts * 2 > pts.length) {
            throw new ArrayIndexOutOfBoundsException(numPts * 2);
        }
        if (numPts >= 2) {
            if (Gdx.gl != null) {
                //tintStack.push(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
                Gdx.gl.glDisable(Gdx.gl.GL_TEXTURE_2D);
                Gdx.gl.glLineWidth(width);
                FloatBuffer fb = BufferUtils.newFloatBuffer(pts.length);
                fb.put(pts);
                fb.position(0);
                Gdx.gl.glEnableVertexAttribArray(0);
                Gdx.gl.glVertexAttribPointer(0, 2, Gdx.gl.GL_FLOAT, false, 0, fb);
                Gdx.gl.glBlendColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
                Gdx.gl.glDrawArrays((drawAsLoop ? Gdx.gl.GL_LINE_LOOP : Gdx.gl.GL_LINE_STRIP), 0, numPts);
                Gdx.gl.glBlendColor(tintStack.r, tintStack.g, tintStack.b, tintStack.a);
                Gdx.gl.glDisableVertexAttribArray(0);
                Gdx.gl.glEnable(Gdx.gl.GL_TEXTURE_2D);
            }
        }
    }

    /**
     * Controls if the mouse cursor is rendered via SW or HW cursors.
     * HW cursors have reduced support for transparency and cursor size.
     *
     * This must be set before loading a theme !
     *
     * @param useSWMouseCursors
     */
    public void setUseSWMouseCursors(boolean useSWMouseCursors) {
        this.useSWMouseCursors = useSWMouseCursors;
    }

    public boolean isUseSWMouseCursors() {
        return useSWMouseCursors;
    }

    /**
     * <p>Queries the current view port size & position and updates all related
     * internal state.</p>
     *
     * <p>It is important that the internal state matches the OpenGL viewport or
     * clipping won't work correctly.</p>
     *
     * <p>This method should only be called when the viewport size has changed.
     * It can have negative impact on performance to call every frame.</p>
     *
     * @see #getWidth()
     * @see #getHeight()
     */
    public void syncViewportSize() {
        /*
        ib16.clear();
        GL11.glGetInteger(GL11.GL_VIEWPORT, ib16);
        viewportX = ib16.get(0);
        width = ib16.get(2);
        height = ib16.get(3);
        viewportBottom = ib16.get(1) + height;
        */
    }
}
