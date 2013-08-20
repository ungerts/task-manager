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

package com.htm.query.views;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.htm.query.FaultView;
import com.htm.query.IQueryContext;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.ITaskParentContext;

/**
 * This class represents a view on a task instance.</br>
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see ITaskInstance
 */
public class TaskInstanceView implements IQueryContext {
    private String id;

    private String name;

    private String taskModelName;

    private String status;

    private int priority;

    private String taskInitiator;

    private String taskParentId;

    private Set<String> businessAdministrators;

    private Set<String> taskStakeholders;

    private Set<String> potentialOwners;

    //private List<String> excludedOwners;

    private boolean hasPotentialOwners;

    private String actualOwner;

    private boolean hasActualOwner;

    private Timestamp createdOn;

    private Timestamp activationTime;

    private Timestamp expirationTime;

    private Timestamp startBy;

    private Timestamp completeBy;

    private Timestamp suspendUntil;

    private boolean startByExists;

    private boolean completeByExists;

    private boolean isSkipable;

    private boolean isExpired;

    private String presentationName;

    private String presentationSubject;

    private String presentationDescription;

    private FaultView fault;

    private boolean hasFault;

    private Object input;

    private boolean hasInput;

    private Object output;

    private boolean hasOutput;

    private Set<AttachmentView> attachments;

    private boolean hasAttachments;

    private ITaskParentContext taskParentContext;

    private boolean isClaimed;

    private boolean isSuspended;

    private Set<ICorrelationProperty> correlationProperties;

    private Double positionX;

    private Double positionY;

    private Long durationMin;

    private Long durationAvg;

    private Long durationMax;

    private String queryProperty1;

    private String queryProperty1Name;

    private String queryProperty2;

    private String queryProperty2Name;

    private Double queryProperty3;

    private String queryProperty3Name;

    private Double queryProperty4;

    private String queryProperty4Name;

    private String contextId;


    public String getQueryProperty1() {
        return queryProperty1;
    }

    public void setQueryProperty1(String queryProperty1) {
        this.queryProperty1 = queryProperty1;
    }

    public String getQueryProperty1Name() {
        return queryProperty1Name;
    }

    public void setQueryProperty1Name(String queryProperty1Name) {
        this.queryProperty1Name = queryProperty1Name;
    }

    public String getQueryProperty2() {
        return queryProperty2;
    }

    public void setQueryProperty2(String queryProperty2) {
        this.queryProperty2 = queryProperty2;
    }

    public String getQueryProperty2Name() {
        return queryProperty2Name;
    }

    public void setQueryProperty2Name(String queryProperty2Name) {
        this.queryProperty2Name = queryProperty2Name;
    }

    public Double getQueryProperty3() {
        return queryProperty3;
    }

    public void setQueryProperty3(Double queryProperty3) {
        this.queryProperty3 = queryProperty3;
    }

    public String getQueryProperty3Name() {
        return queryProperty3Name;
    }

    public void setQueryProperty3Name(String queryProperty3Name) {
        this.queryProperty3Name = queryProperty3Name;
    }

    public Double getQueryProperty4() {
        return queryProperty4;
    }

    public void setQueryProperty4(Double queryProperty4) {
        this.queryProperty4 = queryProperty4;
    }

    public String getQueryProperty4Name() {
        return queryProperty4Name;
    }

