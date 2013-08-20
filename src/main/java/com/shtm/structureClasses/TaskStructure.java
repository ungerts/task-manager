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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.utils.Utilities;
import com.shtm.exceptions.SHTMException;
import com.shtm.exceptions.SHTMInvalidOperationException;
import com.shtm.operationAndStates.EOperations;
import com.shtm.operationAndStates.EStates;

/**
 * This class contains the methods for checking the preconditions for structure
 * and state operations and the methods for applying them. It also gives access
 * for a selected task of this structure and its attributes
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public class TaskStructure {
    private Logger log;

    private StructuredTask strSelectedTask;

    public TaskStructure(String tiid) throws HumanTaskManagerException {
        this.log = Utilities.getLogger(this.getClass());
        this.strSelectedTask = new StructuredTask(tiid);
    }

    public TaskStructure(StructuredTask strTask) throws SHTMException {
        this.log = Utilities.getLogger(this.getClass());
        this.strSelectedTask = strTask;
    }

    /**
     * Check if the root task of this task structure or one of its controlled
     * tasks is exited
     *
     * @throws HumanTaskManagerException
     */
    public void checkExternalTask() throws HumanTaskManagerException {
        StructuredTask rootTask = this.getRootTask();
        if (rootTask.isExited()) {
            String excMsg = "Operation cannot be performed. One of the External Tasks of the structure is exited.";
            throw new SHTMInvalidOperationException(excMsg);
        }

    }

    /**
     * Check if the selected task of this structure is locked or controlled
     *
     * @throws HumanTaskManagerException
     */
    public void checkGeneralOperationPreconditions()
            throws HumanTaskManagerException {
        if (this.getSelectedTask().isLocked()) {
            String excMsg = "Operation cannot be performed. Task "
                    + this.getSelectedTask().getTask_id() + "is locked.";
            throw new SHTMInvalidOperationException(excMsg);
        }
        if (this.getSelectedTask().isControlledTask()) {
            String excMsg = "Operation cannot be performed. Task "
                    + this.getSelectedTask().getTask_id()
                    + "is part of a merge task.";
            throw new SHTMInvalidOperationException(excMsg);
        }

    }

    /**
     * Check if the selected task and its parent has an allowed task combination
     * for the operation
     *
     * @param operation
     * @throws HumanTaskManagerException
     */
    public void checkStatesForOperation(EOperations operation)
            throws HumanTaskManagerException {
        boolean operationAllowed = false;
        EStates state = this.getSelectedTask().getInstanceState();
        EStates parentState = null;

        // find the parent state
        if (this.getSelectedTask().isSubTask()) {
            StructuredTask parentTask = this.getSelectedTask().getParentTask();
            parentState = parentTask.getInstanceState();
        } else if (this.getSelectedTask().isHasControlledTasks()) {
            /*
                * even if the task has no parents its controlled tasks can have, so
                * have to check them all to find a parent state
                */
            for (StructuredTask controlledTask : this.getSelectedTask()
                    .getAllControlledTasks()) {
                if (controlledTask.isSubTask()) {
                    StructuredTask parentTask = controlledTask.getParentTask();
                    parentState = parentTask.getInstanceState();
                }
            }
        }

        // check the state for the operation
        if (operation.isPreState(state)) {
            if (parentState != null) {
                operationAllowed = parentState.allowsChildState(operation
                        .getPostState());
            } else {
                operationAllowed = true;
            }
        }

        if (!operationAllowed) {
            if (parentState != null) {
                String excMsg = "Task "
                        + this.getSelectedTask().getTask_id()
                        + " is in state "
                        + state.toString()
                        + ". Its parent task is in state "
                        + parentState.toString()
                        + ". These states combination is invalid for operation "
                        + operation.toString() + ".";
                throw new SHTMInvalidOperationException(excMsg);
            } else {

                String excMsg = "Task " + this.getSelectedTask().getTask_id()
                        + " is in state " + state.toString()
                        + ". This state is invalid for operation "
                        + operation.toString() + ".";
                throw new SHTMInvalidOperationException(excMsg);
            }
        }
    }

    /**
     * Propagate the state change of the operation recursively on the parent
     * task if all siblings are in the same state
     *
     * @throws HumanTaskManagerException
     */
    public void propagateStateToPredecessor() throws HumanTaskManagerException {
        /*
           * Only the children of the parent task are checked, not the children of
           * the parent tasks of the controlled tasks. That is important, because
           * we do not want to change the state of locked tasks.
           */
        // if parent task is not locked
        if (this.getSelectedTask().isSubTask()) {
            if (!this.getSelectedTask().getParentTask().isLocked()) {

                // get task state and parent task state
                EStates state = this.getSelectedTask().getInstanceState();
                EStates parentState = this.getSelectedTask().getParentTask()
                        .getInstanceState();

                if (!(state == parentState)) {
                    // for FAILED one sub task with this state is enough
                    if (state == EStates.FAILED) {
                        if (this.getSelectedTask().isSkipable()) {
                            Map<String, Object> parameters = new HashMap<String, Object>();
                            parameters.put("faultName", "SubTaskFault");
                            parameters.put("faultData",
                                    "<A fault occured at a sub task>");
                            // propagate the fail-operation on all
                            // successors and all
                            // predecessors (propagateStateOperation calls
                            // performFailOnParent again)

                            this.getSelectedTaskParentTask()
                                    .propagateStateOperation(EOperations.FAIL,
                                            parameters);
                        }
                    } else {
                        // for all over states all sibling must be in same state
                        boolean allSiblingsInSameStates = true;
                        boolean allSiblingsInSimilarStates = true;
                        for (StructuredTask sibling : this.getSelectedTask()
                                .getParentTask().getSubTasks()) {
                            EStates siblingState = sibling.getInstanceState();

                            if (!(state == siblingState)) {
                                allSiblingsInSameStates = false;
                            }
                            /*
                                    * (state == siblingState) is necessary because a
                                    * task is also a sibling of itself
                                    */
                            if (!((state == siblingState.getSuspendedState())
                                    || (state.getSuspendedState() == siblingState) || (state == siblingState))) {
                                allSiblingsInSimilarStates = false;
                            }
                        }

                        ;

                        /*
                               * if all siblings are in same state switch parent state
                               * to this state if all sibling are in similar states
                               * (same state or suspended version of this state)
                               * switch to the non-suspended version of this state
                               */

                        EStates targetState = null;
                        if (allSiblingsInSameStates) {
                            targetState = state;
                        } else if (allSiblingsInSimilarStates) {
                            targetState = state.getResumedState();
                        }

                        StructuredTask parentTask = this.getSelectedTask()
                                .getParentTask();
                        /*
                               * check all possible Parent States for all Sub States
                               * which can have a different Parent State
                               */
                        if (targetState != null && targetState != parentState) {
                            switch (targetState) {
                                case SUSPENDED_READY:
                                    switch (parentState) {
                                        case READY:
                                            parentTask.suspend();
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case RESERVED:
                                    /*
                                          * don't have to check the suspended states,
                                          * cause inside the suspend state a task cannot
                                          * switch its state
                                          */
                                    switch (parentState) {
                                        case IN_PROGRESS:
                                            parentTask.stop();
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case SUSPENDED_RESERVED:
                                    switch (parentState) {
                                        case RESERVED:
                                            parentTask.suspend();
                                            break;
                                        case IN_PROGRESS:
                                            parentTask.stop();
                                            parentTask.suspend();
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case SUSPENDED_IN_PROGRESS:
                                    switch (parentState) {
                                        case IN_PROGRESS:
                                            parentTask.suspend();
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case COMPLETED:
                                    Object outputMessage = "<All subtasks completed>";
                                    switch (parentState) {
                                        case IN_PROGRESS:
                                            parentTask.complete(outputMessage);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case OBSOLETE:
                                    switch (parentState) {
                                        case READY:
                                            parentTask.skip();
                                            break;
                                        case RESERVED:
                                            parentTask.skip();
                                            break;
                                        case IN_PROGRESS:
                                            parentTask.skip();
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    String excMsg = "InvalidStateCombination";
                                    throw new SHTMInvalidOperationException(excMsg);
                            }
                            if (allSiblingsInSameStates) {
                                log.debug("All siblings in state "
                                        + state.toString()
                                        + ". Parent State changed to "
                                        + targetState.toString() + ".");
                            } else {
                                log.debug("All siblings in similar state to "
                                        + state.toString()
                                        + ". Parent State changed to "
                                        + targetState.toString() + ".");
                            }
                            new TaskStructure(parentTask)
                                    .propagateStateToPredecessor();
                        }
                    }
                }

            }
        }

    }

    /**
     * Propagate the effect of the operation on all descendants of the selected
     * task
     *
     * @param operation
     * @param parameter
     * @throws HumanTaskManagerException
     */
    public void propagateStateOperation(EOperations operation,
                                        Map<String, Object> parameter) throws HumanTaskManagerException {
        log.debug("Perform " + operation.toString() + " on Task "
                + this.getSelectedTask().getTask_id());

        /*
           * Perform the operation on this task. This task can be a sub task, so
           * it can be in different states. Not every state is suitable for the
           * operation. So we first have to bring the task into a suitable state
           * for the operation
           */
        EStates state = this.getSelectedTask().getInstanceState();

        switch (operation) {
            case START:
                switch (state) {
                    case RESERVED:
                        this.getSelectedTask().start();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMERESERVED, parameter);
                        this.getSelectedTask().start();
                    default:
                        break;
                }
                break;
            case STOP:
                switch (state) {
                    case RESERVED:
                        propagateStateOperation(EOperations.START, parameter);

                        this.getSelectedTask().stop();
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().stop();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMERESERVED, parameter);

                        propagateStateOperation(EOperations.START, parameter);

                        this.getSelectedTask().stop();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEINPROGRESS, parameter);

                        this.getSelectedTask().stop();
                        break;
                    default:
                        break;
                }
                break;
            case SUSPENDREADY:
                switch (state) {
                    case READY:
                        this.getSelectedTask().suspend();
                        break;
                    default:
                        break;
                }
                break;
            case SUSPENDRESERVED:
                switch (state) {
                    case RESERVED:
                        this.getSelectedTask().suspend();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().suspend();
                        break;
                    default:
                        break;
                }
                break;
            case SUSPENDINPROGRESS:
                switch (state) {
                    case RESERVED:
                        this.getSelectedTask().suspend();
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().suspend();
                        break;
                    case SUSPENDED_READY:
                        this.getSelectedTask().suspend();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().suspend();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().suspend();
                        break;
                    default:
                        break;
                }
                break;
            case SUSPENDUNTILREADY:
                Timestamp pointOfTime = (Timestamp) parameter.get("pointOfTime");
                // suspendUntil is only for non-structured tasks available, so the
                // sub-states don't have to be considered
                this.getSelectedTask().suspendUntil(pointOfTime);
                break;
            case SUSPENDUNTILRESERVED:
                pointOfTime = (Timestamp) parameter.get("pointOfTime");
                // suspendUntil is only for non-structured tasks available, so the
                // sub-states don't have to be considered
                this.getSelectedTask().suspendUntil(pointOfTime);
                break;
            case SUSPENDUNTILINPROGRESS:
                pointOfTime = (Timestamp) parameter.get("pointOfTime");
                // suspendUntil is only for non-structured tasks available, so the
                // sub-states don't have to be considered
                this.getSelectedTask().suspendUntil(pointOfTime);
                break;
            case RESUMEREADY:
                switch (state) {
                    case SUSPENDED_READY:
                        this.getSelectedTask().resume();
                        break;
                    default:
                        break;
                }
                break;
            case RESUMERESERVED:
                switch (state) {
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().resume();
                        break;
                    default:
                        break;
                }
                break;
            case RESUMEINPROGRESS:
                switch (state) {
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().resume();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().resume();
                        break;
                    default:
                        break;
                }
                break;
            case CLAIM:
                switch (state) {
                    case READY:
                        this.getSelectedTask().claim();
                        break;
                    case SUSPENDED_READY:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEREADY, parameter);

                        this.getSelectedTask().release();
                        break;
                    default:
                        break;
                }
                break;
            case RELEASE:
                switch (state) {
                    case RESERVED:
                        this.getSelectedTask().release();
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().release();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMERESERVED, parameter);

                        this.getSelectedTask().release();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEINPROGRESS, parameter);

                        this.getSelectedTask().release();
                        break;
                    default:
                        break;
                }
                break;
            case FORWARD:
                String forwardeeId = (String) parameter.get("forwardeeId");
                switch (state) {
                    case READY:
                        this.getSelectedTask().forward(forwardeeId);
                        break;
                    case RESERVED:
                        this.getSelectedTask().forward(forwardeeId);
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().forward(forwardeeId);
                        break;
                    default:
                        break;
                }
                break;
            case COMPLETE:
                Object outputMessage = parameter.get("outputMessage");
                switch (state) {
                    // Only the first task of the complete-operation (must be
                    // In_Progress) will be
                    // completed, all subtasks will change to obsolete (skip)
                    case IN_PROGRESS:
                        this.getSelectedTask().complete(outputMessage);
                        operation = EOperations.DISCHARGE;
                        break;
                    default:
                        break;
                }
                break;
            case FAIL:
                switch (state) {
                    // Only the first task of the fail-operation and all its
                    // predecessors(must be In_Progress)
                    // will be failed, all other tasks of the structure will change to
                    // obsolete (skip)
                    case IN_PROGRESS:
                        String faultName = (String) parameter.get("faultName");
                        Object faultData = parameter.get("faultData");
                        this.getSelectedTask().fail(faultName, faultData);
                        operation = EOperations.DISCHARGE;
                        break;
                    default:
                        break;
                }
                break;
            case SKIP:
                switch (state) {
                    case READY:
                        this.getSelectedTask().skip();
                        break;
                    case RESERVED:
                        this.getSelectedTask().skip();
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().skip();
                        break;
                    case SUSPENDED_READY:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEREADY, parameter);

                        this.getSelectedTask().skip();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMERESERVED, parameter);

                        this.getSelectedTask().skip();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEINPROGRESS, parameter);

                        this.getSelectedTask().skip();
                        break;
                    default:
                        break;
                }
                break;
            case DISCHARGE:
                switch (state) {
                    case READY:
                        this.getSelectedTask().discharge();
                        break;
                    case RESERVED:
                        this.getSelectedTask().discharge();
                        break;
                    case IN_PROGRESS:
                        this.getSelectedTask().discharge();
                        break;
                    case SUSPENDED_READY:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEREADY, parameter);

                        this.getSelectedTask().discharge();
                        break;
                    case SUSPENDED_RESERVED:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMERESERVED, parameter);

                        this.getSelectedTask().discharge();
                        break;
                    case SUSPENDED_IN_PROGRESS:
                        this.getSelectedTask().deleteSuspendCounter();
                        propagateStateOperation(EOperations.RESUMEINPROGRESS, parameter);

                        this.getSelectedTask().discharge();
                        break;
                    default:
                        break;
                }
                break;
            default:
                if (state.isFinalState()) {
                    break;
                } else {
                    String excMsg = "Task " + this.getSelectedTask().getTask_id()
                            + " is in state " + state.toString()
                            + ". This state is invalid for operation "
                            + operation.toString() + ".";
                    throw new SHTMInvalidOperationException(excMsg);
                }
        }

        // Perform operation on all SubTasks
        if (!state.isFinalState()) {
            for (TaskStructure subTask : this.getSelectedTaskSubTasks()) {
                subTask.propagateStateOperation(operation, parameter);
            }

        }

    }

    /* STRUCTURE OPERATIONS */

    public TaskStructure addSubTask(String taskModelName,
                                    String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        StructuredTask subTaskData = this.getSelectedTask().addSubTask(
                taskModelName, taskInstanceName, inputData);
        return new TaskStructure(subTaskData);
    }

    public void removeSubTask() throws HumanTaskManagerException {
        if (this.getSelectedTask().isSubTask()) {
            this.removeTask();
        } else {
            String excMsg = "Task "
                    + this.getSelectedTask().getTask_id()
                    + " is not a sub task. removeSubTask is invalid for a non sub task.";
            throw new SHTMInvalidOperationException(excMsg);
        }
    }

    public TaskStructure mergeTask(TaskStructure otherTask,
                                   String taskModelName, String taskInstanceName, Object inputData,
                                   boolean root) throws HumanTaskManagerException {

        /*
           * Check preconditions for merging and lock path to the root only for
           * the operand and not for its descendants
           */
        if (root) {
            // both tasks must not be in same structure
            if (this.isSameStructure(otherTask)) {
                String excMsg = "Merging Error: Task "
                        + this.getSelectedTask().getTask_id() + " and Task "
                        + otherTask.getSelectedTask().getTask_id()
                        + " are in the same structure.";
                throw new SHTMInvalidOperationException(excMsg);
            } else {
                // check if parents are in same state
                checkParentStatesEquality(otherTask);
                // check if task are similar structured and in same states
                checkStructureAndStatesEquality(otherTask);
                // lock root path
                if (this.isSelectedTaskSubTask()) {
                    this.getSelectedTaskParentTask().selectedTaskLockPath();
                }
                if (otherTask.isSelectedTaskSubTask()) {
                    otherTask.getSelectedTaskParentTask()
                            .selectedTaskLockPath();

                }
            }
        }

        // merge both tasks and get the new merge task
        StructuredTask mergeTaskData = this.getSelectedTask().mergeTask(
                otherTask.getSelectedTask(), taskModelName, taskInstanceName,
                inputData);

        // apply recursively on subtasks
        for (TaskStructure subTask : this.getSelectedTaskSubTasks()) {
            TaskStructure mergeSubTask = subTask.mergeTask(otherTask
                    .getSelectedTaskSubTaskByStructureNr(subTask
                            .getSelectedTaskStructureNr()), taskModelName,
                    taskInstanceName, inputData, false);

            StructuredTask mergeSubTaskData = mergeSubTask.getSelectedTask();

            // Connect merge-task and merge-sub-task
            mergeSubTaskData.setParentTaskReference(mergeTaskData);

            mergeTaskData.setHasSubTasks(true);
            mergeSubTaskData.setStructureNr(mergeTaskData
                    .getHighestSubTaskStructureNr() + 1);
            mergeSubTaskData.setStructureId(mergeTaskData.getStructureId());
        }

        return new TaskStructure(mergeTaskData);
    }

    public List<TaskStructure> unmergeTask() throws HumanTaskManagerException {
        List<TaskStructure> freedTasks = new ArrayList<TaskStructure>();
        // unmerge only on a merge task which is the root of a task structure
        if (this.getSelectedTask().isHasControlledTasks()
                && !this.getSelectedTask().isSubTask()) {
            // collect the controlled task before the connection is deleted
            for (StructuredTask controlledTask : this.getSelectedTask()
                    .getControlledTasks()) {
                freedTasks.add(new TaskStructure(controlledTask));
            }
            // delete the mergeTask-controlledTask relationship on the structure
            this.removeMerging(true);
            // remove all merge tasks (so all tasks of this mergeTaskStructure
            // are deleted
            this.removeTask();
        } else {
            String excMsg = "Task "
                    + this.getSelectedTask().getTask_id()
                    + " is not a root merge task. removeMerge is invalid for a non root merge task.";
            throw new SHTMInvalidOperationException(excMsg);
        }
        return freedTasks;

    }

    // Navigation Operations

    public TaskStructure getSelectedTaskParentTask()
            throws HumanTaskManagerException {
        return new TaskStructure(this.getSelectedTask().getParentTask());
    }

    public TaskStructure getSelectedTaskMergeTask() throws SHTMException {
        return new TaskStructure(this.getSelectedTask().getMergeTask());
    }

    public List<TaskStructure> getSelectedTaskSubTasks()
            throws HumanTaskManagerException {
        List<TaskStructure> resultList = new ArrayList<TaskStructure>();
        for (StructuredTask subTaskData : this.getSelectedTask().getSubTasks()) {
            resultList.add(new TaskStructure(subTaskData));
        }
        return resultList;
    }

    public TaskStructure getSelectedTaskSubTaskByStructureNr(int structureNr)
            throws HumanTaskManagerException {
        return new TaskStructure(this.getSelectedTask()
                .getSubTaskByStructureNr(structureNr));
    }

    public List<TaskStructure> getSelectedTaskControlledTasks()
            throws SHTMException {
        List<TaskStructure> resultList = new ArrayList<TaskStructure>();
        for (StructuredTask controlledTaskData : this.getSelectedTask()
                .getControlledTasks()) {
            resultList.add(new TaskStructure(controlledTaskData));
        }
        return resultList;
    }

    // Selected Task Property Operations

    public void addSelectedTaskAttachment(IAttachment attachment)
            throws HumanTaskManagerException {
        this.getSelectedTask().addAttachment(attachment);
    }

    public List<IAttachment> getSelectedTaskAttachment(String name)
            throws HumanTaskManagerException {
        return this.getSelectedTask().getAttachments(name);
    }

    public ResultStructure<String, List<IAttachment>> getSelectedTaskAttachmentStructure(
            String name) throws HumanTaskManagerException {
        ResultStructure<String, List<IAttachment>>.AttachmentOperation operation = new ResultStructure<String, List<IAttachment>>().new AttachmentOperation(
                name);
        return new ResultStructure<String, List<IAttachment>>(this, operation);
    }

    public boolean deleteSelectedTaskAttachments(String name)
            throws HumanTaskManagerException {
        boolean result = false;
        result = this.getSelectedTask().deleteAttachments(name);
        return result;
    }

    public void setSelectedTaskOutput(Object output)
            throws HumanTaskManagerException {
        this.getSelectedTask().setOutput(output);
    }

    public Object getSelectedTaskOutput() throws HumanTaskManagerException {
        return this.getSelectedTask().getOutput();
    }

    public ResultStructure<String, Object> getSelectedTaskOutputStructure()
            throws HumanTaskManagerException {
        ResultStructure<String, Object>.OutputOperation operation = new ResultStructure<String, Object>().new OutputOperation();
        return new ResultStructure<String, Object>(this, operation);
    }

    public void deleteSelectedTaskOutput() throws HumanTaskManagerException {
        this.getSelectedTask().deleteOutput();
    }

    public void setSelectedTaskFault(String faultName, Object faultData)
            throws HumanTaskManagerException {
        this.getSelectedTask().setFault(faultName, faultData);
    }

    public IFault getSelectedTaskFault() throws HumanTaskManagerException {
        return this.getSelectedTask().getFault();
    }

    public ResultStructure<String, IFault> getSelectedTaskFaultStructure()
            throws HumanTaskManagerException {
        ResultStructure<String, IFault>.FaultOperation operation = new ResultStructure<String, IFault>().new FaultOperation();
        return new ResultStructure<String, IFault>(this, operation);
    }

    public void deleteSelectedTaskFault() throws HumanTaskManagerException {
        this.getSelectedTask().deleteFault();
    }

    public Object getSelectedTaskInput() throws HumanTaskManagerException {
        return this.getSelectedTask().getInput();
    }

    public ResultStructure<String, Object> getSelectedTaskInputStructure()
            throws HumanTaskManagerException {
        ResultStructure<String, Object>.InputOperation operation = new ResultStructure<String, Object>().new InputOperation();
        return new ResultStructure<String, Object>(this, operation);
    }

    public int getSelectedTaskStructureNr() {
        return this.getSelectedTask().getStructureNr();
    }

    public void setSelectedTaskStructureNr(int strNr)
            throws HumanTaskManagerException {
        // without parent task the structurNr is always 0
        if (this.getSelectedTask().isSubTask()) {
            // strNr must be unique among the siblings
            for (TaskStructure subTaskStr : this.getSelectedTaskParentTask()
                    .getSelectedTaskSubTasks()) {
                if (subTaskStr.getSelectedTaskStructureNr() == strNr) {
                    String excMsg = "Structure number is not unique.";
                    throw new SHTMInvalidOperationException(excMsg);
                }
            }
            this.setSelectedTaskStructureNr(strNr);
        }
    }

    public void setSelectedTaskPriority(int priority)
            throws HumanTaskManagerException {
        this.getSelectedTask().setPriority(priority);
    }

    public TaskInstanceView getSelectedTaskInfo()
            throws HumanTaskManagerException {
        return this.getSelectedTask().getTaskInfo();
    }

    public String getSelectedTaskDescription() throws HumanTaskManagerException {
        return this.getSelectedTask().getTaskDescription();
    }

    public String getSelectedTask_Id() {
        return this.getSelectedTask().getTask_id();
    }

    public int getSelectedTaskSuspendCounter() {
        return this.getSelectedTask().getSuspendCounter();
    }

    public EStates getSelectedTaskState() throws HumanTaskManagerException {
        return this.getSelectedTask().getInstanceState();
    }

    public TaskInstanceView getSelectedTaskInstanceView()
            throws HumanTaskManagerException {
        return this.getSelectedTask().getTaskInstanceView();
    }

    public boolean isSelectedTaskSubTask() {
        return this.getSelectedTask().isSubTask();
    }

    public boolean isSelectedTaskControlledTask() {
        return this.getSelectedTask().isControlledTask();
    }

    public boolean isSelectedTaskHasSubTasks() {
        return this.getSelectedTask().isHasSubTasks();
    }

    public boolean isSelectedTaskHasControlledTasks() {
        return this.getSelectedTask().isHasControlledTasks();
    }

    public boolean isSelectedTaskLocked() {
        return this.getSelectedTask().isLocked();
    }

    /**
     * Returns a formated String that represents this structure
     *
     * @param einschub
     * @param mergeParent
     * @param shortForm
     * @return
     * @throws HumanTaskManagerException
     */
    public String getStructureInfos(String einschub, boolean mergeParent,
                                    boolean shortForm) throws HumanTaskManagerException {
        String typ;
        if (this.isSelectedTaskHasControlledTasks()) {
            typ = "MT:";
        } else {
            typ = "NT:";
        }

        if (this.isSelectedTaskControlledTask() && mergeParent == false) {
            return this.getSelectedTaskMergeTask().getStructureInfos(einschub,
                    false, shortForm);
        } else {
            String structureInfos = einschub + typ
                    + this.getSelectedTask().getTaskInfos(shortForm) + ";";

            structureInfos = structureInfos + "[";
            for (TaskStructure controlledTask : this
                    .getSelectedTaskControlledTasks()) {
                structureInfos = structureInfos
                        + controlledTask.getStructureInfos("", true, shortForm);

            }
            structureInfos = structureInfos + "]";

            if (!this.isSelectedTaskControlledTask()) {
                structureInfos = structureInfos
                        + System.getProperty("line.separator");
                for (TaskStructure subTask : this.getSelectedTaskSubTasks()) {
                    structureInfos = structureInfos
                            + subTask.getStructureInfos(einschub + "    ",
                            false, shortForm);
                }
            }
            return structureInfos;
        }
    }

    /* Private Operations */

    private StructuredTask getSelectedTask() {
        return this.strSelectedTask;
    }

    private StructuredTask getRootTask() throws HumanTaskManagerException {
        return new StructuredTask(this.strSelectedTask.getRootTaskId());
    }

    /**
     * Check if the parent state of two tasks and the parent tasks of their
     * controlled tasks are in the same state
     *
     * @param otherTask
     * @throws HumanTaskManagerException
     */
    private void checkParentStatesEquality(TaskStructure otherTask)
            throws HumanTaskManagerException {
        StructuredTask otherTaskData = otherTask.getSelectedTask();
        EStates parentState = null;
        // find a parent state
        if (this.getSelectedTask().isSubTask()) {
            parentState = this.getSelectedTask().getParentTask()
                    .getInstanceState();
        } else if (this.getSelectedTask().isHasControlledTasks()) {
            /*
                * even if the task has no parent its controlled tasks can have. so
                * we have to check them. if there are parent task of the controlled
                * task they already must be in the same state, so we can take
                * anyone
                */
            for (StructuredTask controlledTask : this.getSelectedTask()
                    .getAllControlledTasks()) {
                if (controlledTask.isSubTask()) {
                    StructuredTask parentTask = controlledTask.getParentTask();
                    parentState = parentTask.getInstanceState();
                }
            }

        }

        // find the parent state of the other task
        EStates otherParentState = null;
        if (parentState != null) {
            if (otherTaskData.isSubTask()) {
                otherParentState = otherTaskData.getParentTask()
                        .getInstanceState();
            } else if (otherTaskData.isHasControlledTasks()) {
                /*
                     * even if the task has no parent its controlled tasks can have.
                     * so we have to check them. if there are parent task of the
                     * controlled task they already must be in the same state, so we
                     * can take anyone
                     */
                for (StructuredTask otherControlledTask : otherTaskData
                        .getAllControlledTasks()) {
                    if (otherControlledTask.isSubTask()) {
                        StructuredTask otherParentTask = otherControlledTask
                                .getParentTask();
                        otherParentState = otherParentTask.getInstanceState();
                    }
                }
            }
        }

        // if we have two parent states both must be in the same state
        if (parentState != null && otherParentState != null
                && parentState != otherParentState) {
            String excMsg = "Merging Error:  Parent of task "
                    + this.getSelectedTask().getTask_id()
                    + " and Parent of task " + otherTaskData.getTask_id()
                    + " are not in same states.";
            throw new SHTMInvalidOperationException(excMsg);
        }

    }

    /**
     * Check if the descendants of two tasks are similar structured and in the
     * same state
     *
     * @param otherTaskStructure
     * @throws HumanTaskManagerException
     */
    private void checkStructureAndStatesEquality(
            TaskStructure otherTaskStructure) throws HumanTaskManagerException {
        StructuredTask otherTask = otherTaskStructure.getSelectedTask();

        /*
           * both tasks must be in the same state, both skipable or not and own
           * the same number of sub tasks. For every sub task of one task must be
           * a sub task of the other task with the same structure number
           */
        if (this.getSelectedTask().getInstanceState() == otherTask
                .getInstanceState()) {
            if (this.getSelectedTask().isSkipable() == otherTask.isSkipable()) {
                if (this.getSelectedTask().getSubTasksCount() == otherTask
                        .getSubTasksCount()) {
                    for (StructuredTask subTask : this.getSelectedTask()
                            .getSubTasks()) {
                        new TaskStructure(subTask)
                                .checkStructureAndStatesEquality(new TaskStructure(
                                        otherTask
                                                .getSubTaskByStructureNr(subTask
                                                        .getStructureNr())));
                    }
                } else {
                    String excMsg = "Merging Error: Task "
                            + this.getSelectedTask().getTask_id()
                            + " and Task " + otherTask.getTask_id()
                            + " have a different count of sub tasks.";
                    throw new SHTMInvalidOperationException(excMsg);
                }
            } else {
                String excMsg = "Merging Error: Task "
                        + this.getSelectedTask().getTask_id() + " and Task "
                        + otherTask.getTask_id()
                        + " have a different skipable state.";
                throw new SHTMInvalidOperationException(excMsg);

            }
        } else {
            String excMsg = "Merging Error: Task "
                    + this.getSelectedTask().getTask_id() + " and Task "
                    + otherTask.getTask_id() + " have different states.";
            throw new SHTMInvalidOperationException(excMsg);
        }
    }

    /**
     * Compares the structure ids of the task and its controlled tasks with the
     * structure ids of the other task and its controlled tasks. If they would
     * have the same id they belong to the same structure
     *
     * @param otherTask
     * @return true, if this task and the other task or one of their controlled
     *         tasks belong to the same task structure
     * @throws HumanTaskManagerException
     */
    private boolean isSameStructure(TaskStructure otherTask)
            throws HumanTaskManagerException {
        Set<String> idSet = this.getSelectedTask()
                .getTaskAndControlledTaskStructureIdSet();
        Set<String> otherIdSet = otherTask.getSelectedTask()
                .getTaskAndControlledTaskStructureIdSet();

        for (String structureId : idSet) {
            if (otherIdSet.contains(structureId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the MergeTask-ControlledTask-Relationship on the task and all its
     * descendants
     *
     * @param root
     * @throws HumanTaskManagerException
     */
    private void removeMerging(boolean root) throws HumanTaskManagerException {
        List<TaskStructure> freedControlledTaskList = new ArrayList<TaskStructure>();

        // delete the MergeTask-ControlledTask-Relationship between this task
        // and its controlled tasks and put the freed controlled task in a list
        for (StructuredTask freedControllTaskData : this.getSelectedTask()
                .removeMerging()) {
            freedControlledTaskList
                    .add(new TaskStructure(freedControllTaskData));
        }

        // if this is the operand of the unMerge-Operation unlock the root paths
        // of the freed controlled tasks
        if (root) {
            for (TaskStructure controllTaskData : freedControlledTaskList) {
                if (controllTaskData.isSelectedTaskSubTask()) {
                    controllTaskData.getSelectedTaskParentTask()
                            .selectedTaskUnlockPath();
                }
            }
        }

        // apply recursively on subtasks but do not unlock the root paths there
        for (TaskStructure subTask : this.getSelectedTaskSubTasks()) {
            subTask.removeMerging(false);
        }

    }

    /**
     * Delete the task and all its descendants
     *
     * @throws HumanTaskManagerException
     */
    private void removeTask() throws HumanTaskManagerException {
        // at first remove all Sub Tasks
        for (TaskStructure subTask : this.getSelectedTaskSubTasks()) {
            subTask.removeTask();
        }
        this.getSelectedTask().setHasSubTasks(false);

        // now delete the task
        this.getSelectedTask().removeTask();
    }

    /**
     * Lock the this tasks and all its predecessors
     *
     * @throws HumanTaskManagerException
     */
    private void selectedTaskLockPath() throws HumanTaskManagerException {
        this.getSelectedTask().setLock();
        if (this.isSelectedTaskSubTask()) {
            this.getSelectedTaskParentTask().selectedTaskLockPath();
        }
    }

    /**
     * Cancel the last lock on this task and all its predecessors
     *
     * @throws HumanTaskManagerException
     */
    private void selectedTaskUnlockPath() throws HumanTaskManagerException {
        this.getSelectedTask().removeLock();
        if (this.isSelectedTaskSubTask()) {
            this.getSelectedTaskParentTask().selectedTaskUnlockPath();
        }
    }

}
