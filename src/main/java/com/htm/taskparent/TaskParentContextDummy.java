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

package com.htm.taskparent;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;

import com.htm.taskinstance.ITaskParentContext;
import com.htm.utils.Utilities;

public class TaskParentContextDummy implements ITaskParentContext {

    public static final String PROCESS_VARIABLES = "humanTask";

    public static final String PRIORITY = "1";

    public static final String STARTBY = "1249622420945";

    public static final String COMPLETEBY = "1249642420945";

    public static final String SKIPABLE = "true";

    private Map<String, Document> properties = new HashMap<String, Document>();

    public TaskParentContextDummy() {
        properties.put(PROCESS_VARIABLES, Utilities.getXMLFromString(
                "<infos>" +
                        "<priority>" + PRIORITY + "</priority>" +
                        "<skipable>" + SKIPABLE + "</skipable>" +
                        "<startBy>" + STARTBY + "</startBy>" +
                        "<completeBy>" + COMPLETEBY + "</completeBy>" +
                        "</infos>"));
    }

    public Map<String, Document> getProperties() {
        return properties;
    }


}
