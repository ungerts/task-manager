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

package com.htm.peopleresolution;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskmodel.IPeopleAssignment;

/**
 * This interface has to be implemented by a concrete people resolution provider.</br>
 * The people resolution provider represents the implementation of a logical people group
 * definition.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public interface IPeopleResolutionProvider {

    /**
     * Executes a people query and returns the result. How the people query is executed depends
     * on the underlying infrastructure, i.e. on the class that implements this method.
     *
     * @param peopleQuery The people query that has to be executed.
     * @param context     The context in which the query is executed. Usually a task instance. The expressions within
     *                    people query arguments (if it has any arguments) can be evaluated on the context
     *                    (e.g. input messages of a task instance)
     * @return The result of the people query, i.e. the organizational entities (e.g. users) that match the people
     *         query. If the people query fails an empty result is returned, i.e. the {@link PeopleAssignmentResult} doesn't
     *         contain a organizational entity.
     * @throws HumanTaskManagerException
     */
    public PeopleAssignmentResult executePeopleQuery(IPeopleAssignment peopleQuery, Object context) throws HumanTaskManagerException;


}
