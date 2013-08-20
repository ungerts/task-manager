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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.htm.ITaskClientInterface;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.shtm.accessProviders.HTMTaskClientAccessProvider;
import com.shtm.accessProviders.IHTMTaskParentAccessProvider;
import com.shtm.accessProviders.IStructuredTaskAccessProvider;
import com.shtm.exceptions.SHTMException;
import com.shtm.exceptions.SHTMIllegalArgumentException;
import com.shtm.operationAndStates.EStates;

/**
 * This class contain all methods which can be performed on a task. This
 * includes structure operations for this task and all states operations
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public class StructuredTask {

    protected IStructuredTaskAccessProvider sdap;

    protected ITaskClientInterface tci;

    protected IHTMTaskParentAccessProvider tpi;

    protected StructureData structureData;

    public StructuredTask(String tiid) throws HumanTaskManagerException {
        this.sdap = IStructuredTaskAccessProvider.Factory.newInstance();
        this.tci = HTMTaskClientAccessProvider.getTaskClientInstance();
        this.tpi = IHTMTaskParentAccessProvider.Factory.newInstance();

        StructureData strData = sdap.getStructureDataByTaskId(tiid);

        if (strData == null) {
            strData = new StructureData();
            strData.setHasControlledTasks(false);
            strData.setHasSubTasks(false);
            strData.setLockCounter(0);
            strData.setStructureNr(0);
            strData.setTask_id(Integer.parseInt(tiid));
            // StructureData must be persisted to get an ID
            sdap.persistStructureData(strData);
            strData.setStructureId(strData.getId());
        }
        this.structureData = strData;
    }

    public StructuredTask(StructureData strData) {
        this.sdap = IStructuredTaskAccessProvider.Factory.newInstance();
        this.tci = HTMTaskClientAccessProvider.getTaskClientInstance();
        this.tpi = IHTMTaskParentAccessProvider.Factory.newInstance();
        this.structureData = strData;
    }

    /* Task Attribute Operations */

    public void setId(String id) {
        this.structureData.setId(Integer.parseInt(id));
    }

    public String getId() {
        return Integer.toString(structureData.getId());
    }

    public void setTask_id(String task_id) {
        this.structureData.task_id = Integer.parseInt(task_id);
    }

    public String getTask_id() {
        return Integer.toString(this.structureData.task_id);
    }

    public void setLock() {
        this.structureData
                .setLockCounter(this.structureData.getLockCounter() + 1);
    }

    public void removeLock() {
        if (this.isLocked()) {
            this.structureData.setLockCounter(this.structureData
                    .getLockCounter() - 1);
        }
    }

    public int getSuspendCounter() {
        return this.structureData.suspendCounter;
    }

    public void deleteSuspendCounter() {
        this.structureData.suspendCounter = 0;
        for (StructuredTask controllTask : this.getControlledTasks()) {
            controllTask.deleteSuspendCounter();
        }
    }

    public void setStructureNr(int structureNr) {
        this.structureData.setStructureNr(structureNr);
    }

    public int getStructureNr() {
        return this.structureData.getStructureNr();
    }

    public void setStructureId(String structureId) {
        this.structureData.setStructureId(Integer.parseInt(structureId));
    }

    public String getStructureId() {
        return Integer.toString(structureData.getStructureId());
    }

    public void setHasSubTasks(boolean hasSubTasks) {
        this.structureData.setHasSubTasks(hasSubTasks);
    }

    public void setHasControlledTasks(boolean hasControlledTasks) {
        this.structureData.setHasControlledTasks(hasControlledTasks);
    }

    public StructureData getStructureData() {
        return this.structureData;
    }

    public int getSubTasksCount() {
        return this.getSubTasks().size();
    }

    public int getHighestSubTaskStructureNr() {
        int max = 0;
        for (StructuredTask childTask : this.getSubTasks()) {
            if (childTask.getStructureNr() > max) {
                max = childTask.getStructureNr();
            }
        }
        return max;
    }

    public TaskInstanceView getTaskInfo() throws HumanTaskManagerException {
        return tci.getTaskInfo(this.getTask_id());
    }

    public String getTaskDescription() throws HumanTaskManagerException {
        return tci.getTaskDescription(this.getTask_id());
    }

    public void addAttachment(IAttachment attachment)
            throws HumanTaskManagerException {
        tci.addAttachment(this.getTask_id(), attachment);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.addAttachment(attachment);
        }
    }

    public List<IAttachment> getAttachments(String name)
            throws HumanTaskManagerException {
        List<IAttachment> resultList = (tci.getAttachments(this.getTask_id(),
                name));
        return resultList;
    }

    public boolean deleteAttachments(String name)
            throws HumanTaskManagerException {
        boolean result = false;
        if (tci.deleteAttachments(this.getTask_id(), name)) {
            result = true;
        }
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            if (controlledTaskData.deleteAttachments(name)) {
                result = true;
            }
        }
        return result;
    }

    public Object getInput() throws HumanTaskManagerException {
        Object input = tci.getInput(this.getTask_id());
        return input;
    }

    public void setOutput(Object output) throws HumanTaskManagerException {
        tci.setOutput(this.getTask_id(), output);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.setOutput(output);
        }
    }

    public Object getOutput() throws HumanTaskManagerException {
        Object output = tci.getOutput(this.getTask_id());
        return output;
    }

    public void deleteOutput() throws HumanTaskManagerException {
        tci.deleteOutput(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.deleteOutput();
        }
    }

    public void setFault(String faultName, Object faultData)
            throws HumanTaskManagerException {
        tci.setFault(this.getTask_id(), faultName, faultData);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.setFault(faultName, faultData);
        }
    }

    public IFault getFault() throws HumanTaskManagerException {
        IFault fault = tci.getFault(this.getTask_id());
        return fault;
    }

    public void deleteFault() throws HumanTaskManagerException {
        tci.deleteFault(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.deleteFault();
        }
    }

    public void setPriority(int priority) throws HumanTaskManagerException {
        tci.setPriority(this.getTask_id(), priority);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.setPriority(priority);
        }
    }

    public TaskInstanceView getTaskInstanceView()
            throws HumanTaskManagerException {
        return tci.getTaskInfo(this.getTask_id());
    }

    public EStates getInstanceState() throws HumanTaskManagerException {

        String tiid = this.getTask_id();
        TaskInstanceView tiview = tci.getTaskInfo(tiid);
        String state = tiview.getStatus();
        return EStates.convertToStructuredInstanceState(state);
    }

    /**
     * @return A set of all structure ids of the task and all its controlled
     *         tasks. Only necessary for isSameStructure
     * @throws HumanTaskManagerException
     */
    public Set<String> getTaskAndControlledTaskStructureIdSet()
            throws HumanTaskManagerException {
        Set<String> idSet = new HashSet<String>();
        idSet.add(this.getStructureId());
        for (StructuredTask controlledTask : this.getControlledTasks()) {
            idSet.addAll(controlledTask
                    .getTaskAndControlledTaskStructureIdSet());
            ;
        }
        return idSet;
    }

    /**
     * Returns a string with all structure data of the task
     *
     * @param shortForm
     * @return
     */
    public String getTaskInfos(boolean shortForm) {
        String state;
        try {
            state = "|" + getInstanceState().toString();
        } catch (HumanTaskManagerException e) {
            state = "|unknown";
        }
        if (shortForm) {
            String locked = "";
            if (this.isLocked()) {
                locked = "|L";
            }

            return (this.getTask_id() + state + locked);
        } else {
            String parentId = "";
            if (this.isSubTask()) {
                parentId = this.getParentTask().getTask_id();
            }
            String mergeTaskId = "";
            if (this.isControlledTask()) {
                mergeTaskId = this.getMergeTask().getTask_id();
            }

            return ("TASK_ID: " + this.getTask_id() + "|" + "STATE: " + state
                    + "|" + "LOCKED: " + this.isLocked() + "|"
                    + "PARENT_TASK_ID: " + parentId + "|" + "MERGE_TASK_ID: "
                    + mergeTaskId + "|" + "STRID: " + this.getStructureId()
                    + "|" + "STRNR: " + this.getStructureNr() + "|"
                    + "HASCONT: " + this.isHasControlledTasks() + "|"
                    + "HASSUB: " + this.isHasSubTasks() + "|" + "ISCONT: "
                    + this.isControlledTask() + "|" + "ISSUB: " + this
                    .isSubTask());
        }
    }

    public boolean isSkipable() throws HumanTaskManagerException {
        return tci.getTaskInfo(this.getTask_id()).isSkipable();
    }

    public boolean isExited() throws HumanTaskManagerException {

        if (this.getInstanceState() == EStates.EXITED) {
            return true;
        } else {
            for (StructuredTask controlledTask : this.getControlledTasks()) {
                if (controlledTask.isExited())
                    return true;
            }
        }
        return false;
    }

    public boolean isSubTask() {
        return (this.structureData.parentTask != null);
    }

    public boolean isControlledTask() {
        return (this.structureData.mergeTask != null);
    }

    public boolean isLocked() {
        return (this.structureData.getLockCounter() > 0);
    }

    public boolean isHasControlledTasks() {
        return this.structureData.isHasControlledTasks();
    }

    public boolean isHasSubTasks() {
        return this.structureData.isHasSubTasks();
    }

    /*
      * State Operations: These operations are performed on the task and its
      * controlled tasks
      */

    public void exit() throws HumanTaskManagerException {
        tpi.exitTaskInstance(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.exit();
        }
    }

    public void claim() throws HumanTaskManagerException {

        //TODO: fix

        if (!tci.getTaskInfo(this.getTask_id()).isClaimed()) {
            tci.claim(this.getTask_id());
        }

        //tci.claim(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.claim();
        }
    }

    public void start() throws HumanTaskManagerException {


        if (!tci.getTaskInfo(this.getTask_id()).getStatus().equalsIgnoreCase(EStates.IN_PROGRESS.toString())) {
            tci.start(this.getTask_id());
        }

        //tci.start(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.start();
        }
    }

    public void stop() throws HumanTaskManagerException {
        tci.stop(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.stop();
        }
    }

    public void release() throws HumanTaskManagerException {
        tci.release(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.release();
        }
    }

    public void suspend() throws HumanTaskManagerException {
        if (this.getInstanceState().isSuspendedState()) {
            this.increaseSuspendCounter();
        } else {
            tci.suspend(this.getTask_id());
        }
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.suspend();
        }
    }

    public void suspendUntil(Timestamp pointOfTime)
            throws HumanTaskManagerException {
        if (this.getInstanceState().isSuspendedState()) {
            this.increaseSuspendCounter();
            tci.resume(this.getTask_id());
        }
        tci.suspendUntil(this.getTask_id(), pointOfTime);

        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.suspend();
        }
    }

    public void forward(String forwardeeId) throws HumanTaskManagerException {
        // no forward on merge tasks, so no forward for controlled tasks
        // needed
        tci.forward(this.getTask_id(), forwardeeId);
    }

    public void resume() throws HumanTaskManagerException {
        if (this.getSuspendCounter() > 0) {
            this.decreaseSuspendCounter();
        } else {
            tci.resume(this.getTask_id());
        }
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.resume();
        }
    }

    public void complete(Object outputData) throws HumanTaskManagerException {
        tci.complete(this.getTask_id(), outputData);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.complete(outputData);

        }
    }

    public void skip() throws HumanTaskManagerException {
        tci.skip(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.skip();
        }
    }

    /**
     * Discharge is a support operation, which should change the state of a task
     * to obsolete, even if the task modell is non-skipable At the moment, only
     * skip is available, so all task models must be skip
     *
     * @throws HumanTaskManagerException
     */
    public void discharge() throws HumanTaskManagerException {
        tci.skip(this.getTask_id());
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.discharge();
        }
    }

    public void fail(String faultName, Object faultData)
            throws HumanTaskManagerException {
        tci.fail(this.getTask_id(), faultName, faultData);
        for (StructuredTask controlledTaskData : this.getControlledTasks()) {
            controlledTaskData.fail(faultName, faultData);
        }
    }

    /* Structure Operations */

    public StructuredTask addSubTask(String taskModelName,
                                     String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {

        // if no taskModelName, taskInstanceName or inputData is given, take
        // them from this task
        if (taskModelName == null) {
            taskModelName = this.getTaskInstanceView().getTaskModelName();
        }
        if (taskInstanceName == null) {
            taskInstanceName = this.getTaskInstanceView().getName() + "SUBTASK";
        }
        if (inputData == null) {
            inputData = this.getTaskInstanceView().getInput();
        }

        // create a new task in HTM
        String stid = tpi.createSubTaskInstance(taskModelName,
                taskInstanceName, inputData);

        // create structure data and connect parent task and sub task
        StructuredTask subTask = new StructuredTask(stid);
        subTask.setParentTaskReference(this);
        subTask.setStructureId(this.getStructureId());
        subTask.setStructureNr(this.getHighestSubTaskStructureNr() + 1);

        // new task is in state ready but must be in same state as parent state
        if (this.getInstanceState() == EStates.RESERVED) {
            tci.claim(subTask.getTask_id());
        } else if (this.getInstanceState() == EStates.IN_PROGRESS) {
            tci.claim(subTask.getTask_id());
            tci.start(subTask.getTask_id());
        }
        this.setHasSubTasks(true);

        // if this is a merge task, add sub tasks to all controlled tasks
        if (this.isHasControlledTasks()) {
            for (StructuredTask controllTask : this.getControlledTasks()) {
                StructuredTask contrSubTask = controllTask.addSubTask(
                        taskModelName, taskInstanceName, inputData);

                contrSubTask.setMergeTaskReference(subTask);
            }
            subTask.setHasControlledTasks(true);
        }

        return subTask;
    }

    public void setParentTaskReference(StructuredTask parentTask) {
        this.structureData.setParentTask(parentTask.getStructureData());
        // In JPA relations between two entities are always bidirectional
        parentTask.structureData.getSubTasks().add(this.getStructureData());
    }

    public void removeTask() throws HumanTaskManagerException {
        // if this is a MergeTask split it into NormalTasks and remove them
        if (this.isHasControlledTasks()) {
            List<StructuredTask> controlledTasks = this.getControlledTasks();
            this.removeMerging();

            for (StructuredTask controlledTaskData : controlledTasks) {
                controlledTaskData.removeTask();
            }
        }

        StructuredTask parentTaskData = null;
        /* split it from its Parent Tasks */
        if (this.isSubTask()) {
            parentTaskData = this.getParentTask();
            this.removeParentTaskReference();
        }
        // update parent task state
        if (parentTaskData != null) {
            if (parentTaskData.getSubTasksCount() == 0) {
                parentTaskData.setHasSubTasks(false);
            }
        }

        // now delete the task instance in HTM
        deleteTaskInstance();
    }

    public StructuredTask mergeTask(StructuredTask otherTask,
                                    String taskModelName, String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        // if no taskModelName, taskInstanceName or inputData is given, take
        // them from this task
        if (taskModelName == null) {
            taskModelName = this.getTaskInstanceView().getTaskModelName();
        }
        if (taskInstanceName == null) {
            taskInstanceName = this.getTaskInstanceView().getName()
                    + "MERGETASK";
        }
        if (inputData == null) {
            inputData = this.getTaskInstanceView().getInput();
        }

        // create the mergeTask in HTM
        String mtid = tpi.createMergeTaskInstance(taskModelName,
                taskInstanceName, inputData);

        // create structure data and connect merge task and controlled tasks
        StructuredTask mergeTask = new StructuredTask(mtid);
        this.setMergeTaskReference(mergeTask);
        otherTask.setMergeTaskReference(mergeTask);
        mergeTask.setHasControlledTasks(true);

        // new task is in state ready but must be in same state as parent state
        EStates state = this.getInstanceState();
        if (state == EStates.RESERVED) {
            mergeTask.claim();
        } else if (state == EStates.IN_PROGRESS) {
            mergeTask.claim();
            mergeTask.start();
        }

        // add attachments of the controlled Tasks to the merge task
        for (IAttachment attachment : this.getAttachments(null)) {
            mergeTask.addAttachment(attachment);
        }
        for (IAttachment attachment : otherTask.getAttachments(null)) {
            mergeTask.addAttachment(attachment);
        }

        return mergeTask;
    }

    public List<StructuredTask> removeMerging()
            throws HumanTaskManagerException {
        List<StructuredTask> resultList = new ArrayList<StructuredTask>();
        for (StructuredTask controlledTask : this.getControlledTasks()) {
            resultList.add(controlledTask);

            // split controlled tasks from merge tasks
            controlledTask.removeMergeTaskReference();
        }
        this.setHasControlledTasks(false);
        return resultList;
    }

    /* Navigation Operations */

    public String getRootTaskId() throws SHTMException {
        StructureData strData = sdap.getStructureDataByStructureId(this
                .getStructureId());
        return Integer.toString(strData.getTask_id());
    }

    public StructuredTask getParentTask() {
        return new StructuredTask(this.structureData.getParentTask());
    }

    public List<StructuredTask> getSubTasks() {
        List<StructuredTask> subTasksDataWrapper = new ArrayList<StructuredTask>();
        for (StructureData subTask : this.structureData.getSubTasks()) {
            subTasksDataWrapper.add(new StructuredTask(subTask));
        }
        return subTasksDataWrapper;
    }

    public StructuredTask getSubTaskByStructureNr(int structureNr) throws SHTMIllegalArgumentException {
        StructuredTask result = null;
        for (StructuredTask subTask : this.getSubTasks()) {
            if (subTask.getStructureNr() == structureNr)
                result = subTask;
        }
        if (result == null) {
            String excMsg = "Sub task with structure number " + structureNr
                    + " does not exist.";
            throw new SHTMIllegalArgumentException(excMsg);
        } else {
            return result;
        }
    }

    public StructuredTask getMergeTask() {
        return new StructuredTask(this.structureData.getMergeTask());
    }

    public List<StructuredTask> getControlledTasks() {
        List<StructuredTask> controllTasksDataWrapper = new ArrayList<StructuredTask>();
        for (StructureData controllTask : this.structureData
                .getControlledTasks()) {
            controllTasksDataWrapper.add(new StructuredTask(controllTask));
        }
        return controllTasksDataWrapper;
    }

    /**
     * Returns a list of direct and indirect controlled task of the task
     *
     * @return
     */
    public List<StructuredTask> getAllControlledTasks() {
        List<StructuredTask> controlledTaskList = new ArrayList<StructuredTask>();
        for (StructuredTask controllTask : this.getControlledTasks()) {
            controlledTaskList.add(controllTask);
            controlledTaskList.addAll(controllTask.getAllControlledTasks());
        }
        return controlledTaskList;
    }

    private void removeMergeTaskReference() {
        this.getMergeTask().removeAllControlledTaskReferences();
        this.structureData.mergeTask = null;
    }

    private void setMergeTaskReference(StructuredTask mergeTask) {
        this.structureData.setMergeTask(mergeTask.getStructureData());
        // In JPA relations between two entities are always bidirectional
        mergeTask.structureData.getControlledTasks().add(
                this.getStructureData());
    }

    private void removeParentTaskReference() {
        this.getParentTask().removeSubTaskReference(this);
        this.structureData.parentTask = null;
    }

    private void removeSubTaskReference(StructuredTask subTask) {
        this.structureData.getSubTasks().remove(subTask.getStructureData());
    }

    private void removeAllControlledTaskReferences() {
        this.structureData.removeAllControlledTasks();
    }

    private void increaseSuspendCounter() {
        this.structureData.suspendCounter = this.structureData.suspendCounter + 1;
    }

    private void decreaseSuspendCounter() {
        if (this.structureData.suspendCounter > 0) {
            this.structureData.suspendCounter = this.structureData.suspendCounter - 1;
        }
    }

    /**
     * Deletes this task in HTM
     *
     * @throws HumanTaskManagerException
     */
    private void deleteTaskInstance() throws HumanTaskManagerException {
        if (!this.getInstanceState().isFinalState()) {
            tpi.exitTaskInstance(this.getTask_id());
        }
    }

}
