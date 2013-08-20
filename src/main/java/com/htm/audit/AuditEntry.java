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

package com.htm.audit;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long eventId;

    private Long transactionId;

    private Timestamp eventTimeStamp;

    private String eAction;

    private Long tiid;

    private Long tmid;

    private String taskModelName;

    private String taskInstanceName;

    private String originator;

    private Boolean orginatorIsBusinessAdministrator;

    private Boolean originatorIsPOwner;

    private Boolean originatorIsStakeholder;

    private String taskInitiator;

    private String actualOwner;

    private String state;

    private String oldState;

    private Boolean hasSubTasks;

    private Boolean hasControlledTasks;

    public AuditEntry() {

    }

    public AuditEntry(Long transactionId, Timestamp eventTimeStamp,
                      String eAction, Long tiid, Long tmid, String taskModelName,
                      String taskInstanceName, String originator,
                      Boolean orginatorIsBusinessAdministrator,
                      Boolean originatorIsPOwner, Boolean originatorIsStakeholder,
                      String taskInitiator, String actualOwner, String state,
                      String oldState, Boolean hasSubTasks, Boolean hasControlledTasks) {
        super();
        this.transactionId = transactionId;
        this.eventTimeStamp = eventTimeStamp;
        this.eAction = eAction;
        this.tiid = tiid;
        this.tmid = tmid;
        this.taskModelName = taskModelName;
        this.taskInstanceName = taskInstanceName;
        this.originator = originator;
        this.orginatorIsBusinessAdministrator = orginatorIsBusinessAdministrator;
        this.originatorIsPOwner = originatorIsPOwner;
        this.originatorIsStakeholder = originatorIsStakeholder;
        this.taskInitiator = taskInitiator;
        this.actualOwner = actualOwner;
        this.state = state;
        this.oldState = oldState;
        this.hasSubTasks = hasSubTasks;
        this.hasControlledTasks = hasControlledTasks;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getEventTimeStamp() {
        return eventTimeStamp;
    }

    public void setEventTimeStamp(Timestamp eventTimeStamp) {
        this.eventTimeStamp = eventTimeStamp;
    }

    public String geteAction() {
        return eAction;
    }

    public void seteAction(String eAction) {
        this.eAction = eAction;
    }

    public Long getTiid() {
        return tiid;
    }

    public void setTiid(Long tiid) {
        this.tiid = tiid;
    }

    public Long getTmid() {
        return tmid;
    }

    public void setTmid(Long tmid) {
        this.tmid = tmid;
    }

    public String getTaskModelName() {
        return taskModelName;
    }

    public void setTaskModelName(String taskModelName) {
        this.taskModelName = taskModelName;
    }

    public String getTaskInstanceName() {
        return taskInstanceName;
    }

    public void setTaskInstanceName(String taskInstanceName) {
        this.taskInstanceName = taskInstanceName;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public Boolean getOrginatorIsBusinessAdministrator() {
        return orginatorIsBusinessAdministrator;
    }

    public void setOrginatorIsBusinessAdministrator(
            Boolean orginatorIsBusinessAdministrator) {
        this.orginatorIsBusinessAdministrator = orginatorIsBusinessAdministrator;
    }

    public Boolean getOriginatorIsPOwner() {
        return originatorIsPOwner;
    }

    public void setOriginatorIsPOwner(Boolean originatorIsPOwner) {
        this.originatorIsPOwner = originatorIsPOwner;
    }

    public Boolean getOriginatorIsStakeholder() {
        return originatorIsStakeholder;
    }

    public void setOriginatorIsStakeholder(Boolean originatorIsStakeholder) {
        this.originatorIsStakeholder = originatorIsStakeholder;
    }

    public String getTaskInitiator() {
        return taskInitiator;
    }

    public void setTaskInitiator(String taskInitiator) {
        this.taskInitiator = taskInitiator;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public Boolean getHasSubTasks() {
        return hasSubTasks;
    }

    public void setHasSubTasks(Boolean hasSubTasks) {
        this.hasSubTasks = hasSubTasks;
    }

    public Boolean getHasControlledTasks() {
        return hasControlledTasks;
    }

    public void setHasControlledTasks(Boolean hasControlledTasks) {
        this.hasControlledTasks = hasControlledTasks;
    }

}
