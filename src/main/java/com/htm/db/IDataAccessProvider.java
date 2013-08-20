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

package com.htm.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;

public interface IDataAccessProvider {

    public final static boolean JUNIT_TEST = true;


    public static class Factory {

        //private static IDataAccessProvider dap = null;

        public static IDataAccessProvider newInstance() {

//			/* Singleton - Only one instance of the data access provider can be created */
//			if (dap == null) {
//				if (JUNIT_TEST) {
////					BeanFactory beanFac = new XmlBeanFactory(new FileSystemResource(System.getProperty("user.dir")+"/src/test/resources/services-spring.xml"));
//					dap = new DataAccessProviderJpaJUnit(); 
//				} else {
//					dap = DatabaseAccessProviderJPA.newInstance();
//				}
//
//			}
//			return dap;
            // TODO introduce properties to maintain "lightness"
            return new JEEDatabaseAccessProvider();
        }
    }

    //Only for testing purposes
    public abstract void open();

    public abstract void beginTx();

    public abstract void commitTx() throws DatabaseException;

    public abstract void rollbackTx();

    public abstract boolean isTxActive();

    public abstract void close();

    public abstract ILogicalPeopleGroupDef getLogicalPeopleGroupDef(String name)
            throws DatabaseException;

    public Map<String, ILogicalPeopleGroupDef> getLogicalPeopleGroupDefs()
            throws DatabaseException;

    public abstract ITaskModel getHumanTaskModel(String modelName)
            throws DatabaseException;

    public abstract Map<String, ITaskModel> getHumanTaskModels()
            throws DatabaseException;

    public abstract ITaskInstance getTaskInstance(String tiid)
            throws DatabaseException, IllegalArgumentException;

    //public abstract Set<String> getHumanTaskInstanceIds(String modelName) throws DatabaseException;

    public abstract void persistTaskModel(ITaskModel taskModel) throws DatabaseException;

    public abstract void persistLogicalPeopleGroupDef(ILogicalPeopleGroupDef lpgDef) throws DatabaseException;

    public abstract boolean deleteLogicalPeopleGroupDef(String lpgDefName)
            throws DatabaseException;

    public abstract boolean deleteHumanTaskModel(String modelName)
            throws DatabaseException;

    public List<IWorkItem> getWorkItems(ITaskInstance taskInstance) throws DatabaseException;

    public List<IWorkItem> getWorkItems(String tiid) throws DatabaseException;

    public List<IWorkItem> getWorkItems(String tiid, IAssignedUser user) throws DatabaseException;

    public List<IWorkItem> getWorkItems(ITaskInstance taskInstance, IAssignedUser user) throws DatabaseException;

    public IWorkItem getWorkItem(String tiid, IAssignedUser user, EHumanRoles role) throws DatabaseException;

    public boolean deleteWorkItem(String wiid) throws DatabaseException;

    public abstract void persistHumanTaskInstance(ITaskInstance taskInstance) throws HumanTaskManagerException;

    public abstract boolean deleteHumanTaskInstance(String tiid) throws DatabaseException;

    public abstract void persistWorkItem(IWorkItem workItem) throws DatabaseException;

    public abstract void persistWorkItems(List<IWorkItem> workItems) throws DatabaseException;

    public abstract IAssignedUser getAssignedUser(String userid) throws DatabaseException;

    public boolean assginedUserExists(String userid) throws DatabaseException;

    //TODO add functionality for querying complex data (now only simple types are supported)
    public abstract List<IWorkItem> query(String whereClause, int maxResults) throws DatabaseException;

    public abstract List<IWorkItem> query(String whereClause) throws DatabaseException;

    public abstract List<ITaskInstance> getMyTasks(String genericHumanRole, Set<ETaskInstanceState> states, Timestamp createdOn, String whereClause) throws DatabaseException;

    /* Access to the user directory */

    public abstract Set<String> getUserIdsByGroup(String groupName) throws DatabaseException;

    public void persistUser(IUser user) throws DatabaseException;

    public IUser getUser(String userId) throws DatabaseException;

    public boolean deleteUser(String userId) throws DatabaseException;

    public void persistGroup(IGroup group) throws DatabaseException;

    public IGroup getGroup(String groupName) throws DatabaseException;

    public Set<String> getGroupNames() throws DatabaseException;

    public boolean deleteGroup(String groupName) throws DatabaseException;

    public List<ITaskInstance> getNonFinalizedTaskInstances() throws DatabaseException;
}