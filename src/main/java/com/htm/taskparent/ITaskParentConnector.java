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

import java.util.HashMap;
import java.util.Map;

import com.htm.exceptions.ConfigurationException;
import com.htm.taskinstance.ITaskParentContext;

/**
 * A task parent connector enables the HTM to access the parent application of a task instance
 * either to perform a callback or to access the task context (e.g. process variables).
 * The interface has to be implemented by each task parent connector.</br>
 * The task parent connectors have to be registered with their task parent id at the enclosed {@link Factory}.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public interface ITaskParentConnector {

    /**
     * This factory creates a task parent connector object.</br>
     * All task parent connectors have to be registered here with the unique
     * id of the task parent they are representing.
     *
     * @author Sebastian Wagner
     * @author Tobias Unger
     */
    public static class Factory {

        static Map<String, ITaskParentConnector> taskParentsConnector = new HashMap<String, ITaskParentConnector>();

        static {
            /*
                * Add the callback handlers for each possible task parent of the HTM
                */
            ITaskParentConnector dummyConnector = new TaskParentConnectorDummy();

            taskParentsConnector.put(dummyConnector.getTaskParentId(), dummyConnector);

        }


        /**
         * Returns the task parent connector that is associated with the task parent id.
         *
         * @param taskParentId The id of a task parent.
         * @return A task parent connector.
         * @throws ConfigurationException Thrown when no task parent connector ould be found.
         */
        public static ITaskParentConnector newInstance(String taskParentId)
                throws ConfigurationException {
            /* Get the task parent connector by the task parent id */
            ITaskParentConnector tpCon = Factory.taskParentsConnector
                    .get(taskParentId);
            if (tpCon != null) {
                return tpCon;
            }

            String errorMsg = "A task parent connector with task parent id '"
                    + taskParentId + "' could not be found.";
            throw new ConfigurationException(errorMsg);
        }
    }

    /**
     * @return A task callback handler that cn be used to inform the parent application
     *         about changes of the task instance.
     */
    public ICallbackHandler getCallbackHandler();

    /**
     * @return Information about the context the task where the task resides in.
     */
    public ITaskParentContext getTaskParentContext();

    /**
     * @return The id the uniquely identifies the task parent.
     */
    public String getTaskParentId();

}
