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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the LPG_ARGUMENTDEF database table.
 */
@Entity
@Table(name = "LPG_ARGUMENTDEF")
public class LpgArgumentdef implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    //bi-directional many-to-one association to Logicalpeoplegroupdef
    @ManyToOne
    @JoinColumn(name = "LPG")
    private Logicalpeoplegroupdef logicalpeoplegroupdef;

    //bi-directional many-to-one association to Peoplequeryargument
    @OneToMany(mappedBy = "lpgArgumentdef", cascade = CascadeType.PERSIST)
    private List<Peoplequeryargument> peoplequeryarguments;

    public LpgArgumentdef() {
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

    public Logicalpeoplegroupdef getLogicalpeoplegroupdef() {
        return this.logicalpeoplegroupdef;
    }

    public void setLogicalpeoplegroupdef(Logicalpeoplegroupdef logicalpeoplegroupdef) {
        this.logicalpeoplegroupdef = logicalpeoplegroupdef;
    }

    public List<Peoplequeryargument> getPeoplequeryarguments() {
        return this.peoplequeryarguments;
    }

    public void setPeoplequeryarguments(List<Peoplequeryargument> peoplequeryarguments) {
        this.peoplequeryarguments = peoplequeryarguments;
    }

}