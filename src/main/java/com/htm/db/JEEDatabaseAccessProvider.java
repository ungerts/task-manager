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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.htm.dm.EHumanRoles;
import com.htm.entities.jpa.Assigneduser;
import com.htm.entities.jpa.Group;
import com.htm.entities.jpa.Humantaskinstance;
import com.htm.entities.jpa.Humantaskmodel;
import com.htm.entities.jpa.Logicalpeoplegroupdef;
import com.htm.entities.jpa.User;
import com.htm.entities.jpa.Workitem;
import com.htm.entities.jpa.Workitemtaskview;
import com.htm.exceptions.AuthorizationException;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskinstance.WorkItemFactory;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.userdirectory.IGroup;
import com.htm.userdirectory.IUser;
import com.htm.userdirectory.UserDirectoryFactory;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

public class JEEDatabaseAccessProvider implements IDataAccessProvider {

    // public static final boolean CLOSE_ON_COMMIT = true;
    // public static final boolean CLOSE_ON_ROLLBACK = true;

    public static final String NO_ACTIVE_TX_ERROR = "No active transaction could be found. "
            + "Start a transaction before executing a query";

    // private static JEEDatabaseAccessProvider dapInstance;

    protected EntityManager em;

    protected SessionContext ctx;

    // protected EntityManagerFactory emf;

    // protected EntityTransaction tx;

    protected Logger log;

    public JEEDatabaseAccessProvider(EntityManager em, SessionContext ctx) {
        // this.emf =
        // Persistence.createEntityManagerFactory("HumanTaskManager");
        this.em = em;
        this.ctx = ctx;
        this.log = Utilities.getLogger(this.getClass());
    }

    public JEEDatabaseAccessProvider() {
        this.log = Utilities.getLogger(this.getClass());
        // try {
        // InitialContext ctx = new InitialContext();
        // } catch (NamingException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        this.em = JEEUtils.getEntityManager(JEEUtils.PERSISTENCE_MANAGER_HTM);
        this.ctx = JEEUtils.getSessionContext();
    }

    public static JEEDatabaseAccessProvider newInstance() {

        return new JEEDatabaseAccessProvider();
    }

