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

package com.htm.taskinstance;

import java.util.List;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.jpa.WorkItemFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class WorkItemFactory {

    @Autowired
    protected TaskInstanceFactory taskInstanceFactory;

    //public static WorkItemFactory newInstance() {
    //    return new WorkItemFactoryImpl();
    //}

    /**
     * Creates all work items for the given task instance based TODO
     *
     * @param taskInstance
     * @return A list of work items for each user that is assigned to the
     *         task instance. If an organizational entity has several role also
     *         several work items are created for the same entity.
     * @throws HumanTaskManagerException
     */
    public abstract List<IWorkItem> createWorkItemsForTaskInstance(
            ITaskInstance taskInstance) throws HumanTaskManagerException;

    public abstract IWorkItem createWorkItemFromEntity(Object workItemObject);

    /**
     * @param userid
     * @param humanRole
     * @param taskInstance
     * @return
     * @throws DatabaseException
     */
    public abstract IWorkItem createNewWorkItem(String userid,
                                                EHumanRoles humanRole, ITaskInstance taskInstance) throws DatabaseException;


}