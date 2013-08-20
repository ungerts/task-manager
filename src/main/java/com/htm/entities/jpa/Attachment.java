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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the ATTACHMENT database table.
 */
@Entity
public class Attachment implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String accesstype;

    private Timestamp attachedat;

    private String contenttype;

    private String name;

    @Lob()
    private byte[] value;

    //bi-directional many-to-one association to Humantaskinstance
    @ManyToOne
    @JoinColumn(name = "TIID")
    private Humantaskinstance humantaskinstance;

    //bi-directional many-to-one association to Assigneduser

    @JoinColumn(name = "ATTACHEDBY")
    private Assigneduser attachedBy;

    public Attachment() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccesstype() {
        return this.accesstype;
    }

    public void setAccesstype(String accesstype) {
        this.accesstype = accesstype;
    }

    public Timestamp getAttachedat() {
        return this.attachedat;
    }

    public void setAttachedat(Timestamp attachedat) {
        this.attachedat = attachedat;
    }

    public String getContenttype() {
        return this.contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Humantaskinstance getHumantaskinstance() {
        return this.humantaskinstance;
    }

    public void setHumantaskinstance(Humantaskinstance humantaskinstance) {
        this.humantaskinstance = humantaskinstance;
    }

    public Assigneduser getAssigneduser() {
        return this.attachedBy;
    }

    public void setAssigneduser(Assigneduser assigneduser) {
        this.attachedBy = assigneduser;
    }

}