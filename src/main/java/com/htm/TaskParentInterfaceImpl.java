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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.htm.audit.AuditAction;
import com.htm.audit.AuditFactory;
import com.htm.audit.IAuditLogger;
import com.htm.configuration.Configuration;
import com.htm.db.IDataAccessProvider;
import com.htm.dm.EHumanRoles;
import com.htm.events.CreateWorkItemEvent;
import com.htm.events.EventHandler;
import com.htm.exceptions.AuthenticationException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.InvalidOperationException;
import com.htm.exceptions.UserException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.security.AuthorizationManager;
import com.htm.security.EActions;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskinstance.WorkItemFactory;
import com.htm.utils.SessionUtils;
import com.htm.utils.TaskInstanceTimers;
import com.htm.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * The implementation of the task parent interface.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */

@Transactional
public class TaskParentInterfaceImpl implements TaskParentInterface {

    @Autowired
    protected IDataAccessProvider dataAccessProvider;

    protected EventHandler evenHandler;

    protected Logger log = Utilities.getLogger(this.getClass());

    @Autowired
    private TaskInstanceFactory taskInstanceFactory;

    @Autowired
    private WorkItemFactory workItemFactory;

    /**
     * Creates a new {@link TaskParentInterfaceImpl} object.</b> It initializes
     * the {@link IDataAccessProvider}</b> Moreover all timers are reactivated
     * (e.g. suspend until timer, expiration timer). See
     * {@link TaskInstanceTimers} for more information.
     */
    public TaskParentInterfaceImpl() {
        //this.dataAccessProvider = IDataAccessProvider.Factory.newInstance();
        this.evenHandler = EventHandler.newInstance();

        /*
           * Reactivate all task instance timers (e.g. suspend until timer,
           * expiration timer) i.e. schedule the execution of certain actions
           * until their timer has expired.
           */
        TaskInstanceTimers.reactivateTimers();
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.TaskParentInterface#createTaskInstance(java.lang.String,
      * java.lang.String, java.lang.Object, java.util.Set, java.lang.String,
      * java.sql.Timestamp)
      */
    public String createTaskInstance(String taskParentId,
                                     Set<ICorrelationProperty> correlationProperties,
                                     String taskModelName, String taskInstanceName, Object inputData,
                                     Set<IAttachment> attachments, Timestamp expirationTime)
            throws HumanTaskManagerException {

        try {

            // TODO contextId
            String initiatorUserId = getCurrentUser();

            if (StringUtils.isEmpty(initiatorUserId)) {
                String errorMsg = "The username of the task initiator could not be determined.";
                log.error(errorMsg);
                throw new HumanTaskManagerException(errorMsg);
            }

            /* Start transaction for creating a task instance */
            dataAccessProvider.beginTx();
            AuthorizationManager.authorizeTaskParentAction(initiatorUserId,
                    null, EActions.CREATE_TASK_INSTANCE);

            /* Create the task instance model */
            TaskInstanceFactory taskFac = this.taskInstanceFactory;
            ITaskInstance taskInstance = taskFac.createTaskInstance(
                    taskModelName, taskInstanceName, inputData, taskParentId,
                    correlationProperties, expirationTime);

            /* Store the task instance instance */
            dataAccessProvider.persistHumanTaskInstance(taskInstance);

            // Audit
            // if (Configuration.isLoggingEnabled()) {
            // IAuditLogger auditLogger = AuditFactory.newInstance();
            // TaskInstanceView taskInstanceView = new
            // TaskInstanceView(taskInstance);
            // AuditAction action = new
            // AuditAction(EActions.CREATE_TASK_INSTANCE.toString(),
            // taskInstanceView, taskInstanceView.getStatus(), null,
            // initiatorUserId);
            // auditLogger.logAction(action);
            // }

            /* Activate the task instance expiration timer. */
            TaskInstanceTimers.activateExpirationTimer(taskInstance);

            /*
                * Create Work Items and add attachments
                */
            List<IWorkItem> workItems = new ArrayList<IWorkItem>();

            WorkItemFactory workItemFac = this.workItemFactory;
            /*
                * A single work item for the task initiator has to be created
                * because she is not defined in the task model by a people query.
                * This should be done before the other work items are created since
                * the people queries in the task model could refer on the task
                * initiator.
                */
            workItems.add(workItemFac.createNewWorkItem(initiatorUserId,
                    EHumanRoles.TASK_INITIATOR, taskInstance));
            /*
                * The attachments should also be added before the other work items
                * are created then the people queries can refer on the attachments.
                */
            taskInstance.setAttachments(this.taskInstanceFactory
                    .createAssignedUser(initiatorUserId), attachments);

            /*
                * Create work items for roles that are defined via people queries
                * within the task model i.e. for roles business administrators,
                * task stakeholder, potential owners
                */
            workItems.addAll(workItemFac
                    .createWorkItemsForTaskInstance(taskInstance));

            // TODO: evaluate QueryProperties
            taskFac.evaluateQueryProperties(taskInstance.getId(), taskModelName);

            /*
                * If the task instance has one or more potential owners it
                * transitions to ready state i.e. it is activated
                */
            boolean hasPotentialOwner = taskInstance.hasPotentialOwners();

            log.debug("Create task instance - Task instance has potential owners : "
                    + hasPotentialOwner);
            /*
                * The task instance can only set to the READY state if it has
                * potential owners and if the task instance is not already expired.
                */
            String oldState = taskInstance.getStatus().toString();
            if (taskInstance.hasPotentialOwners() && !taskInstance.isExpired()) {
                taskInstance.setStatus(ETaskInstanceState.READY);
                taskInstance.setActivationTime(new Timestamp(Calendar
                        .getInstance().getTimeInMillis()));
                // TODO only one potential owner -> immediately go to reserved
                // state
            }

            if (!hasPotentialOwner) {
                log.info("Create task instance - " + "Can't transition to '"
                        + ETaskInstanceState.READY
                        + "' state because no potential owner(s) were found.");
            }
            if (taskInstance.isExpired()) {
                log.info("Create task instance - " + "The task instance is "
                        + ETaskInstanceState.OBSOLETE
                        + " created because it has already expired at '"
                        + Utilities.formatTimestamp(expirationTime) + "'.");
            }

            dataAccessProvider.persistWorkItems(workItems);
            /* Inform event subscriber about the new work items */
            publishNewWorkItemEvent(workItems);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.CREATE_TASK_INSTANCE.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, initiatorUserId);
                auditLogger.logAction(action);
            }

