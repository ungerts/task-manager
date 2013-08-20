/*
 * Copyright 2012 Bangkok Project Team, GRIDSOLUT GmbH + Co.KG, and
 * University of Stuttgart (Institute of Architecture of Application Systems)
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allweathercomputing.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class MyNameSpaceContext implements NamespaceContext {

    private Hashtable<String, String> namespaces = new Hashtable<String, String>();

    public MyNameSpaceContext() {

    }

    public void addNamespace(String prefix, String namespaceURI) {
        this.namespaces.put(prefix, namespaceURI);
    }

    @Override
    public String getNamespaceURI(String prefix) {

        return this.namespaces.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        String prefix = null;
        if (this.namespaces.containsValue(namespaceURI)) {
            for (String prefix1 : this.namespaces.keySet()) {
                if (this.namespaces.get(prefix1).equals(namespaceURI)) {
                    prefix = prefix1;
                    break;
                }
            }
        }
        return prefix;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        ArrayList<String> prefixes = new ArrayList<String>();
        for (String prefix : this.namespaces.keySet()) {
            if (namespaceURI.equals(this.namespaces.get(prefix))) {
                prefixes.add(prefix);

            }
        }
        return prefixes.iterator();

    }

}
