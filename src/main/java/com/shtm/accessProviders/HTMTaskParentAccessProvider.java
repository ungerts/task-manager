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

package com.shtm.accessProviders;

import com.htm.TaskParentInterface;
import com.htm.TaskParentInterfaceImpl;
import com.htm.exceptions.HumanTaskManagerException;

public class HTMTaskParentAccessProvider implements
        IHTMTaskParentAccessProvider {

    public static final String TASK_PARENT_ID = "64";
    // private static HTMTaskParentAccessProvider tpaInstance;
    private TaskParentInterface tpi;

    public static HTMTaskParentAccessProvider getTaskParentInstance() {

        // if (tpaInstance == null) {
        // tpaInstance = new HTMTaskParentAccessProvider();
        // }

        // return tpaInstance;
        return new HTMTaskParentAccessProvider();
    }

    private HTMTaskParentAccessProvider() {
        tpi = new TaskParentInterfaceImpl();
    }

    public String createSubTaskInstance(String taskModelName,
                                        String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        String tiid = tpi.createTaskInstance(TASK_PARENT_ID, null,
                taskModelName, taskInstanceName, inputData, null, null);
        return tiid;

    }

    public String createMergeTaskInstance(String taskModelName,
                                          String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        String tiid = tpi.createTaskInstance(TASK_PARENT_ID, null,
                taskModelName, taskInstanceName, inputData, null, null);

        return tiid;
    }

    public void exitTaskInstance(String tiid) throws HumanTaskManagerException {
        tpi.exit(tiid);
    }

}
