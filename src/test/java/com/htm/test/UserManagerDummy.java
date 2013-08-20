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

package com.htm.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


import com.htm.exceptions.HumanTaskManagerException;
import com.htm.security.IUserManager;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;
import com.htm.utils.Utilities;

public class UserManagerDummy extends SecurityContextInitializer {

    protected Logger log = Utilities.getLogger(this.getClass());

    protected IUserManager um = getUserManager();

    protected List<IUser> dummyUsers = new ArrayList<IUser>();

    protected List<IGroup> dummyGroups = new ArrayList<IGroup>();

    public static final String GROUP_DUMMY_INTERNS = "interns";

    public static final String GROUP_DUMMY_EMPLOYEES = "employees";

    public static final String GROUP_DUMMY_MANAGERS = "managers";

    public static final String GROUP_DUMMY_WORKON_DEV = "workon developers";

    public static final String USER_PASSWORD = "password";

    public static final String USER_MANAGER_ID = "admin";

    public static final String USER_MANAGER_PASSWORD = "admin";

    protected void createDummyUsersAndGroups() throws HumanTaskManagerException {

        IUserManager um = IUserManager.Factory.newInstance();

        /* Create Groups users.add(user); */
        IGroup interns = um.addGroup(GROUP_DUMMY_INTERNS);
        dummyGroups.add(interns);
        IGroup workOnDevs = um.addGroup(GROUP_DUMMY_WORKON_DEV);
        dummyGroups.add(workOnDevs);
        IGroup employees = um.addGroup(GROUP_DUMMY_EMPLOYEES);
        dummyGroups.add(employees);
        IGroup managers = um.addGroup(GROUP_DUMMY_MANAGERS);
        dummyGroups.add(managers);

        /* Create dummy users, associate them to one or more groups users.add(user);*/
        IUser user = um.addUser("edelmaal", "Alexander", "Edelmann", USER_PASSWORD);
        workOnDevs.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = um.addUser(
                "khalilno", "Nouman", "Khalil", USER_PASSWORD);
        workOnDevs.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = IUserManager.Factory.newInstance().addUser(
                "lokanava", "Vanchin", "Lokanathan", USER_PASSWORD);
        workOnDevs.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = um.addUser("sundarvi", "Vijayan", "Sundararajan", USER_PASSWORD);
        employees.addMember(user);
        dummyUsers.add(user);

        user = IUserManager.Factory.newInstance().addUser("gallemel", "Melanie", "Galle", USER_PASSWORD);
        interns.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = um.addUser("schmitcl", "Clemens", "Schmitt", USER_PASSWORD);
        interns.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = IUserManager.Factory.newInstance().addUser(
                "wagnerse", "Sebastian", "Wagner", USER_PASSWORD);
        interns.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = um.addUser("wodarsan", "Andre", "Wodarsch", USER_PASSWORD);
        interns.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

        user = um.addUser("friedrth", "Thomas", "Friedrichs", USER_PASSWORD);
        managers.addMember(user);
        employees.addMember(user);
        dummyUsers.add(user);

    }

    protected IUserManager getUserManager() {
        return IUserManager.Factory.newInstance();
    }

    protected void deleteDummyUsersAndGroups() throws HumanTaskManagerException {
        IUserManager um = IUserManager.Factory.newInstance();

        /* Set the the user name and password in the security context of spring */
        initSecurityContext(USER_MANAGER_ID, USER_MANAGER_PASSWORD);

        log.debug("Deleting dummy users and groups");
        /* Delete users */
        Iterator<IUser> userIter = dummyUsers.iterator();
        while (userIter.hasNext()) {
            um.deleteUser(userIter.next().getUserId());

        }
        /* Delete groups*/
        Iterator<IGroup> groupIter = dummyGroups.iterator();
        while (groupIter.hasNext()) {
            um.deleteGroup(groupIter.next().getGroupName());

        }
    }


}
