/* 
 * Copyright (C) 2013 The JFinal Rythm Plugin project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.rythmengine.jfinal;

import com.jfinal.plugin.activerecord.Model;
import org.rythmengine.extension.IPropertyAccessor;
import org.rythmengine.utils.S;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enable use @foo.key notation to reference @foo.get("key")
 */
public class ActiveRecordPropAccessor implements IPropertyAccessor {
    @Override
    public Class getTargetType() {
        return Model.class;
    }
    
    private Map<String, Method> getters = new HashMap<String, Method>();
    private Set<String> nonGetters = new HashSet<String>();

    @Override
    public Object getProperty(String name, Object contextObj) {
        if (null == contextObj) return null;
        Model model = (Model)contextObj;
        if (name.contains("attr")) {
            if ("attrNames".equalsIgnoreCase(name)) {
                return model.getAttrNames();
            } else if ("attrsEntrySet".equalsIgnoreCase(name)) {
                return model.getAttrsEntrySet();
            } else if ("attrValues".equalsIgnoreCase(name)) {
                return model.getAttrValues();
            }
        }
        // try reflection first in case user defined 
        // their own logic to get property
        Class c = contextObj.getClass();
        String k = c.getName() + ":" + name;
        if (nonGetters.contains(k)) {
            return model.get(name);
        }
        Method m = getters.get(k);
        if (null == m) {
            // try javabean method names
            String mn = "get" + S.capFirst(name);
            try {
                m = c.getMethod(mn);
            } catch (Exception e) {
                // ignore it
            }
            mn = "is" + S.capFirst(name);
            try {
                m = c.getMethod(mn);
            } catch (Exception e) {
                // ignore it
            }
        }
        if (null == m) {
            nonGetters.add(k);
            return model.get(name);
        } else {
            getters.put(k, m);
            try {
                return m.invoke(contextObj);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private Map<String, Method> setters = new HashMap<String, Method>();
    private Set<String> nonSetters = new HashSet<String>();

    @Override
    public Object setProperty(String name, Object contextObj, Object value) {
        if (null == contextObj) return null;
        Model model = (Model)contextObj;
        if (name.equalsIgnoreCase("attrs")) {
            if (null == value) return null;
            if (value instanceof Map) {
                model.setAttrs((Map) value);
            } else if (value instanceof Model) {
                model.setAttrs((Model) value);
            }
            return null;
        }
        // try reflection first in case user defined 
        // their own logic to get property
        Class c = contextObj.getClass();
        String k = c.getName() + ":" + name;
        if (nonSetters.contains(k)) {
            model.set(name, value);
        }
        Method m = setters.get(k);
        if (null == m) {
            // try javabean method names
            String mn = "set" + S.capFirst(name);
            try {
                m = c.getMethod(mn);
            } catch (Exception e) {
                // ignore it
            }
        }
        if (null == m) {
            nonSetters.add(k);
            model.set(name, value);
        } else {
            setters.put(k, m);
            try {
                m.invoke(contextObj, value);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }
}
