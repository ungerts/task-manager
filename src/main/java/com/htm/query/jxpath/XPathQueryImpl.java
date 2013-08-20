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

import java.io.Serializable;
import java.util.Hashtable;

import com.htm.query.IQuery;
import com.htm.query.IQueryEvaluator;

public class XPathQueryImpl implements IQuery, IJXPathQuery, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7698579420657301156L;

    private boolean durationQuery;

    private String query;

    private Hashtable<String, String> namespaces;

    public XPathQueryImpl() {
        this.durationQuery = false;
    }

    public XPathQueryImpl(String query) {
        super();
        this.query = query;
        this.durationQuery = false;
    }

    public XPathQueryImpl(String query, Hashtable<String, String> namespaces) {
        super();
        this.query = query;
        this.namespaces = namespaces;
        this.durationQuery = false;
    }


    public XPathQueryImpl(boolean durationQuery, String query,
                          Hashtable<String, String> namespaces) {
        super();
        this.durationQuery = durationQuery;
        this.query = query;
        this.namespaces = namespaces;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getQueryLanguage() {
        return IQueryEvaluator.XPATH_QUERY;
    }

    @Override
    public void setQuery(String query) {
        this.query = query;

    }

    @Override
    public void addNamespace(String prefix, String namespaceURI) {
        if (this.namespaces == null) {
            this.namespaces = new Hashtable<String, String>();
        }
        this.namespaces.put(prefix, namespaceURI);

    }

    @Override
    public Hashtable<String, String> getNamespaces() {
        // TODO Auto-generated method stub
        return this.namespaces;
    }

    @Override
    public void setNamespaces(Hashtable<String, String> namespaces) {
        this.namespaces = namespaces;

    }

    @Override
    public void removeNamespace(String prefix) {
        if (this.namespaces == null) {
            this.namespaces.remove(prefix);
        }

    }

    @Override
    public boolean isDurationQuery() {
        return durationQuery;
    }

    @Override
    public void setDurationQuery(boolean durationQuery) {
        this.durationQuery = durationQuery;
    }


}
