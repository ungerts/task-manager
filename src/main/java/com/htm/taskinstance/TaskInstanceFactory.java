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

import java.sql.Timestamp;
import java.util.Set;


import com.htm.entities.WrappableEntity;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.jpa.TaskInstanceFactoryJPA;

public abstract class TaskInstanceFactory {

    //public static TaskInstanceFactory newInstance() {
    //    return new TaskInstanceFactoryJPA();
    //}

    //TODO optional attributes like actual people, priority, expire


    public abstract ITaskInstance createTaskInstance(String taskModelName,
                                                     String taskInstanceName, Object input,
                                                     String taskParentId, Set<ICorrelationProperty> correlationProperties, Timestamp expirationTime) throws HumanTaskManagerException;

    public abstract ITaskInstance createTaskInstanceFromEntity(WrappableEntity taskInstanceObject);

    public abstract IAttachment createAttachmentFromEntity(WrappableEntity attachmentObject);

    public abstract ICorrelationProperty createCorrelationPropertyFromEntity(WrappableEntity correlationPropsObject);

    public abstract ICorrelationProperty createCorrelationProperty(String name);

    public abstract IAssignedUser createAssignedUserFromEntity(WrappableEntity assignedUserObject);

    public abstract IAssignedUser createAssignedUser(String userId) throws DatabaseException;

    public abstract IAttachment createAttachment(String name);

    public abstract IFault createFault(String name, Object faultData);

    public abstract void evaluateQueryProperties(String id, String taskModelName);
}
