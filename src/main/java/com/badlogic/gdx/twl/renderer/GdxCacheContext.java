/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.twl.renderer;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ObjectMap;

import de.matthiasmann.twl.renderer.CacheContext;

/**
 * @author Yifu Huang
 * @author Nathan Sweet
 */
public class GdxCacheContext implements CacheContext {

    final GdxRenderer renderer;
    private final ObjectMap<String, GdxTexture> textures = new ObjectMap();
    private boolean valid = true;

    GdxCacheContext (GdxRenderer renderer) {
        this.renderer = renderer;
    }

    GdxTexture loadTexture(FileHandle handle) throws IOException {
        String urlString = handle.toString();
        GdxTexture texture = textures.get(urlString);
        if (texture == null) {
            if (!valid) {
                throw new IllegalStateException("CacheContext has been destroyed.");
            }
            texture = new GdxTexture(renderer, handle);
            textures.put(urlString, texture);
        }
        return texture;
    }

    public ObjectMap.Values<GdxTexture> getTextures() {
        return textures.values();
    }

    public BitmapFont loadBitmapFont(FileHandle fontFile) throws IOException {
        String urlString = fontFile.toString();
        return renderer.getAssetManager().get(fontFile.path());
    }

    public boolean isValid () {
        return valid;
    }

    public void destroy () {
        try {
            for (GdxTexture texture : textures.values()) {
                texture.destroy();
            }
            /*for (BitmapFont bitmapFont : fonts.values())
                bitmapFont.dispose();*/
        } finally {
            textures.clear();
            //fonts.clear();
            valid = false;
        }
    }
}
