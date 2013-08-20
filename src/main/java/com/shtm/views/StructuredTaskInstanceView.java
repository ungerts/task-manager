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

package com.shtm.views;

import java.util.ArrayList;
import java.util.List;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.shtm.operationAndStates.EStates;
import com.shtm.structureClasses.TaskStructure;

public class StructuredTaskInstanceView {
    List<String> controlledTaskIds = new ArrayList<String>();
    List<String> subTaskIds = new ArrayList<String>();
    String mergeTaskId = null;
    String parentTaskId = null;
    int structureNr;
    boolean hasSubTasks;
    boolean hasControlledTasks;
    boolean locked;
    boolean controlled;
    boolean subTask;
    int suspendCounter;

    EStates state;

    TaskInstanceView taskInstance;

    public StructuredTaskInstanceView(TaskInstanceView taskInstance,
                                      TaskStructure structureData) throws HumanTaskManagerException {

        for (TaskStructure controlledTask : structureData
                .getSelectedTaskControlledTasks()) {
            controlledTaskIds.add(controlledTask.getSelectedTask_Id());
        }

        for (TaskStructure subTask : structureData.getSelectedTaskSubTasks()) {
            subTaskIds.add(subTask.getSelectedTask_Id());
        }

        if (structureData.isSelectedTaskSubTask()) {
            parentTaskId = structureData.getSelectedTaskParentTask().getSelectedTask_Id();
        }

        if (structureData.isSelectedTaskControlledTask()) {
            mergeTaskId = structureData.getSelectedTaskMergeTask().getSelectedTask_Id();
        }

        structureNr = structureData.getSelectedTaskStructureNr();

        hasSubTasks = structureData.isSelectedTaskHasSubTasks();

        hasControlledTasks = structureData.isSelectedTaskHasControlledTasks();

        locked = structureData.isSelectedTaskLocked();

        state = structureData.getSelectedTaskState();

        this.suspendCounter = structureData.getSelectedTaskSuspendCounter();

        this.controlled = structureData.isSelectedTaskControlledTask();

        this.subTask = structureData.isSelectedTaskSubTask();

        this.taskInstance = taskInstance;
    }


    public TaskInstanceView getTaskInstance() {
        return taskInstance;
    }

    public List<String> getControlledTaskIds() {
        return controlledTaskIds;
    }

    public List<String> getSubTaskIds() {
        return subTaskIds;
    }

    public String getMergeTaskId() {
        return mergeTaskId;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public int getStructureNr() {
        return structureNr;
    }

    public int getSuspendCounter() {
        return suspendCounter;
    }


    public boolean isHasSubTasks() {
        return hasSubTasks;
    }

    public boolean isHasControlledTasks() {
        return hasControlledTasks;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isSubTask() {
        return subTask;
    }

    public boolean isControlled() {
        return controlled;
    }

    public EStates getState() {
        return state;
    }

}
