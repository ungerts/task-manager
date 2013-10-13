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

package com.htm.utils;

import org.apache.log4j.Logger;

public class SessionUtils {

    private static final Logger log = Utilities.getLogger(SessionUtils.class);

    private static String currentUser;

    public static String getCurrentUser() {
        log.debug("Current user: " + currentUser);
        return currentUser;
    }

    public static void setCurrentUser(String userId) {
        currentUser = userId;
    }
}
