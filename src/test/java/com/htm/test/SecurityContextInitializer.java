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

import com.htm.ITaskClientInterface;
import com.htm.ITaskModelStore;
import com.htm.TaskParentInterface;
import com.htm.security.AuthorizationManager;
import com.htm.utils.SessionUtils;

/**
 * This class has to extended by the test classes to set the security context.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public class SecurityContextInitializer {


    /**
     * This method has always to be called before a user can perform operations with the task manager. If the user
     * wants to perform task client operations (e.g. claiming a task instance)
     * he has to be assigned to the task instance via a work item with the corresponding role.
     *
     * @param userId   The user identifier.
     * @param password The corresponding password.
     * @see ITaskClientInterface
     * @see ITaskModelStore
     * @see TaskParentInterface
     * @see AuthorizationManager
     */
    public void initSecurityContext(String userId, String password) {

        /* TODO Replace by more sophisticated authorization */
        SessionUtils.setCurrentUser(userId);

    }

}
