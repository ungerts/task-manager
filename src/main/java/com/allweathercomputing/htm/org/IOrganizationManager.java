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

import java.util.List;

import javax.ejb.Local;

import com.htm.exceptions.HumanTaskManagerException;

@Local
public interface IOrganizationManager {

    public void addPerson(Person person) throws HumanTaskManagerException;

    public void addRole(Role role) throws HumanTaskManagerException;

    public void addOrganization(Organization organization)
            throws HumanTaskManagerException;

    public void deletePerson(String personId) throws HumanTaskManagerException;

    public void deleteRole(String roleId) throws HumanTaskManagerException;

    public void deleteOrganization(String organizationId)
            throws HumanTaskManagerException;

    public void addRoleMembership(String userId, String roleId)
            throws HumanTaskManagerException;

    public void addOrganizationMembership(String userId, String organizationId)
            throws HumanTaskManagerException;

    public void delteRoleMembership(String userId, String roleId)
            throws HumanTaskManagerException;

    public void delteOrganizationMembership(String userId)
            throws HumanTaskManagerException;

    public List<Person> getPersons() throws HumanTaskManagerException;

    public List<Role> getRoles() throws HumanTaskManagerException;

    public List<Organization> getOrganizations()
            throws HumanTaskManagerException;

    public Person getPerson(String userId) throws HumanTaskManagerException;

    public Organization getOrganization(String organizationId)
            throws HumanTaskManagerException;

    public Role getRole(String roleId) throws HumanTaskManagerException;

    public void setParentOrganization(String organizationId,
                                      String parentOrganizationId) throws HumanTaskManagerException;

    public void setRoleCoordinator(String roleId, String userId)
            throws HumanTaskManagerException;

    public void setOrganizationManager(String organizationId, String userId)
            throws HumanTaskManagerException;

}