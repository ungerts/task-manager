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
import java.util.List;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.security.AuthorizationManager;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;


/**
 * Contains all operations that can be performed by client applications i.e.  end users. These operation base on the participant operations defined in the WS-Human Task specification version 1.</br> The methods can only be performed if the user has the appropriate roles (see     {@link AuthorizationManager}    ).
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @uml.dependency supplier="com.bosch.workflow.core.taskhandler.taskinstance.jpa.AttachmentWrapper"
 * @uml.dependency supplier="com.bosch.workflow.core.taskhandler.taskinstance.IAttachment"
 */
public interface ITaskClientInterface {

    /**
     * Claim responsibility for a task instance, i.e. set the task to
     * status 'Reserved'.</br>
     * To claim the task instance it has to be in the 'Ready' state.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void claim(String tiid) throws HumanTaskManagerException;


    /**
     *  Claim responsibility for a task instance, i.e. set the task to
     * status 'Reserved'.</br>
     * To claim the task instance it has to be in the 'Ready' state.
     *
     * <p><b>Required authorization roles:</b>
     *   <li>{@link EHumanRoles#POTENTIAL_OWNER}
     *	<li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     *	<li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     * @see ETaskInstanceState
     *
     * @param tiid The id of the task instance.
     * @param userid The userid of the user
     * @throws HumanTaskManagerException
     */
    //public void claim(String tiid, String userid) throws HumanTaskManagerException;

    /**
     * Starts the execution of the task instance, i.e. set the task to
     * status 'InProgress'.</br>
     * To start the execution of the task instance it has to be in the
     * 'Reserved' state.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void start(String tiid) throws HumanTaskManagerException;

    /**
     * Cancel/stop the processing of the task instance.
     * The task instance returns to the 'Reserved' state.</br>
     * <p/>
     * To stop the execution of the task instance it has to be in the
     * started i.e. in the 'InProgress' state.
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void stop(String tiid) throws HumanTaskManagerException;

    /**
     * Release a claimed task instance, i.e. set it back to status 'Ready'.</br>
     * <p/>
     * To release the execution of the task instance it has to be
     * claimed i.e. in the state 'Reserved' state.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void release(String tiid) throws HumanTaskManagerException;

    /**
     * Suspend the task instance, i.e. it is set either to the state
     * 'Suspended Ready', 'Suspended Reserved' or 'Suspended InProgress'
     * (depends on its current state).</br>
     * <p/>
     * To suspend the execution of the task instance it has to be in the
     * 'Ready', 'Reserved' or 'InProgress' state.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void suspend(String tiid) throws HumanTaskManagerException;

    /**
     * Forwards a task instance to another user.</br>
     * <p/>
     * If the user the task instance is forwarded to is not a potential owner she is added
     * to the list of potential owners.
     * A task that is in a 'reserved' or 'inProgress' state is automatically released and
     * the user that has performed the forwarding is removed from the list of potential owners.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#POTENTIAL_OWNER} (if the task is in the 'ready' state)
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid        The id of the task instance.
     * @param forwardeeId The id of the user the task is forwarded to.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void forward(String tiid, String forwardeeId) throws HumanTaskManagerException;


    /**
     * Suspend the task instance until a certain point in time, i.e. it is set either to the state
     * 'Suspended Ready', 'Suspended Reserved' or 'Suspended InProgress'
     * (depends on its current state).</br>
     * <p/>
     * To suspend the execution of the task instance it has to be in the
     * 'Ready', 'Reserved' or 'InProgress' state.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid        The id of the task instance.
     * @param pointOfTime The point of time until when the task instance has to be suspended.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void suspendUntil(String tiid, Timestamp pointOfTime) throws HumanTaskManagerException;//Not implemented

    /**
     * Resumes the task instance, i.e. it is set either to the state
     * 'Ready', 'Reserved' or 'InProgress' (depends on state the task instance was suspended from).</br>
     * <p/>
     * To resume the execution of the task instance it has to be in the
     * 'Suspended Ready', 'Suspended Reserved' or 'Suspended InProgress' state.</br>
     * The task instance is even resumed if it is suspended until a certain point of time and
     * which has not passed by yet.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     */
    public void resume(String tiid) throws HumanTaskManagerException;

