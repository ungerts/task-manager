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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import com.htm.entities.WrappableEntity;

/**
 * The persistent class for the HUMANTASKMODEL database table.
 */
@Entity
public class Humantaskmodel implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Lob()
    private byte[] completeby;

    @Lob()
    private byte[] faultschema;

    @Lob()
    private byte[] inputschema;

    private String name;

    @Lob()
    private byte[] outputschema;

    @Lob()
    private byte[] priority;

    @Lob()
    private byte[] activationTime;

    @Lob()
    private byte[] skipable;

    @Lob()
    private byte[] startby;

    @Lob()
    private byte[] positionY;

    @Lob()
    private byte[] positionX;

    @Lob()
    private byte[] durationMin;

    @Lob()
    private byte[] durationAvg;

    @Lob()
    private byte[] durationMax;

    /* Task query properties */

    @Lob()
    private byte[] queryProperty1;

    private String queryProperty1Name;

    @Lob()
    private byte[] queryProperty2;

    private String queryProperty2Name;

    @Lob()
    private byte[] queryProperty3;

    private String queryProperty3Name;

    @Lob()
    private byte[] queryProperty4;

    private String queryProperty4Name;

    // bi-directional many-to-one association to Humantaskinstance
    @OneToMany(mappedBy = "humantaskmodel", cascade = CascadeType.PERSIST)
    private List<Humantaskinstance> humantaskinstances;

    // bi-directional many-to-one association to Presentationinformation
    @OneToMany(mappedBy = "humantaskmodel", cascade = CascadeType.ALL)
    private List<Presentationinformation> presentationinformations;

    // bi-directional many-to-one association to Peoplequery
    @OneToMany(mappedBy = "humantaskmodel", cascade = CascadeType.ALL)
    private List<Peoplequery> peoplequeries;

    // bi-directional many-to-one association to Presentationinformation
    @OneToMany(mappedBy = "humantaskmodel", cascade = CascadeType.ALL)
    private List<Literal> literals;

    public Humantaskmodel() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getCompleteby() {
        return this.completeby;
    }

    public void setCompleteby(byte[] completeby) {
        this.completeby = completeby;
    }

    public byte[] getFaultschema() {
        return this.faultschema;
    }

    public void setFaultschema(byte[] faultschema) {
        this.faultschema = faultschema;
    }

    public byte[] getInputschema() {
        return this.inputschema;
    }

    public void setInputschema(byte[] inputschema) {
        this.inputschema = inputschema;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getOutputschema() {
        return this.outputschema;
    }

    public void setOutputschema(byte[] outputschema) {
        this.outputschema = outputschema;
    }

    public byte[] getPriority() {
        return this.priority;
    }

    public void setPriority(byte[] priority) {
        this.priority = priority;
    }

    public byte[] getSkipable() {
        return this.skipable;
    }

    public void setSkipable(byte[] skipable) {
        this.skipable = skipable;
    }

    public byte[] getStartby() {
        return this.startby;
    }

    public void setStartby(byte[] startby) {
        this.startby = startby;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public void setLiterals(List<Literal> literals) {
        this.literals = literals;
    }

    public void setHumantaskinstances(List<Humantaskinstance> humantaskinstances) {
        this.humantaskinstances = humantaskinstances;
    }

    public List<Humantaskinstance> getHumantaskinstances() {
        return this.humantaskinstances;
    }

    public List<Presentationinformation> getPresentationinformations() {
        return this.presentationinformations;
    }

    public void setPresentationinformations(
            List<Presentationinformation> presentationinformations) {
        this.presentationinformations = presentationinformations;
    }

    public List<Peoplequery> getPeoplequeries() {
        return this.peoplequeries;
    }

    public void setPeoplequeries(List<Peoplequery> peoplequeries) {
        this.peoplequeries = peoplequeries;
    }

    public byte[] getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(byte[] activationTime) {
        this.activationTime = activationTime;
    }

    public byte[] getPositionY() {
        return positionY;
    }

    public void setPositionY(byte[] positionY) {
        this.positionY = positionY;
    }

    public byte[] getPositionX() {
        return positionX;
    }

    public void setPositionX(byte[] positionX) {
        this.positionX = positionX;
    }

    public byte[] getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(byte[] durationMin) {
        this.durationMin = durationMin;
    }

    public byte[] getDurationAvg() {
        return durationAvg;
    }

    public void setDurationAvg(byte[] durationAvg) {
        this.durationAvg = durationAvg;
    }

    public byte[] getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(byte[] durationMax) {
        this.durationMax = durationMax;
    }

    public byte[] getQueryProperty1() {
        return queryProperty1;
    }

    public void setQueryProperty1(byte[] queryProperty1) {
        this.queryProperty1 = queryProperty1;
    }

    public String getQueryProperty1Name() {
        return queryProperty1Name;
    }

    public void setQueryProperty1Name(String queryProperty1Name) {
        this.queryProperty1Name = queryProperty1Name;
    }

    public byte[] getQueryProperty2() {
        return queryProperty2;
    }

    public void setQueryProperty2(byte[] queryProperty2) {
        this.queryProperty2 = queryProperty2;
    }

    public String getQueryProperty2Name() {
        return queryProperty2Name;
    }

    public void setQueryProperty2Name(String queryProperty2Name) {
        this.queryProperty2Name = queryProperty2Name;
    }

    public byte[] getQueryProperty3() {
        return queryProperty3;
    }

    public void setQueryProperty3(byte[] queryProperty3) {
        this.queryProperty3 = queryProperty3;
    }

    public String getQueryProperty3Name() {
        return queryProperty3Name;
    }

    public void setQueryProperty3Name(String queryProperty3Name) {
        this.queryProperty3Name = queryProperty3Name;
    }

    public byte[] getQueryProperty4() {
        return queryProperty4;
    }

    public void setQueryProperty4(byte[] queryProperty4) {
        this.queryProperty4 = queryProperty4;
    }

    public String getQueryProperty4Name() {
        return queryProperty4Name;
    }

    public void setQueryProperty4Name(String queryProperty4Name) {
        this.queryProperty4Name = queryProperty4Name;
    }

}