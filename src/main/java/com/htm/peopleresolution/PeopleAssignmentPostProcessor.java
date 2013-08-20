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

import com.htm.dm.EHumanRoles;
import com.htm.taskinstance.ITaskInstance;


/**
 * Provides an interface that has to be implemented
 * by classes that modify the people query result.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public abstract class PeopleAssignmentPostProcessor {

    /**
     * Creates a new people assignment post processor.
     *
     * @return A concrete people assignment post processor instance.
     */
    public static PeopleAssignmentPostProcessor newInstance() {
        return new PeopleQueryPostProcessorDummy();
    }

    /**
     * Returns a modified people assignment result.</br>
     * Implementations of this method enlarge or reduce the
     * people assignment result.
     *
     * @param assignmentResult The people assignment result that has to be modified.
     * @param humanRole        The generic human role that is represented by the
     *                         people assignment result.
     * @param taskInstance     The task instance forms is the context of the people assignment.
     * @return The modified people assignment result.
     */
    public abstract PeopleAssignmentResult postProcess(PeopleAssignmentResult assignmentResult,
                                                       EHumanRoles humanRole,
                                                       ITaskInstance taskInstance);
}
