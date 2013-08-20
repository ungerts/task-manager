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

package com.shtm.structureClasses;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class StructureData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String name;
    int task_id;
    @ManyToOne
    @JoinColumn(name = "PARENTTASK_ID")
    StructureData parentTask;
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL)
    List<StructureData> subTasks = new ArrayList<StructureData>();
    @ManyToOne
    @JoinColumn(name = "MERGETASK_ID")
    StructureData mergeTask;
    //@OneToMany(mappedBy = "mergeTask", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "mergeTask", cascade = CascadeType.ALL)
    List<StructureData> controlledTasks = new ArrayList<StructureData>();
    int lockCounter;
    int suspendCounter;
    boolean hasSubTasks;
    boolean hasControlledTasks;
    int structure_Id;
    int structureNr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public StructureData getParentTask() {
        return parentTask;
    }

    public void setParentTask(StructureData parentTask) {
        this.parentTask = parentTask;
    }

    public List<StructureData> getSubTasks() {
        return this.subTasks;
    }

    public StructureData getMergeTask() {
        return mergeTask;
    }

    public void setMergeTask(StructureData mergeTask) {
        this.mergeTask = mergeTask;
    }

    public List<StructureData> getControlledTasks() {
        return controlledTasks;
    }

    public void removeAllControlledTasks() {
        controlledTasks = new ArrayList<StructureData>();
    }

    public int getLockCounter() {
        return lockCounter;
    }

    public void setLockCounter(int lockCounter) {
        this.lockCounter = lockCounter;
    }

    public int getSuspendCounter() {
        return suspendCounter;
    }

    public void setSuspendCounter(int suspendCounter) {
        this.suspendCounter = suspendCounter;
    }

    public boolean isHasSubTasks() {
        return hasSubTasks;
    }

    public void setHasSubTasks(boolean hasSubTasks) {
        this.hasSubTasks = hasSubTasks;
    }

    public boolean isHasControlledTasks() {
        return hasControlledTasks;
    }

    public void setHasControlledTasks(boolean hasControlledTasks) {
        this.hasControlledTasks = hasControlledTasks;
    }

    public int getStructureId() {
        return structure_Id;
    }

    public void setStructureId(int structureId) {
        this.structure_Id = structureId;
    }

    public int getStructureNr() {
        return structureNr;
    }

    public void setStructureNr(int structureNr) {
        this.structureNr = structureNr;
    }
}
