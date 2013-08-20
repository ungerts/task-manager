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

package com.htm.security;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;

public interface IUserManager {

    public static class Factory {

        private static IUserManager userManager;

        public static IUserManager newInstance() {
            /* Singleton */
            if (userManager == null) {
                userManager = new UserManagerBasicImpl();
            }
            return userManager;

        }
    }

    public IUser addUser(String userId, String firstName, String lastName, String password) throws HumanTaskManagerException;

    public IGroup addGroup(String groupName) throws HumanTaskManagerException;

    public boolean deleteGroup(String groupName) throws HumanTaskManagerException;

    public IGroup getGroup(String groupName) throws HumanTaskManagerException;

    public IUser getUser(String userId) throws HumanTaskManagerException;

    public boolean changePassword(String userId, String newPassword) throws HumanTaskManagerException;

    public boolean deleteUser(String userid) throws HumanTaskManagerException;

}
