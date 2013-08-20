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

import com.htm.taskinstance.ITaskParentContext;

/**
 * This is a task parent connector created for testing purposes.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public class TaskParentConnectorDummy implements ITaskParentConnector {

    public static final String TASK_PARENT_ID = "64";

    /* (non-Javadoc)
      * @see com.bosch.workflow.core.taskhandler.taskparent.ITaskParentConnector#getTaskParentContext()
      */
    public ITaskParentContext getTaskParentContext() {
        return new TaskParentContextDummy();
    }


    /* (non-Javadoc)
      * @see com.bosch.workflow.core.taskhandler.taskparent.ITaskParentConnector#getCallbackHandler()
      */
    public ICallbackHandler getCallbackHandler() {
        return new DummyCallbackHandler();
    }

    /* (non-Javadoc)
      * @see com.bosch.workflow.core.taskhandler.taskparent.ITaskParentConnector#getTaskParentId()
      */
    public String getTaskParentId() {
        return TASK_PARENT_ID;
    }


}
