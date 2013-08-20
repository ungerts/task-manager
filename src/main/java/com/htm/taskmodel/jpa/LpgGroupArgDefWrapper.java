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

package com.htm.taskmodel.jpa;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.LpgArgumentdef;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;


public class LpgGroupArgDefWrapper implements ILogicalPeopleGroupArgumentDef {

    protected LpgArgumentdef lpgArgumentDef;

    public LpgGroupArgDefWrapper(LpgArgumentdef adaptee) {
        this.lpgArgumentDef = adaptee;
    }

    public LpgGroupArgDefWrapper() {
        this.lpgArgumentDef = new LpgArgumentdef();
    }

    public String getName() {
        return lpgArgumentDef.getName();
    }

    public void setName(String name) {
        lpgArgumentDef.setName(name);

    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public String getId() {
        return Integer.toString(lpgArgumentDef.getId());
    }

    public WrappableEntity getAdaptee() {
        return lpgArgumentDef;
    }


}
