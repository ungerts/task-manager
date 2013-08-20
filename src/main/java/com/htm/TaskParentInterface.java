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

package com.htm;

import java.sql.Timestamp;
import java.util.Set;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskparent.ICallbackHandler;


/**
 * Contains methods that can be performed by task
 * parent applications (e.g. a process engine or an human being).</br>
 * That's why this interface provides the methods to instantiate and
 * to exit a task instance.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public interface TaskParentInterface {

    /**
     * Creates a new instance of a task model and creates the corresponding work items.</br>
     * After the task instance is created the task instance state is {@link ETaskInstanceState#CREATED}.
     * <p>
     * Since the properties of a task instance (e.g. priority) are determined at runtime
     * by using the queries defined within the task model there must be an order
     * in which the attributes are determined. For example if somebody has defined an expression
     * that says that the task instance is skipable if the priority is higher than 5
     * the priority attribute has to determined before. Otherwise the skipable attribute can't be determined
     * since it depends on the value of the priority attribute.</br></br>
     * <p/>
     * <ol>
     * <li>Task instance name
     * <li>created on
     * <li>expiration
     * <li>priority
     * <li>skipable
     * <li>start by
     * <li>complete by
     * <li>presentation title
     * <li>presentation subject
     * <li>presentation description
     * </ol>
     * <p/>
     * </p>
     * <p>
     * After the task model has been instantiated work items are created based on the
     * people query results. Compliant implementations have to evaluate the
     * people queries in the following way:</br>
     * First the {@link IPeopleQuery people queries} which are defined within the task model are evaluated.
     * Then the results of the people queries can be modified by a {@link PeopleQueryPostProcessor} implementation.</br>
     * The work items for the human roles are created in the following order:
     * <ol>
     * <li>{@link EHumanRoles#TASK_INITIATOR Task Initiator} (implicitly defined by credentials parameter)
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR Business Administrators}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER Task Stakholders}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER Potenial Owners} (all potential owners that are not returned by the {@link EHumanRoles#EXCLUDED_OWNER Excluded Owners} query)
     * </ol>
     * </p>
     *
     * @param taskParentId          A unique id of a task parent. This attribute is required by the
     *                              {@link ICallbackHandler} to perform the callback for the correct task parent.
     * @param correlationProperties The correlation properties for enabling task parents to correlate callbacks from the
     *                              task manager. Can be <code>null</code>.
     * @param taskModelName         The name of the task model where the task instance shall be created from.
     * @param taskInstanceName      The name that the new task instance
     * @param inputMessage          The input message. Can be <code>null</code>.
     * @param attachments           The attachments of the task instance. Can be <code>null</code>.
     * @param expirationTime        Indicates when the task instance expires, i.e. when the task parent
     *                              is not interested any more in the result of the task. Can be <code>null</code>.
     * @return The task instance identifier
     * @throws HumanTaskManagerException
     * @see ITaskModel
     * @see ITaskModelStore
     * @see IPeopleQuery
     * @see ITaskInstance
     * @see PeopleQueryPostProcessor
     */
    public String createTaskInstance(String taskParentId, Set<ICorrelationProperty> correlationProperties,
                                     String taskModelName, String taskInstanceName,
                                     Object inputData, Set<IAttachment> attachments, Timestamp expirationTime) throws HumanTaskManagerException;


    /**
     * Informs the task manager that the task parent is no longer interested in the
     * outcome of the task instance.</br>
     * The task instance transitions to the state {@link ETaskInstanceState#EXITED}.
     *
     * @param tiid The task instance id of the task instance.
     * @throws HumanTaskManagerException
     */
    public void exit(String tiid) throws HumanTaskManagerException;


}
