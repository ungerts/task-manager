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

package com.htm.taskinstance;

import java.sql.Timestamp;
import java.util.Set;

import com.htm.dm.IDataModelElement;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskmodel.ITaskModel;

public interface ITaskInstance extends IDataModelElement {

    public ITaskModel getTaskModel();

    public String getId();

    public String getName();

    public String getTaskModelName();

    public ETaskInstanceState getStatus();

    public int getPriority();

    public String getTaskInitiator();

    public Set<String> getTaskStakeholders();

    public Set<String> getPotentialOwners();

    public Set<String> getBusinessAdministrators();

    //public List<String> getExcludedOwners();

    public boolean hasPotentialOwners();

    public String getActualOwner();

    public boolean hasActualOwner();

    public Timestamp getCreatedOn();

    public Timestamp getActivationTime();

    public Timestamp getExpirationTime();

    public boolean isSkipable();

    public Timestamp getStartBy();

    public Timestamp getCompleteBy();

    public Timestamp getSuspendUntil();

    public boolean startByExists();

    public boolean completeByExists();

    public String getPresentationName();

    public String getPresentationSubject();

    public String getPresentationDescription();

    public IFault getFault();

    public Object getInput();

    public boolean hasInputData();

    public Object getOutput();

    public String getTaskParentId();

    public Set<ICorrelationProperty> getCorrelationProperties();

    public boolean hasOutputData();

    public boolean hasFault();

    public boolean hasAttachments();

    public boolean isClaimed();

    public boolean isSuspended();

    public boolean isExpired();

    public ITaskParentContext getTaskParentContext();

    public Set<IAttachment> getAttachments();

    public void setPresentationTitle(String presentationTitle);

    public void setPresentationSubject(String presentationSubject);

    public void setPresentationDescription(String presentationDescription);

    public void setFaultName(String faultName);

    public void setId(String id);

    /**
     * Sets the name of the task model the task instance is instantiated from.
     *
     * @param taskModel The task model name.
     */
    public void setTaskModel(ITaskModel taskModel);

    public void setStatus(ETaskInstanceState state) throws IllegalArgumentException;

    public void setActivationTime(Timestamp activationTime);

    public void setCompleteBy(Timestamp completeBy);

    public void setCreatedOn(Timestamp createdOn);

    public void setExpirationTime(Timestamp expirationTime);

    public void setSuspended(boolean isSuspended) throws IllegalArgumentException;

    public void setSuspendedUntil(Timestamp suspendUntil) throws IllegalArgumentException;

    /**
     * Determines whether a task can actually be skipped at runtime.</br>
     * Skipped task instances move to the stte 'Obsolete'
     *
     * @param isSkipable <code>True</code> if the task instance is skipable - <code>false</code> else.
     * @see ETaskInstanceState
     */
    public void setSkipable(boolean isSkipable);

    /**
     * Sets the priority of the task instance.</br>
     * Larger numbers represent lower priorities thus <code>0</code> is the highest priority.
     *
     * @param priority The priority.
     */
    public void setPriority(int priority);

    /**
     * Adds the set of attachments to the task instance.</br>
     * If the task instance already has a set attachments they are replaced by
     * the new set.</br>
     * The user that adds the attachments to the task instance must be associated to
     * it via a work item.
     *
     * @param attachedBy  The user associated to the task instance that adds the attachments.
     * @param attachments The set of attachments that are added. </br>
     *                    If <code>null</code> then the attachments are removed from the task instance.
     * @see ITaskInstance#addAttachment(IAssignedUser, IAttachment)
     */
    public void setAttachments(IAssignedUser attachedBy, Set<IAttachment> attachments);

    /**
     * Adds the attachment to the task instance.</br>
     * The user that adds the attachment to the task instance must be associated to
     * it via a work item.
     *
     * @param attachedBy The user associated to the task instance that adds the attachment.
     * @param attachment The attachments that is added.
     * @see ITaskInstance#setAttachments(IAssignedUser, Set)
     */
    public void addAttachment(IAssignedUser attachedBy, IAttachment attachment);

    public void setFaultData(Object faultData) throws HumanTaskManagerException;

    /**
     * Sets the input data of the task instance.
     *
     * @param inputData The object representation of the input data. This value is optional,
     *                  i.e. it can be <code>null</code>
     * @throws HumanTaskManagerException
     */
    public void setInputData(Object inputData) throws HumanTaskManagerException;

    public void setOutputData(Object outputData) throws HumanTaskManagerException;

    public void setName(String name);

    public void setStartBy(Timestamp startBy);

    public void setTaskParentId(String taskParentId);

    public void setCorrelationProperties(Set<ICorrelationProperty> correlationProperties);

    public void addCorrelationProperty(ICorrelationProperty correlationProperty);

    public void removeCorrelationProperty(ICorrelationProperty correlationProperty);

    public Set<IWorkItem> getWorkItems();

    public void setPostionX(Double positionX);

    public Double getPositionX();

    public void setPostionY(Double positionY);

    public Double getPositionY();

    public Long getDurationMin();

    public void setDurationMin(Long durationMin);

    public Long getDurationAvg();

    public void setDurationAvg(Long durationAvg);

    public Long getDurationMax();

    public void setDurationMax(Long durationMax);


    public String getQueryProperty1();

    public void setQueryProperty1(String queryProperty1);

    public String getQueryProperty1Name();

    public void setQueryProperty1Name(String queryProperty1Name);

    public String getQueryProperty2();

    public void setQueryProperty2(String queryProperty2);

    public String getQueryProperty2Name();

    public void setQueryProperty2Name(String queryProperty2Name);

    public Double getQueryProperty3();

    public void setQueryProperty3(Double queryProperty3);

    public String getQueryProperty3Name();

    public void setQueryProperty3Name(String queryProperty3Name);

    public Double getQueryProperty4();

    public void setQueryProperty4(Double queryProperty4);

    public String getQueryProperty4Name();

    public void setQueryProperty4Name(String queryProperty4Name);

    public String getContextId();

    public void setContextId(String contextId);


}
