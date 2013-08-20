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

package com.htm.taskinstance.jpa;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Assigneduser;
import com.htm.taskinstance.IAssignedUser;

public class AssignedUserWrapper implements IAssignedUser {

    protected Assigneduser assignedUserEntity;


    public AssignedUserWrapper(Assigneduser adaptee) {
        this.assignedUserEntity = adaptee;
    }

    public AssignedUserWrapper() {
        this.assignedUserEntity = new Assigneduser();
    }

    public AssignedUserWrapper(String userId) {
        this.assignedUserEntity = new Assigneduser();
        this.assignedUserEntity.setUserid(userId);
    }

    public String getUserId() {
        return assignedUserEntity.getUserid();
    }

    public void setUserID(String userId) {
        assignedUserEntity.setUserid(userId);
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);
    }

    public WrappableEntity getAdaptee() {
        return assignedUserEntity;
    }

    public String getId() {
        return Integer.toString(assignedUserEntity.getId());
    }


}
