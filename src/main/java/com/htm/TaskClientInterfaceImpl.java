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
import java.util.Collection;
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
import com.htm.events.EventHandler;
import com.htm.events.ModifyWorkItemEvent;
import com.htm.exceptions.AuthenticationException;
import com.htm.exceptions.AuthorizationException;
import com.htm.exceptions.ConfigurationException;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.InvalidOperationException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.security.AuthorizationManager;
import com.htm.security.EActions;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskinstance.WorkItemFactory;
import com.htm.taskparent.ITaskParentConnector;
import com.htm.utils.SessionUtils;
import com.htm.utils.TaskInstanceTimers;
import com.htm.utils.Utilities;

/**
 * The implementation of the task client interface.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public class TaskClientInterfaceImpl implements ITaskClientInterface {

    /**
     * Provides access to the persistence layer.
     */
    private IDataAccessProvider dap;

    protected EventHandler evenHandler;

    /**
     * The log4j logger.
     */
    private Logger log;

    /**
     * Creates a new {@link TaskClientInterfaceImpl} object.</b> It initializes
     * the {@link IDataAccessProvider} and the logger.</b> Moreover all timers
     * are reactivated (e.g. suspend until timer, expiration timer). See
     * {@link TaskInstanceTimers} for more information.
     */
    public TaskClientInterfaceImpl() {
        this.dap = IDataAccessProvider.Factory.newInstance();
        this.log = Utilities.getLogger(this.getClass());
        this.evenHandler = EventHandler.newInstance();

        /*
           * Reactivate all task instance timers (e.g. suspend until timer,
           * expiration timer) i.e. schedule the execution of certain actions
           * until their timers have expired.
           */
        TaskInstanceTimers.reactivateTimers();

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#claim(java.lang.String)
      */
    public void claim(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            /* Check if task instance is already claimed */
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();
            if (taskInstance.isClaimed()) {
                String errorMsg = "The operation '" + EActions.CLAIM
                        + "' can not be performed since the task"
                        + " instance '" + tiid + "' is already claimed by '"
                        + taskInstance.getActualOwner() + "'.";
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg);
            }

            checkIfTaskInstanceExpired(taskInstance, EActions.CLAIM.toString());

            IAssignedUser user = AuthorizationManager
                    .authorizeTaskClientAction(tiid, EActions.CLAIM);

            /*
                * Claimed tasks instances go to the reserved state. An exception is
                * thrown if the transition to the reserved state is not possible
                */
            try {
                taskInstance.setStatus(ETaskInstanceState.RESERVED);
            } catch (IllegalArgumentException e) {
                String errorMsg = "The task instance '" + tiid
                        + "' can not be claimed. ";
                log.error(errorMsg, e);
                throw new InvalidOperationException(e);
            }

            List<IWorkItem> workItems = dap.getWorkItems(tiid, user);
            Iterator<IWorkItem> iter = workItems.iterator();

            /*
                * Set each work item work item to the status "claimed" that
                * associates the user to the task instance. The user can have
                * multiple roles (e.g. Business Admin or Task Stakeholder) thus
                * multiple work items could have been returned.
                */
            while (iter.hasNext()) {
                IWorkItem workItem = iter.next();
                workItem.setClaimed(true);
                workItem.setGenericHumanRole(EHumanRoles.ACTUAL_OWNER);
                /* Inform the subscribers that the work item was modified */
                evenHandler.notifySubscribers(new ModifyWorkItemEvent(
                        new WorkItemView(workItem)));

            }

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.CLAIM.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#complete(java.lang.String,
      * java.lang.Object)
      */
    public void complete(String tiid, Object outputData)
            throws IllegalArgumentException, HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.COMPLETE);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance,
                    EActions.COMPLETE.toString());

            /* Output data is required for completing a task instance */
            // TODO what happens if the output data object was set before via
            // setOutput method
            if (outputData == null) {
                String errorMsg = "Task instance '"
                        + tiid
                        + "' can not be completed because no output message was set.";
                log.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            taskInstance.setOutputData(outputData);
            try {
                taskInstance.setStatus(ETaskInstanceState.COMPLETED);
            } catch (IllegalArgumentException e) {
                String errorMsg = "Task instance '" + tiid
                        + "' can not be completed.";
                log.error(errorMsg, e);
                throw new InvalidOperationException(errorMsg, e);
            }

            /* Inform task parent and observers that task was completed */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.COMPLETE.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#addAttachment(java.lang.String,
      * com.htm.dm.taskinstance.IAttachment)
      */
    public void addAttachment(String tiid, IAttachment attachment)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            IAssignedUser user = AuthorizationManager
                    .authorizeTaskClientAction(tiid, EActions.ADD_ATTACHMENT);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            IAssignedUser assignedUser = dap.getAssignedUser(user.getUserId());

            /*
                * Should never become null since method authorizeUserForAction
                * already checks if the user is assigned to the task instance.
                */
            if (assignedUser != null) {
                attachment.setAttachedAt(Utilities.getCurrentTime());
                taskInstance.addAttachment(assignedUser, attachment);

            } else {
                String errorMsg = "The user " + user.getUserId()
                        + " is not assigned to the task instance '" + tiid
                        + "'.";
                throw new AuthorizationException(errorMsg);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#deleteAttachments(java.lang.String,
      * java.lang.String)
      */
    public boolean deleteAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            IAssignedUser assignedUser = AuthorizationManager
                    .authorizeTaskClientAction(tiid,
                            EActions.DELETE_ATTACHMENTS);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            Set<IAttachment> attachments = taskInstance.getAttachments();
            boolean attachmentDeleted = false;
            if (attachments != null) {
                Iterator<IAttachment> iter = attachments.iterator();
                /*
                     * Remove attachments which have the specified name. Currently
                     * the name of an attachment is unique i.e. maximal one
                     * attachment is deleted.
                     */
                while (iter.hasNext()) {
                    IAttachment attachment = iter.next();
                    if (attachment.getName().equals(name)) {
                        iter.remove();
                        attachmentDeleted = true;
                        log.debug("Delete attachment - Attachment '"
                                + attachment.getName() + "' of task instance '"
                                + tiid
                                + "' was deleted. Number of attachments left: "
                                + attachments.size());
                    }
                }
            }
            /* Set a new list of attachments without the deleted ones */
            taskInstance.setAttachments(assignedUser, attachments);

            dap.commitTx();
            return attachmentDeleted;
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getAttachments(java.lang.String,
      * java.lang.String)
      */
    public List<IAttachment> getAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_ATTACHMENTS);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            Set<IAttachment> attachments = taskInstance.getAttachments();
            /* The list contains the attachments which have the specified names */
            List<IAttachment> attachments2Return = new ArrayList<IAttachment>();
            if (attachments != null) {
                Iterator<IAttachment> iter = attachments.iterator();
                /*
                     * Currently the name of an attachment is unique i.e. maximal
                     * one attachment is added to the list.
                     */
                while (iter.hasNext()) {
                    IAttachment attachment = iter.next();
                    /*
                          * If no attachment name was specified add every attachment,
                          * otherwise only the attachments with the specified name
                          */
                    if (name == null || attachment.getName().equals(name)) {
                        attachments2Return.add(attachment);
                    }
                }
            }

            dap.commitTx();
            return attachments2Return;
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#deleteFault(java.lang.String)
      */
    public void deleteFault(String tiid) throws HumanTaskManagerException {

        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.DELETE_FAULT);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            taskInstance.setFaultData(null);
            taskInstance.setFaultName(null);

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#deleteOutput(java.lang.String)
      */
    public void deleteOutput(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.DELETE_OUTPUT);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            taskInstance.setOutputData(null);

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.bosch.workflow.core.taskhandler.ITaskClientInterface#fail(java.lang
      * .String, java.lang.String, java.lang.Object)
      */
    public void fail(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid, EActions.FAIL);
            // TODO what happens if the fault object was set before via setFault
            // method
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance, EActions.FAIL.toString());

            /* Fault name and message message is required for raising a fault */
            if (StringUtils.isEmpty(faultName)) {
                String errorMsg = "A fault for the task instance '"
                        + tiid
                        + "' can not be raised because no fault message was set.";
                log.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            if (faultData == null) {
                throw new IllegalArgumentException("Task instance '" + tiid
                        + "' can not raise fault '" + faultName
                        + "' since no fault message was set.");
            }
            try {
                taskInstance.setStatus(ETaskInstanceState.FAILED);
            } catch (IllegalArgumentException e) {
                String errorMsg = "Task instance '" + tiid
                        + "' can not raise fault '" + faultName + "'. ";
                log.error(errorMsg, e);
                throw new InvalidOperationException(errorMsg, e);
            }
            taskInstance.setFaultName(faultName);
            taskInstance.setFaultData(faultData);
            /* Inform task parent that task failed */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.FAIL.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getFault(java.lang.String)
      */
    public IFault getFault(String tiid) throws HumanTaskManagerException {
        try {
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_FAULT);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            dap.commitTx();
            return taskInstance.getFault();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getInput(java.lang.String)
      */
    public Object getInput(String tiid) throws HumanTaskManagerException {

        try {
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_INPUT);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            dap.commitTx();
            return taskInstance.getInput();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getOutput(java.lang.String)
      */
    public Object getOutput(String tiid) throws HumanTaskManagerException {
        try {
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_OUTPUT);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            dap.commitTx();
            return taskInstance.getOutput();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getTaskDescription(java.lang.String)
      */
    public String getTaskDescription(String tiid)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_TASK_DESCRIPTION);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            dap.commitTx();
            return taskInstance.getPresentationDescription();

        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#getTaskInfo(java.lang.String)
      */
    public TaskInstanceView getTaskInfo(String tiid)
            throws HumanTaskManagerException {

        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.GET_TASK_INFO);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            dap.commitTx();

            return new TaskInstanceView(taskInstance);
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#login(java.lang.String,
      * java.lang.String)
      */
    @Deprecated
    public String login(String userId, String password)
            throws AuthenticationException {
        return null;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#release(java.lang.String)
      */
    public void release(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.RELEASE);

            /* Fist simply change the state of the task instance to ready */
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance,
                    EActions.RELEASE.toString());

            try {
                taskInstance.setStatus(ETaskInstanceState.READY);
            } catch (IllegalArgumentException e) {
                String errorMsg = "Task instance '"
                        + tiid
                        + "' can not be released. Check if it was claimed before.";
                log.error(errorMsg, e);
                throw new InvalidOperationException(errorMsg, e);
            }

            /* Secondly revoke the 'claimed' state for all work items. */
            List<IWorkItem> workItems = dap.getWorkItems(tiid);
            Iterator<IWorkItem> iter = workItems.iterator();
            while (iter.hasNext()) {
                IWorkItem workItem = (IWorkItem) iter.next();
                workItem.setClaimed(false);
            }

            /* Inform task parent that task was released */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.RELEASE.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#resume(java.lang.String)
      */
    public void resume(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.RESUME);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance, EActions.RESUME.toString());

            try {
                taskInstance.setSuspended(false);
            } catch (IllegalArgumentException e) {
                String errorMsg = "Resume task instance - Task instance '"
                        + tiid + "' can not be resumed. "
                        + "Check if it was suspended before.";
                log.error(errorMsg, e);
                throw new InvalidOperationException(errorMsg, e);
            }
            /* Inform task parent that task was resumed */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.RESUME.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.bosch.workflow.core.taskhandler.ITaskClientInterface#setFault(java
      * .lang.String, java.lang.String, java.lang.Object)
      */
    public void setFault(String tiid, String faultName, Object faultMessage)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.SET_FAULT);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            taskInstance.setFaultName(faultName);
            taskInstance.setFaultData(faultMessage);

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#setOutput(java.lang.String,
      * java.lang.Object)
      */
    public void setOutput(String tiid, Object output)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.SET_OUTPUT);

            ITaskInstance taskInstance = dap.getTaskInstance(tiid);
            taskInstance.setOutputData(output);

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#setPriority(java.lang.String, int)
      */
    public void setPriority(String tiid, int priority)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.SET_PRIORITY);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            taskInstance.setPriority(priority);

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#skip(java.lang.String)
      */
    public void skip(String tiid) throws IllegalArgumentException,
            HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid, EActions.SKIP);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance, EActions.SKIP.toString());

            if (taskInstance.isSkipable()) {
                try {
                    taskInstance.setStatus(ETaskInstanceState.OBSOLETE);
                } catch (IllegalArgumentException e) {
                    String errorMsg = "Skip task instance - Task instance '"
                            + tiid + "' can not be skipped. Check"
                            + " if it is not already terminated.";
                    log.error(errorMsg, e);
                    throw new InvalidOperationException(errorMsg, e);
                }

                /* Inform task parent that task was skipped */
                publishStateChange(taskInstance);

                // Audit
                if (Configuration.isLoggingEnabled()) {
                    IAuditLogger auditLogger = AuditFactory.newInstance();
                    TaskInstanceView taskInstanceView = new TaskInstanceView(
                            taskInstance);
                    AuditAction action = new AuditAction(
                            EActions.SKIP.toString(), taskInstanceView,
                            taskInstanceView.getStatus(), oldState,
                            SessionUtils.getCurrentUser());
                    auditLogger.logAction(action);
                }

            } else {
                String errorMsg = "Skip task instance - Task instance '" + tiid
                        + "' can not be skipped "
                        + "because it is not marked as skipable";
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg);
            }
            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#start(java.lang.String)
      */
    public void start(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager
                    .authorizeTaskClientAction(tiid, EActions.START);
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance, EActions.START.toString());

            taskInstance.setStatus(ETaskInstanceState.IN_PROGRESS);
            /* Inform task parent that task was started */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.START.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#stop(java.lang.String)
      */
    public void stop(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            AuthorizationManager.authorizeTaskClientAction(tiid, EActions.STOP);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance, EActions.STOP.toString());
            try {
                taskInstance.setStatus(ETaskInstanceState.RESERVED);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "The task instance can not be stopped. "
                                + "Check if it was started before.", e);
            }
            /* Inform task parent that task was stopped */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.STOP.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#suspend(java.lang.String)
      */
    public void suspend(String tiid) throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.SUSPEND);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance,
                    EActions.SUSPEND.toString());
            try {
                taskInstance.setSuspended(true);
            } catch (IllegalArgumentException e) {
                String errorMsg = "Suspend task instance - Task instance '"
                        + tiid + "' was not be suspended";
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg, e);
            }
            /* Inform task parent that task was stopped */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.SUSPEND.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#suspendUntil(java.lang.String,
      * java.lang.String, java.sql.Timestamp)
      */
    public void suspendUntil(String tiid, Timestamp pointInTime)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.SUSPEND_UNTIL);

            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance,
                    EActions.SUSPEND_UNTIL.toString());

            /* If the task instance was suspended before it can't be suspended */
            if (taskInstance.isSuspended()) {
                String errorMsg = "Suspend task instance - The task instance '"
                        + tiid + "' is already suspended.";
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg);
            }
            /*
                * Even if the point of time until the task instance is suspended
                * has passed by it is set. But then the task instance isn't in the
                * state suspended.
                */
            taskInstance.setSuspendedUntil(pointInTime);

            /* Inform task parent that task was suspended */
            publishStateChange(taskInstance);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.SUSPEND_UNTIL.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            /*
                * Install a timer thread that resumes the task instance when the
                * suspend until time has expired
                */
            TaskInstanceTimers.activateSuspendUntilTimer(taskInstance);
            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#query(java.lang.String, int)
      */
    public List<WorkItemView> query(String whereClause, int maxResults)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskQueryAction(EActions.QUERY);
            List<IWorkItem> workItems = dap.query(whereClause, maxResults);
            Iterator<IWorkItem> iter = workItems.iterator();
            List<WorkItemView> workItemViews = new ArrayList<WorkItemView>();

            /*
                * Filter out those work items the user is not authorized to fetch
                * (e.g. a potential owner of a task instance is only allowed to
                * fetch the work item related to the task instance she is
                * associated to)
                */
            while (iter.hasNext()) {
                IWorkItem workItem = (IWorkItem) iter.next();
                try {
                    /*
                          * Currently anybody can get any information about any task
                          * instance/work item. This can be changed in the
                          * AuthorizationManager
                          */
                    AuthorizationManager.authorizeTaskClientAction(workItem
                            .getTaskInstance().getId(),
                            EActions.READ_WORK_ITEM_VIEW_TUPLE);
                    /* Create the view of the work item */
                    workItemViews.add(new WorkItemView(workItem));

                } catch (AuthorizationException e) {
                    /*
                          * If an AuthorizationException is thrown the user is not
                          * allowed to view the work item tuple -> it must not be
                          * added to the list of work item views.
                          */
                }
            }
            dap.commitTx();
            return workItemViews;
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ITaskClientInterface#query(java.lang.String,
      * java.lang.String)
      */
    public List<WorkItemView> query(String whereClause)
            throws HumanTaskManagerException {
        return query(whereClause, -1);
    }

    /**
     * Calls the task instance parent back to inform it about a status change.
     *
     * @param taskInstance The task instance where the status was changed.
     * @throws ConfigurationException
     */
    protected void publishStateChange(ITaskInstance taskInstance)
            throws ConfigurationException {
        ITaskParentConnector tpConnector = ITaskParentConnector.Factory
                .newInstance(taskInstance.getTaskParentId());

        tpConnector.getCallbackHandler().callTaskParent(
                taskInstance.getCorrelationProperties(),
                new TaskInstanceView(taskInstance));
    }

    /**
     * Returns a task instance from the persistence layer.
     *
     * @param tiid The id of the task instance that is returned.
     * @return The model representation of the task instance.
     * @throws IllegalArgumentException Thrown if the task instance specified by the task instance id
     *                                  could not be found.
     * @throws DatabaseException
     */
    protected ITaskInstance getTaskInstance(String tiid)
            throws IllegalArgumentException, DatabaseException {
        ITaskInstance taskInstance = dap.getTaskInstance(tiid);

        if (taskInstance != null) {
            return taskInstance;
        }
        String errorMessage = "Fetch task instance - A task instance with id '"
                + tiid + "' could not be found.";
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Throws an exception if the task instance is expired.</b> If it is expired
     * operations like start, stop, suspend etc. can not be performed anymore.
     * That's why they should check beforehand if the task instance is not
     * already expired.
     *
     * @param taskInstance The task instance that is to be checked.
     * @param operation    The operation that checks if the task instance is
     *                     expired.</br> This parameter is only required for making
     *                     exceptions more readable.
     * @throws InvalidOperationException Thrown if the task instance is already expired.
     */
    protected void checkIfTaskInstanceExpired(ITaskInstance taskInstance,
                                              String operation) throws InvalidOperationException {
        /* Check if task instance is expired */
        if (taskInstance.isExpired()) {
            String errorMsg = "The operation '"
                    + operation
                    + "' can not be performed since "
                    + "the task instance '"
                    + taskInstance.getId()
                    + "' expired on "
                    + Utilities.formatTimestamp(taskInstance
                    .getExpirationTime());
            log.error(errorMsg);
            throw new InvalidOperationException(errorMsg);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.bosch.workflow.core.taskhandler.ITaskClientInterface#forward(java
      * .lang.String, java.lang.String)
      */
    public void forward(String tiid, String forwardeeId)
            throws HumanTaskManagerException {
        // TODO delete work items , aber nur die von dnenen die nicht
        // stakeholder sind
        try {
            /* Start transaction */
            dap.beginTx();
            AuthorizationManager.authorizeTaskClientAction(tiid,
                    EActions.FORWARD);

            /* Fist simply change the state of the task instance to ready */
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            checkIfTaskInstanceExpired(taskInstance,
                    EActions.RELEASE.toString());

            /*
                * In the try catch block below it is checked if the task is either
                * claimed or in the 'ready'. If not it can not be forwarded.
                */
            String invalidStateErrorMsg = "Task instance '"
                    + tiid
                    + "' can not be forwarded to '"
                    + forwardeeId
                    + "' because it is not in the 'ready', 'reserved' or 'inProgress' state.";
            try {
                ETaskInstanceState currentState = taskInstance.getStatus();

                /*
                     * Check if the task instance is claimed, i.e. in the 'reserved'
                     * or in 'inProgress' state. If it is claimed release it.
                     */
                if (currentState.equals(ETaskInstanceState.RESERVED)
                        || currentState.equals(ETaskInstanceState.IN_PROGRESS)) {

                    release(tiid);
                    /*
                          * If the task is not claimed it must be in the 'ready'
                          * state
                          */
                } else if (!currentState.equals(ETaskInstanceState.READY)) {
                    log.error(invalidStateErrorMsg);
                    throw new InvalidOperationException(invalidStateErrorMsg);
                }
            } catch (IllegalArgumentException e) {
                log.error(invalidStateErrorMsg, e);
                throw new InvalidOperationException(invalidStateErrorMsg);
            }

            /*
                * Check if the forwardee is already in the list of potential owner,
                * i.e. if she is already assigned to a task via a work item having
                * the role potential owner. If not add her to the list.
                */
            IAssignedUser assignedUser = TaskInstanceFactory.newInstance()
                    .createAssignedUser(forwardeeId);
            IWorkItem potentialOwnerWI = dap.getWorkItem(tiid, assignedUser,
                    EHumanRoles.POTENTIAL_OWNER);
            if (potentialOwnerWI == null) {
                /* Add the forwardee to the list of potential owners */
                WorkItemFactory workItemFac = WorkItemFactory.newInstance();
                potentialOwnerWI = workItemFac.createNewWorkItem(forwardeeId,
                        EHumanRoles.POTENTIAL_OWNER, taskInstance);
                dap.persistWorkItem(potentialOwnerWI);
            }
            /*
                * Since she has to become an actual owner the task must be claimed
                * by her.
                */
            claim(tiid, forwardeeId);

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(
                        EActions.FORWARD.toString(), taskInstanceView,
                        taskInstanceView.getStatus(), oldState,
                        SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /**
     * @uml.property name="iAttachment"
     * @uml.associationEnd multiplicity="(0 -1)" inverse=
     * "taskClientInterfaceImpl:com.bosch.workflow.core.taskhandler.taskinstance.IAttachment"
     */
    private Collection<IAttachment> iAttachment;

    /**
     * Getter of the property <tt>iAttachment</tt>
     *
     * @return Returns the iAttachment.
     * @uml.property name="iAttachment"
     */
    public Collection<IAttachment> getIAttachment() {
        return iAttachment;
    }

    /**
     * Setter of the property <tt>iAttachment</tt>
     *
     * @param iAttachment The iAttachment to set.
     * @uml.property name="iAttachment"
     */
    public void setIAttachment(Collection<IAttachment> iAttachment) {
        this.iAttachment = iAttachment;
    }

    // TODO reduce redundancy
    // @Override
    private void claim(String tiid, String userid)
            throws HumanTaskManagerException {
        try {
            /* Start transaction */
            dap.beginTx();

            /* Check if task instance is already claimed */
            ITaskInstance taskInstance = getTaskInstance(tiid);
            String oldState = taskInstance.getStatus().toString();

            if (taskInstance.isClaimed()) {
                String errorMsg = "The operation '" + EActions.CLAIM
                        + "' can not be performed since the task"
                        + " instance '" + tiid + "' is already claimed by '"
                        + taskInstance.getActualOwner() + "'.";
                log.error(errorMsg);
                throw new InvalidOperationException(errorMsg);
            }

            checkIfTaskInstanceExpired(taskInstance, EActions.CLAIM.toString());

            IAssignedUser user = AuthorizationManager
                    .authorizeTaskClientAction2(tiid, userid, EActions.CLAIM);

            /*
                * Claimed tasks instances go to the reserved state. An exception is
                * thrown if the transition to the reserved state is not possible
                */
            try {
                taskInstance.setStatus(ETaskInstanceState.RESERVED);
            } catch (IllegalArgumentException e) {
                String errorMsg = "The task instance '" + tiid
                        + "' can not be claimed. ";
                log.error(errorMsg, e);
                throw new InvalidOperationException(e);
            }

            List<IWorkItem> workItems = dap.getWorkItems(tiid, user);
            Iterator<IWorkItem> iter = workItems.iterator();

            /*
                * Set each work item work item to the status "claimed" that
                * associates the user to the task instance. The user can have
                * multiple roles (e.g. Business Admin or Task Stakeholder) thus
                * multiple work items could have been returned.
                */
            while (iter.hasNext()) {
                IWorkItem workItem = iter.next();
                workItem.setClaimed(true);
                workItem.setGenericHumanRole(EHumanRoles.ACTUAL_OWNER);
                /* Inform the subscribers that the work item was modified */
                evenHandler.notifySubscribers(new ModifyWorkItemEvent(
                        new WorkItemView(workItem)));

            }

            // Audit
            if (Configuration.isLoggingEnabled()) {
                IAuditLogger auditLogger = AuditFactory.newInstance();
                TaskInstanceView taskInstanceView = new TaskInstanceView(
                        taskInstance);
                AuditAction action = new AuditAction(EActions.CLAIM.toString(),
                        taskInstanceView, taskInstanceView.getStatus(),
                        oldState, SessionUtils.getCurrentUser());
                auditLogger.logAction(action);
            }

            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

}