    // Only for testing purposes
    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#open()
      */
    public void open() {

        // should be open by default

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#beginTx()
      */
    public void beginTx() {
        // container-managed transaction already started
        /* Check if a transaction was already started */
        // if (!isTxActive()) {
        // /* Creates a new entity manager if the current one is closed */
        // open();
        // this.tx = this.em.getTransaction();
        // log.debug("Try to start transaction: " + tx.toString());
        // tx.begin();
        // }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#commitTx()
      */
    public void commitTx() throws DatabaseException {
        // commited by container...
        // try {
        // /* Only active transactions can be committed */
        // if (isTxActive()) {
        // tx.commit();
        // }
        //
        // } catch (Exception e) {
        // log.error(e.getMessage());
        // throw new DatabaseException(e);
        // }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#rollbackTx()
      */
    public void rollbackTx() {
        /* Only active transactions can be rolled back */
        // if (isTxActive()) {
        // tx.rollback();
        // }
        ctx.setRollbackOnly();
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#isActiveTx()
      */
    public boolean isTxActive() {
        // return this.tx != null && this.tx.isActive();
        return true;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#close()
      */
    public void close() {

        // container managed

        // try {
        // if (this.em.isOpen()) {
        // // EntityTransaction tx = this.em.getTransaction();
        // /*
        // * We have to explicitly rollback a transaction if it is still
        // * open otherwise the EntityManager is closed but not the
        // * underlying transaction.
        // */
        // if (isTxActive()) {
        // ctx.setRollbackOnly();
        // }
        // }
        // } finally {
        // if (this.em.isOpen())
        // this.em.close();
        // }
    }

    public Query createQuery(String query) {
        return em.createQuery(query);
    }

    protected void persist(Object entity) throws DatabaseException {

        try {
            if (isTxActive()) {
                em.persist(entity);
            } else {
                log.error(NO_ACTIVE_TX_ERROR);
                throw new DatabaseException(NO_ACTIVE_TX_ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DatabaseException(e);
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.db.DataAccessProvider#getLogicalPeopleGroupDef(java.lang.String)
      */
    public ILogicalPeopleGroupDef getLogicalPeopleGroupDef(String name)
            throws DatabaseException {
        /* Get the JPA model from the database */
        Logicalpeoplegroupdef lpgDefinitionEntity = getLogicalPeopleGroupDefEntity(name);

        /* Build model of LPG definition and return it */
        return ModelElementFactory.newInstance().createPeopleGroupDefinition(
                lpgDefinitionEntity);

    }

    public Logicalpeoplegroupdef getLogicalPeopleGroupDefEntity(String name)
            throws DatabaseException {
        Query query = em
                .createQuery("SELECT lpg FROM Logicalpeoplegroupdef lpg WHERE lpg.name = :lpgName");
        query.setParameter("lpgName", name);

        return (Logicalpeoplegroupdef) executeSingleResultQuery(query);
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#getHumanTaskModel(java.lang.String)
      */
    public ITaskModel getHumanTaskModel(String modelName)
            throws DatabaseException {

        /* Get the JPA task model from the database */
        Query query = em
                .createQuery("SELECT htm FROM Humantaskmodel htm WHERE htm.name = :modelName");
        query.setParameter("modelName", modelName);

        Humantaskmodel taskModelEntity = (Humantaskmodel) executeSingleResultQuery(query);

        /* Build model of the human task model and return it */
        return ModelElementFactory.newInstance().createTaskModel(
                taskModelEntity);

    }

    protected Object executeSingleResultQuery(Query query)
            throws DatabaseException {
        try {
            if (isTxActive()) {
                return query.getSingleResult();
            } else {
                throw new DatabaseException(NO_ACTIVE_TX_ERROR);
            }
        } catch (NoResultException e) {
            /*
                * If there are no queries which meet the query condition return
                * null
                */
            return null;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    protected List<?> executeMultipleResultQuery(Query query)
            throws DatabaseException {
        try {
            if (isTxActive()) {
                return query.getResultList();
            } else {
                throw new DatabaseException(NO_ACTIVE_TX_ERROR);
            }
        } catch (NoResultException e) {
            /*
                * If there are no queries which meet the query condition return
                * null
                */
            return null;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.db.DataAccessProvider#persistHumanTaskModel(com.htm.model.taskmodel
      * .ITaskModel)
      */
    public void persistTaskModel(ITaskModel taskModel) throws DatabaseException {
        persist(taskModel.getAdaptee());
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.db.DataAccessProvider#persistLogicalPeopleGroupDef(com.htm.model
      * .taskmodel.IPeopleGroupDef)
      */
    public void persistLogicalPeopleGroupDef(ILogicalPeopleGroupDef lpgDef)
            throws DatabaseException {
        // Query query =
        // em.createNativeQuery("SELECT * from INFORMATION_SCHEMA.TABLES");
        // query.getResultList();
        // ResultSet rs = query.getResultList();
        // while (rs.next()) {
        // String tableName = rs.getString("TABLE_NAME");
        // System.out.println(" " + tableName);
        // System.out.println(rs.getString("TABLE_SCHEM"));
        // }
        // executeMultipleResultQuery(query);
        persist(lpgDef.getAdaptee());

    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.db.DataAccessProvider#deleteLogicalPeopleGroupDef(java.lang.String
      * )
      */
    public boolean deleteLogicalPeopleGroupDef(String lpgDefName)
            throws DatabaseException {
        Query query = em
                .createQuery("DELETE FROM Logicalpeoplegroupdef lpg WHERE lpg.name = :lpgName");
        query.setParameter("lpgName", lpgDefName);

        return executeUpdate(query);
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#deleteHumanTaskModel(java.lang.String)
      */
    public boolean deleteHumanTaskModel(String modelName)
            throws DatabaseException {
        Query query = em
                .createQuery("DELETE FROM Humantaskmodel htm WHERE htm.name = :modelName");
        query.setParameter("modelName", modelName);

        return executeUpdate(query);

    }

    public boolean deleteHumanTaskInstance(String tiid)
            throws DatabaseException {
        Query query = em
                .createQuery("DELETE FROM Humantaskinstance hti WHERE hti.id = :tiid");
        query.setParameter("tiid", Utilities.transfrom2PrimaryKey(tiid));

        return executeUpdate(query);

    }

    public List<ITaskInstance> executeQuery(String query, int maxResults) {
        // Query queryd = em
        // .createQuery("SELECT ti FROM Humantaskinstance ti WHERE id = :tiid");
        // queryd.setParameter("tiid", null);queryd.
        // executeSingleResultQuery(queryd);
        return null;
    }

    protected boolean executeUpdate(Query query) throws DatabaseException {

        try {
            /*
                * executeUpdate returns the number of tuples that were deleted. If
                * there was at least one tuple deleted return true
                */

            if (isTxActive()) {
                return query.executeUpdate() > 0 ? true : false;
            } else {
                throw new DatabaseException(NO_ACTIVE_TX_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ITaskInstance getTaskInstance(String tiid) throws DatabaseException {

        try {
            Query query = em
                    .createQuery("SELECT hti FROM Humantaskinstance hti WHERE hti.id = :tiid");
            query.setParameter("tiid", Utilities.transfrom2PrimaryKey(tiid));

            /*
                * Task instance id (tiid) is unique thus only one result is
                * expected
                */
            Humantaskinstance taskInstanceEntity = (Humantaskinstance) executeSingleResultQuery(query);
            if (taskInstanceEntity == null) {
                log.error("Task instance can not be loaded from database. Human task instance ('"
                        + tiid + "') is null.");
                throw new DatabaseException(
                        "Task instance can not be loaded from database. Human task instance ('"
                                + tiid + "') is null.");
            }

            /* Build model of the human task instance and return it */
            return TaskInstanceFactory.newInstance()
                    .createTaskInstanceFromEntity(taskInstanceEntity);

        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Cannot fetch task instance '" + tiid + "'", e);
            throw new DatabaseException("Cannot fetch task instance '" + tiid
                    + "'", e);
        }
    }

    protected Assigneduser getAssignedUserEntity(String userid)
            throws DatabaseException {

        try {
            Query query = em
                    .createQuery("SELECT au FROM Assigneduser au WHERE au.userid = :userid");
            query.setParameter("userid", userid);

            /* user id is unique thus only one result expected */
            return (Assigneduser) executeSingleResultQuery(query);

        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public IWorkItem getWorkItem(String wiid) throws DatabaseException {

        try {
            Query query = em
                    .createQuery("SELECT wi FROM Workitem wi WHERE wi.id = :wiid");
            query.setParameter("wiid", Utilities.transfrom2PrimaryKey(wiid));

            Workitem workItemEntity = (Workitem) executeSingleResultQuery(query);

            /* Build model of the human task instance and return it */
            return WorkItemFactory.newInstance().createWorkItemFromEntity(
                    workItemEntity);

        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public List<IWorkItem> getWorkItems(String tiid) throws DatabaseException {
        try {

            Query query = em
                    .createQuery("SELECT wi FROM Workitem wi WHERE wi.humantaskinstance.id = :tiid");
            query.setParameter("tiid", Integer.valueOf(tiid));

            List<?> workItemEntities = (List<?>) executeMultipleResultQuery(query);

            /*
                * Create a list with work item models from the list of work item
                * entities
                */
            return createWorkItemModelsFromEntity(workItemEntities);

        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public List<IWorkItem> getWorkItems(ITaskInstance taskInstance)
            throws DatabaseException {
        try {
            /*
                * In the where clause of the query the human task instance JPA
                * entity has to be set not the tiid i.e. the task instance id. That
                * is different from native queries.
                */
            Query query = em
                    .createQuery("SELECT wi FROM Workitem wi WHERE wi.humantaskinstance= :tiid");
            query.setParameter("tiid", taskInstance.getAdaptee());

            List<?> workItemEntities = (List<?>) executeMultipleResultQuery(query);

            /*
                * Create a list with work item models from the list of work item
                * entities
                */
            return createWorkItemModelsFromEntity(workItemEntities);

        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    protected List<IWorkItem> createWorkItemModelsFromEntity(
            List<?> workItemEntities) {
        /* List that is to be returned. It contains the work items */
        List<IWorkItem> workItems = new ArrayList<IWorkItem>();

        if (workItemEntities != null) {
            Iterator<?> iter = workItemEntities.iterator();

            while (iter.hasNext()) {
                /*
                     * Build model of the work item and add it to the list that is
                     * to returned
                     */
                IWorkItem workItem = WorkItemFactory.newInstance()
                        .createWorkItemFromEntity((Workitem) iter.next());
                workItems.add(workItem);
            }
        }

        return workItems;

    }

    public List<IWorkItem> getWorkItems(String tiid, IAssignedUser user)
            throws DatabaseException {
        ITaskInstance taskInstance = getTaskInstance(tiid);
        if (taskInstance != null) {
            return getWorkItems(taskInstance, user);
        }
        /* Return empty work item model list when the task instance wasn't found */
        return new ArrayList<IWorkItem>();
    }

    public List<IWorkItem> getWorkItems(ITaskInstance taskInstance,
                                        IAssignedUser user) throws DatabaseException {

        /*
           * List that is returned. It contains the work items that are associated
           * to the given user and task instance.
           */
        List<IWorkItem> workitemsByUserAndTask = new ArrayList<IWorkItem>();
        List<IWorkItem> workitemsByTiid = getWorkItems(taskInstance);
        Iterator<IWorkItem> iter = workitemsByTiid.iterator();
        /*
           * Iterate over all work items and filter those which are not associated
           * to the given user
           */
        while (iter.hasNext()) {
            IWorkItem workItem = (IWorkItem) iter.next();

            IAssignedUser assignedUser = workItem.getAssignee();
            /*
                * A user is associated to a work item if either the work item is
                * associated to every user or if it is explicitly added to the user
                * specified by the parameter userid.
                */
            if ((assignedUser == null && workItem.isAssignedToEverybody())
                    || user.getUserId().equals(assignedUser.getUserId())) {
                workitemsByUserAndTask.add(workItem);
            }

        }
        return workitemsByUserAndTask;
    }

    public boolean deleteWorkItem(String wiid) throws DatabaseException {
        Query query = em
                .createQuery("DELETE FROM Workitem wi WHERE wi.id = :wiid");
        query.setParameter("wiid", Utilities.transfrom2PrimaryKey(wiid));

        return executeUpdate(query);
    }

    public boolean deleteAllWorkItems() throws DatabaseException {
        Query query = em.createQuery("DELETE FROM Workitem wi");
        return executeUpdate(query);
    }

    public boolean deleteAllTaskInstances() throws DatabaseException {
        Query query = em.createQuery("DELETE FROM Humantaskinstance hti");
        return executeUpdate(query);
    }

    public void persistHumanTaskInstance(ITaskInstance taskInstance)
            throws HumanTaskManagerException {
        /*
           * Check if there exists a work item for each user that added an
           * attachment to the task
           */
        // checkAttachmentAssigneesValid(taskInstance,
        // taskInstance.getAttachments());
        // TODO how to check for task initiator if task instance is associated
        // to work item
        persist(taskInstance.getAdaptee());
    }

    protected void checkAttachmentAssigneesValid(ITaskInstance taskInstance,
                                                 Set<IAttachment> attachments) throws HumanTaskManagerException {
        if (attachments != null) {
            /*
                * Iterate over the given attachment models and check for each model
                * if the respective assignee is associated with a task instance via
                * a work item.
                */
            Iterator<IAttachment> iter = attachments.iterator();
            while (iter.hasNext()) {
                IAttachment attachment = (IAttachment) iter.next();
                /*
                     * Get all the work items of the attachment's task instance
                     * where the assignee is associated to
                     */
                String assignee = attachment.getAttachedBy().getUserId();
                List<IWorkItem> workItems = getWorkItems(taskInstance,
                        attachment.getAttachedBy());
                /*
                     * If the list is empty the loop can be stopped since we have
                     * found an assignee which is not allowed to add an attachment
                     */
                if (workItems.isEmpty()) {
                    throw new AuthorizationException("User with user id "
                            + assignee
                            + " is not permitted to add attachment: "
                            + attachment.getName() + ". "
                            + "A user has to be assigned to a task instance "
                            + "for adding attachments.");
                }
            }
        }
    }

    public IAssignedUser getAssignedUser(String userid)
            throws DatabaseException {
        try {
            Query query = em
                    .createQuery("SELECT au FROM Assigneduser au WHERE au.userid = :userid");
            query.setParameter("userid", userid);

            /*
                * Since user ids are supposed to be unique only one result is
                * expected
                */
            Assigneduser assignedUserEntity = (Assigneduser) executeSingleResultQuery(query);

            if (assignedUserEntity != null) {
                return TaskInstanceFactory.newInstance()
                        .createAssignedUserFromEntity(assignedUserEntity);
            }

            return null;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public boolean assginedUserExists(String userid) throws DatabaseException {
        return getAssignedUser(userid) != null;
    }

    public void persistWorkItem(IWorkItem workItem) throws DatabaseException {
        persist(workItem.getAdaptee());
    }

    public void persistWorkItems(List<IWorkItem> workItems)
            throws DatabaseException {

        if (workItems != null) {
            Iterator<IWorkItem> iter = workItems.iterator();
            while (iter.hasNext()) {
                persistWorkItem((IWorkItem) iter.next());

            }
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#getHumanTaskModels()
      */
    public Map<String, ITaskModel> getHumanTaskModels()
            throws DatabaseException {

        /*
           * The map contain as key the name of the task model and as value the
           * actual model
           */
        Map<String, ITaskModel> htmModels = new HashMap<String, ITaskModel>();

        /* Get the JPA task model from the database */
        Query query = em.createQuery("SELECT htm FROM Humantaskmodel htm");

        List<?> taskModelEntities = executeMultipleResultQuery(query);

        if (taskModelEntities != null) {
            Iterator<?> iter = taskModelEntities.iterator();

            while (iter.hasNext()) {
                Humantaskmodel htmEntity = (Humantaskmodel) iter.next();
                /*
                     * Build model of the human task model and add it to the map
                     * along with the name of the model
                     */
                htmModels.put(htmEntity.getName(), ModelElementFactory
                        .newInstance().createTaskModel(htmEntity));
            }
        }

        return htmModels;
    }

    public Map<String, ILogicalPeopleGroupDef> getLogicalPeopleGroupDefs()
            throws DatabaseException {

        /*
           * The map contain as key the name of the LPG and as value the actual
           * model
           */
        Map<String, ILogicalPeopleGroupDef> lpgModels = new HashMap<String, ILogicalPeopleGroupDef>();

        /* Get the JPA LPG from the database */
        Query query = em
                .createQuery("SELECT lpg FROM Logicalpeoplegroupdef lpg");

        List<?> lpgEntities = executeMultipleResultQuery(query);

        if (lpgEntities != null) {
            Iterator<?> iter = lpgEntities.iterator();

            while (iter.hasNext()) {
                Logicalpeoplegroupdef lpgEntity = (Logicalpeoplegroupdef) iter
                        .next();
                /*
                     * Build model of the LPG and add it to the map along with the
                     * name of the model
                     */
                lpgModels.put(lpgEntity.getName(), ModelElementFactory
                        .newInstance().createPeopleGroupDefinition(lpgEntity));
            }
        }

        return lpgModels;
    }

    public IWorkItem getWorkItem(String tiid, IAssignedUser user,
                                 EHumanRoles role) throws DatabaseException {
        List<IWorkItem> workItems = getWorkItems(tiid, user);

        Iterator<IWorkItem> iter = workItems.iterator();

        /*
           * Iterate over all work items that assign the user and the task
           * instance and return the work item that fits to the specified human
           * role.
           */
        while (iter.hasNext()) {
            IWorkItem workItem = (IWorkItem) iter.next();
            /*
                * If a work item was found the loop can be interrupted since task
                * instance id, user id and human role uniquely identifies a work
                * item
                */
            if (workItem.getGenericHumanRole().equals(role)) {
                return workItem;
            }
        }

        return null;
    }

    public List<IWorkItem> query(String whereClause, int maxResults)
            throws DatabaseException {

        String queryString = "SELECT * FROM Workitemtaskview wiview ";

        /* If a where clause was specified attach it to the query */
        if (!StringUtils.isEmpty(whereClause)) {
            queryString += "where " + whereClause;
        }
        log.debug("Query Workitemtaskview - SQL Query : " + queryString);

        /*
           * Execute a query where Workitemtaskview objects are expected to be
           * returned
           */
        Query query = em.createNativeQuery(queryString, Workitemtaskview.class);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        List<?> workItemEntities = executeMultipleResultQuery(query);

        /* List that is to be returned. It contains work item models */
        List<IWorkItem> workItems = new ArrayList<IWorkItem>();

        if (workItemEntities != null) {
            /* Create a work item from each work item view entity */
            Iterator<?> iter = workItemEntities.iterator();
            while (iter.hasNext()) {
                Workitemtaskview viewEntity = (Workitemtaskview) iter.next();
                /*
                     * Get the underlying work item of the view entity
                     */
                IWorkItem workItem = getWorkItem(Integer.toString(viewEntity
                        .getWiid()));
                workItems.add(workItem);
            }
        }
        return workItems;
    }

    public List<IWorkItem> query(String whereClause) throws DatabaseException {
        return query(whereClause, -1);
    }

    /*
      * Methods for User directory
      */

    public Set<String> getUserIdsByGroup(String groupName)
            throws DatabaseException {

        try {
            Query query = em
                    .createQuery("SELECT gr FROM Group gr WHERE gr.groupname = :groupName");
            query.setParameter("groupName", groupName);

            /*
                * Since group names are supposed to be unique only one result is
                * expected
                */
            Group groupEntity = (Group) executeSingleResultQuery(query);

            /*
                * Get the users associated with the group and add their userIds to
                * the list of user ids.
                */
            Set<String> userIds = new HashSet<String>();
            List<User> users = groupEntity.getUsers();
            if (users != null) {
                Iterator<User> usersIter = users.iterator();
                while (usersIter.hasNext()) {
                    /* Add user ids */
                    userIds.add(usersIter.next().getUserid());
                }
            }
            return userIds;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public Set<String> getGroupNames() throws DatabaseException {

        try {
            Query query = em.createQuery("SELECT gr FROM Group gr");

            List<?> groupEntities = executeMultipleResultQuery(query);

            /*
                * Get the group entities and add their group name to the set of
                * goup names.
                */
            Set<String> groupIds = new HashSet<String>();

            if (groupEntities != null) {
                Iterator<?> usersIter = groupEntities.iterator();
                while (usersIter.hasNext()) {
                    /* Add group ids */
                    groupIds.add(((Group) usersIter.next()).getGroupname());
                }
            }
            return groupIds;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public void persistUser(IUser user) throws DatabaseException {
        persist(user.getAdaptee());
    }

    public IUser getUser(String userId) throws DatabaseException {
        try {
            Query query = em
                    .createQuery("SELECT user FROM User user WHERE user.userid = :userid");
            query.setParameter("userid", userId);

            /*
                * Since user ids are supposed to be unique only one result is
                * expected
                */
            User userEntity = (User) query.getSingleResult();

            if (userEntity != null) {
                return UserDirectoryFactory.newInstance().createUserFromEntity(
                        userEntity);
            }

            return null;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public boolean deleteUser(String userId) throws DatabaseException {
        log.debug("Trying to delete user with id: " + userId);
        Query query = em
                .createQuery("DELETE FROM User user WHERE user.userid = :userid");
        query.setParameter("userid", userId);
        boolean deleted = executeUpdate(query);
        log.debug("User deleted successfully: " + deleted);
        return deleted;
    }

    public void persistGroup(IGroup group) throws DatabaseException {
        persist(group.getAdaptee());
    }

    public void persistAssignedUser(IAssignedUser assignedUser)
            throws DatabaseException {
        persist(assignedUser.getAdaptee());
    }

    public IGroup getGroup(String groupName) throws DatabaseException {
        try {
            Query query = em
                    .createQuery("SELECT gr FROM Group gr WHERE gr.groupname = :groupName");
            query.setParameter("groupName", groupName);

            /*
                * Since group names are supposed to be unique only one result is
                * expected
                */
            Group groupEntity = (Group) query.getSingleResult();

            if (groupEntity != null) {
                return UserDirectoryFactory.newInstance()
                        .createGroupFromEntity(groupEntity);
            }

            return null;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public boolean deleteGroup(String groupName) throws DatabaseException {
        log.debug("Trying to delete group: " + groupName);
        Query query = em
                .createQuery("DELETE FROM Group gr WHERE gr.groupname = :groupname");
        query.setParameter("groupname", groupName);
        boolean deleted = executeUpdate(query);
        log.debug("Group deleted successfully: " + deleted);
        return deleted;
    }

    public List<ITaskInstance> getMyTasks(String genericHumanRole,
                                          Set<ETaskInstanceState> states, Timestamp createdOn,
                                          String whereClause) throws DatabaseException {
        // TODO Implement me
        // StringBuffer queryString = new StringBuffer(
        // "SELECT * FROM Workitemtaskview view ");
        // final String logicalOR = " OR ";
        //
        // if (genericHumanRole != null || (states != null && !states.isEmpty())
        // || createdOn != null || whereClause != null) {
        // queryString.append("where ");
        // if (genericHumanRole != null) {
        // queryString.append("genericHumanRole = " + genericHumanRole
        // + " ");
        // }
        //
        // if (states != null && !states.isEmpty()) {
        // /*
        // * The following loop creates a logical OR E.g. select * from
        // * table
        // */
        // queryString.append("AND ");// TODO nur end wenn vorher
        // // genericHumanRole gesetzt
        // Iterator<ETaskInstanceState> iter = states.iterator();
        // while (iter.hasNext()) {
        // ETaskInstanceState state = (ETaskInstanceState) iter.next();
        // queryString.append("status =" + state.toString() + " OR ");
        // }
        // /* Replace the last logical OR with a blank space */
        // queryString.replace(queryString.lastIndexOf(logicalOR),
        // queryString.length(), " ");
        // }
        // }

        return null;
    }

    public List<ITaskInstance> getNonFinalizedTaskInstances()
            throws DatabaseException {
        String statment = "Select * from Humantaskinstance where status not in "
                + "('"
                + ETaskInstanceState.COMPLETED.toString()
                + "', '"
                + ETaskInstanceState.ERROR
                + "', '"
                + ETaskInstanceState.FAILED
                + "', '"
                + ETaskInstanceState.EXITED
                + "', '"
                + ETaskInstanceState.OBSOLETE + "')";
        log.debug(statment);
        Query query = em.createNativeQuery(statment, Humantaskinstance.class);

        List<?> taskInstanceEntities = executeMultipleResultQuery(query);
        List<ITaskInstance> taskInstances = new ArrayList<ITaskInstance>();

        Iterator<?> iter = taskInstanceEntities.iterator();
        while (iter.hasNext()) {
            taskInstances.add(TaskInstanceFactory.newInstance()
                    .createTaskInstanceFromEntity(
                            (Humantaskinstance) iter.next()));
        }

        return taskInstances;
    }

}
