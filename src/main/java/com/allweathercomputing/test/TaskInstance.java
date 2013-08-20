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

package com.allweathercomputing.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TaskInstance {

    private long tiid;

    private long tmid;

    private Node inputData;

    private String description;

    public TaskInstance() {

    }

    public TaskInstance(long tiid, long tmid, Node inputData,
                        String description) {
        //super();
        this.tiid = tiid;
        this.tmid = tmid;
        this.inputData = inputData;
        this.description = description;
    }

    public long getTiid() {
        return tiid;
    }

    public void setTiid(long tiid) {
        this.tiid = tiid;
    }

    public long getTmid() {
        return tmid;
    }

    public void setTmid(long tmid) {
        this.tmid = tmid;
    }

    public Node getInputData() {
        return inputData;
    }

    public void setInputData(Node inputData) {
        this.inputData = inputData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
