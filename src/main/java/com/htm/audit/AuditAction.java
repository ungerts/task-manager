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

package com.htm.audit;

import com.htm.query.views.TaskInstanceView;

public class AuditAction {

    private String action;

    private TaskInstanceView taskInstanceView;

    private String state;

    private String oldState;

    private String originator;

    public AuditAction() {

    }

    public AuditAction(String action, TaskInstanceView taskInstanceView,
                       String state, String oldState, String originatror) {
        super();
        this.action = action;
        this.taskInstanceView = taskInstanceView;
        this.state = state;
        this.oldState = oldState;
        this.originator = originatror;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TaskInstanceView getTaskInstanceView() {
        return taskInstanceView;
    }

    public void setTaskInstanceView(TaskInstanceView taskInstanceView) {
        this.taskInstanceView = taskInstanceView;
    }

}
