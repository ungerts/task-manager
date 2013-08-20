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
 * A people query post processor implementation for testing purposes.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public class PeopleQueryPostProcessorDummy extends PeopleAssignmentPostProcessor {

    /* (non-Javadoc)
      * @see com.htm.peopleresolution.PeopleQueryPostProcessor#postProcess(com.htm.peopleresolution.PeopleQueryResult, com.htm.dm.EHumanRoles, com.htm.dm.taskinstance.ITaskInstance)
      */
    @Override
    public PeopleAssignmentResult postProcess(PeopleAssignmentResult queryResult,
                                              EHumanRoles humanRole, ITaskInstance taskinstance) {
        //People query result is simply returned unmodified
        return queryResult;
    }

}
