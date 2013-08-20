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

package com.htm.taskparent;

import java.util.Set;

import com.htm.query.views.TaskInstanceView;
import com.htm.taskinstance.ICorrelationProperty;

/**
 * The interface has to be implemented by all task parent callback
 * handlers.</br>
 * A callback handler is used to inform the parent application of a task about state changes.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public interface ICallbackHandler {

    /**
     * The method is called when the state of a task instance has  beeen changed.
     *
     * @param correlationProperties These properties can be used to correlate the callback to a  task instance
     *                              creation request made earlier.
     * @param taskInstanceView      A  snapshot of the task instance when the state was changed.
     */
    public void callTaskParent(Set<ICorrelationProperty> correlationProperties,
                               TaskInstanceView taskInstanceView);


}
