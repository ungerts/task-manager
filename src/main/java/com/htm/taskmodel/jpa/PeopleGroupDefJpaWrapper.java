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

import org.apache.log4j.Logger;


import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Logicalpeoplegroupdef;
import com.htm.entities.jpa.LpgArgumentdef;
import com.htm.entities.jpa.Peoplequery;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;


public class PeopleGroupDefJpaWrapper implements ILogicalPeopleGroupDef {

    private Logger log;

    protected Logicalpeoplegroupdef lpgDef;


    public PeopleGroupDefJpaWrapper(Logicalpeoplegroupdef adaptee) {
        this.lpgDef = adaptee;
        this.log = Utilities.getLogger(this.getClass());
    }

    public PeopleGroupDefJpaWrapper(String name) {
        this.lpgDef = new Logicalpeoplegroupdef();
        this.lpgDef.setName(name);
        this.log = Utilities.getLogger(this.getClass());
    }

    protected Map<String, ILogicalPeopleGroupArgumentDef> getArgumentDefinitionMap() {
        List<LpgArgumentdef> lpgArgumentDefEntities = lpgDef.getLpgArgumentdefs();

        /*
           * The map contains as key the name of the LPG argument definition
           * and as value the actual LPG definition.
           */
        Map<String, ILogicalPeopleGroupArgumentDef> arguments = new HashMap<String, ILogicalPeopleGroupArgumentDef>();
        if (lpgArgumentDefEntities != null) {
            /* Get all LPG argument definitions and add them to the map */
            Iterator<LpgArgumentdef> iter = lpgArgumentDefEntities.iterator();
            while (iter.hasNext()) {
                LpgArgumentdef peoplequeryArgument = (LpgArgumentdef) iter
                        .next();
                String argumentDefName = peoplequeryArgument.getName();
                if (argumentDefName != null) {
                    /* Add the name and the LPG argument definition model to the map */
                    arguments.put(argumentDefName,
                            /* Determine the model of the LPG argument definition */
                            ModelElementFactory.newInstance().createPeopleGroupArgumentDef(peoplequeryArgument));
                }
            }
        }
        return arguments;
    }

    /* Set decoupled from actual argument entities thus
     *  if an argument definition is removed from the set it's
     * not removed from the underlying data model
     */
    public Set<ILogicalPeopleGroupArgumentDef> getArguments() {
        return new HashSet<ILogicalPeopleGroupArgumentDef>(getArgumentDefinitionMap().values());
    }

    public void removeArgumentDefinition(String argumentDefToDelete) {
        List<LpgArgumentdef> lpgArgumentDefEntities = lpgDef.getLpgArgumentdefs();

        if (lpgArgumentDefEntities != null) {
            Iterator<LpgArgumentdef> iter = lpgArgumentDefEntities.iterator();
            /* Simply iterate over all LPG argument definitions and remove the
                * LPG argument definition which name equals to the name of the definition
                * that has to be deleted.
                */
            while (iter.hasNext()) {
                LpgArgumentdef lpgArgumentdefEntity = (LpgArgumentdef) iter.next();
                if (lpgArgumentdefEntity.getName().equals(argumentDefToDelete)) {
                    /* Remove the LPG argument definition */
                    iter.remove();
                }
            }
        }
    }

    public String getName() {
        return lpgDef.getName();
    }

