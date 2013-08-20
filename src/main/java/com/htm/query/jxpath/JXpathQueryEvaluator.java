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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.Logger;

import com.htm.exceptions.InvalidQueryException;
import com.htm.query.IQuery;
import com.htm.query.IQueryEvaluator;
import com.htm.query.views.TaskInstanceView;
import com.htm.taskinstance.ITaskInstance;
import com.htm.utils.Utilities;

public class JXpathQueryEvaluator implements IQueryEvaluator {

    private JXPathContext jXpathContext;

    public static final String FUNCTIONS_PREFIX = "htm";

    private Logger log;

    public JXpathQueryEvaluator(Object evalContext) {
        this.setContext(evalContext);
        this.log = Utilities.getLogger(this.getClass());
        /*
           * Enable lenient mode to prevent that there exceptions are thrown when
           * xpath does not map to an existing property - instead null is returned
           */
        this.jXpathContext.setLenient(true);
        /* Add our user defined XPath functions */
        this.jXpathContext.setFunctions(new ClassFunctions(
                XPathFunctions.class, FUNCTIONS_PREFIX));

    }

    public List<?> evaluateQuery(IQuery query)
            throws com.htm.exceptions.IllegalArgumentException {
        log.debug("JXPath: Evaluating XPath query '" + query.getQuery()
                + "' within context " + jXpathContext.getContextBean());
        if (query instanceof XPathQueryImpl) {
            XPathQueryImpl xpath = (XPathQueryImpl) query;
            Hashtable<String, String> namespaces = xpath.getNamespaces();
            if (namespaces != null) {
                for (String key : namespaces.keySet()) {
                    this.jXpathContext.registerNamespace(key,
                            namespaces.get(key));
                }
            }
        }
        try {
            // TODO if a jdom object is addressed by an xpath expression that is
            // no leaf node
            // then the values of all leaf nodes are returned. This ha to be
            // handled somehow

            // Object result = this.jXpathContext.getValue(query.getQuery());
            //
            // /* Return empty list if xpath query doesn't match any property */
            // if (result == null) {
            // log.debug("JXPath: Query didn't match");
            // return new ArrayList<Object>();
            // } else if (result instanceof List<?>) {
            // log.debug("JXPath: Query matched. Result type "
            // + result.getClass().getName());
            // return (List<?>) result;
            //
            // } else {
            // log.debug("JXPath: Query matched. Result type "
            // + result.getClass().getName());
            // List<Object> resultList = new ArrayList<Object>();
            // resultList.add(result);
            //
            // return resultList;
            // }
            ArrayList<Object> list = new ArrayList<Object>();

            Iterator iterator = this.jXpathContext.iteratePointers(query
                    .getQuery());
            Pointer pointer;
            while (iterator.hasNext()) {
                pointer = (Pointer) iterator.next();
                log.debug("JXPath pointer: " + pointer.asPath());
                list.add(pointer.getNode());
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InvalidQueryException(e);
        }

    }

    public void setContext(Object context) {

        /*
           * If the context is the same like the current context do nothing this
           * reduces multiple instantiation (e.g. of task instance views). If the
           * context is of type task instance a view object of this task instance
           * is created where the queries can be performed on
           */
        if (jXpathContext != null
                && jXpathContext.getContextBean().equals(context)) {
            return;
        } else if (context instanceof ITaskInstance) {
            TaskInstanceView instanceView = new TaskInstanceView(
                    (ITaskInstance) context);
            this.jXpathContext = JXPathContext.newContext(instanceView);

        } else {

            this.jXpathContext = JXPathContext.newContext(context);
        }

    }

    public Object getContext() {
        return this.jXpathContext.getContextBean();
    }

}
