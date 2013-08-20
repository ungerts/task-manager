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

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.htm.TaskModelStore;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
//TODO Roles(Security)
@Stateless(name = "TaskModelStoreBean")
@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
//public class TaskModelStoreBean implements ITaskModelStore {
public class TaskModelStoreBean implements ITaskModelStoreBean {

    private Logger log;
    private TaskModelStore taskModelStore;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        this.taskModelStore = new TaskModelStore();
    }

    @Override
    public void addLogicalPeopleGroupDef(ILogicalPeopleGroupDef lgpDef)
            throws HumanTaskManagerException {
        this.taskModelStore.addLogicalPeopleGroupDef(lgpDef);

    }

    @Override
    public boolean deleteLogicalPeopleGroup(String lpgDefName)
            throws HumanTaskManagerException {
        return this.taskModelStore.deleteLogicalPeopleGroup(lpgDefName);
    }

    @Override
    public boolean deleteTaskModel(String modelName)
            throws HumanTaskManagerException {
        return this.taskModelStore.deleteTaskModel(modelName);
    }

    @Override
    public boolean forceDeleteTaskModel(String modelName)
            throws HumanTaskManagerException {
        return this.taskModelStore.forceDeleteTaskModel(modelName);
    }

    @Override
    public void addTaskModel(ITaskModel taskModel)
            throws HumanTaskManagerException {
        log.debug("Trying to add task model. Name: " + taskModel.getName());
        this.taskModelStore.addTaskModel(taskModel);

    }

    @Override
    public boolean updateTaskModel(String name, ITaskModel newTaskModel)
            throws HumanTaskManagerException {
        return this.taskModelStore.updateTaskModel(name, newTaskModel);
    }

    @Override
    public List<ITaskModel> getTaskModels() throws HumanTaskManagerException {
        return this.taskModelStore.getTaskModels();
    }

    @Override
    public Set<String> getTaskModelNames() throws HumanTaskManagerException {
        return this.taskModelStore.getTaskModelNames();
    }

    @Override
    public ITaskModel getTaskModel(String name)
            throws HumanTaskManagerException {
        return this.taskModelStore.getTaskModel(name);
    }

    @Override
    public ILogicalPeopleGroupDef getLogicalPeopleGroupDef(String name)
            throws HumanTaskManagerException {
        return this.taskModelStore.getLogicalPeopleGroupDef(name);
    }

    @Override
    public List<ILogicalPeopleGroupDef> getLogicalPeopleGroupDefs()
            throws HumanTaskManagerException {
        return this.taskModelStore.getLogicalPeopleGroupDefs();
    }

}
