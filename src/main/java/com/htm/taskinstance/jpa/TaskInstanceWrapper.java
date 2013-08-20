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

package com.htm.taskinstance.jpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.htm.dm.EHumanRoles;
import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Assigneduser;
import com.htm.entities.jpa.Attachment;
import com.htm.entities.jpa.Callbackcorrelationproperty;
import com.htm.entities.jpa.Humantaskinstance;
import com.htm.entities.jpa.Humantaskmodel;
import com.htm.entities.jpa.Workitem;
import com.htm.exceptions.ConfigurationException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.ITaskParentContext;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskinstance.WorkItemFactory;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.taskparent.ITaskParentConnector;
import com.htm.utils.Utilities;

public class TaskInstanceWrapper implements ITaskInstance {

    protected Humantaskinstance taskInstanceEntity;

    protected Logger log = Utilities.getLogger(this.getClass());
    ;

    public TaskInstanceWrapper(Humantaskinstance taskInstanceEntity) {
        this.taskInstanceEntity = taskInstanceEntity;
    }

    public TaskInstanceWrapper(String taskName) {
        this.taskInstanceEntity = new Humantaskinstance();
        this.taskInstanceEntity.setName(taskName);
        this.taskInstanceEntity
                .setStatus(ETaskInstanceState.CREATED.toString());

    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.dm.taskinstance.ITaskInstance#addAttachment(com.htm.dm.taskinstance
      * .IAssignedUser, com.htm.dm.taskinstance.IAttachment)
      */
    public void addAttachment(IAssignedUser attachedBy, IAttachment attachment) {

        Attachment attachmentEntity = (Attachment) attachment.getAdaptee();
        /* Set user that attached the attachment to the task instance */
        attachmentEntity
                .setAssigneduser((Assigneduser) attachedBy.getAdaptee());
        /*
           * If first attachment is added the list of attachment entities is null
           */
        List<Attachment> attachmentEntities = taskInstanceEntity
                .getAttachments();
        if (attachmentEntities == null) {
            attachmentEntities = new ArrayList<Attachment>();
            taskInstanceEntity.setAttachments(attachmentEntities);
        }
        /* In JPA relations between two entities are always bidirectional */
        attachmentEntities.add(attachmentEntity);
        attachmentEntity.setHumantaskinstance(taskInstanceEntity);

    }

    public Set<ICorrelationProperty> getCorrelationProperties() {

        return new HashSet<ICorrelationProperty>(getCorrelationPropertyMap()
                .values());

    }

    public void setActivationTime(Timestamp activationTime) {
        taskInstanceEntity.setActivationtime(activationTime);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.dm.taskinstance.ITaskInstance#setAttachments(com.htm.dm.taskinstance
      * .IAssignedUser, java.util.Set)
      */
    public void setAttachments(IAssignedUser attachedBy,
                               Set<IAttachment> attachments) {

        if (attachedBy != null && attachments != null) {
            /* Always operate on an empty list of attachments */
            taskInstanceEntity.getAttachments().clear();
            /* Add all attachments */
            Iterator<IAttachment> iter = attachments.iterator();
            while (iter.hasNext()) {
                IAttachment attachmentModel = (IAttachment) iter.next();
                addAttachment(attachedBy, attachmentModel);
            }

        }

    }

    public void setCompleteBy(Timestamp completeBy) {
        taskInstanceEntity.setCompleteby(completeBy);

    }

    public void setCorrelationProperties(
            Set<ICorrelationProperty> correlationProperties) {

        if (correlationProperties != null) {
            /*
                * Add all correlation properties in the list to the wrapped task
                * instance entity
                */
            Iterator<ICorrelationProperty> iter = correlationProperties
                    .iterator();
            while (iter.hasNext()) {
                addCorrelationProperty(iter.next());
            }
        }
    }

    public void setCreatedOn(Timestamp createdOn) {
        taskInstanceEntity.setCreatedon(createdOn);

    }

