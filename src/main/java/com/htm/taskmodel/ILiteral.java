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

import com.htm.dm.EHumanRoles;
import com.htm.dm.IDataModelElement;

/**
 * Implementors of this interface represent a model of a literal.
 * A literal represents the identifier of the organizational entity (e.g. user identifier or group name "work queueing"
 * but this is not supported yet) to assign this entity directly to a generic human role
 * of a task.</br>
 * Using literals for assigning people to tasks is more simple but also less flexible
 * than using people queries since a fixed set of names is specified
 * (each member of the set is represented by an instance of the class the implements this interface).
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see IPeopleAssignment
 */
public interface ILiteral extends IDataModelElement {

    /**
     * @return The generic human role of the
     *         organizational entity (e.g. potential owner, business administrator).
     */
    public EHumanRoles getGenericHumanRole();


    /**
     * Sets the generic human role of the organizational entity.
     *
     * @param humanRole The human role.
     */
    public void setGenericHumanRole(EHumanRoles humanRole);

    /**
     * @return The identifier of the organizational entity.
     */
    public String getOrganizationalEntityId();

    /**
     * Sets the identifier of the organizational entity (e.g. the user id).
     *
     * @param orgEntityId The identifier of the organizational entity.
     */
    public void setOrganizationalEntityId(String orgEntityId);

}
