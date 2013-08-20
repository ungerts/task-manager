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

package com.htm.query.jxpath;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestContainer {

    private List<Integer> nums = new ArrayList<Integer>();

    private List<String> names = new ArrayList<String>();

    private Timestamp timestamp;

    public List<String> getNames() {
        return this.names;
    }

    public void addName(String name) {
        this.names.add(name);
    }

    public List<Integer> getNums() {
        return this.nums;
    }

    public void addNum(Integer num) {
        this.nums.add(num);
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


}
