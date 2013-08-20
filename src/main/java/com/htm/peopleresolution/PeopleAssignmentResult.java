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

package com.htm.peopleresolution;

import java.util.HashSet;
import java.util.Set;


import com.htm.taskmodel.IPeopleAssignment;

/**
 * This class represents a result of a people assignment.</b>
 * It contains the identifiers of the users which are
 * determined by a people assignment and it can also contain the user ids
 * of users that are represented by literals.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see IPeopleAssignment
 * @see IPeopleResolutionProvider
 * @see PeopleAssignmentPostProcessor
 * @see Literal
 */
public class PeopleAssignmentResult {

    /**
     * This attribute indicates that everybody
     * i.e. every users meets the people query.</b>
     */
    private boolean everybody = false;

    /**
     * Contains the ids of the users that were
     * determined by the people assignment.
     */
    private Set<String> userids = new HashSet<String>();

    public boolean isAssignedToEverybody() {
        return everybody;
    }

    public void setNobodyAssigned() {
        this.userids.clear();
    }

    public boolean isNobodyAssigned() {
        /*
           * The list of user ids is either empty if
           * a people query returns "everybody" or if
           * no user is returned.
           */
        return !everybody && userids.isEmpty();
    }

    public void setEverybodyAssigned(boolean everybody) {
        this.everybody = everybody;

        /* If the people assignment result goes to the status "everybody"
           * the list of user ids is not needed anymore.
           */
        if (everybody) {
            this.userids.clear();
        }
    }

    public Set<String> getUserids() {
        return userids;
    }

    public void setUserids(Set<String> userids) {
        this.userids = userids;
    }

    public void addUserids(Set<String> userids) {
        this.userids.addAll(userids);
    }

    public void addUserId(String userid) {
        this.userids.add(userid);
    }
}