    public void setQueryProperty4Name(String queryProperty4Name) {
        this.queryProperty4Name = queryProperty4Name;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    /**
     * Creates a new view object.</br>
     * The properties of a task instance where expressions can be
     * evaluated on are set here.
     *
     * @param taskInstance The task instance where the view has to be created from.
     */
    public TaskInstanceView(ITaskInstance taskInstance) {
        setId(taskInstance.getId());
        setName(taskInstance.getName());
        setTaskModelName(taskInstance.getTaskModelName());
        setTaskParentId(taskInstance.getTaskParentId());

        setStatus(taskInstance.getStatus().toString());
        setSuspended(taskInstance.isSuspended());
        setPriority(taskInstance.getPriority());
        setSkipable(taskInstance.isSkipable());

        setStartBy(taskInstance.getStartBy());
        setCompleteBy(taskInstance.getCompleteBy());
        setCreatedOn(taskInstance.getCreatedOn());
        setActivationTime(taskInstance.getActivationTime());
        setExpirationTime(taskInstance.getExpirationTime());

        setCompleteByExists(taskInstance.completeByExists());
        setStartByExists(taskInstance.startByExists());
        setExpired(taskInstance.isExpired());


        setActualOwner(taskInstance.getActualOwner());
        setTaskInitiator(taskInstance.getTaskInitiator());
        setBusinessAdministrators(taskInstance.getBusinessAdministrators());
        setTaskStakeholders(taskInstance.getTaskStakeholders());
        setBusinessAdministrators(taskInstance.getBusinessAdministrators());
        setPotentialOwners(taskInstance.getPotentialOwners());
        //setExcludedOwners(taskInstance.getExcludedOwners());
        setClaimed(taskInstance.isClaimed());
        setHasPotentialOwners(taskInstance.hasPotentialOwners());

        setAttachments(taskInstance.getAttachments());

        setInput(taskInstance.getInput());
        setOutput(taskInstance.getOutput());

        IFault fault = taskInstance.getFault();

        if (fault != null) {
            setFault(new FaultView(taskInstance.getFault()));
        }

        setPresentationName(
                taskInstance.getPresentationName());
        setPresentationSubject(
                taskInstance.getPresentationSubject());
        setPresentationDescription(
                taskInstance.getPresentationDescription());

        setCorrelationProperties(taskInstance.getCorrelationProperties());
        setTaskParentContext(taskInstance.getTaskParentContext());

        setPositionX(taskInstance.getPositionX());

        setPositionY(taskInstance.getPositionY());

        setDurationAvg(taskInstance.getDurationAvg());

        setDurationMax(taskInstance.getDurationMax());

        setDurationMin(taskInstance.getDurationMin());

        this.queryProperty1 = taskInstance.getQueryProperty1();

        this.queryProperty1Name = taskInstance.getQueryProperty1Name();

        this.queryProperty2 = taskInstance.getQueryProperty2();

        this.queryProperty2Name = taskInstance.getQueryProperty2Name();

        this.queryProperty3 = taskInstance.getQueryProperty3();

        this.queryProperty3Name = taskInstance.getQueryProperty3Name();

        this.queryProperty4 = taskInstance.getQueryProperty4();

        this.queryProperty4Name = taskInstance.getQueryProperty4Name();

        this.contextId = taskInstance.getContextId();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskModelName() {
        return taskModelName;
    }

    public void setTaskModelName(String taskModelName) {
        this.taskModelName = taskModelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTaskInitiator() {
        return taskInitiator;
    }

    public void setTaskInitiator(String taskInitiator) {
        this.taskInitiator = taskInitiator;
    }

    public void setBusinessAdministrators(Set<String> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    public Set<String> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public Set<String> getTaskStakeholders() {
        return taskStakeholders;
    }

    public void setTaskStakeholders(Set<String> taskStakeholders) {
        this.taskStakeholders = taskStakeholders;
    }

    public Set<String> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(Set<String> potentialOwners) {
        this.potentialOwners = potentialOwners;

        if (potentialOwners != null && !potentialOwners.isEmpty()) {
            setHasPotentialOwners(true);
        }
    }

//	public void setExcludedOwners(List<String> excludedOwners) {
//		this.excludedOwners = excludedOwners;
//	}

//	public List<String> getExcludedOwners() {
//		return excludedOwners;
//	}

    public boolean hasPotentialOwners() {
        return hasPotentialOwners;
    }

    public void setHasPotentialOwners(boolean hasPotentialOwners) {
        this.hasPotentialOwners = hasPotentialOwners;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;

        if (!StringUtils.isEmpty(actualOwner)) {
            setHasActualOwner(true);
        }
    }

    public boolean hasActualOwner() {
        return hasActualOwner;
    }

    public void setHasActualOwner(boolean hasActualOwner) {
        this.hasActualOwner = hasActualOwner;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Timestamp activationTime) {
        this.activationTime = activationTime;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Timestamp getStartBy() {
        return startBy;
    }


    /**
     * Sets the time until the task has to be started.
     *
     * @param startBy The time when the task has to be started as string.
     *                If this parameter is empty not startBy time is set.
     */
    public void setStartBy(Timestamp startBy) {
        this.startBy = startBy;

        if (startBy != null) {
            setStartByExists(true);
        } else {
            setStartByExists(false);
        }
    }

    public Timestamp getCompleteBy() {
        return completeBy;
    }


    public void setCompleteBy(Timestamp completeBy) {
        this.completeBy = completeBy;

        if (completeBy != null) {
            setCompleteByExists(true);
        } else {
            setCompleteByExists(false);
        }
    }

    public Timestamp getSuspendUntil() {
        return suspendUntil;
    }

    public void setSuspendUntil(Timestamp suspendUntil) {
        this.suspendUntil = suspendUntil;
    }

    public boolean startByExists() {
        return startByExists;
    }

    public void setStartByExists(boolean startByExists) {
        this.startByExists = startByExists;
    }

    public boolean completeByExists() {
        return completeByExists;
    }

    public void setCompleteByExists(boolean completeByExists) {
        this.completeByExists = completeByExists;
    }

    public boolean isSkipable() {
        return isSkipable;
    }

    public void setSkipable(boolean isSkipable) {
        this.isSkipable = isSkipable;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public String getPresentationSubject() {
        return presentationSubject;
    }

    public void setPresentationSubject(String presentationSubject) {
        this.presentationSubject = presentationSubject;
    }

    public String getPresentationDescription() {
        return presentationDescription;
    }

    public void setPresentationDescription(String presentationDescription) {
        this.presentationDescription = presentationDescription;
    }

    public FaultView getFault() {
        return fault;
    }

    public void setFault(FaultView fault) {
        this.fault = fault;

        if (fault != null) {
            setHasFault(true);
        } else {
            setHasFault(false);
        }
    }


    public boolean isHasFault() {
        return hasFault;
    }

    public void setHasFault(boolean hasFault) {
        this.hasFault = hasFault;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object inputData) {
        this.input = inputData;

        if (inputData != null) {
            setHasInputData(true);
        } else {
            setHasInputData(false);
        }
    }

    public boolean hasInput() {
        return hasInput;
    }

    public void setHasInputData(boolean hasInputData) {
        this.hasInput = hasInputData;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object outputData) {
        this.output = outputData;

        if (outputData != null) {
            setHasOutput(true);
        } else {
            setHasOutput(false);
        }
    }

    public boolean isHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(boolean hasOutputData) {
        this.hasOutput = hasOutputData;
    }

    public Set<AttachmentView> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<IAttachment> attachments) {

        Set<AttachmentView> attachmentViews = new HashSet<AttachmentView>();

        if (attachments != null) {
            Iterator<IAttachment> iter = attachments.iterator();
            while (iter.hasNext()) {
                IAttachment attachment = (IAttachment) iter.next();
                attachmentViews.add(new AttachmentView(attachment));
            }
        }

        this.attachments = attachmentViews;

        if (attachments != null && !attachments.isEmpty()) {
            setHasAttachments(true);
        } else {
            setHasAttachments(false);
        }
    }


    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public ITaskParentContext getTaskParentContext() {
        return taskParentContext;
    }

    public void setTaskParentContext(ITaskParentContext taskParentContext) {
        this.taskParentContext = taskParentContext;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }


    public String getTaskParentId() {
        return taskParentId;
    }

    public void setTaskParentId(String taskParentId) {
        this.taskParentId = taskParentId;
    }


    public Set<ICorrelationProperty> getCorrelationProperties() {
        return correlationProperties;
    }

    public void setCorrelationProperties(
            Set<ICorrelationProperty> correlationProperties) {
        this.correlationProperties = correlationProperties;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public Long getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Long durationMin) {
        this.durationMin = durationMin;
    }

    public Long getDurationAvg() {
        return durationAvg;
    }

    public void setDurationAvg(Long durationAvg) {
        this.durationAvg = durationAvg;
    }

    public Long getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Long durationMax) {
        this.durationMax = durationMax;
    }


}
