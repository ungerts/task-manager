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

package com.shtm.test;

import static org.junit.Assert.assertEquals;

import com.shtm.operationAndStates.EStates;
import com.shtm.views.StructuredTaskInstanceView;
import org.junit.Ignore;

@Ignore
public class TestStructureData {
    String task_id;
    EStates state;
    boolean locked;
    boolean controlled;
    boolean checked = false;

    public TestStructureData(String task_id, boolean controlled,
                             boolean locked, EStates state) {
        this.controlled = controlled;
        this.locked = locked;
        this.state = state;
        this.task_id = task_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public EStates getState() {
        return state;
    }

    public void setState(EStates state) {
        this.state = state;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isControlled() {
        return controlled;
    }

    public void setControlled(boolean controlled) {
        this.controlled = controlled;
    }

    public boolean isChecked() {
        return checked;
    }

    public void checkTaskAssertions(StructuredTaskInstanceView task) {
        assertEquals("Task " + this.task_id + " Locked", this.isLocked(), task
                .isLocked());
        //assertEquals("Task " + this.task_id + " ControlledState", this
        //	.isControlled(), task.isControlled());
        assertEquals("Task " + this.task_id + " State", this.getState(), task
                .getState());
        this.checked = true;
    }

}