    public void setExpirationTime(Timestamp expirationTime) {
        taskInstanceEntity.setExpirationtime(expirationTime);

    }

    public void setFaultData(Object faultData) throws HumanTaskManagerException {
        if (faultData != null) {
            taskInstanceEntity.setFaultdata(Utilities
                    .getBlobFromObject(faultData));
        } else {
            taskInstanceEntity.setFaultdata(null);
        }
    }

    public void setFaultName(String faultName) {
        taskInstanceEntity.setFaultname(faultName);

    }

    @Deprecated
    public void setId(String id) {
        taskInstanceEntity.setId(Utilities.transfrom2PrimaryKey(id));

    }

    public void setInputData(Object inputData) throws HumanTaskManagerException {
        /* Input data are optional */
        if (inputData != null) {
            taskInstanceEntity.setInputdata(Utilities
                    .getBlobFromObject(inputData));

        }
    }

    public void setTaskModel(ITaskModel taskModel) {
        taskInstanceEntity.setHumantaskmodel((Humantaskmodel) taskModel
                .getAdaptee());

        /*
           * In JPA relations between two entities are always bidirectional thus
           * we have to add the human task instance entity to the correlation
           * property as well
           */

    }

    public void setName(String name) {
        taskInstanceEntity.setName(name);

    }

    public void setOutputData(Object outputData)
            throws HumanTaskManagerException {
        if (outputData != null) {
            taskInstanceEntity.setOutputdata(Utilities
                    .getBlobFromObject(outputData));
        } else {
            taskInstanceEntity.setOutputdata(null);
        }
    }

    public void setPresentationDescription(String presentationDescription) {
        taskInstanceEntity.setPresentationdescription(Utilities
                .getBLOBFromString(presentationDescription));
    }

    public void setPresentationSubject(String presentationSubject) {
        taskInstanceEntity.setPresentationsubject(presentationSubject);
    }

    public void setPresentationTitle(String presentationTitle) {
        taskInstanceEntity.setPresentationname(presentationTitle);

    }

    public void setPriority(int priority) {
        taskInstanceEntity.setPriority(priority);

    }

    public void setSkipable(boolean isSkipable) {
        taskInstanceEntity.setSkipable(Utilities.booleanToShort(isSkipable));
    }

    public void setStartBy(Timestamp startBy) {
        taskInstanceEntity.setStartby(startBy);

    }

    public boolean completeByExists() {
        return taskInstanceEntity.getCompleteby() != null ? true : false;
    }

    public Timestamp getActivationTime() {
        return taskInstanceEntity.getActivationtime();
    }

    public Set<IAttachment> getAttachments() {

        List<Attachment> attachmentEntities = taskInstanceEntity
                .getAttachments();
        Set<IAttachment> attachmentModels = new HashSet<IAttachment>();

        /*
           * If no attachments are associated with the task instance
           * attachmentEntities is null.
           */
        if (attachmentEntities == null) {
            /* Return empty list of attachments */
            return attachmentModels;
        }

        Iterator<Attachment> iter = attachmentEntities.iterator();

        while (iter.hasNext()) {
            IAttachment attachmentModel = TaskInstanceFactory.newInstance()
                    .createAttachmentFromEntity((Attachment) iter.next());
            attachmentModels.add(attachmentModel);
        }

        return attachmentModels;
    }

    protected Map<String, ICorrelationProperty> getCorrelationPropertyMap() {
        List<Callbackcorrelationproperty> argumentEntities = taskInstanceEntity
                .getCallbackcorrelationproperties();

        Map<String, ICorrelationProperty> correlationProperties = new HashMap<String, ICorrelationProperty>();
        if (argumentEntities != null) {
            /*
                * Get all correlation properties and their associated names and
                * them to the map
                */
            Iterator<Callbackcorrelationproperty> iter = argumentEntities
                    .iterator();
            while (iter.hasNext()) {
                /* Get the correlation property entity */
                Callbackcorrelationproperty correlationProperty = (Callbackcorrelationproperty) iter
                        .next();
                String correlationPropertyName = correlationProperty.getName();
                if (correlationPropertyName != null) {
                    /*
                          * Add the name and the correlation property model to the
                          * map
                          */
                    correlationProperties.put(
                            correlationPropertyName,
                            /* Determine the model of the correlation property */
                            TaskInstanceFactory.newInstance()
                                    .createCorrelationPropertyFromEntity(
                                            correlationProperty));
                }
            }
        }
        return correlationProperties;
    }

