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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the PRESENTATIONINFORMATION database table.
 */
@Entity
public class Presentationinformation implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Lob()
    private byte[] description;

    private String subject;

    private String title;

    //bi-directional many-to-one association to Humantaskmodel
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Humantaskmodel humantaskmodel;

    public Presentationinformation() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getDescription() {
        return this.description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Humantaskmodel getHumantaskmodel() {
        return this.humantaskmodel;
    }

    public void setHumantaskmodel(Humantaskmodel humantaskmodel) {
        this.humantaskmodel = humantaskmodel;
    }

}