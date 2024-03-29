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
package de.matthiasmann.twl.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.util.HashMap;

/**
 *
 * @author Matthias Mann
 */
public class ClassUtils {

    private ClassUtils() {
    }

    private static final HashMap<Class<?>, Class<?>> primitiveTypeMap = new HashMap<Class<?>, Class<?>>();
    static {
        primitiveTypeMap.put(Boolean.TYPE, Boolean.class);
        primitiveTypeMap.put(Byte.TYPE, Byte.class);
        primitiveTypeMap.put(Short.TYPE, Short.class);
        primitiveTypeMap.put(Character.TYPE, Character.class);
        primitiveTypeMap.put(Integer.TYPE, Integer.class);
        primitiveTypeMap.put(Long.TYPE, Long.class);
        primitiveTypeMap.put(Float.TYPE, Float.class);
        primitiveTypeMap.put(Double.TYPE, Double.class);
    }

    public static Class<?> mapPrimitiveToWrapper(Class<?> clazz) {
        Class<?> mappedClass = primitiveTypeMap.get(clazz);
        return mappedClass != null ? mappedClass : clazz;
    }

    public static boolean isParamCompatible(Class<?> type, Object obj) {
        if(obj == null && !type.isPrimitive()) {
            return true;
        }
        type = mapPrimitiveToWrapper(type);
        return ClassReflection.isInstance(type, obj);
    }

    public static boolean isParamsCompatible(Class<?>[] types, Object[] params) {
        if(types.length != params.length) {
            return false;
        }
        for(int i=0 ; i<types.length ; i++) {
            if(!isParamCompatible(types[i], params[i])) {
                return false;
            }
        }
        return true;
    }
}