    public Timestamp getCompleteBy() {
        return taskInstanceEntity.getCompleteby();
    }

    public Timestamp getCreatedOn() {
        return taskInstanceEntity.getCreatedon();
    }

    public Timestamp getExpirationTime() {
        return taskInstanceEntity.getExpirationtime();
    }

    public IFault getFault() {
        String faultName = getFaultName();
        Object faultData = getFaultData();

        /* Fault data and fault name are required to create a fault object */
        if (faultName == null || faultData == null) {
            return null;
        }

        return TaskInstanceFactory.newInstance().createFault(faultName,
                faultData);
    }

    protected Object getFaultData() {
        try {
            return Utilities.getObjectFromBlob(taskInstanceEntity
                    .getFaultdata());
        } catch (HumanTaskManagerException e) {
            log.error(e.getMessage());// TODO Exception Handling
            throw new RuntimeException(e);
        }
    }

    protected String getFaultName() {
        return taskInstanceEntity.getFaultname();
    }

    public String getId() {
        return Integer.toString(taskInstanceEntity.getId());
    }

    public Object getInput() {
        try {
            return Utilities.getObjectFromBlob(taskInstanceEntity
                    .getInputdata());
        } catch (HumanTaskManagerException e) {
            throw new RuntimeException(e);
        }
    }

    public ITaskModel getTaskModel() {
        return ModelElementFactory.newInstance().createTaskModel(
                taskInstanceEntity.getHumantaskmodel());
    }

    public String getName() {
        return taskInstanceEntity.getName();
    }

