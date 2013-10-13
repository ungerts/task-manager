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

package com.shtm.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.Utilities;
import com.shtm.IStructuredTaskClientInterface;
import com.shtm.operationAndStates.EOperations;
import com.shtm.views.StructuredTaskInstanceView;
import org.junit.Ignore;

@Ignore
public class StructureTestTask {
    EOperations state;
    int strNr = 0;
    boolean locked = false;
    boolean controlled = false;
    StructureTestTask parentTask = null;
    StructureTestTask mergeTask = null;
    List<StructureTestTask> subTasks = new ArrayList<StructureTestTask>();
    List<StructureTestTask> controlledTasks = new ArrayList<StructureTestTask>();

    private Logger log = Utilities.getLogger(this.getClass());

    public StructureTestTask() {
    }

    public StructureTestTask(boolean locked) {
        this.locked = locked;
    }

    public void setLock(boolean locked) {
        this.locked = locked;
    }

    public void setControll(boolean controlled) {
        this.controlled = controlled;
    }

    public void addSubTask(StructureTestTask subTask) {
        subTask.setParentTask(this);

        int max = 0;
        for (StructureTestTask childTask : this.getSubTasks()) {
            if (childTask.getStrNr() > max) {
                max = childTask.getStrNr();
            }
        }

        subTask.setStrNr(max + 1);
        this.subTasks.add(subTask);
    }

    public StructureTestTask getMergeTaskCopy() {
        StructureTestTask copy = new StructureTestTask();
        for (StructureTestTask controlledTask : this.getControlledTasks()) {
            copy.addControlledTask(controlledTask.getMergeTaskCopy());
        }
        return copy;
    }

    public void removeSubTask(StructureTestTask subTask) {
        subTask.setParentTask(null);
        this.subTasks.remove(subTask);
    }

    public void removeControlledTask(StructureTestTask controlledTask) {
        controlledTask.setMergeTask(null);
        controlledTask.setControll(false);
        this.controlledTasks.remove(controlledTask);
    }

    public void addControlledTask(StructureTestTask controlledTask) {
        controlledTask.setMergeTask(this);
        controlledTask.setControll(true);
        this.controlledTasks.add(controlledTask);
    }

    public List<StructureTestTask> getSubTasks() {
        return this.subTasks;
    }

    public List<StructureTestTask> getControlledTasks() {
        return this.controlledTasks;
    }

    private void setParentTask(StructureTestTask parentTask) {
        this.parentTask = parentTask;
    }

    private void setMergeTask(StructureTestTask mergeTask) {
        this.mergeTask = mergeTask;
    }

    public int getStrNr() {
        return this.strNr;
    }

    public void setStrNr(int strNr) {
        this.strNr = strNr;
    }

    public static int getTotalMergeTaskCount(String tiid,
                                             IStructuredTaskClientInterface stci)
            throws HumanTaskManagerException {
        List<String> controlledTasks = stci.getControlledTasks(tiid);
        int count = controlledTasks.size();
        for (String controlledTask : controlledTasks) {
            count = count + getTotalMergeTaskCount(controlledTask, stci);
        }
        return count;
    }

    public static int getTotalTestMergeTaskCount(StructureTestTask task,
                                                 IStructuredTaskClientInterface stci) {
        List<StructureTestTask> controlledTasks = task.getControlledTasks();
        int count = controlledTasks.size();
        for (StructureTestTask controlledTask : controlledTasks) {
            count = count + getTotalTestMergeTaskCount(controlledTask, stci);
        }
        return count;
    }

    public boolean verifyStructure(String tiid,
                                   IStructuredTaskClientInterface stci)
            throws HumanTaskManagerException {
        log.info("Verify Structure of Task " + tiid);

        StructuredTaskInstanceView task = stci.getStructuredTaskInfo(tiid);

        if (task.isLocked() != this.locked) {
            log.warn("Locking state of Task " + tiid + " is not correct ("
                    + this.locked + ")");
            return false;
        }

        if (task.isControlled() != this.controlled) {
            log.warn("Controll state of Task " + tiid + " is not correct ("
                    + this.controlled + ")");
            return false;
        }

        log.info("Locked and Controll state of Task " + tiid + " is correct");

        if (getTotalMergeTaskCount(tiid, stci) != getTotalTestMergeTaskCount(this, stci)) {
            log.info("!!!Merge Task size is not correct!!!");
            return false;
        }
        log.info("Total merge task size of Task " + tiid + " correct ("
                + getTotalTestMergeTaskCount(this, stci) + ")");

        List<String> subTasks = stci.getSubTasks(tiid);

        if (subTasks.size() == this.getSubTasks().size()) {
            for (StructureTestTask testSubTask : this.getSubTasks()) {
                String subTask = stci.getSubTaskByStructureNr(tiid, testSubTask
                        .getStrNr());

                log.info("Sub Task " + subTask + " correctly found");
                if (!testSubTask.verifyStructure(subTask, stci)) {
                    return false;
                }
            }
        } else {
            log.info("!!!Sub Task size is not correct!!!");
            return false;
        }

        return true;
    }

}
