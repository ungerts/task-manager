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

package com.htm.taskmodel.jpa;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.LpgArgumentdef;
import com.htm.entities.jpa.Peoplequeryargument;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.IQuery;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.IPeopleQueryArgument;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;

public class PeopleQueryArgumentWrapper implements IPeopleQueryArgument {

    private Peoplequeryargument peopleQueryArgumentEntity;
    private Logger log;

    public PeopleQueryArgumentWrapper(Peoplequeryargument adaptee) {

        if (adaptee.getLpgArgumentdef() == null) {
            throw new RuntimeException("Invalid people query argument. "
                    + "There must be always a logical people group argument "
                    + "defintion associated with the people query argument.");
        }

        this.peopleQueryArgumentEntity = adaptee;
        this.log = Utilities.getLogger(this.getClass());
    }

    public PeopleQueryArgumentWrapper(ILogicalPeopleGroupArgumentDef argumentDef) {
        this.peopleQueryArgumentEntity = new Peoplequeryargument();
        /*
           * Each people argument has to be associated with an LPG argument
           * definition
           */
        setArgumentDefinition(argumentDef);
        this.log = Utilities.getLogger(this.getClass());

    }

    public ILogicalPeopleGroupArgumentDef getArgumentDefinition() {
        return ModelElementFactory.newInstance().createPeopleGroupArgumentDef(
                peopleQueryArgumentEntity.getLpgArgumentdef());
    }

    public String getName() {
        /*
           * The name is defined in the LPG argument definition this argument is
           * associated with
           */
        return peopleQueryArgumentEntity.getLpgArgumentdef().getName();
    }

    public IQuery getValue() {
        // return ModelElementFactory.newInstance().createQuery(
        // Utilities.getStringFromBLOB(peopleQueryArgumentEntity.getExpression()));
        try {
            return (IQuery) Utilities
                    .getObjectFromBlob(peopleQueryArgumentEntity
                            .getExpression());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot read BLOB", e);
        }
        return null;
    }

    public void setArgumentDefinition(
            ILogicalPeopleGroupArgumentDef argumentDefinition) {

        LpgArgumentdef lpgArgumentDefEntity = (LpgArgumentdef) argumentDefinition
                .getAdaptee();

        /*
           * There is a bidirectional relation between LPG argument definitions
           * and people query arguments in the generated JPA entities consequently
           * both sides of the relationship have to be set
           */

        peopleQueryArgumentEntity.setLpgArgumentdef(lpgArgumentDefEntity);

        List<Peoplequeryargument> argumentEntities = lpgArgumentDefEntity
                .getPeoplequeryarguments();

        /*
           * If this people argument is the first argument that is associated with
           * the LPG argument definition the list is null thus it has to be
           * instantiated
           */
        if (argumentEntities == null) {
            argumentEntities = new ArrayList<Peoplequeryargument>();
            lpgArgumentDefEntity.setPeoplequeryarguments(argumentEntities);
        }
        argumentEntities.add(peopleQueryArgumentEntity);
    }

    public void setValue(IQuery query) {
        try {
            peopleQueryArgumentEntity.setExpression(Utilities
                    .getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot write BLOB", e);
        }
    }

    public String getId() {
        return Integer.toString(peopleQueryArgumentEntity.getId());
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public WrappableEntity getAdaptee() {
        return peopleQueryArgumentEntity;
    }

}
