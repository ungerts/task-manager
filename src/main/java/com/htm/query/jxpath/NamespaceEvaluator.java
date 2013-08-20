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

package com.htm.query.jxpath;

import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.htm.utils.Utilities;

public class NamespaceEvaluator implements NamespaceContext {

    private static final Logger log = Utilities.getLogger(NamespaceEvaluator.class);

    private Hashtable<String, String> namespaces;

    private Element queryNode;

    public NamespaceEvaluator(Element queryNode) {
        this.namespaces = new Hashtable<String, String>();
        this.queryNode = queryNode;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        String namespace;
        namespace = this.queryNode.lookupNamespaceURI(prefix);
        if (namespace != null) {
            if (!this.namespaces.containsKey(prefix)) {
                log.debug("Adding namespace '" + namespace + "' for prefix '" + prefix + "'");
                this.namespaces.put(prefix, namespace);
            }
        } else {
            log.error("Cannot lookup namespace for prefix '" + prefix + "'");
            //namespace = null;
        }
        return namespace;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    public Hashtable<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Hashtable<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Element getQueryNode() {
        return queryNode;
    }

    public void setQueryNode(Element queryNode) {
        this.queryNode = queryNode;
    }


}
