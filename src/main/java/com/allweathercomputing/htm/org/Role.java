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

package com.allweathercomputing.htm.org;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Role {

    public static final int ROLE_TYPE_SECURITY = 1;

    public static final int ROLE_TYPE_ORG = 0;

    public static final int ROLE_TYPE_BOTH = 2;

    @Id
    private String roleId;

    private String roleName;

    private int roleType;

    @ManyToMany(mappedBy = "roles")
    private Collection<Person> members = new ArrayList<Person>();

    @ManyToOne
    private Person coordinator;

    public Role() {

    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public Collection<Person> getMembers() {
        return members;
    }

    public void setMembers(Collection<Person> members) {
        this.members = members;
    }

    public Person getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Person coordinator) {
        this.coordinator = coordinator;
    }


}
