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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.htm.entities.WrappableEntity;

/**
 * The persistent class for the HUMANTASKINSTANCE database table.
 */
@Entity
public class Humantaskinstance implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Timestamp activationtime;

    private Timestamp completeby;

    private Timestamp createdon;

    private Timestamp expirationtime;

    private Timestamp suspenduntil;

    @Lob()
    private byte[] faultdata;

    private String faultname;

    @Lob()
    private byte[] inputdata;

    private String name;

    @Lob()
    private byte[] outputdata;

    @Lob()
    private byte[] presentationdescription;

    private String presentationname;

    private String presentationsubject;

    private int priority;

    private short skipable;

    private Timestamp startby;

    private String status;

    private String taskParentId;

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

    // bi-directional many-to-one association to Workitem
    @OneToMany(mappedBy = "humantaskinstance", cascade = CascadeType.PERSIST)
    private List<Workitem> workitems;

    // bi-directional many-to-one association to Attachment
    @OneToMany(mappedBy = "humantaskinstance", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    // bi-directional many-to-one association to Callbackcorrelationproperty
    @OneToMany(mappedBy = "humantaskinstance", cascade = CascadeType.ALL)
    private List<Callbackcorrelationproperty> callbackcorrelationproperties;

    // bi-directional many-to-one association to Humantaskmodel
    @ManyToOne
    @JoinColumn(name = "MODELID")
    private Humantaskmodel humantaskmodel;

    public Humantaskinstance() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getActivationtime() {
        return this.activationtime;
    }

    public void setActivationtime(Timestamp activationtime) {
        this.activationtime = activationtime;
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

    public Timestamp getExpirationtime() {
        return this.expirationtime;
    }

    public void setExpirationtime(Timestamp expirationtime) {
        this.expirationtime = expirationtime;
    }

    public Timestamp getSuspenduntil() {
        return suspenduntil;
    }

    public void setSuspenduntil(Timestamp suspenduntil) {
        this.suspenduntil = suspenduntil;
    }

    public byte[] getFaultdata() {
        return this.faultdata;
    }

    public void setFaultdata(byte[] faultdata) {
        this.faultdata = faultdata;
    }

    public String getFaultname() {
        return this.faultname;
    }

    public void setFaultname(String faultname) {
        this.faultname = faultname;
    }

    public byte[] getInputdata() {
        return this.inputdata;
    }

    public void setInputdata(byte[] inputdata) {
        this.inputdata = inputdata;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getOutputdata() {
        return this.outputdata;
    }

    public void setOutputdata(byte[] outputdata) {
        this.outputdata = outputdata;
    }

    public byte[] getPresentationdescription() {
        return this.presentationdescription;
    }

    public void setPresentationdescription(byte[] presentationdescription) {
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

    public short getSkipable() {
        return this.skipable;
    }

    public void setSkipable(short skipable) {
        this.skipable = skipable;
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

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Callbackcorrelationproperty> getCallbackcorrelationproperties() {
        return this.callbackcorrelationproperties;
    }

    public void setCallbackcorrelationproperties(
            List<Callbackcorrelationproperty> callbackcorrelationproperties) {
        this.callbackcorrelationproperties = callbackcorrelationproperties;
    }

    public Humantaskmodel getHumantaskmodel() {
        return this.humantaskmodel;
    }

    public void setHumantaskmodel(Humantaskmodel humantaskmodel) {
        this.humantaskmodel = humantaskmodel;
    }

    public List<Workitem> getWorkitems() {
        return this.workitems;
    }

    public void setWorkitems(List<Workitem> workitems) {
        this.workitems = workitems;
    }

    public String getTaskParentId() {
        return taskParentId;
    }

    public void setTaskParentId(String taskParentId) {
        this.taskParentId = taskParentId;
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


}