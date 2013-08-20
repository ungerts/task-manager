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

package com.htm.userdirectory.jpa;

import java.util.ArrayList;
import java.util.List;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Group;
import com.htm.entities.jpa.User;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;

public class GroupWrapper implements IGroup {

    private Group groupEntity;

    public GroupWrapper(Group adaptee) {
        groupEntity = adaptee;
    }

    public GroupWrapper(String groupName) {
        groupEntity = new Group();
        setGroupName(groupName);
    }

    public String getGroupName() {
        return groupEntity.getGroupname();
    }

    public void setGroupName(String name) {
        groupEntity.setGroupname(name);

    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public WrappableEntity getAdaptee() {
        return groupEntity;
    }

    public String getId() {
        return Integer.toString(groupEntity.getId());
    }

    public void addMember(IUser member) {
        List<User> userEntities = groupEntity.getUsers();
        /* If the first user is added the list of users entities is null */
        if (userEntities == null) {
            userEntities = new ArrayList<User>();
            groupEntity.setUsers(userEntities);
        }
        userEntities.add((User) member.getAdaptee());
    }

}
