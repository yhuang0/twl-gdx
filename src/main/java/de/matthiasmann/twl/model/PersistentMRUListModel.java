/*
 * Copyright (c) 2008-2009, Matthias Mann
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A persistent MRU list model.
 *
 * Entries are stored compressed (deflate) using serialization and
 * <code>putByteArray</code> except Strings which use <code>put</code>
 *
 * @param <T> the data type stored in this MRU model
 * 
 * @see java.util.zip.Deflater
 * @see java.util.prefs.Preferences#putByteArray(java.lang.String, byte[])
 * @see java.util.prefs.Preferences#put(java.lang.String, java.lang.String)
 * 
 * @author Matthias Mann
 */
public class PersistentMRUListModel<T extends Serializable> extends SimpleMRUListModel<T> {

    private final Class<T> clazz;
    private final Preferences prefs;
    private final String prefKey;

    public PersistentMRUListModel(int maxEntries, Class<T> clazz, Preferences prefs, String prefKey) {
        super(maxEntries);
        if(clazz == null) {
            throw new NullPointerException("clazz");
        }
        if(prefs == null) {
            throw new NullPointerException("prefs");
        }
        if(prefKey == null) {
            throw new NullPointerException("prefKey");
        }
        this.clazz = clazz;
        this.prefs = prefs;
        this.prefKey = prefKey;

        int numEntries = Math.min(prefs.getInteger(keyForNumEntries(), 0), maxEntries);
        for(int i=0 ; i<numEntries ; ++i) {
            T entry = null;
            if(clazz == String.class) {
                entry = (T)prefs.getString(keyForIndex(i), null);
            } else {
                String data = prefs.getString(keyForIndex(i), null);
                if(data != null && data.length() > 0) {
                    entry = deserialize(data);
                }
            }
            if(entry != null) {
                entries.add(entry);
            }
        }
    }

    @Override
    public void addEntry(T entry) {
        if(!ClassReflection.isInstance(clazz, entry)) {
            throw new ClassCastException();
        }
        super.addEntry(entry);
    }

    @Override
    protected void saveEntries() {
        for(int i=0 ; i<entries.size() ; ++i) {
            T obj = entries.get(i);
            if(clazz == String.class) {
                prefs.putString(keyForIndex(i), (String) obj);
            } else {
                String data = serialize(obj);
                prefs.putString(keyForIndex(i), data);
            }
        }
        prefs.putInteger(keyForNumEntries(), entries.size());
    }

    protected String serialize(T obj) {
        Json json = new Json();
        return json.toJson(obj, clazz);
    }

    protected T deserialize(String data) {
        Json json = new Json();
        return json.fromJson(clazz, data);
    }
    
    protected String keyForIndex(int idx) {
        return prefKey + "_" + idx;
    }
    protected String keyForNumEntries() {
        return prefKey + "_entries";
    }

    private void close(Closeable c) {
        try {
            c.close();
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "exception while closing stream", ex);
        }
    }

    Logger getLogger() {
        return Logger.getLogger(PersistentMRUListModel.class.getName());
    }
}
