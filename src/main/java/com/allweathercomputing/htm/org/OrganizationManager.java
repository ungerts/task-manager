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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
@Stateless(name = "OrganizationManagerBean")
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// TODO Roles(Security)
public class OrganizationManager implements IOrganizationManager {

    private Logger log;

    @Resource
    private SessionContext ctx;

    @PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
    private EntityManager em;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#addPerson(com.allweathercomputing.htm.org.Person)
      */
    @Override
    public void addPerson(Person person) throws HumanTaskManagerException {
        try {
            em.persist(person);
        } catch (Exception e) {
            String message = "Cannot persist person " + person.getUserId();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#addRole(com.allweathercomputing.htm.org.Role)
      */
    @Override
    public void addRole(Role role) throws HumanTaskManagerException {
        try {
            em.persist(role);
        } catch (Exception e) {
            String message = "Cannot persist role " + role.getRoleId();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#addOrganization(com.allweathercomputing.htm.org.Organization)
      */
    @Override
    public void addOrganization(Organization organization)
            throws HumanTaskManagerException {
        try {
            em.persist(organization);
        } catch (Exception e) {
            String message = "Cannot persist organization "
                    + organization.getOrganizationId();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#deletePerson(java.lang.String)
      */
    @Override
    public void deletePerson(String personId) throws HumanTaskManagerException {
        Person person;
        try {
            person = em.find(Person.class, personId);
            em.remove(person);
        } catch (Exception e) {
            String message = "Cannot delete person " + personId;
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#deleteRole(java.lang.String)
      */
    @Override
    public void deleteRole(String roleId) throws HumanTaskManagerException {
        Role role;
        try {
            role = em.find(Role.class, roleId);
            em.remove(role);
        } catch (Exception e) {
            String message = "Cannot delete role " + roleId;
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#deleteOrganization(java.lang.String)
      */
    @Override
    public void deleteOrganization(String organizationId)
            throws HumanTaskManagerException {
        Organization organization;
        try {
            organization = em.find(Organization.class, organizationId);
            em.remove(organization);
        } catch (Exception e) {
            String message = "Cannot delete organization " + organizationId;
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#addRoleMembership(java.lang.String, java.lang.String)
      */
    @Override
    public void addRoleMembership(String userId, String roleId)
            throws HumanTaskManagerException {
        Person person;
        Role role;
        try {
            person = em.find(Person.class, userId);
            role = em.find(Role.class, roleId);
            if (!person.getRoles().contains(role)) {
                person.getRoles().add(role);
                em.persist(person);
            }
        } catch (Exception e) {
            String message = "Cannot add role membership of user " + userId
                    + " to role " + roleId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#addOrganizationMembership(java.lang.String, java.lang.String)
      */
    @Override
    public void addOrganizationMembership(String userId, String organizationId)
            throws HumanTaskManagerException {
        Person person;
        Organization organization;
        try {
            person = em.find(Person.class, userId);
            organization = em.find(Organization.class, organizationId);
            person.setOrganization(organization);
            em.persist(person);
        } catch (Exception e) {
            String message = "Cannot add organization membership of user "
                    + userId + " to organization " + organizationId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#delteRoleMembership(java.lang.String, java.lang.String)
      */
    @Override
    public void delteRoleMembership(String userId, String roleId)
            throws HumanTaskManagerException {
        Person person;
        Role role;
        try {
            person = em.find(Person.class, userId);
            role = em.find(Role.class, roleId);
            if (person.getRoles().contains(role)) {
                person.getRoles().remove(role);
                em.persist(person);
            }
        } catch (Exception e) {
            String message = "Cannot delete role membership of user " + userId
                    + " to role " + roleId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#delteOrganizationMembership(java.lang.String)
      */
    @Override
    public void delteOrganizationMembership(String userId)
            throws HumanTaskManagerException {
        Person person;
        try {
            person = em.find(Person.class, userId);
            person.setOrganization(null);
            em.persist(person);
        } catch (Exception e) {
            String message = "Cannot add organization membership of user "
                    + userId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getPersons()
      */
    @Override
    public List<Person> getPersons() throws HumanTaskManagerException {
        try {
            TypedQuery<Person> query = em.createQuery("SELECT * FROM person;",
                    Person.class);
            return query.getResultList();
        } catch (Exception e) {
            String message = "Cannot fetch persons";
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getRoles()
      */
    @Override
    public List<Role> getRoles() throws HumanTaskManagerException {
        try {
            TypedQuery<Role> query = em.createQuery("SELECT * FROM role;",
                    Role.class);
            return query.getResultList();
        } catch (Exception e) {
            String message = "Cannot fetch roles";
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getOrganizations()
      */
    @Override
    public List<Organization> getOrganizations()
            throws HumanTaskManagerException {
        try {
            TypedQuery<Organization> query = em.createQuery(
                    "SELECT * FROM organization;", Organization.class);
            return query.getResultList();
        } catch (Exception e) {
            String message = "Cannot fetch organizations";
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getPerson(java.lang.String)
      */
    @Override
    public Person getPerson(String userId) throws HumanTaskManagerException {
        try {

            return em.find(Person.class, userId);
        } catch (Exception e) {
            String message = "Cannot fetch person " + userId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getOrganization(java.lang.String)
      */
    @Override
    public Organization getOrganization(String organizationId)
            throws HumanTaskManagerException {
        try {

            return em.find(Organization.class, organizationId);
        } catch (Exception e) {
            String message = "Cannot fetch organization " + organizationId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#getRole(java.lang.String)
      */
    @Override
    public Role getRole(String roleId) throws HumanTaskManagerException {

        try {

            return em.find(Role.class, roleId);
        } catch (Exception e) {
            String message = "Cannot fetch role " + roleId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#setParentOrganization(java.lang.String, java.lang.String)
      */
    @Override
    public void setParentOrganization(String organizationId,
                                      String parentOrganizationId) throws HumanTaskManagerException {
        Organization organization, parentOrganization;
        try {
            organization = em.find(Organization.class, organizationId);
            parentOrganization = em.find(Organization.class,
                    parentOrganizationId);
            organization.setParentOrganization(parentOrganization);
            em.persist(organization);

        } catch (Exception e) {
            String message = "Cannot set parent organization ("
                    + parentOrganizationId + ") for organization ("
                    + organizationId + ")";
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#setRoleCoordinator(java.lang.String, java.lang.String)
      */
    @Override
    public void setRoleCoordinator(String roleId, String userId)
            throws HumanTaskManagerException {
        Person person;
        Role role;
        try {
            person = em.find(Person.class, userId);
            role = em.find(Role.class, roleId);
            role.setCoordinator(person);
            em.persist(role);

        } catch (Exception e) {
            String message = "Cannot set coordinator for role " + roleId
                    + "; Coordinator: " + userId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    /* (non-Javadoc)
      * @see com.allweathercomputing.htm.org.IOrganizationManager#setOrganizationManager(java.lang.String, java.lang.String)
      */
    @Override
    public void setOrganizationManager(String organizationId, String userId)
            throws HumanTaskManagerException {
        Person person;
        Organization organization;
        try {
            person = em.find(Person.class, userId);
            organization = em.find(Organization.class, organizationId);
            organization.setManager(person);
            em.persist(person);
        } catch (Exception e) {
            String message = "Cannot set manager for organization " + organizationId
                    + "; Manager: " + userId;
            log.debug(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

}
