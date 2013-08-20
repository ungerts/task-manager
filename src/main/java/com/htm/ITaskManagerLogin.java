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

package com.htm;

import com.htm.exceptions.AuthenticationException;

/**
 * This interface has to be implemented by all task manager interfaces that
 * provide access to task manager business functions to an end user
 * (e.g. task parent or task client).
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @deprecated The authentication is now done by the spring framework.
 */
@Deprecated
public interface ITaskManagerLogin {
    /**
     * Authenticates a user that wants to use the task manager interfaces.</br>
     * This method has to be called before one of the business methods can be called since
     * it returns a credential token that has to be passed to as parameter to each of
     * the business method to authorize the user.
     *
     * @param userid   The id of the user that wants to login
     * @param password The password of the user.
     * @return The credential token that identifies the user.
     * @throws AuthenticationException Thrown if the user id and password combination is invalid.
     */
    public String login(String userid, String password) throws AuthenticationException;
}
