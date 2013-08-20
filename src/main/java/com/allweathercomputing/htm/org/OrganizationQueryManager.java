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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
@Stateless(name = "OrganizationQueryManagerBean")
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// TODO Roles(Security)
public class OrganizationQueryManager implements IOrganizationQueryManager {

    private Logger log;

    @Resource
    private SessionContext ctx;

    @PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
    private EntityManager em;

    @EJB
    private IOrganizationManager organizationManager;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
    }

    // TODO pure JDBC should be more efficient
    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationQueryManager#resolveRole(java.lang.String)
      */
    @Override
    public List<String> resolveRole(String roleId)
            throws HumanTaskManagerException {
        ArrayList<String> members = new ArrayList<String>(25);
        log.debug("Resolve role: " + roleId);
        Collection<Person> memberCollection = organizationManager.getRole(
                roleId).getMembers();
        for (Person p : memberCollection) {
            members.add(p.getUserId());
        }
        return members;
    }

    // TODO pure JDBC should be more efficient
    // TODO subOrganization
    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationQueryManager#resolveOrganization(java.lang.String, boolean)
      */
    @Override
    public List<String> resolveOrganization(String organizationId,
                                            boolean includeSubOrganizations) throws HumanTaskManagerException {
        throw new HumanTaskManagerException("Not implemented");
    }

    // TODO pure JDBC should be more efficient
    // TODO subOrganization
    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationQueryManager#resolveOrganizationAndRole(java.lang.String, java.lang.String, boolean)
      */
    @Override
    public List<String> resolveOrganizationAndRole(String roleId,
                                                   String organizationId, boolean includeSubOrganizations)
            throws HumanTaskManagerException {
        throw new HumanTaskManagerException("Not implemented");
    }

    // TODO pure JDBC should be more efficient
    // TODO parentOrganization
    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationQueryManager#managerOfPerson(java.lang.String, boolean)
      */
    @Override
    public List<String> managerOfPerson(String userId,
                                        boolean includeParentOrganizations)
            throws HumanTaskManagerException {
        throw new HumanTaskManagerException("Not implemented");
    }

}
