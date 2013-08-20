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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Logicalpeoplegroupdef;
import com.htm.entities.jpa.LpgArgumentdef;
import com.htm.entities.jpa.Peoplequery;
import com.htm.entities.jpa.Peoplequeryargument;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.IPeopleQueryArgument;
import com.htm.taskmodel.ModelElementFactory;


public class PeopleQueryWrapper implements IPeopleAssignment {


    protected Peoplequery peopleQueryEntity;

    public PeopleQueryWrapper(Peoplequery adaptee) {

        if (adaptee.getLogicalpeoplegroupdef() == null) {
            throw new RuntimeException("Invalid people query. " +
                    "There must be always a logical people group definition " +
                    "associated with the people query.");
        }
        this.peopleQueryEntity = adaptee;
    }

    public PeopleQueryWrapper(ILogicalPeopleGroupDef lpgDefinition) {

        if (lpgDefinition == null) {
            throw new RuntimeException("People query instantiation failed. " +
                    "The attribute lpgDefinition must not be null since a people query " +
                    "must be always bound to a logical people definition.");
        } //TODO improve exception handling

        this.peopleQueryEntity = new Peoplequery();
        setLogicalPeopleGroupDef(lpgDefinition);
    }

    public void addArgument(IPeopleQueryArgument argument) {

        if (getArgumentMap().containsKey(argument.getName())) {
            throw new RuntimeException("There is already exists an argument with name " +
                    argument.getName() + " within the people query.");
        }

        List<Peoplequeryargument> peopleQueryArgEntities = peopleQueryEntity.getPeoplequeryarguments();

        /* If the people query doesn't contain any arguments yet the list
           * of people query arguments is null.
           */
        if (peopleQueryArgEntities == null) {
            peopleQueryArgEntities = new ArrayList<Peoplequeryargument>();
            peopleQueryEntity.setPeoplequeryarguments(peopleQueryArgEntities);
        }

        Peoplequeryargument peopleQueryArgEntity = (Peoplequeryargument) argument.getAdaptee();
        /*
           * There is a bidirectional relation between people
           * query entities and people query argument entities consequently
           * both sides of the relationship have to be set.
           */
        peopleQueryArgEntities.add(peopleQueryArgEntity);
        peopleQueryArgEntity.setPeoplequeryBean(peopleQueryEntity);
    }

    protected Map<String, IPeopleQueryArgument> getArgumentMap() {
        List<Peoplequeryargument> argumentEntities = peopleQueryEntity.getPeoplequeryarguments();

        Map<String, IPeopleQueryArgument> arguments = new HashMap<String, IPeopleQueryArgument>();
        if (argumentEntities != null) {
            /* Get all people query arguments and their associated names */
            Iterator<Peoplequeryargument> iter = argumentEntities.iterator();
            while (iter.hasNext()) {
                Peoplequeryargument peoplequeryArgument = (Peoplequeryargument) iter
                        .next();
                /* Get the name of the people query argument */
                String argumentName = getPeopleQueryArgumentName(peoplequeryArgument);
                if (argumentName != null) {
                    /* Add the name and the people query argument model to the map */
                    arguments.put(argumentName,
                            /* Determine the model of the people query argument */
                            ModelElementFactory.newInstance().createPeopleGroupArgument(peoplequeryArgument));
                }
            }
        }
        return arguments;
    }

    protected String getPeopleQueryArgumentName(Peoplequeryargument peoplequeryArgument) {

        /* The lpg argument definition contains the name of the people query argument. */
        LpgArgumentdef lpgArgumentDef = peoplequeryArgument.getLpgArgumentdef();
        /* Should always evaluate to true since a people query argument can only be created based on
           * an lpg argument definition */
        if (lpgArgumentDef != null) {
            return lpgArgumentDef.getName();
        }
        return null;
    }

    public IPeopleQueryArgument getArgument(String argumentName) {
        return getArgumentMap().get(argumentName);
    }

    public Set<String> getArgumentNames() {
        return getArgumentMap().keySet();
    }

    public ILogicalPeopleGroupDef getBoundPeopleGroup() {
        /* Create the model of the LPG definition from the entity model */
        return ModelElementFactory.newInstance().createPeopleGroupDefinition(peopleQueryEntity.getLogicalpeoplegroupdef());
    }

    public void removeArgument(IPeopleQueryArgument argumentToRemove) {
        List<Peoplequeryargument> queryArgumentEntities = peopleQueryEntity.getPeoplequeryarguments();

        /* Search for the argument that is to be removed (simply by name).
           * If the people query hasn't any arguments queryArgumentEntities is null */
        if (queryArgumentEntities != null) {
            Iterator<Peoplequeryargument> iter = queryArgumentEntities.iterator();
            /* Iterate over all arguments and remove the argument that has the same
                * name like the argument that is to be delete.  */
            while (iter.hasNext()) {
                String argumentName = getPeopleQueryArgumentName(iter.next());
                if (argumentName.equals(argumentToRemove.getName())) {
                    /* Remove people query argument */
                    iter.remove();
                }
            }
        }
    }

    public String getId() {
        return Integer.toString(peopleQueryEntity.getId());
    }

    public Set<IPeopleQueryArgument> getArguments() {
        return new HashSet<IPeopleQueryArgument>(getArgumentMap().values());
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);
    }

    public WrappableEntity getAdaptee() {
        return peopleQueryEntity;
    }

    public void setLogicalPeopleGroupDef(ILogicalPeopleGroupDef lpgDefinition) {
        Logicalpeoplegroupdef lpgDefEntity = (Logicalpeoplegroupdef) lpgDefinition.getAdaptee();

        /*
           * There is a bidirectional relation between LPG definitions and people
           * queries in the generated JPA entities consequently both sides of the relationship
           * have to be set.
           */

        peopleQueryEntity.setLogicalpeoplegroupdef(lpgDefEntity);

        /* The list contains all people queries which are already
           * associated with the LPG definition */
        List<Peoplequery> peopleQueryEntities = lpgDefEntity.getPeoplequeries();

        /* If this people query is the first one that is associated with the
           * LPG definition the list is null thus it has to be instantiated */
        if (peopleQueryEntities == null) {
            peopleQueryEntities = new ArrayList<Peoplequery>();
            lpgDefEntity.setPeoplequeries(peopleQueryEntities);
        }
        peopleQueryEntities.add(peopleQueryEntity);

    }


}
