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
import javax.persistence.OneToMany;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the LOGICALPEOPLEGROUPDEF database table.
 */
@Entity
public class Logicalpeoplegroupdef implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    //bi-directional many-to-one association to LpgArgumentdef
    @OneToMany(mappedBy = "logicalpeoplegroupdef", cascade = CascadeType.ALL)
    private List<LpgArgumentdef> lpgArgumentdefs;

    //bi-directional many-to-one association to Peoplequery
    @OneToMany(mappedBy = "logicalpeoplegroupdef", cascade = CascadeType.PERSIST)
    private List<Peoplequery> peoplequeries;

    public Logicalpeoplegroupdef() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LpgArgumentdef> getLpgArgumentdefs() {
        return this.lpgArgumentdefs;
    }

    public void setLpgArgumentdefs(List<LpgArgumentdef> lpgArgumentdefs) {
        this.lpgArgumentdefs = lpgArgumentdefs;
    }

    public List<Peoplequery> getPeoplequeries() {
        return this.peoplequeries;
    }

    public void setPeoplequeries(List<Peoplequery> peoplequeries) {
        this.peoplequeries = peoplequeries;
    }

}