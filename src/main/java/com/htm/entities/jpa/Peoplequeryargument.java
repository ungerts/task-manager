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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the PEOPLEQUERYARGUMENT database table.
 */
@Entity
public class Peoplequeryargument implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Lob()
    private byte[] expression;

    //bi-directional many-to-one association to LpgArgumentdef
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "LPGARGUMENT")
    private LpgArgumentdef lpgArgumentdef;

    //bi-directional many-to-one association to Peoplequery
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PEOPLEQUERY")
    private Peoplequery peoplequeryBean;

    public Peoplequeryargument() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getExpression() {
        return this.expression;
    }

    public void setExpression(byte[] query) {
        this.expression = query;
    }

    public LpgArgumentdef getLpgArgumentdef() {
        return this.lpgArgumentdef;
    }

    public void setLpgArgumentdef(LpgArgumentdef lpgArgumentdef) {
        this.lpgArgumentdef = lpgArgumentdef;
    }

    public Peoplequery getPeoplequeryBean() {
        return this.peoplequeryBean;
    }

    public void setPeoplequeryBean(Peoplequery peoplequeryBean) {
        this.peoplequeryBean = peoplequeryBean;
    }

}