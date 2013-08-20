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

package com.htm.query.views;

import java.sql.Timestamp;

import com.htm.query.IQueryContext;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;

/**
 * This class represents a view on a work item.</br>
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see IWorkItem
 */
public class WorkItemView implements IQueryContext {

    private IAssignedUser assignee;

    private boolean isAssignedToEverybody;

    private ITaskInstance taskInstance;

    private String genericHumanRole;

    private boolean isClaimed;

    private Timestamp creationTime;

    private String id;

    /**
     * Creates a new view object.</br>
     * The properties of an work item where expressions can be
     * evaluated on are set here.
     *
     * @param workItem The workItem where the view has to be created from.
     */
    public WorkItemView(IWorkItem workItem) {
        setId(workItem.getId());
        setAssignee(workItem.getAssignee());
        setAssignedToEverybody(workItem.isAssignedToEverybody());
        setTaskInstance(workItem.getTaskInstance());
        setGenericHumanRole(workItem.getGenericHumanRole().toString());
        setClaimed(workItem.isClaimed());
        setCreationTime(workItem.getCreationTime());
    }


    public IAssignedUser getAssignee() {
        return assignee;
    }

    public void setAssignee(IAssignedUser assignee) {
        this.assignee = assignee;
    }

    public boolean isAssignedToEverybody() {
        return isAssignedToEverybody;
    }

    public void setAssignedToEverybody(boolean isAssignedToEverybody) {
        this.isAssignedToEverybody = isAssignedToEverybody;
    }

    public ITaskInstance getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(ITaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }

    public String getGenericHumanRole() {
        return genericHumanRole;
    }

    public void setGenericHumanRole(String genericHumanRole) {
        this.genericHumanRole = genericHumanRole;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
