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

package com.shtm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.utils.Utilities;
import com.shtm.accessProviders.IStructuredTaskAccessProvider;
import com.shtm.exceptions.SHTMDataBaseException;
import com.shtm.exceptions.SHTMException;
import com.shtm.exceptions.SHTMInvalidOperationException;
import com.shtm.operationAndStates.EOperations;
import com.shtm.operationAndStates.EStates;
import com.shtm.structureClasses.ResultStructure;
import com.shtm.structureClasses.StructuredTask;
import com.shtm.structureClasses.TaskStructure;
import com.shtm.views.StructuredTaskInstanceView;
import com.shtm.views.StructuredWorkItemView;

/**
 * Implementations of the IStructuredTaskClientInterface. In contains all
 * methods of the ITaskClientInterface and the new methods for structuring. All
 * new methods are commented in the IStructuredTaskClientInterface. All other
 * structures behaves as before on non-structured tasks. For structured tasks
 * see the comments about the performStateOperation method and the
 * performStructureOperation method
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public class StructuredTaskClientInterfaceImpl implements
        IStructuredTaskClientInterface {

    private IStructuredTaskAccessProvider sdap;
    /**
     * The log4j logger.
     */
    private Logger log;

    public StructuredTaskClientInterfaceImpl() {
        this.sdap = IStructuredTaskAccessProvider.Factory.newInstance();
        this.log = Utilities.getLogger(this.getClass());
    }

    /*
      * State Operations: All state operations call the
      * performStateOperation-Method to propagate their effect on the task
      * structure
      */

    public void claim(String tiid) throws HumanTaskManagerException {
        performStateOperation(tiid, EOperations.CLAIM, null);
    }

    public void release(String tiid) throws HumanTaskManagerException {
        performStateOperation(tiid, EOperations.RELEASE, null);
    }

    public void start(String tiid) throws HumanTaskManagerException {
        performStateOperation(tiid, EOperations.START, null);
    }

    public void stop(String tiid) throws HumanTaskManagerException {
        performStateOperation(tiid, EOperations.STOP, null);
    }

    public void suspend(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();

            // we distinguish between three suspend operations to let them fit
            // into the system of structured task system
            TaskStructure taskStructure = new TaskStructure(tiid);
            EStates currentState = taskStructure.getSelectedTaskState();
            if (currentState == EStates.READY) {
                performStateOperation(tiid, EOperations.SUSPENDREADY, null);
            } else if (currentState == EStates.RESERVED) {
                performStateOperation(tiid, EOperations.SUSPENDRESERVED, null);
            } else if (currentState == EStates.IN_PROGRESS) {
                performStateOperation(tiid, EOperations.SUSPENDINPROGRESS, null);
            } else {
                String msg = "Task is not in a prestate for Suspend";
                throw new HumanTaskManagerException(msg);
            }
            sdap.commitTx();
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void suspendUntil(String tiid, Timestamp pointOfTime)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            // suspend can be only applied on non-structured tasks
            if (taskStructure.isSelectedTaskHasSubTasks()
                    || taskStructure.isSelectedTaskSubTask()
                    || taskStructure.isSelectedTaskHasControlledTasks()) {
                String msg = "Task may not have a parent task, any sub tasks or any controlled task for operation SUSPENDUNTIL";
                throw new HumanTaskManagerException(msg);
            } else {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("pointOfTime", pointOfTime);
                EStates currentState = taskStructure.getSelectedTaskState();
                if (currentState == EStates.READY) {
                    performStateOperation(tiid, EOperations.SUSPENDUNTILREADY,
                            parameters);
                } else if (currentState == EStates.RESERVED) {
                    performStateOperation(tiid,
                            EOperations.SUSPENDUNTILRESERVED, parameters);
                } else if (currentState == EStates.IN_PROGRESS) {
                    performStateOperation(tiid,
                            EOperations.SUSPENDUNTILINPROGRESS, parameters);
                } else {
                    String msg = "Task is not in a prestate for Suspend";
                    throw new HumanTaskManagerException(msg);
                }
            }
            sdap.commitTx();
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void resume(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();

            // we distinguish between three resume operations to let them fit
            // into the system of structured task system
            TaskStructure taskStructure = new TaskStructure(tiid);
            EStates currentState = taskStructure.getSelectedTaskState();
            if (currentState == EStates.SUSPENDED_READY) {
                performStateOperation(tiid, EOperations.RESUMEREADY, null);
            } else if (currentState == EStates.SUSPENDED_RESERVED) {
                performStateOperation(tiid, EOperations.RESUMERESERVED, null);
            } else if (currentState == EStates.SUSPENDED_IN_PROGRESS) {
                performStateOperation(tiid, EOperations.RESUMEINPROGRESS, null);
            } else {
                String msg = "Task is not in a prestate for Resume";
                throw new HumanTaskManagerException(msg);
            }
            sdap.commitTx();
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void complete(String tiid, Object outputMessage)
            throws HumanTaskManagerException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("outputMessage", outputMessage);
        performStateOperation(tiid, EOperations.COMPLETE, parameters);
    }

    public void skip(String tiid) throws HumanTaskManagerException {
        performStateOperation(tiid, EOperations.SKIP, null);
    }

    public void fail(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("faultName", faultName);
        parameters.put("faultData", faultData);
        performStateOperation(tiid, EOperations.FAIL, parameters);
    }

    public void forward(String tiid, String forwardeeId)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            // forward can be only applied on root tasks
            if (taskStructure.isSelectedTaskSubTask()
                    || taskStructure.isSelectedTaskHasControlledTasks()) {
                String msg = "Task may not have a parent task or any controlled task for operation FORWARD";
                throw new HumanTaskManagerException(msg);
            } else {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("forwardeeId", forwardeeId);
                performStateOperation(tiid, EOperations.FORWARD, parameters);
            }
            sdap.commitTx();
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    /*
      * Structure Operations: All structure operations call the
      * performStructureOperation-Method to propagate their effect on the task
      * structure
      */
    public String addSubTask(String tiid, String taskModelName,
                             String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("taskModelName", taskModelName);
        parameters.put("taskInstanceName", taskInstanceName);
        parameters.put("inputData", inputData);
        return performStructureOperation(tiid, null, EOperations.ADDSUBTASK,
                parameters);
    }

    public void removeSubTask(String tiid) throws HumanTaskManagerException {
        performStructureOperation(tiid, null, EOperations.REMOVESUBTASK, null);
    }

    public String mergeTasks(String tiid1, String tiid2, String taskModelName,
                             String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("taskModelName", taskModelName);
        parameters.put("taskInstanceName", taskInstanceName);
        parameters.put("inputData", inputData);

        return performStructureOperation(tiid1, tiid2, EOperations.MERGE,
                parameters);

    }

    public void unmerge(String tiid) throws HumanTaskManagerException {
        performStructureOperation(tiid, null, EOperations.UNMERGE, null);
    }

    /* Navigation Operations */

    public String getParentTask(String tiid) throws HumanTaskManagerException {
        try {
            String result = null;
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            if (taskStructure.getSelectedTaskParentTask() != null) {
                result = taskStructure.getSelectedTaskParentTask()
                        .getSelectedTask_Id();
            }
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public List<String> getSubTasks(String tiid)
            throws HumanTaskManagerException {
        try {
            List<String> result = new ArrayList<String>();
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            for (TaskStructure subTask : taskStructure
                    .getSelectedTaskSubTasks()) {
                result.add(subTask.getSelectedTask_Id());
            }
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public String getSubTaskByStructureNr(String tiid, int strNr)
            throws HumanTaskManagerException {
        try {
            String result = null;
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            result = taskStructure.getSelectedTaskSubTaskByStructureNr(strNr)
                    .getSelectedTask_Id();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public String getMergeTask(String tiid) throws HumanTaskManagerException {
        try {
            String result = null;
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            if (taskStructure.getSelectedTaskMergeTask() != null) {
                result = taskStructure.getSelectedTaskMergeTask()
                        .getSelectedTask_Id();
            }
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public List<String> getControlledTasks(String tiid)
            throws HumanTaskManagerException {
        try {
            List<String> result = new ArrayList<String>();
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            for (TaskStructure controlledTask : taskStructure
                    .getSelectedTaskControlledTasks()) {
                result.add(controlledTask.getSelectedTask_Id());
            }
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    /* Task Attribute Operations */

    public void addAttachment(String tiid, IAttachment attachment)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.addSelectedTaskAttachment(attachment);
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public List<IAttachment> getAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            List<IAttachment> result = taskStructure
                    .getSelectedTaskAttachment(name);
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public ResultStructure<String, List<IAttachment>> getAttachmentStructure(
            String tiid, String name) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            ResultStructure<String, List<IAttachment>> resultList = taskStructure
                    .getSelectedTaskAttachmentStructure(name);
            sdap.commitTx();
            return resultList;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public boolean deleteAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            boolean result = false;
            result = taskStructure.deleteSelectedTaskAttachments(name);
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void setOutput(String tiid, Object output)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.setSelectedTaskOutput(output);
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public Object getOutput(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            Object result = taskStructure.getSelectedTaskOutput();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }

    }

    public ResultStructure<String, Object> getOutputStructure(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            ResultStructure<String, Object> result = taskStructure
                    .getSelectedTaskOutputStructure();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void deleteOutput(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.deleteSelectedTaskOutput();
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void setFault(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.setSelectedTaskFault(faultName, faultData);
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public IFault getFault(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            IFault result = taskStructure.getSelectedTaskFault();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public ResultStructure<String, IFault> getFaultStructure(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            ResultStructure<String, IFault> result = taskStructure
                    .getSelectedTaskFaultStructure();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void deleteFault(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.deleteSelectedTaskFault();
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public Object getInput(String tiid) throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            Object result = taskStructure.getSelectedTaskInput();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }

    }

    public ResultStructure<String, Object> getInputStructure(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            ResultStructure<String, Object> result = taskStructure
                    .getSelectedTaskInputStructure();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void setPriority(String tiid, int priority)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.setSelectedTaskPriority(priority);
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public void setStructureNr(String tiid, int strNr)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            taskStructure.setSelectedTaskStructureNr(strNr);
            sdap.commitTx();
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public int getStructureNr(String tiid) throws HumanTaskManagerException {
        try {
            int strNr;
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            strNr = taskStructure.getSelectedTaskStructureNr();
            sdap.commitTx();
            return strNr;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public StructuredTaskInstanceView getStructuredTaskInfo(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();

            TaskStructure taskStructure = new TaskStructure(tiid);
            sdap.commitTx();

            return new StructuredTaskInstanceView(taskStructure
                    .getSelectedTaskInstanceView(), taskStructure);

        } catch (SHTMException e) {
            sdap.rollbackTx();
            throw new SHTMException(e);
        } finally {
            sdap.close();
        }
    }

    public void printStructureInfos(String tiid, boolean shortForm)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();

            TaskStructure strTask = new TaskStructure(tiid);

            System.out.println(System.getProperty("line.separator")
                    + strTask.getStructureInfos("", false, shortForm));

            sdap.commitTx();

        } catch (SHTMException e) {
            sdap.rollbackTx();
            throw new SHTMException(e);
        } finally {
            sdap.close();
        }
    }

    public void printAndDeleteStructureDatas() throws SHTMDataBaseException,
            SHTMException {
        try {
            sdap.beginTx();

            for (StructuredTask taskStructure : sdap.getAllStructureData()) {
                System.out.println(taskStructure.getTaskInfos(false));
            }
            sdap.deleteStructureDatas();
            sdap.commitTx();

        } catch (SHTMDataBaseException e) {
            sdap.rollbackTx();
            throw e;
        } finally {
            sdap.close();
        }
    }

    public String getTaskDescription(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            String result = taskStructure.getSelectedTaskDescription();
            sdap.commitTx();
            return result;
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw e;
        } finally {
            sdap.close();
        }

    }

    public List<WorkItemView> query(String whereClause)
            throws HumanTaskManagerException {
        return this.query(whereClause, -1);
    }

    public List<WorkItemView> query(String whereClause, int maxResults)
            throws HumanTaskManagerException {
        List<WorkItemView> resultList = new ArrayList<WorkItemView>();

        List<StructuredWorkItemView> strResults = this.query(whereClause, null,
                maxResults);
        for (StructuredWorkItemView strWorkItem : strResults) {
            resultList.add(strWorkItem.getWorkItem());
        }
        return resultList;

    }

    public List<StructuredWorkItemView> query(String whereClause,
                                              String structureWhereClause, int max)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            List<StructuredWorkItemView> resultList = sdap.query(whereClause,
                    structureWhereClause, max);
            sdap.commitTx();
            return resultList;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    public TaskInstanceView getTaskInfo(String tiid)
            throws HumanTaskManagerException {
        try {
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);
            TaskInstanceView result = taskStructure.getSelectedTaskInfo();
            sdap.commitTx();
            return result;
        } catch (HumanTaskManagerException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }

    }

    /**
     * Perform all state operations
     *
     * @param tiid
     * @param operation
     * @param parameters
     * @throws HumanTaskManagerException
     */
    private void performStateOperation(String tiid, EOperations operation,
                                       Map<String, Object> parameters) throws HumanTaskManagerException {
        try {
            log.info(operation.toString() + " on Task " + tiid);
            sdap.beginTx();
            TaskStructure taskStructure = new TaskStructure(tiid);

            // check if the External Task of the structure is already exited by
            // the parent task
            taskStructure.checkExternalTask();

            // check if the task is locked or controlled
            taskStructure.checkGeneralOperationPreconditions();

            // check if the correct state pair for this operation are given
            taskStructure.checkStatesForOperation(operation);

            // propagate operation on all descendants
            taskStructure.propagateStateOperation(operation, parameters);

            // propagate operation on all predecessor
            taskStructure.propagateStateToPredecessor();

            sdap.commitTx();
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }
    }

    /**
     * Perform all structure operations
     *
     * @param tiid1
     * @param tiid2
     * @param operation
     * @param parameters
     * @return
     * @throws HumanTaskManagerException
     */
    private String performStructureOperation(String tiid1, String tiid2,
                                             EOperations operation, Map<String, Object> parameters)
            throws HumanTaskManagerException {
        try {
            String taskModelName;
            String taskInstanceName;
            Object inputData;

            if (tiid2 == null) {
                log.info(operation.toString() + " on Task " + tiid1);
            } else {
                log.info(operation.toString() + " on Task " + tiid1
                        + " and on Task " + tiid2);
            }
            sdap.beginTx();

            String result = null;

            TaskStructure taskStructure = new TaskStructure(tiid1);

            // check if both tasks are not locked or controlled
            taskStructure.checkGeneralOperationPreconditions();

            TaskStructure strTask2 = null;
            if (tiid2 != null) {
                strTask2 = new TaskStructure(tiid2);
                strTask2.checkGeneralOperationPreconditions();
            }

            switch (operation) {
                case ADDSUBTASK:
                    taskModelName = (String) parameters.get("taskModelName");
                    taskInstanceName = (String) parameters.get("taskInstanceName");
                    inputData = (Object) parameters.get("inputData");

                    TaskStructure subTask = taskStructure.addSubTask(taskModelName,
                            taskInstanceName, inputData);
                    result = subTask.getSelectedTask_Id();
                    break;
                case REMOVESUBTASK:
                    taskStructure.removeSubTask();
                    break;
                case MERGE:
                    taskModelName = (String) parameters.get("taskModelName");
                    taskInstanceName = (String) parameters.get("taskInstanceName");
                    inputData = (Object) parameters.get("inputData");
                    result = taskStructure.mergeTask(strTask2, taskModelName,
                            taskInstanceName, inputData, true).getSelectedTask_Id();
                    break;
                case UNMERGE:
                    List<TaskStructure> freedTasks = taskStructure.unmergeTask();
                    for (TaskStructure freeTask : freedTasks) {
                        freeTask.propagateStateToPredecessor();
                    }

                    break;
                default:
                    String excMsg = "Operation " + operation.toString()
                            + " is no structure operation.";
                    throw new SHTMInvalidOperationException(excMsg);
            }

            sdap.commitTx();
            return result;
        } catch (DatabaseException e) {
            sdap.rollbackTx();
            throw new HumanTaskManagerException(e);
        } finally {
            sdap.close();
        }

    }
}
