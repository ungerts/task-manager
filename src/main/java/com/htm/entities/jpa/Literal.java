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
import javax.persistence.ManyToOne;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the LITERALS database table.
 */
@Entity
public class Literal implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String entityidentifier;

    private String humanrole;

    //bi-directional many-to-one association to Humantaskmodel
    @ManyToOne(cascade = CascadeType.ALL)
    private Humantaskmodel humantaskmodel;

    public Literal() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntityidentifier() {
        return this.entityidentifier;
    }

    public void setEntityidentifier(String entityidentifier) {
        this.entityidentifier = entityidentifier;
    }

    public String getHumanrole() {
        return this.humanrole;
    }

    public void setHumanrole(String humanrole) {
        this.humanrole = humanrole;
    }

    public Humantaskmodel getHumantaskmodel() {
        return this.humantaskmodel;
    }

    public void setHumantaskmodel(Humantaskmodel humantaskmodel) {
        this.humantaskmodel = humantaskmodel;
    }

}