    public Object getOutput() {
        try {
            return Utilities.getObjectFromBlob(taskInstanceEntity
                    .getOutputdata());
        } catch (HumanTaskManagerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPresentationDescription() {
        return Utilities.getStringFromBLOB(taskInstanceEntity
                .getPresentationdescription());
    }

    public String getPresentationSubject() {
        return taskInstanceEntity.getPresentationsubject();
    }

    public String getPresentationName() {
        return taskInstanceEntity.getPresentationname();
    }

    public int getPriority() {
        return taskInstanceEntity.getPriority();
    }

    public Timestamp getStartBy() {
        return taskInstanceEntity.getStartby();
    }

    /*
      * (non-Javadoc)
      *
      * @see com.bosch.workflow.core.taskhandler.taskinstance.ITaskInstance#
      * getTaskParentContext()
      */
    public ITaskParentContext getTaskParentContext() {
        try {
            /* Get the task parent context by using the task parent connector */
            return ITaskParentConnector.Factory.newInstance(getTaskParentId())
                    .getTaskParentContext();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isSkipable() {
        return Utilities.shortToBoolean(taskInstanceEntity.getSkipable());
    }

    public boolean startByExists() {
        return taskInstanceEntity.getStartby() != null ? true : false;
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);
    }

    public WrappableEntity getAdaptee() {
        return taskInstanceEntity;
    }

    public void addCorrelationProperty(ICorrelationProperty correlationProperty) {
        /* Check if there is already a correlation property with the same name */
        if (getCorrelationPropertyMap().containsKey(
                correlationProperty.getName())) {
            throw new RuntimeException("An correlation property with name "
                    + correlationProperty.getName() + " already exists.");
            // TODO exception handling
        }

        /*
           * If there was never added a correlation property before the list
           * correlation properties within the wrapped task instance entity might
           * be null - thus it must be created.
           */
        if (taskInstanceEntity.getCallbackcorrelationproperties() == null) {
            taskInstanceEntity
                    .setCallbackcorrelationproperties(new ArrayList<Callbackcorrelationproperty>());
        }

        /*
           * Finally get the wrapped correlation property and add it. In JPA
           * relations between two entities are always bidirectional thus we have
           * to add the human task instance entity to the correlation property as
           * well
           */
        Callbackcorrelationproperty correlationPropertyEntity = (Callbackcorrelationproperty) correlationProperty
                .getAdaptee();
        taskInstanceEntity.getCallbackcorrelationproperties().add(
                correlationPropertyEntity);
        correlationPropertyEntity.setHumantaskinstance(taskInstanceEntity);

    }

    public void removeCorrelationProperty(
            ICorrelationProperty correlationPropertyToDelete) {
        List<Callbackcorrelationproperty> correlationEntities = taskInstanceEntity
                .getCallbackcorrelationproperties();
        if (correlationEntities != null) {
            /*
                * Search for the correlation property entity that has the same name
                * like the property that is to be deleted.
                */
            Iterator<Callbackcorrelationproperty> iter = correlationEntities
                    .iterator();
            while (iter.hasNext()) {
                Callbackcorrelationproperty correlationEntity = (Callbackcorrelationproperty) iter
                        .next();
                if (correlationEntity.getName().equals(
                        correlationPropertyToDelete.getName())) {
                    /* The correlation property entity was found thus delete it */
                    iter.remove();
                }
            }
        }

    }

    public boolean isClaimed() {
        List<Workitem> workItemEntities = taskInstanceEntity.getWorkitems();

        if (workItemEntities != null) {
            Iterator<Workitem> iter = workItemEntities.iterator();
            /*
                * If at least one work item is claimed the task instance is
                * considered as claimed
                */
            while (iter.hasNext()) {
                Workitem workItemEntity = iter.next();
                if (Utilities.shortToBoolean(workItemEntity.getClaimed())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setSuspendedUntil(Timestamp suspendUntil)
            throws IllegalArgumentException {

        /*
           * Check if the point of time until the task instance has to be
           * suspended not already has passed by
           */
        if (!Utilities.hasTimeExpired(suspendUntil)) {
            setSuspended(true);
        } else {
            setSuspended(false);
        }

        /*
           * Even if the point in time has already passed by set the 'suspend
           * until' time
           */
        taskInstanceEntity.setSuspenduntil(suspendUntil);

    }

    public Timestamp getSuspendUntil() {
        return taskInstanceEntity.getSuspenduntil();
    }

    public String getActualOwner() {
        Set<String> actualOwner = getUsersByHumanRole(EHumanRoles.ACTUAL_OWNER);
        /*
           * Either the task instance is claimed by exactly one actual owner or by
           * no one
           */
        if (actualOwner != null && actualOwner.size() == 1) {
            return actualOwner.iterator().next();
        }
        return null;
    }

    public Set<String> getBusinessAdministrators() {
        return getUsersByHumanRole(EHumanRoles.BUSINESS_ADMINISTRATOR);
    }

    public Set<String> getPotentialOwners() {
        return getUsersByHumanRole(EHumanRoles.POTENTIAL_OWNER);
    }

    public ETaskInstanceState getStatus() {

        ETaskInstanceState currentState = ETaskInstanceState
                .valueOf(taskInstanceEntity.getStatus());

        return currentState;
    }

    public String getTaskInitiator() {
        Set<String> taskInitiator = getUsersByHumanRole(EHumanRoles.TASK_INITIATOR);

        /* There is always exactly one task initiator */
        if (taskInitiator != null && taskInitiator.size() == 1) {
            return taskInitiator.iterator().next();
        }

        return null;
    }

    public String getTaskModelName() {
        Humantaskmodel taskModelEntity = taskInstanceEntity.getHumantaskmodel();

        if (taskModelEntity != null) {
            return taskModelEntity.getName();
        }
        return null;
    }

    public Set<String> getTaskStakeholders() {
        return getUsersByHumanRole(EHumanRoles.TASK_STAKEHOLDER);
    }

    public boolean hasAttachments() {
        return !getAttachments().isEmpty();
    }

    public boolean hasFault() {
        return taskInstanceEntity.getFaultname() != null
                && taskInstanceEntity.getFaultdata() != null;
    }

    public boolean hasPotentialOwners() {
        return !getUsersByHumanRole(EHumanRoles.POTENTIAL_OWNER).isEmpty();
    }

    public boolean hasActualOwner() {
        /*
           * The list contains exactly one actual owner if the task instance is
           * claimed
           */
        return !getUsersByHumanRole(EHumanRoles.ACTUAL_OWNER).isEmpty();
    }

    public boolean hasInputData() {
        return getInput() != null;
    }

    public boolean hasOutputData() {
        return getOutput() != null;
    }

    protected Set<String> getUsersByHumanRole(EHumanRoles role) {

        List<Workitem> workItemEntities = taskInstanceEntity.getWorkitems();

        /*
           * Contains the users assigned to the task instance with the specified
           * role.
           */
        Set<String> resultList = new HashSet<String>();
        /*
           * If there are no work items associated with the task instance there
           * aren't any owners either.
           */
        if (workItemEntities != null) {
            log.debug("Get users by role '" + role + "' - There are "
                    + workItemEntities.size()
                    + " work items associated to the task instance.");

            Iterator<Workitem> iter = workItemEntities.iterator();
            /*
                * Add all users assigned to the task instance to the result list
                * which have the appropriate role.
                */
            while (iter.hasNext()) {
                IWorkItem workItem = WorkItemFactory.newInstance()
                        .createWorkItemFromEntity((Workitem) iter.next());
                log.debug("Get users by role '" + role + "' - "
                        + "Work item with id '" + workItem.getId()
                        + "' has role '" + workItem.getGenericHumanRole() + "'");
                if (role.equals(workItem.getGenericHumanRole())) {
                    String userId = workItem.getAssignee().getUserId();
                    log.debug("Get users by role '"
                            + role
                            + "' - Found work item that is associated to user '"
                            + userId + "' which has the required human role.");
                    /*
                          * Since we add the userId to a set each userId is added
                          * only once even if a user is assigned to the task instance
                          * via different work items (e.g. as business administrator
                          * and task stake holder)
                          */
                    resultList.add(userId);
                }
            }
        }
        return resultList;

    }

    public void setStatus(ETaskInstanceState newState) {
        setStatus(newState, null);
    }

    public void setStatus(ETaskInstanceState newState, String description)
            throws IllegalArgumentException {
        String statusAsString = this.taskInstanceEntity.getStatus();
        log.debug("Set new status - Current status is '" + statusAsString
                + "'. " + "Trying to set to status to '" + newState + "'.");

        /*
           * Should never become null since the status is set to CREATED in the
           * constructor
           */
        if (statusAsString != null) {

            ETaskInstanceState currentState = ETaskInstanceState
                    .valueOf(statusAsString);
            /*
                * Before transitioning the task check if it is not suspend until a
                * certain point of time and if the transition to the new state is
                * valid.
                */
            if (!Utilities.hasTimeExpired(getSuspendUntil())) {
                String errorMsg = "Set new status - The status of task instance '"
                        + getName()
                        + "' can't be set from state '"
                        + currentState.toString()
                        + "' to '"
                        + newState.toString()
                        + "' because it is still suspended until : "
                        + Utilities.formatTimestamp(getSuspendUntil());
                log.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            } else if (!currentState.canBeChangedTo(newState)) {
                String errorMsg = "Set new status - The status of task instance '"
                        + getName()
                        + "' can't be set from state '"
                        + currentState.toString()
                        + "' to '"
                        + newState.toString()
                        + "' because this is an invalid predecessor status.";
                log.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            /* All conditions are met to transition to the new state */
            this.taskInstanceEntity.setStatus(newState.toString());
            log.debug("Set new status - Status was set to '"
                    + this.taskInstanceEntity.getStatus() + "'.");

        }
    }

    public void setSuspended(boolean isSuspended)
            throws IllegalArgumentException {
        /* Either suspend (true) or resume (false) */
        ETaskInstanceState currentState = getStatus();
        if (isSuspended) {
            suspend(currentState);
        } else {
            resume(currentState);
        }
    }

    protected ETaskInstanceState suspend(ETaskInstanceState currentState)
            throws IllegalArgumentException {
        ETaskInstanceState newState = null;
        if (currentState.equals(ETaskInstanceState.READY)) {
            newState = ETaskInstanceState.SUSPENDED_READY;
        } else if (currentState.equals(ETaskInstanceState.RESERVED)) {
            newState = ETaskInstanceState.SUSPENDED_RESERVED;
        } else if (currentState.equals(ETaskInstanceState.IN_PROGRESS)) {
            newState = ETaskInstanceState.SUSPENDED_IN_PROGRESS;
        } else if (currentState.isSuspended()) {
            String errorMsg = "Suspend task instance - The task instance is already suspended. "
                    + "The suspended state is '" + currentState + "'";
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        } else {
            String errorMsg = "Suspend task instance - The status '"
                    + currentState
                    + "' of task instance '"
                    + getName()
                    + "' can't be"
                    + " suspended. For suspending it it has to be in one of the following states: "
                    + ETaskInstanceState.READY + ", "
                    + ETaskInstanceState.RESERVED + ", "
                    + ETaskInstanceState.IN_PROGRESS;
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        setStatus(newState);
        return newState;
    }

    protected ETaskInstanceState resume(ETaskInstanceState currentState)
            throws IllegalArgumentException {
        ETaskInstanceState newState = null;
        /* Check if task instance is in one of the suspension states */
        if (currentState.equals(ETaskInstanceState.SUSPENDED_READY)) {
            newState = ETaskInstanceState.READY;
        } else if (currentState.equals(ETaskInstanceState.SUSPENDED_RESERVED)) {
            newState = ETaskInstanceState.RESERVED;
        } else if (currentState
                .equals(ETaskInstanceState.SUSPENDED_IN_PROGRESS)) {
            newState = ETaskInstanceState.IN_PROGRESS;
        } else {
            String errorMsg = "Resume task instance - The status '"
                    + currentState
                    + "' of task instance '"
                    + getName()
                    + "' can't be"
                    + " resumed. For resuming it it has to be in one of the following states: "
                    + ETaskInstanceState.SUSPENDED_READY + ", "
                    + ETaskInstanceState.SUSPENDED_RESERVED + ", "
                    + ETaskInstanceState.SUSPENDED_IN_PROGRESS;
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        setStatus(newState);
        return newState;
    }

    public boolean isSuspended() {
        return getStatus().isSuspended();
    }

    public Set<IWorkItem> getWorkItems() {
        List<Workitem> workItemEntities = taskInstanceEntity.getWorkitems();

        Set<IWorkItem> taskInstances = new HashSet<IWorkItem>();
        /*
           * If there are no work items associated to the task instance the list
           * may be empty
           */
        if (workItemEntities != null) {
            Iterator<Workitem> iter = workItemEntities.iterator();
            while (iter.hasNext()) {
                /*
                     * Create the model of the work items and add it to the list of
                     * instance models
                     */
                taskInstances.add(WorkItemFactory.newInstance()
                        .createWorkItemFromEntity(iter.next()));
            }

        }
        return taskInstances;
    }

    public boolean isExpired() {
        Timestamp expirationTime = getExpirationTime();
        Timestamp currentTime = Utilities.getCurrentTime();
        if (expirationTime != null
                && expirationTime.getTime() < currentTime.getTime()
                && getStatus().equals(ETaskInstanceState.OBSOLETE)) {
            return true;
        } else {
            return false;
        }
    }

    public String getTaskParentId() {
        return taskInstanceEntity.getTaskParentId();
    }

    public void setTaskParentId(String taskParentId) {
        taskInstanceEntity.setTaskParentId(taskParentId);
    }

    @Override
    public void setPostionX(Double positionX) {
        taskInstanceEntity.setPositionX(positionX);

    }

    @Override
    public Double getPositionX() {
        return taskInstanceEntity.getPositionX();
    }

    @Override
    public void setPostionY(Double positionY) {
        taskInstanceEntity.setPositionY(positionY);

    }

    @Override
    public Double getPositionY() {
        return taskInstanceEntity.getPositionY();
    }

    @Override
    public Long getDurationMin() {
        return taskInstanceEntity.getDurationMin();
    }

    @Override
    public void setDurationMin(Long durationMin) {
        taskInstanceEntity.setDurationMin(durationMin);

    }

    @Override
    public Long getDurationAvg() {
        return taskInstanceEntity.getDurationAvg();
    }

    @Override
    public void setDurationAvg(Long durationAvg) {
        taskInstanceEntity.setDurationAvg(durationAvg);

    }

    @Override
    public Long getDurationMax() {
        return taskInstanceEntity.getDurationMax();
    }

    @Override
    public void setDurationMax(Long durationMax) {
        taskInstanceEntity.setDurationMax(durationMax);

    }

    @Override
    public String getQueryProperty1() {
        return taskInstanceEntity.getQueryProperty1();
    }

    @Override
    public void setQueryProperty1(String queryProperty1) {
        taskInstanceEntity.setQueryProperty1(queryProperty1);

    }

    @Override
    public String getQueryProperty1Name() {
        return taskInstanceEntity.getQueryProperty1Name();
    }

    @Override
    public void setQueryProperty1Name(String queryProperty1Name) {
        taskInstanceEntity.setQueryProperty1Name(queryProperty1Name);

    }

    @Override
    public String getQueryProperty2() {
        return taskInstanceEntity.getQueryProperty2();
    }

    @Override
    public void setQueryProperty2(String queryProperty2) {
        taskInstanceEntity.setQueryProperty2(queryProperty2);

    }

    @Override
    public String getQueryProperty2Name() {
        return taskInstanceEntity.getQueryProperty2Name();
    }

    @Override
    public void setQueryProperty2Name(String queryProperty2Name) {
        taskInstanceEntity.setQueryProperty2Name(queryProperty2Name);

    }

    @Override
    public Double getQueryProperty3() {
        return taskInstanceEntity.getQueryProperty3();
    }

    @Override
    public void setQueryProperty3(Double queryProperty3) {
        taskInstanceEntity.setQueryProperty3(queryProperty3);

    }

    @Override
    public String getQueryProperty3Name() {
        return taskInstanceEntity.getQueryProperty3Name();
    }

    @Override
    public void setQueryProperty3Name(String queryProperty3Name) {
        taskInstanceEntity.setQueryProperty3Name(queryProperty3Name);

    }

    @Override
    public Double getQueryProperty4() {
        return taskInstanceEntity.getQueryProperty4();
    }

    @Override
    public void setQueryProperty4(Double queryProperty4) {
        taskInstanceEntity.setQueryProperty4(queryProperty4);
    }

    @Override
    public String getQueryProperty4Name() {
        return taskInstanceEntity.getQueryProperty4Name();
    }

    @Override
    public void setQueryProperty4Name(String queryProperty4Name) {
        taskInstanceEntity.setQueryProperty4Name(queryProperty4Name);
    }

    @Override
    public String getContextId() {
        return taskInstanceEntity.getContextId();
    }

    @Override
    public void setContextId(String contextId) {
        taskInstanceEntity.setContextId(contextId);

    }

}
