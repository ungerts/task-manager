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

import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Group;
import com.htm.entities.jpa.User;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;
import com.htm.userdirectory.UserDirectoryFactory;
import com.htm.utils.Utilities;

public class UserDirectoryFactoryJPA extends UserDirectoryFactory {

    @Override
    public IGroup createGroupFromEntity(WrappableEntity groupEntity) {

        Utilities.isValidClass(groupEntity, Group.class);

        return new GroupWrapper((Group) groupEntity);
    }

    @Override
    public IGroup createNewGroup(String groupName) {
        return new GroupWrapper(groupName);
    }

    @Override
    public IUser createNewUser(String userId, String firstName,
                               String lastName, String password) {
        IUser user = new UserWrapper();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);

        return user;

    }

    @Override
    public IUser createUserFromEntity(WrappableEntity userEntity) {

        Utilities.isValidClass(userEntity, User.class);

        return new UserWrapper((User) userEntity);
    }

}
