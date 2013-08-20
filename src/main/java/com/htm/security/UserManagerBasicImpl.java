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


import org.apache.log4j.Logger;

import com.htm.db.IDataAccessProvider;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;
import com.htm.userdirectory.UserDirectoryFactory;
import com.htm.utils.Utilities;

public class UserManagerBasicImpl implements IUserManager {

    public final static String ADMIN_ROLE = "admin";

    public final static String DUMMY_ADMIN_USERNAME = "admin";

    public final static String DUMMY_ADMIN_PASSWORD = "admin";

    protected final static String CREDENTIAL_DELIMITER = "_";

    protected final String ADMIN_CREDENTIAL = DUMMY_ADMIN_USERNAME.concat(CREDENTIAL_DELIMITER).concat(DUMMY_ADMIN_PASSWORD);

    protected IDataAccessProvider dap;

    protected Logger log;

    public UserManagerBasicImpl() {
        this.dap = IDataAccessProvider.Factory.newInstance();
        this.log = Utilities.getLogger(this.getClass());
    }

    public IUser addUser(String userId,
                         String firstName, String lastName, String password) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.ADD_USER);

        IUser user = UserDirectoryFactory.newInstance().createNewUser(
                userId, firstName, lastName, password);
        dap.persistUser(user);

        return user;
    }

    public boolean changePassword(String userId, String newpassword) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.CHANGE_PASSWORD);

        /* Get the user model from the database and set the new pasword */
        IUser user = dap.getUser(userId);
        if (user != null) {
            user.setPassword(newpassword);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String userId) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_USER);

        return dap.deleteUser(userId);

    }

    public IGroup addGroup(String groupName) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.ADD_GROUP);
        /* Create group model and persist it */
        IGroup group = UserDirectoryFactory.newInstance().createNewGroup(groupName);
        dap.persistGroup(group);

        return group;
    }

    public boolean deleteGroup(String groupName) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_GROUP);

        return dap.deleteGroup(groupName);
    }

    public IGroup getGroup(String groupName) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_GROUP);
        /* Check if the passed credentials are correct */
        return dap.getGroup(groupName);

    }

    public IUser getUser(String userId) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_USER);
        return dap.getUser(userId);

    }

    protected String createCredential(String userid, String password) {
        return userid.concat(CREDENTIAL_DELIMITER).concat(password);
    }


}
