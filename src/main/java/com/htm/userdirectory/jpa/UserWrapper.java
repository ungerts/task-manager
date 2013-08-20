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
import java.util.Iterator;
import java.util.List;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Group;
import com.htm.entities.jpa.User;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;
import com.htm.userdirectory.UserDirectoryFactory;

public class UserWrapper implements IUser {

    protected User userEntity;

    public UserWrapper(User adaptee) {
        this.userEntity = adaptee;
    }

    public UserWrapper() {
        this.userEntity = new User();
    }

    public String getFirstName() {
        return userEntity.getFirstname();
    }

    public List<IGroup> getGroups() {

        List<IGroup> groupModels = new ArrayList<IGroup>();
        List<Group> groupEntities = userEntity.getGroups();
        if (groupEntities != null) {
            Iterator<Group> iter = groupEntities.iterator();
            /* Create group models from group entities and add them to the list of group models */
            while (iter.hasNext()) {
                IGroup group =
                        UserDirectoryFactory.newInstance().createGroupFromEntity((Group) iter.next());
                groupModels.add(group);
            }
        }

        return groupModels;
    }

    public String getLastName() {
        return userEntity.getLastname();
    }

    public String getUserId() {
        return userEntity.getUserid();
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public WrappableEntity getAdaptee() {
        return userEntity;
    }

    public String getId() {
        return Integer.toString(userEntity.getId());
    }

    public void setFirstName(String firstName) {
        userEntity.setFirstname(firstName);
    }

    public void setLastName(String lastName) {
        userEntity.setLastname(lastName);

    }

    public void setUserId(String userId) {
        userEntity.setUserid(userId);
    }

    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPassword(String password) {
        // TODO Auto-generated method stub

    }

}