            dataAccessProvider.commitTx();
            return taskInstance.getId();
        } catch (HumanTaskManagerException e) {
            dataAccessProvider.rollbackTx();
            throw e;
        } finally {
            dataAccessProvider.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.TaskParentInterface#exit(java.lang.String)
      */
    public void exit(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction for creating a task instance */
            dataAccessProvider.beginTx();
            log.debug("Exit task instance - Trying to exit task instance '"
                    + tiid + "'");
            AuthorizationManager.authorizeTaskParentAction(getCurrentUser(),
                    tiid, EActions.EXIT);
            ITaskInstance taskInstance = dataAccessProvider.getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            // TODO for skipped, completed, faulted task instances an individual
            // error message should
            // be created. Currently only an invalidarguemntexception is thrown
            if (taskInstance.isExpired()) {
                String errorMsg = "The task instance '"
                        + taskInstance.getId()
                        + "' can not be exited "
                        + "because it expired on "
                        + Utilities.formatTimestamp(taskInstance
                        .getExpirationTime());
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg);
            }

            taskInstance.setStatus(ETaskInstanceState.EXITED);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.EXIT.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }
            dataAccessProvider.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessProvider.rollbackTx();
            throw e;

        } finally {
            dataAccessProvider.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskManagerLogin#login(java.lang.String, java.lang.String)
      */
    @Deprecated
    public String login(String userid, String password)
            throws AuthenticationException {
        return null;
    }

    protected String getCurrentUser() throws UserException {
        return SessionUtils.getCurrentUser();
    }

    protected void publishNewWorkItemEvent(List<IWorkItem> workItems) {
        if (workItems != null) {
            Iterator<IWorkItem> iter = workItems.iterator();
            /*
                * Create an event for each work item that was created and inform
                * the subscribers about it
                */
            while (iter.hasNext()) {
                evenHandler.notifySubscribers(new CreateWorkItemEvent(
                        new WorkItemView((IWorkItem) iter.next())));
            }

        }

    }

}
