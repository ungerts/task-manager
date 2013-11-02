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

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.*;
import com.htm.taskinstance.IAssignedUser;

import java.lang.IllegalArgumentException;
import java.util.List;

public interface IAuthorizationManager {

    public IAssignedUser authorizeTaskQueryAction(EActions action)
            throws HumanTaskManagerException;

    public void authorizeTaskParentAction(String userId, String tiid,
                                   EActions action) throws AuthenticationException,
            IllegalArgumentException, DatabaseException, AuthorizationException;

    public IAssignedUser authorizeTaskClientAction(String tiid,
                                            EActions action) throws AuthorizationException,
            AuthenticationException, IllegalArgumentException,
            DatabaseException, UserException;

    // TODO is implementation correct?
    // TODO reduce redundancy
    public IAssignedUser authorizeTaskClientAction2(String tiid,
                                             String userId, EActions action) throws AuthorizationException,
            AuthenticationException, IllegalArgumentException,
            DatabaseException, UserException;

    public List<EHumanRoles> getRolesOfUser(String tiid, String userId)
            throws HumanTaskManagerException;

}