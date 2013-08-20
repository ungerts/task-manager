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

import java.util.List;

import com.htm.ITaskClientInterface;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.shtm.exceptions.SHTMDataBaseException;
import com.shtm.exceptions.SHTMException;
import com.shtm.structureClasses.ResultStructure;
import com.shtm.views.StructuredTaskInstanceView;
import com.shtm.views.StructuredWorkItemView;

/**
 * Contains all new operation which are necessary to structure a human task and
 * to get informations about the structure
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public interface IStructuredTaskClientInterface extends ITaskClientInterface {
    /* Structure Operations */

    /**
     * Add a sub task to specified task
     *
     * @param tiid             Id of the new parent task
     * @param taskModelName    Model Name of the sub task. If it is null, the task model of
     *                         the parent task is used
     * @param taskInstanceName Name of the sub task instance. If it is null, the instance
     *                         name of the parent task is used
     * @param inputData        InputData of the sub task. If it is null, the InputData of the
     *                         parent task is used
     * @return Id of the new sub task
     * @throws HumanTaskManagerException
     */
    public String addSubTask(String tiid, String taskModelName,
                             String taskInstanceName, Object inputData) throws HumanTaskManagerException;

    /**
     * Deletes a sub task and all its descendants
     *
     * @param tiid Id of the sub task
     * @throws HumanTaskManagerException
     */
    public void removeSubTask(String tiid) throws HumanTaskManagerException;

    /**
     * Creates a merge task and adds the two specified tasks as controlled tasks
     *
     * @param tiid1            Id of the first specified task
     * @param tiid2            Id of the second specified task
     * @param taskModelName    Model name of the merge task. If it is null, the task model of
     *                         the first specified task is used
     * @param taskInstanceName Task instance name of the merge task. If it is null, the task
     *                         instance name of the first specified task is used
     * @param inputData        input Data of the merge task. If it is null, the input data of
     *                         the first specified task is used
     * @return Id of the new merge task
     * @throws HumanTaskManagerException
     */
    public String mergeTasks(String tiid1, String tiid2, String taskModelName,
                             String taskInstanceName, Object inputData) throws HumanTaskManagerException;

    /**
     * Deletes the specified merge task and sets its controlled tasks free
     *
     * @param tiid Id of the merge task
     * @throws HumanTaskManagerException
     */
    public void unmerge(String tiid) throws HumanTaskManagerException;

    /* Navigation Operations */

    /**
     * Returns the id of the parent task of the specified task
     *
     * @param tiid Id of the specified task
     * @return Id of the parent task
     * @throws HumanTaskManagerException
     */
    public String getParentTask(String tiid) throws HumanTaskManagerException;

    /**
     * Returns a list of all sub tasks of the specified task
     *
     * @param tiid Id of the specified task
     * @return List of ids of the sub tasks
     * @throws HumanTaskManagerException
     */
    public List<String> getSubTasks(String tiid) throws HumanTaskManagerException;

    /**
     * Returns the id of a the sub task of the specified task with the specified
     * structure number
     *
     * @param tiid  Id of the specified task
     * @param strNr Structure number of the sub tasks
     * @return Id of the sub task
     * @throws HumanTaskManagerException
     */
    public String getSubTaskByStructureNr(String tiid, int strNr)
            throws HumanTaskManagerException;

    /**
     * Returns the id of the Merge task of the specified task
     *
     * @param tiid Id of the specified task
     * @return Id of the merge task
     * @throws HumanTaskManagerException
     */
    public String getMergeTask(String tiid) throws HumanTaskManagerException;

    /**
     * Returns a list of all controlled tasks of the specified task
     *
     * @param tiid Id of the specified task
     * @return List of ids of the controlled tasks
     * @throws HumanTaskManagerException
     */
    public List<String> getControlledTasks(String tiid) throws HumanTaskManagerException;


    /* Task Attribute Operations */

    /**
     * Returns a ResultStructure about the specified task with input datas
     *
     * @param tiid Id of the specified task
     * @return ResultStructure with input datas
     * @throws HumanTaskManagerException
     */
    public ResultStructure<String, Object> getInputStructure(String tiid)
            throws HumanTaskManagerException;

    /**
     * Returns a ResultStructure about the specified task with output datas
     *
     * @param tiid Id of the specified task
     * @return ResultStructure with output datas
     * @throws HumanTaskManagerException
     */
    public ResultStructure<String, Object> getOutputStructure(String tiid)
            throws HumanTaskManagerException;

    /**
     * Returns a ResultStructure about the specified task with fault datas
     *
     * @param tiid Id of the specified task
     * @return ResultStructure with fault datas
     * @throws HumanTaskManagerException
     */
    public ResultStructure<String, IFault> getFaultStructure(String tiid)
            throws HumanTaskManagerException;

    /**
     * Returns a ResultStructure about the specified task with attachments of
     * the specified name
     *
     * @param tiid Id of the specified task
     * @param name Name of the attachments
     * @return ResultStructure with attachment
     * @throws HumanTaskManagerException
     */
    public ResultStructure<String, List<IAttachment>> getAttachmentStructure(
            String tiid, String name) throws HumanTaskManagerException;

    /**
     * Set the structure number for a specified task. This number must be unique
     * among their sibling tasks
     *
     * @param tiid  Id of the specified task
     * @param strNr Structure number
     * @throws HumanTaskManagerException
     */
    public void setStructureNr(String tiid, int strNr) throws HumanTaskManagerException;

    /**
     * Returns the structure number of the specified task
     *
     * @param tiid Id of the specified task
     * @return Structure number
     * @throws HumanTaskManagerException
     */
    public int getStructureNr(String tiid) throws HumanTaskManagerException;

    /**
     * Return a view with the task instance data and the structure data of the
     * specified task
     *
     * @param tiid Id of the specified task
     * @return A view of the task instance and its structure data
     * @throws HumanTaskManagerException
     */
    public StructuredTaskInstanceView getStructuredTaskInfo(String tiid)
            throws HumanTaskManagerException;

    /**
     * Retrieve work items views of all work items view tuples that meet the
     * query condition and the structure condition
     *
     * @param whereClause          The where clause in an SQL-like syntax.
     * @param structureWhereClause The structure-where clause in an SQL-like syntax.
     * @param max                  The number of work item views returned by the query will not
     *                             exceed this limit.
     * @return A list of structured work item views.
     * @throws HumanTaskManagerException
     */
    public List<StructuredWorkItemView> query(String whereClause,
                                              String structureWhereClause, int max) throws HumanTaskManagerException;

    /**
     * A support operation, that writes the structure of the specified task to
     * the console
     *
     * @param tiid      Id of the specified task
     * @param shortForm if true for each task will just id, state and lock state be
     *                  returned else all structure information will be returned
     * @throws HumanTaskManagerException
     */
    public void printStructureInfos(String tiid, boolean shortForm)
            throws HumanTaskManagerException;

    /**
     * Clean up operation. Writes all data from the structure database to the
     * console and then deletes them
     *
     * @throws DatabaseException
     * @throws HumanTaskManagerException
     */
    public void printAndDeleteStructureDatas() throws SHTMDataBaseException,
            SHTMException;

}