    public void setName(String name) {
        this.lpgDef.setName(name);

    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public String getId() {
        return Integer.toString(lpgDef.getId());
    }

    public ILogicalPeopleGroupArgumentDef getArgumentDef(String name) {
        return getArgumentDefinitionMap().get(name);
    }

    public void addArgumentDefinition(ILogicalPeopleGroupArgumentDef argumentDefToAdd) {

        /*
           * Only add the argument definition if it not already exists
           * This is done by checking if there is already a definition with the
           * same name like the one that is to be added.
           */
        ILogicalPeopleGroupArgumentDef argumentDef = getArgumentDefinitionMap().get(argumentDefToAdd.getName());

        if (argumentDef == null) {
            List<LpgArgumentdef> argumentDefEntities = this.lpgDef.getLpgArgumentdefs();
            /* If there was no LPG argument definition added yet
                * the list of argument definitions doesn't exist (it is null)
                * thus it has to be created. */
            if (argumentDefEntities == null) {
                argumentDefEntities = new ArrayList<LpgArgumentdef>();
                this.lpgDef.setLpgArgumentdefs(argumentDefEntities);

            }
            LpgArgumentdef lpgArgumentDefEntity = (LpgArgumentdef) argumentDefToAdd.getAdaptee();

            /*
                * There is a bidirectional relation between  LPG definitions and LPG
                * argument definitions in JPA thus both sides of the relation have to be set.
                */
            argumentDefEntities.add(lpgArgumentDefEntity);
            lpgArgumentDefEntity.setLogicalpeoplegroupdef(lpgDef);
        }
    }

    public boolean equals(ILogicalPeopleGroupDef lpgDef) {

        boolean areLpgDefsEqual = false;

        /*
           * Two LPG definitions are considered as equal if they have the same and
           * if the contain the same LPG argument definitions.
           */
        log.debug("Comparing LPG Definitions: Name of this LPG definition: " +
                this.getName() + " Name LPG Definition to compare with: " + lpgDef.getName());

        if (lpgDef != null &&
                this.lpgDef.getName().equals(lpgDef.getName())) {

            log.debug("Comparing LPG Definitions: Both have the same name " + lpgDef.getName());

            /*
                * Check if both LPG definitions have the same arguments i.e.
                * if they contain the same number of argument
                * definitions and if the argument definitions within the LPG
                * definitions have the same names.
                */
            Set<String> thisArgumentDefNames = getArgumentDefinitionNames();
            Set<String> referenceArgumentDefNames = lpgDef.getArgumentDefinitionNames();
            boolean hasSameNumberOfArgumentDefs = thisArgumentDefNames.size() == referenceArgumentDefNames.size();
            boolean containsAllArgumentDefs = thisArgumentDefNames.containsAll(referenceArgumentDefNames);

            log.debug("Comparing LPG Definitions: Both have the same number of argument definitions : " + hasSameNumberOfArgumentDefs);
            log.debug("Comparing LPG Definitions: Contain the same argument definitions : " + containsAllArgumentDefs);

            areLpgDefsEqual = hasSameNumberOfArgumentDefs && containsAllArgumentDefs;
        }
        log.debug("Comparing LPG Definitions: LPG definitions equal " + areLpgDefsEqual);
        return areLpgDefsEqual;
    }

    public WrappableEntity getAdaptee() {
        return lpgDef;
    }

    public Set<String> getArgumentDefinitionNames() {
        return getArgumentDefinitionMap().keySet();
    }

    public Set<IPeopleAssignment> getAssociatedPeopleQueries() {
        List<Peoplequery> peopleQueryEntities = lpgDef.getPeoplequeries();
        Set<IPeopleAssignment> peopleQueries = new HashSet<IPeopleAssignment>();
        /* If there are no people queries associated with this
           * LPG definition the list may be empty */
        if (peopleQueryEntities != null) {
            Iterator<Peoplequery> iter = peopleQueryEntities.iterator();
            /* Create the model of the people group and add it to the list of
                * people group models */
            while (iter.hasNext()) {
                peopleQueries.add(ModelElementFactory.newInstance().createPeopleQuery((Peoplequery) iter.next()));

            }
        }
        return peopleQueries;
    }

    public boolean isAssociatedToPeopleQueries() {
        List<Peoplequery> peopleQueryEntities =
                lpgDef.getPeoplequeries();
        /* True if the list of people queries exist
           * and if it contains at least one element */
        return peopleQueryEntities != null && !peopleQueryEntities.isEmpty();
    }

}
