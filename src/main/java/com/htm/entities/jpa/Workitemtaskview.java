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

package com.htm.entities.jpa;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.eclipse.persistence.annotations.ReadOnly;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the WORKITEMTASKVIEW database table.
 */
@Entity
@ReadOnly
public class Workitemtaskview implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;


    private Timestamp activatedon;


    private short assignedtoeverybody;


    private String assignee;


    private Timestamp completeby;


    private Timestamp createdon;


    private Timestamp expireson;

    @Lob()

    private String faultdata;


    private String faultname;


    private String generichumanrole;

    @Lob()

    private String inputdata;


    private short isclaimed;


    private short isskipable;

    @Lob()

    private String outputdata;

    @Lob()

    private String presentationdescription;


    private String presentationname;


    private String presentationsubject;


    private int priority;


    private Timestamp startby;


    private String status;


    private Timestamp suspenduntil;


    private Timestamp taskcreatedon;


    private String taskinstancename;


    private int taskmodelid;


    private int tiid;


    @Id
    private int wiid;


    //new attributes

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

    public Workitemtaskview() {
    }

    public Timestamp getActivatedon() {
        return this.activatedon;
    }

    public void setActivatedon(Timestamp activatedon) {
        this.activatedon = activatedon;
    }

    public short getAssignedtoeverybody() {
        return this.assignedtoeverybody;
    }

    public void setAssignedtoeverybody(short assignedtoeverybody) {
        this.assignedtoeverybody = assignedtoeverybody;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Timestamp getCompleteby() {
        return this.completeby;
    }

    public void setCompleteby(Timestamp completeby) {
        this.completeby = completeby;
    }

    public Timestamp getCreatedon() {
        return this.createdon;
    }

    public void setCreatedon(Timestamp createdon) {
        this.createdon = createdon;
    }

    public Timestamp getExpireson() {
        return this.expireson;
    }

    public void setExpireson(Timestamp expireson) {
        this.expireson = expireson;
    }

    public String getFaultdata() {
        return this.faultdata;
    }

    public void setFaultdata(String faultdata) {
        this.faultdata = faultdata;
    }

    public String getFaultname() {
        return this.faultname;
    }

    public void setFaultname(String faultname) {
        this.faultname = faultname;
    }

    public String getGenerichumanrole() {
        return this.generichumanrole;
    }

    public void setGenerichumanrole(String generichumanrole) {
        this.generichumanrole = generichumanrole;
    }

    public String getInputdata() {
        return this.inputdata;
    }

    public void setInputdata(String inputdata) {
        this.inputdata = inputdata;
    }

    public short getIsclaimed() {
        return this.isclaimed;
    }

    public void setIsclaimed(short isclaimed) {
        this.isclaimed = isclaimed;
    }

    public short getIsskipable() {
        return this.isskipable;
    }

    public void setIsskipable(short isskipable) {
        this.isskipable = isskipable;
    }

    public String getOutputdata() {
        return this.outputdata;
    }

    public void setOutputdata(String outputdata) {
        this.outputdata = outputdata;
    }

    public String getPresentationdescription() {
        return this.presentationdescription;
    }

    public void setPresentationdescription(String presentationdescription) {
        this.presentationdescription = presentationdescription;
    }

    public String getPresentationname() {
        return this.presentationname;
    }

    public void setPresentationname(String presentationname) {
        this.presentationname = presentationname;
    }

    public String getPresentationsubject() {
        return this.presentationsubject;
    }

    public void setPresentationsubject(String presentationsubject) {
        this.presentationsubject = presentationsubject;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Timestamp getStartby() {
        return this.startby;
    }

    public void setStartby(Timestamp startby) {
        this.startby = startby;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getSuspenduntil() {
        return this.suspenduntil;
    }

    public void setSuspenduntil(Timestamp suspenduntil) {
        this.suspenduntil = suspenduntil;
    }

    public Timestamp getTaskcreatedon() {
        return this.taskcreatedon;
    }

    public void setTaskcreatedon(Timestamp taskcreatedon) {
        this.taskcreatedon = taskcreatedon;
    }

    public String getTaskinstancename() {
        return this.taskinstancename;
    }

    public void setTaskinstancename(String taskinstancename) {
        this.taskinstancename = taskinstancename;
    }

    public int getTaskmodelid() {
        return this.taskmodelid;
    }

    public void setTaskmodelid(int taskmodelid) {
        this.taskmodelid = taskmodelid;
    }

    public int getTiid() {
        return this.tiid;
    }

    public void setTiid(int tiid) {
        this.tiid = tiid;
    }

    public int getWiid() {
        return this.wiid;
    }

    public void setWiid(int wiid) {
        this.wiid = wiid;
    }

}