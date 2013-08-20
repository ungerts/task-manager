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

import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the WORKITEM database table.
 */
@Entity
public class Workitem implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private short claimed;

    private Timestamp creationtime;

    private short everybody;

    private String generichumanrole;

    //bi-directional many-to-one association to Humantaskinstance

    @ManyToOne
    @JoinColumn(name = "TIID")
    private Humantaskinstance humantaskinstance;

    //bi-directional many-to-one association to Assigneduser
    @JoinColumn(name = "ASSIGNEE")
    @OneToOne(optional = false, cascade = ALL)
    private Assigneduser assigneduser;

    public Workitem() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getClaimed() {
        return this.claimed;
    }

    public void setClaimed(short claimed) {
        this.claimed = claimed;
    }

    public Timestamp getCreationtime() {
        return this.creationtime;
    }

    public void setCreationtime(Timestamp creationtime) {
        this.creationtime = creationtime;
    }

    public short getEverybody() {
        return this.everybody;
    }

    public void setEverybody(short everybody) {
        this.everybody = everybody;
    }

    public String getGenerichumanrole() {
        return this.generichumanrole;
    }

    public void setGenerichumanrole(String generichumanrole) {
        this.generichumanrole = generichumanrole;
    }

    public Humantaskinstance getHumantaskinstance() {
        return this.humantaskinstance;
    }

    public void setHumantaskinstance(Humantaskinstance humantaskinstance) {
        this.humantaskinstance = humantaskinstance;
    }


    public Assigneduser getAssigneduser() {
        return this.assigneduser;
    }

    public void setAssigneduser(Assigneduser assigneduser) {
        this.assigneduser = assigneduser;
    }

}