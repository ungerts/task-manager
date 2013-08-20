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
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.ICorrelationProperty;

/**
 * This class was only created for testing purposes.
 * It acts as dummy task parent.
 *
 * @author sew71sgp
 */
public class DummyCallbackHandler implements ICallbackHandler {

    public void callTaskParent(
            Set<ICorrelationProperty> correlationProperties, TaskInstanceView taskInstanceView) {
        /* The callback is simply realized by setting the values in the task client test class.
           * The respective test case can use the values for assertions  */
        TaskParentCallbackResponseContainer.setCorrelationProperties(correlationProperties);
        TaskParentCallbackResponseContainer.setState(ETaskInstanceState.valueOf(taskInstanceView.getStatus()));

    }

    public static class TaskParentCallbackResponseContainer {

        private static Set<ICorrelationProperty> correlationProperties;

        private static ETaskInstanceState state;

        private static String taskParentId;
        ;

        public static String getTaskParentId() {
            return taskParentId;
        }

        public static void setTaskParentId(String taskParentId) {
            TaskParentCallbackResponseContainer.taskParentId = taskParentId;
        }

        public static void setState(ETaskInstanceState state) {
            TaskParentCallbackResponseContainer.state = state;
        }

        public static ETaskInstanceState getState() {
            return state;
        }

        public static Set<ICorrelationProperty> getCorrelationProperties() {
            return correlationProperties;
        }

        public static void setCorrelationProperties(
                Set<ICorrelationProperty> correlationProperties) {
            TaskParentCallbackResponseContainer.correlationProperties = correlationProperties;
        }

    }

    public String getTaskParentId() {
        return "DummmyCallbackHandler";
    }
}
