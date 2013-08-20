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

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.allweathercomputing.htm.org.IOrganizationManager;
import com.allweathercomputing.htm.org.IOrganizationQueryManager;
import com.htm.exceptions.HumanTaskManagerException;

public class JEEUtils {
    /**
     * OrganizationManagerBean!com.allweathercomputing.htm.org.
     * OrganizationManager:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy
     * OrganizationManagerBean
     * !com.allweathercomputing.htm.org.IOrganizationManager:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy TaskClientBean:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy
     * OrganizationQueryManagerBean
     * !com.allweathercomputing.htm.org.IOrganizationQueryManager:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy
     * OrganizationQueryManagerBean
     * !com.allweathercomputing.htm.org.OrganizationQueryManager:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy
     * TaskClientBean!com.htm.ejb.TaskClientBean:
     * com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy
     */

    public static final String PERSISTENCE_MANAGER_HTM = "persistence/htm";

    // public static final String PERSISTENCE_MANAGER_SHTM = "persistence/shtm";
    public static final String PERSISTENCE_MANAGER_SHTM = "persistence/htm";

    // public static final String PERSISTENCE_MANAGER_ORG = "persistence/org";
    public static final String PERSISTENCE_MANAGER_ORG = "persistence/htm";

    public static final String PERSISTENCE_PREFIX = "java:comp/env/";

    public static final String ORGANIZATION_MANAGER_BEAN_NAME = "OrganizationManagerBean";

    public static final String ORGANIZATION_QUERY_MANAGER_BEAN_NAME = "OrganizationQueryManagerBean";

    public static final String BEAN_CONTEXT = "java:global/TaskManagerTest/";

    public static Logger log;

    static {
        log = Utilities.getLogger(JEEUtils.class);
    }

    public static EntityManager getEntityManager(String name) {
        try {
            InitialContext ic = new InitialContext();
            // return (EntityManager)
            // ic.lookup("java:comp/env/persistence/org");
            return (EntityManager) ic.lookup(PERSISTENCE_PREFIX + name);

        } catch (Exception ex) {
            log.error(ex.getMessage());

        }
        return null;
    }

    public static SessionContext getSessionContext() {
        try {
            InitialContext ic = new InitialContext();

            return (SessionContext) ic.lookup("java:comp/EJBContext");

        } catch (NamingException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    public static IOrganizationManager getOrganizationManagerBean() throws HumanTaskManagerException {
        try {
            InitialContext ic = new InitialContext();
            //return (IOrganizationManager) ic.lookup("java:comp/env/com.allweathercomputing.htm.org.OrganizationQueryManager");
            return (IOrganizationManager) ic.lookup(BEAN_CONTEXT + ORGANIZATION_MANAGER_BEAN_NAME);
        } catch (Exception e) {
            String message = "Cannot look up OrganizationManagerBean: " + e.getMessage();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    public static IOrganizationQueryManager getOrganizationQueryManagerBean() throws HumanTaskManagerException {
        try {
            InitialContext ic = new InitialContext();
            //return (IOrganizationManager) ic.lookup("java:comp/env/com.allweathercomputing.htm.org.OrganizationQueryManager");
            return (IOrganizationQueryManager) ic.lookup(BEAN_CONTEXT + ORGANIZATION_QUERY_MANAGER_BEAN_NAME);
        } catch (Exception e) {
            String message = "Cannot look up OrganizationManagerBean: " + e.getMessage();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

}
