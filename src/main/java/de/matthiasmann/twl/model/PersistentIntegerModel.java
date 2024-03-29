/*
 * Copyright (c) 2008, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.matthiasmann.twl.model;

import com.badlogic.gdx.Preferences;

/**
 *
 * @author Matthias Mann
 */
public class PersistentIntegerModel extends AbstractIntegerModel {

    private final Preferences prefs;
    private final String prefKey;
    private final int minValue;
    private final int maxValue;

    private int value;
    
    public PersistentIntegerModel(Preferences prefs, String prefKey, int minValue, int maxValue, int defaultValue) {
        if(maxValue < minValue) {
            throw new IllegalArgumentException("maxValue < minValue");
        }
        if(prefs == null) {
            throw new NullPointerException("prefs");
        }
        if(prefKey == null) {
            throw new NullPointerException("prefKey");
        }
        this.prefs = prefs;
        this.prefKey = prefKey;
        this.minValue = minValue;
        this.maxValue = maxValue;
        setValue(prefs.getInteger(prefKey, defaultValue));
    }

    public PersistentIntegerModel(int minValue, int maxValue, int value) {
        if(maxValue < minValue) {
            throw new IllegalArgumentException("maxValue < minValue");
        }
        this.prefs = null;
        this.prefKey = null;
        this.minValue = minValue;
        this.maxValue = maxValue;
        setValue(value);
    }

    public int getValue() {
        return value;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setValue(int value) {
        if(value > maxValue) {
            value = maxValue;
        } else if(value < minValue) {
            value = minValue;
        }
        if(this.value != value) {
            this.value = value;
            storeSetting();
            doCallback();
        }
    }

    private void storeSetting() {
        if(prefs != null) {
            prefs.putInteger(prefKey, value);
        }
    }
    
}
