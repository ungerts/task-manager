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
public interface IOrganizationQueryManager {

    // TODO pure JDBC should be more efficient
    public List<String> resolveRole(String roleId)
            throws HumanTaskManagerException;

    // TODO pure JDBC should be more efficient
    // TODO subOrganization
    public List<String> resolveOrganization(String organizationId,
                                            boolean includeSubOrganizations) throws HumanTaskManagerException;

    // TODO pure JDBC should be more efficient
    // TODO subOrganization
    public List<String> resolveOrganizationAndRole(String roleId,
                                                   String organizationId, boolean includeSubOrganizations)
            throws HumanTaskManagerException;

    // TODO pure JDBC should be more efficient
    // TODO parentOrganization
    public List<String> managerOfPerson(String userId,
                                        boolean includeParentOrganizations)
            throws HumanTaskManagerException;

}