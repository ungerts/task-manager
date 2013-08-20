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

package com.htm.ejb;

import java.sql.Timestamp;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.htm.TaskClientInterfaceImpl;
import com.htm.TaskParentInterfaceImpl;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
//TODO Roles(Security)
@Stateless(name = "TaskParentBean")
@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskParentBean implements ITaskParentBean {

    private TaskClientInterfaceImpl taskClient;
    private Logger log;
    private TaskParentInterfaceImpl taskParent;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        this.taskClient = new TaskClientInterfaceImpl();
        this.taskParent = new TaskParentInterfaceImpl();
    }

    /* (non-Javadoc)
      * @see com.htm.ITaskParentBean#createTaskInstance(java.lang.String, java.util.Set, java.lang.String, java.lang.String, java.lang.Object, java.util.Set, java.sql.Timestamp)
      */
    @Override
    public String createTaskInstance(String taskParentId,
                                     Set<ICorrelationProperty> correlationProperties,
                                     String taskModelName, String taskInstanceName, Object inputData,
                                     Set<IAttachment> attachments, Timestamp expirationTime)
            throws HumanTaskManagerException {
        log.debug("createTaskInstance of model: " + taskModelName);
        //return null;
        return taskParent.createTaskInstance(taskParentId, correlationProperties, taskModelName, taskInstanceName, inputData, attachments, expirationTime);
    }


    /* (non-Javadoc)
      * @see com.htm.ITaskParentBean#exit(java.lang.String)
      */
    @Override
    public void exit(String tiid) throws HumanTaskManagerException {
        taskParent.exit(tiid);

    }

}
