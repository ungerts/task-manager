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

package com.htm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.htm.db.IDataAccessProvider;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.security.AuthorizationManager;
import com.htm.security.EActions;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.utils.Utilities;


public class TaskModelStore implements ITaskModelStore {

    //TODO Check credentials
    private Logger log = Utilities.getLogger(this.getClass());


    public void addTaskModel(ITaskModel taskModel) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.ADD_TASK_MODEL);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        try {
            dap.beginTx();
            dap.persistTaskModel(taskModel);
            dap.commitTx();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw new RuntimeException(e);//TODO

        } finally {
            dap.close();
        }
    }


    /**
     * Currently it can handle only Literals! TODO: change to deal with people queries as well
     */


    public boolean deleteTaskModel(String modelName, boolean forceDelete) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_TASK_MODEL);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        boolean isDeleted = false;
        try {
            dap.beginTx();
            ITaskModel taskModel = getTaskModel(modelName);

            /*
                * Task models can only be deleted if their are no running
                * instances or if forced deletion is enabled
                */
            if (taskModel != null) {
                if (!forceDelete && taskModel.isInstantiated()) {
                    String errorMsg = "Task model " + modelName
                            + " could not be deleted because there are "
                            + "still running task instances of this model.";
                    log.debug(errorMsg);
                    throw new RuntimeException(errorMsg);
                } else {
                    deleteTaskInstances(taskModel);
                    isDeleted = dap.deleteHumanTaskModel(modelName);
                }
            } else {
                log.debug("Task model " + modelName +
                        " could not be deleted since it wasn't found in the database.");
            }
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw new RuntimeException(e);// TODO
        } finally {
            dap.close();
        }
        return isDeleted;
    }

    protected void deleteTaskInstances(ITaskModel taskModel) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_TASK_INSTANCES);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        if (taskModel != null) {
            /* Delete all task instances that are instantiated from the task model.
                * This is a forced delete i.e. even if the task instance has not terminated
                * or completed it is deleted. */
            Set<ITaskInstance> taskInstances = taskModel.getTaskInstances();
            Iterator<ITaskInstance> iter = taskInstances.iterator();
            while (iter.hasNext()) {
                ITaskInstance taskInstance = iter.next();
                /* Work items that are associated to the task instance
                     * have to be deleted before task instance can be deleted */
                deleteWorkItems(taskInstance);
                dap.deleteHumanTaskInstance(taskInstance.getId());

            }
        }
    }

    protected void deleteWorkItems(ITaskInstance taskInstance)
            throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_WORK_ITEM);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        if (taskInstance != null) {
            /* Delete all work items that are associated to the task instance. */
            Set<IWorkItem> workItems = taskInstance.getWorkItems();
            Iterator<IWorkItem> iter = workItems.iterator();
            while (iter.hasNext()) {
                dap.deleteWorkItem(iter.next().getId());
            }
        }

    }

    public List<ITaskModel> getTaskModels() throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_TASK_MODELS);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        List<ITaskModel> taskModels = null;

        try {
            dap.beginTx();
            taskModels = new ArrayList<ITaskModel>(dap.getHumanTaskModels().values());
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
        } finally {
            dap.close();
        }

        return taskModels;
    }

    public Set<String> getTaskModelNames() throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_TASK_MODEL_NAMES);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        Set<String> taskModelNames = null;

        try {
            dap.beginTx();
            /* The key set of the map returned by getHumanTaskModels contains the names of all task models */
            taskModelNames = dap.getHumanTaskModels().keySet();
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
        } finally {
            dap.close();
        }

        return taskModelNames;
    }

    public boolean updateTaskModel(String modelName, ITaskModel newTaskModel) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.UPDATE_TASK_MODEL);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        try {
            /*
                * Update of the task model is simply done by
                * deleting it from the data store and creating
                * a new one with the same name.
                */
            dap.beginTx();

            /* Check if the task model that is to be updated exists */
            if (dap.getHumanTaskModel(modelName) == null) {
                String errorMsg = "A task model with the name '" + modelName +
                        "' doesn't exist in the task model store, thus is can not be updated";
                log.error(errorMsg);
                throw new HumanTaskManagerException(errorMsg);
            }


            boolean isDeleted = deleteTaskModel(modelName);
            /* Should always evaluate to true since it was checked
                * before if the task model exists. */
            if (isDeleted) {
                addTaskModel(newTaskModel);

                /* If there was no exception thrown
                     * the task model was successfully added.
                     */
                return true;
            }
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
        return false;
    }


    public void addLogicalPeopleGroupDef(ILogicalPeopleGroupDef lpgDef) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.ADD_LPG_DEFINITION);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        try {
            dap.beginTx();
            dap.persistLogicalPeopleGroupDef(lpgDef);
            dap.commitTx();
        } catch (DatabaseException e) {
            log.error(e.getMessage());
            dap.rollbackTx();
            throw new DatabaseException(e);
        } finally {
            dap.close();
        }
    }

    public boolean deleteLogicalPeopleGroup(String lpgDefName) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_LPG_DEFINITION);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        boolean isDeleted = false;
        try {
            dap.beginTx();
            ILogicalPeopleGroupDef lpgDef =
                    getLogicalPeopleGroupDef(lpgDefName);

            /* An LPG definition can only be deleted if there are no
                * people queries associated to it */
            if (lpgDef != null && !lpgDef.isAssociatedToPeopleQueries()) {
                //TODO consider raising an exception when there still people queries associated with the LPG def
                isDeleted = dap.deleteLogicalPeopleGroupDef(lpgDefName);
            }

            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw new RuntimeException(e);//TODO
        } finally {
            dap.close();
        }
        return isDeleted;


    }

    public boolean updateLogicalPeopleGroupDef(String lpgDefName, ILogicalPeopleGroupDef newLpgDef) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.UPDATE_LPG_DEFINITION);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        try {
            /*
                * Update of the LPG definition is simply done by
                * deleting it from the data store and creating
                * a new one with the same name.
                */
            dap.beginTx();
            boolean isDeleted =
                    deleteLogicalPeopleGroup(lpgDefName);
            if (isDeleted) {
                addLogicalPeopleGroupDef(newLpgDef);

                /* If there was no exception thrown
                     * the task model was successfully added.
                     */
                return true;
            }
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw new RuntimeException(e);//TODO
        } finally {
            dap.close();
        }
        return false;

    }

    public ITaskModel getTaskModel(String name) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_TASK_MODEL);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        ITaskModel taskModel = null;

        try {
            dap.beginTx();
            taskModel = dap.getHumanTaskModel(name);
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
        } finally {
            dap.close();
        }

        return taskModel;
    }


    public ILogicalPeopleGroupDef getLogicalPeopleGroupDef(String name) throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_LOGICAL_PEOPLE_GROUP_DEF);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        ILogicalPeopleGroupDef lpgDef = null;

        try {
            dap.beginTx();
            lpgDef = dap.getLogicalPeopleGroupDef(name);
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
        } finally {
            dap.close();
        }

        return lpgDef;
    }

    public List<ILogicalPeopleGroupDef> getLogicalPeopleGroupDefs() throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.GET_TASK_MODELS);
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        List<ILogicalPeopleGroupDef> lpgDefs = null;

        try {
            dap.beginTx();
            /* Get all LPG definitions and add them to the list */
            lpgDefs = new ArrayList<ILogicalPeopleGroupDef>(dap.getLogicalPeopleGroupDefs().values());
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
        } finally {
            dap.close();
        }

        return lpgDefs;
    }


    public boolean deleteTaskModel(String modelName)
            throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.DELETE_TASK_MODEL);
        return deleteTaskModel(modelName, false);
    }

    public boolean forceDeleteTaskModel(String modelName)
            throws HumanTaskManagerException {
        AuthorizationManager.authorizeAdministrativeAction(EActions.FORCE_DELETE_TASK_MODEL);
        return deleteTaskModel(modelName, true);
    }


}
