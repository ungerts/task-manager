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

import com.htm.dm.EHumanRoles;
import com.htm.dm.IDataModelElement;


public interface IWorkItem extends IDataModelElement {

    public String getId();

    public IAssignedUser getAssignee();

    public boolean isAssignedToEverybody();

    public ITaskInstance getTaskInstance();

    public EHumanRoles getGenericHumanRole();

    public boolean isClaimed();

    public Timestamp getCreationTime();

    public void setTaskInstance(ITaskInstance taskInstance);

    public void setClaimed(boolean claimed);

    public void setAssignedToEverbody(boolean assignedToEverbody);

    public void setId(String id);

    public void setGenericHumanRole(EHumanRoles genericHumanRole);

    public void setCreationTime(Timestamp creationTime);

    public void setAssignee(IAssignedUser assignee);

}
