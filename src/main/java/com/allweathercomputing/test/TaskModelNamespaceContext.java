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

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;

import com.htm.utils.Utilities;

public class TaskModelNamespaceContext implements NamespaceContext {

    private static final Logger log = Utilities.getLogger(TaskModelNamespaceContext.class);

    @Override
    public String getNamespaceURI(String prefix) {
        log.debug("getNamespaceURI - prefix '" + prefix + "'");
        return "http://example.org";
    }

    @Override
    public String getPrefix(String namespaceURI) {

        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {

        return null;
    }

}