    /**
     * Finishes the execution of the task instance successfully.</br>
     * <p/>
     * To complete the execution of the task instance it has to be in the
     * 'InProgress' state.</br>
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid       The id of the task instance.
     * @param outputData The output data of the task instance. It can be <code>null</code>
     *                   if it is was set before.
     * @throws IllegalArgumentException  Thrown if no output data is set.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     * @see ITaskClientInterface#setOutput(String, String, Object)
     * @see ITaskClientInterface#getOutput(String, String)
     */
    public void complete(String tiid, Object outputData) throws com.htm.exceptions.IllegalArgumentException, HumanTaskManagerException;

    /**
     * Finishes the execution of the task instance by raising a fault.</br>
     * <p/>
     * To finish the execution of the task instance by raising a fault
     * it has to be in the 'InProgress' state.</br>
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid      The id of the task instance.
     * @param faultName The name of the fault that is raised. It can be <code>null</code>
     *                  if it is was set before.
     * @param faultData The fault data associated to the fault. It can be <code>null</code>
     *                  if it is was set before.
     * @throws IllegalArgumentException  Thrown if no fault name or fault data is set.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     * @see ITaskClientInterface#setFault(String, String, Object)
     * @see ITaskClientInterface#getFault(String, String)
     */
    public void fail(String tiid, String faultName, Object faultData) throws HumanTaskManagerException;

    /**
     * Skips the task instance. i.e. the it is set to the state 'Obsolete'.</br>
     * <p/>
     * To skip the task instance it has to be marked as 'Skipable'.</br>
     * In order to check if it is skipable use {@link ITaskClientInterface#getTaskInfo(String, String)}
     * method to get the task instance view object and call the method {@link TaskInstanceView#isSkipable()}
     * on that object.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#TASK_INITIATOR}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws IllegalArgumentException  Thrown if the task instance is not marked as 'Skipable'.
     * @throws HumanTaskManagerException
     * @see ETaskInstanceState
     * @see ITaskClientInterface#getTaskInfo(String, String)
     * @see TaskInstanceView#isSkipable()
     */
    public void skip(String tiid) throws IllegalArgumentException, HumanTaskManagerException;

    /**
     * Changes the priority of the task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid     The id of the task instance.
     * @param priority The priority as integer value where <code>0</code> is the highest priority.
     * @throws HumanTaskManagerException
     */
    public void setPriority(String tiid, int priority) throws HumanTaskManagerException;

    /**
     * Adds an attachment to the task instance.</br>
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid       The id of the task instance.
     * @param attachment The attachment that is added.
     * @throws HumanTaskManagerException
     * @see IAttachment
     */
    public void addAttachment(String tiid, IAttachment attachment) throws HumanTaskManagerException;

    /**
     * Returns all attachments with the given name that are attached to a task instance.</br>
     * Currently the attachment names are unique, i.e. that the list will contain at most one
     * attachment model.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @param name All attachments with this name are returned. If this parameter is <code>null</code> all attachments are returned.
     * @return A list of attachments.</br> If no attachment with the specified name could be found an empty list is returned.
     * @throws HumanTaskManagerException
     * @see IAttachment
     */
    public List<IAttachment> getAttachments(String tiid, String name) throws HumanTaskManagerException;


    /**
     * Deletes all attachments with the given name that are attached to a task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @param name The name of the attachments that have to be deleted.
     * @return <code>true</code> if at least one attachment was deleted, <code>false</code> otherwise
     * @throws HumanTaskManagerException
     * @see IAttachment
     * @see ITaskClientInterface#addAttachment(String, String, IAttachment)
     * @see ITaskClientInterface#getAttachments(String, String, String)
     */
    public boolean deleteAttachments(String tiid, String name) throws HumanTaskManagerException;

    /**
     * Returns an {@link TaskInstanceView} object. This object contains information i.e. properties of the task instance</br>
     * <b>Note:</b> The task instance view is detached from the task instance i.e. if the properties of the task instance
     * are changed these changes are not reflected by the task instance view. This method has to be executed again to get a
     * task instance view that is up to date.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ANYBODY}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @return Always a view of the task instance.</br>
     *         If the task instance doesn't exit an exception is thrown by the {@link AuthorizationManager}.
     * @throws HumanTaskManagerException
     */
    public TaskInstanceView getTaskInfo(String tiid) throws HumanTaskManagerException;

    /**
     * Returns the presentation description of the task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ANYBODY}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @return The description of the task instance. <code>Null</code> if no description exists.</br>
     *         If the task instance doesn't exit an exception is thrown by the {@link AuthorizationManager}.
     * @throws HumanTaskManagerException
     */
    public String getTaskDescription(String tiid) throws HumanTaskManagerException; //Content type not supported

