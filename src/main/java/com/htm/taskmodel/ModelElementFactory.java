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

package com.htm.taskmodel;

import java.util.Hashtable;

import com.htm.dm.EHumanRoles;
import com.htm.entities.WrappableEntity;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.IQuery;
import com.htm.taskmodel.jpa.ModelElementFactoryJPA;

public abstract class ModelElementFactory {

    public static ModelElementFactory newInstance() {
        return new ModelElementFactoryJPA();
    }

    /**
     * Creates the object model of the task model definition.</br>
     * To do that the task model definition is fetched from the task model store and the object
     * representation of this task model is created.
     *
     * @param taskModelObject The task model definition. The type of the
     *                        object depends on the underlying persistence mechanism.
     * @return The model representation of the task model definition.
     * @throws HumanTaskManagerException
     */
    public abstract ITaskModel createTaskModel(WrappableEntity taskModelObject);

    /**
     * Creates the object model of the specified logical people group (LPG) definition.</br>
     * To do that the LPG definition is fetched from the task model store and the object
     * representation of this LPG is created.
     *
     * @param lpgDefinitonObject The logical people group definition.
     *                           The type of the object depends on the underlying persistence mechanism.
     * @return The model representation of the LPG definition.
     * @throws HumanTaskManagerException
     */
    public abstract ILogicalPeopleGroupDef createPeopleGroupDefinition(WrappableEntity lpgDefinitonObject);

    public abstract ILogicalPeopleGroupDef createPeopleGroupDefinition(String name);

    public abstract ITaskModel createTaskModel();

    public abstract ILogicalPeopleGroupArgumentDef createPeopleGroupArgumentDef(WrappableEntity lpgArgumentDefObject);

    public abstract ILogicalPeopleGroupArgumentDef createPeopleGroupArgumentDef();

    public abstract IPeopleQueryArgument createPeopleGroupArgument(WrappableEntity lpgArgumentObjects);

    // Always argument def required
    public abstract IPeopleQueryArgument createPeopleGroupArgument(ILogicalPeopleGroupArgumentDef argumentDef);

    public abstract IPeopleAssignment createPeopleQuery(WrappableEntity peopleQueryObject);

    public abstract IPeopleAssignment createPeopleQuery(ILogicalPeopleGroupDef lpgDefinition);

    public abstract IPresentationModel createPresentationModel(WrappableEntity presentationObject);

    public abstract IPresentationModel createPresentationModel();

    public abstract IQuery createXPathQuery(String queryValue, Hashtable<String, String> namespaces);

    public abstract IQuery createQuery(String queryValue);

    public abstract IQuery createQuery();

    /**
     * Creates a new literal model from an existing entity.
     *
     * @param literalObject
     * @return The literal model.
     */
    public abstract ILiteral createLiteral(WrappableEntity literalObject);

    /**
     * Creates a new literal model from the scratch.
     *
     * @param orgEntityId The identifier of the organizational entity.
     * @param role        The generic human role of that is represented by the new literla object.
     * @return The literal model.
     */
    public abstract ILiteral createLiteral(String orgEntityId, EHumanRoles role);


}
