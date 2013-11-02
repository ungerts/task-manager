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

package com.htm.taskinstance.jpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.htm.dm.EHumanRoles;
import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Assigneduser;
import com.htm.entities.jpa.Humantaskinstance;
import com.htm.entities.jpa.Workitem;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.utils.Utilities;

public class WorkItemWrapper extends Observable implements IWorkItem {

    protected Workitem workItemEntity;

    public WorkItemWrapper(Workitem adaptee) {
        this.workItemEntity = adaptee;
    }

    public WorkItemWrapper() {
        this.workItemEntity = new Workitem();
    }


    public IAssignedUser getAssignee() {
        //return TaskInstanceFactory.newInstance().createAssignedUserFromEntity(
        //        workItemEntity.getAssigneduser());
        //return workItemEntity.getAssigneduser();
        //Assigneduser assignedUserObject = workItemEntity.getAssigneduser();
        return Utilities.createAssignedUserFromEntity(workItemEntity.getAssigneduser());

    }

    public Timestamp getCreationTime() {
        return workItemEntity.getCreationtime();
    }

    public EHumanRoles getGenericHumanRole() {
        /* If the work item is claimed the owner
           * of the work item is the actual owner */
        if (isClaimed()) {
            return EHumanRoles.ACTUAL_OWNER;
        }

        return EHumanRoles.valueOf(workItemEntity.getGenerichumanrole());

    }

    public boolean isAssignedToEverybody() {
        return Utilities.shortToBoolean(workItemEntity.getEverybody());
    }

    public boolean isClaimed() {
        return Utilities.shortToBoolean(workItemEntity.getClaimed());
    }

    public String getId() {
        return Integer.toString(workItemEntity.getId());
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public void setAssignee(IAssignedUser assignee) {

        Assigneduser assignedUserEntity = (Assigneduser) assignee.getAdaptee();
        workItemEntity.setAssigneduser(assignedUserEntity);

    }

    public void setCreationTime(Timestamp creationTime) {
        workItemEntity.setCreationtime(creationTime);
    }

    public void setGenericHumanRole(EHumanRoles genericHumanRole) {
        /*
           * If the role 'Actual Owner' is set the work item
           * is in the 'claimed'. If the work item is claimed the former
           * human role must be preserved because if the work item is released once
           * the former role has to be restored. That's why we only set the work item to
           * claimed.
           */
        if (EHumanRoles.ACTUAL_OWNER.equals(genericHumanRole)) {
            setClaimed(true);
        } else {
            workItemEntity.setGenerichumanrole(genericHumanRole.toString());
        }
    }

    public void setId(String id) {
        workItemEntity.setId(Utilities.transfrom2PrimaryKey(id));
    }

    public void setAssignedToEverbody(boolean assignedToEverbody) {
        workItemEntity.setEverybody(Utilities.booleanToShort(assignedToEverbody));
    }

    public void setClaimed(boolean claimed) {
        workItemEntity.setClaimed(Utilities.booleanToShort(claimed));

    }

    public void setTaskInstance(ITaskInstance taskInstance) {
        Humantaskinstance taskInstanceEntity = (Humantaskinstance) taskInstance.getAdaptee();

        workItemEntity.setHumantaskinstance(taskInstanceEntity);

        /* This is a bi-directional jpa relationship between work items and task instances
           * thus the work item has to be added to the task instance as well
           */
        List<Workitem> taskWorkitemEntities = taskInstanceEntity.getWorkitems();
        if (taskWorkitemEntities == null) {
            taskWorkitemEntities = new ArrayList<Workitem>();
            taskInstanceEntity.setWorkitems(taskWorkitemEntities);
        }

        taskWorkitemEntities.add(workItemEntity);

    }

    public ITaskInstance getTaskInstance() {
        return Utilities.createTaskInstanceFromEntity(
                workItemEntity.getHumantaskinstance());
    }

    public WrappableEntity getAdaptee() {
        return workItemEntity;
    }


}