    /**
     * Sets the output message of the task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid   The id of the task instance.
     * @param output The output data.
     * @throws HumanTaskManagerException
     */
    public void setOutput(String tiid, Object output) throws HumanTaskManagerException; //Part name omitted

    /**
     * Deletes the output message of the task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     */
    public void deleteOutput(String tiid) throws HumanTaskManagerException;

    /**
     * Sets the fault information i.e. fault name and fault message of the task instance.</br>
     * If the task instance is in the state {@link ETaskInstanceState#FAILED}
     * the fault name and message provide more detailed information.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid      The id of the task instance.
     * @param faultName The name of the fault.
     * @param faultData The fault message.
     * @throws HumanTaskManagerException
     */
    public void setFault(String tiid, String faultName, Object faultData) throws HumanTaskManagerException;

    /**
     * Deletes the fault information of the task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @throws HumanTaskManagerException
     */
    public void deleteFault(String tiid) throws HumanTaskManagerException;

    /**
     * Returns the input message of a task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#POTENTIAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @return The input data. If no input data is associated to the task instance <code>null</code> is returned.
     * @throws HumanTaskManagerException
     */
    public Object getInput(String tiid) throws HumanTaskManagerException; // part name omitted

    /**
     * Returns the output message of a task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @return The output data. If no output data is associated to the task instance <code>null</code> is returned.
     * @throws HumanTaskManagerException
     */
    public Object getOutput(String tiid) throws HumanTaskManagerException;

    /**
     * Returns the fault information of a task instance.
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ACTUAL_OWNER}
     * <li>{@link EHumanRoles#BUSINESS_ADMINISTRATOR}
     * <li>{@link EHumanRoles#TASK_STAKEHOLDER}
     * </p>
     * </br>
     *
     * @param tiid The id of the task instance.
     * @return The fault information. If no fault information is associated to the task instance <code>null</code> is returned.
     * @throws HumanTaskManagerException
     */
    public IFault getFault(String tiid) throws HumanTaskManagerException;

    /**
     * Retrieve {@link WorkItemView} of all work items view tuples that meet the query condition.</br>
     * The where clause can be created based on the view <code>WorkItemTaskView</code>
     * of the human task manager schema. </br>
     * For example:
     * <pre>taskClient.query("myCredential",
     * 	"GENERICHUMANROLE='POTENTIAL_OWNER' AND ASSIGNEE='wagnerse' AND ISCLAIMED=1")
     * 	</pre>
     * <p/>
     * <p/>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ANYBODY}
     * TODO: Currently anybody can get any information about any work item view.
     * This should be changed in future releases in the {@link AuthorizationManager}
     * </p>
     * </br>
     *
     * @param whereClause The where clause in an SQL-like syntax.
     * @param maxResults  The number of work item views returned by the query will not exceed this limit.
     * @return A list of work item views.</br>
     *         If no work item view tuple can be found that meets the query condition <code>null</code> is returned.
     * @throws HumanTaskManagerException
     * @see Human Task Manager schema.
     */
    public List<WorkItemView> query(String whereClause)
            throws HumanTaskManagerException;

    /**
     * Retrieve {@link WorkItemView} of all work items view tuples that meet the query condition.</br>
     * The where clause can be created based on the view <code>WorkItemTaskView</code>
     * of the human task manager schema. </br>
     * For example:
     * <pre>taskClient.query("myCredential",
     * 	"GENERICHUMANROLE='POTENTIAL_OWNER' AND ASSIGNEE='wagnerse' AND ISCLAIMED=1")
     * 	</pre>
     * <p><b>Required authorization roles:</b>
     * <li>{@link EHumanRoles#ANYBODY}
     * TODO: Currently anybody can get any information about any work item view.
     * This should be changed in future releases in the {@link AuthorizationManager}
     * </p>
     * </br>
     *
     * @param whereClause The where clause in an SQL-like syntax.
     * @param maxResults  The number of work item views returned by the query will not exceed this limit.
     * @return A list of work item views.</br>
     *         If no work item view tuple can be found that meets the query condition <code>null</code> is returned.
     * @throws HumanTaskManagerException
     * @see Human Task Manager schema.
     */
    public List<WorkItemView> query(String whereClause, int maxResults) throws HumanTaskManagerException;


}
