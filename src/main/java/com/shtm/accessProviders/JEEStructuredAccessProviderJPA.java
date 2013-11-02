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

package com.shtm.accessProviders;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.WorkItemView;
//import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;
import com.shtm.exceptions.SHTMDataBaseException;
import com.shtm.structureClasses.StructureData;
import com.shtm.structureClasses.StructuredTask;
import com.shtm.structureClasses.TaskStructure;
import com.shtm.views.StructuredWorkItemView;

public class JEEStructuredAccessProviderJPA implements
        IStructuredTaskAccessProvider {

    public static final boolean CLOSE_ON_COMMIT = true;
    public static final boolean CLOSE_ON_ROLLBACK = true;

    public static final String NO_ACTIVE_TX_ERROR = "No active transaction could be found. "
            + "Start a transaction before executing a query";

    // private static JEEStructuredAccessProviderJPA sdapInstance;

    protected EntityManager em;

    // protected EntityManagerFactory emf;

    // protected EntityTransaction tx;

    protected Logger log;

    //private SessionContext ctx;

    //protected JEEStructuredAccessProviderJPA() {
    //    // this.emf = Persistence
    //    // .createEntityManagerFactory("StructureDataManager");
    //    this.em = JEEUtils.getEntityManager(JEEUtils.PERSISTENCE_MANAGER_SHTM);
    //    this.ctx = JEEUtils.getSessionContext();
    //    this.log = Utilities.getLogger(this.getClass());
    //}

    public static JEEStructuredAccessProviderJPA newInstance() {

        // if (sdapInstance == null) {
        // sdapInstance = new JEEStructuredAccessProviderJPA();
        // }

        return new JEEStructuredAccessProviderJPA();
    }

    // Only for testing purposes
    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#open()
      */
    public void open() {

        // should be open by default (container)
        // if (!this.em.isOpen()) {
        // this.em = emf.createEntityManager();
        // }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#beginTx()
      */
    public void beginTx() {
        // TA-Begin executed by container...
        /* Check if a transaction was already started */
        // if (!isTxActive()) {
        // /* Creates a new entity manager if the current one is closed */
        // open();
        // this.tx = this.em.getTransaction();
        // tx.begin();
        // }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#commitTx()
      */
    public void commitTx() throws SHTMDataBaseException {

        // TA is committed by container

        // try {
        // /* Only active transactions can be committed */
        // if (isTxActive()) {
        // tx.commit();
        // }
        //
        // } catch (Exception e) {
        // log.error(e.getMessage());
        // throw new SHTMDataBaseException(e);
        // }

    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#rollbackTx()
      */
    public void rollbackTx() {

        // TODO reduce code as isTXActive() always returns true

        /* Only active transactions can be rolled back */
        if (isTxActive()) {
        //    ctx.setRollbackOnly();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#isActiveTx()
      */
    public boolean isTxActive() {
        // return this.tx != null && this.tx.isActive();
        // should be active by default
        return true;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.db.DataAccessProvider#close()
      */
    public void close() {

        // Container-managed

        // try {
        // if (this.em.isOpen()) {
        // // EntityTransaction tx = this.em.getTransaction();
        // /*
        // * We have to explicitly rollback a transaction if it is still
        // * open otherwise the EntityManager is closed but not the
        // * underlying transaction.
        // */
        // if (isTxActive()) {
        // tx.rollback();
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

    protected void persist(Object entity) throws SHTMDataBaseException {
        // TODO reduce code as isActive always returns true
        try {
            //	if (tx.isActive()) {
            em.persist(entity);
            //	} else {
            //		log.error(NO_ACTIVE_TX_ERROR);
            //		throw new SHTMDataBaseException(NO_ACTIVE_TX_ERROR);
            //	}
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SHTMDataBaseException(e);
        }

    }

    public void persistStructureData(StructureData strData)
            throws SHTMDataBaseException {
        persist(strData);
    }

    public StructureData getStructureDataByStructureId(String tiid)
            throws SHTMDataBaseException {
        Query query = em
                .createQuery("SELECT sd FROM StructureData sd WHERE sd.id = :tiid");
        query.setParameter("tiid", Integer.parseInt(tiid));

        /*
           * Since strucureDatas are supposed to be unique only one result is
           * expected
           */
        if (query.getResultList().isEmpty()) {
            return null;
        } else {
            StructureData strData = (StructureData) query.getSingleResult();
            return strData;
        }
    }

    public StructureData getStructureDataByTaskId(String tiid)
            throws SHTMDataBaseException {
        Query query = em
                .createQuery("SELECT sd FROM StructureData sd WHERE sd.task_id = :tiid");
        query.setParameter("tiid", Integer.parseInt(tiid));

        /*
           * Since strucureDatas are supposed to be unique only one result is
           * expected
           */
        if (query.getResultList().isEmpty()) {
            return null;
        } else {
            StructureData strData = (StructureData) query.getSingleResult();
            return strData;
        }
    }

    public void deleteStructureDatas() {
        Query q = em.createQuery("DELETE FROM StructureData au");
        q.executeUpdate();
    }

    public void printAllStructureDatas() {
        Query q = em.createQuery("DELETE FROM StructureData au");
        q.executeUpdate();
    }

    public List<StructuredTask> getAllStructureData() {

        Query query = em
                .createQuery("SELECT sd FROM StructureData sd ORDER BY sd.task_id");
        List<StructuredTask> dataList = new ArrayList<StructuredTask>();

        for (Object entry : query.getResultList()) {
            StructureData strData = (StructureData) entry;
            dataList.add(new StructuredTask(strData));
        }

        return dataList;
    }

    @SuppressWarnings("unchecked")
    public List<StructuredWorkItemView> query(String whereClause,
                                              String structureWhereClause, int maxResults)
            throws HumanTaskManagerException {
        List<StructuredWorkItemView> resultList = new ArrayList<StructuredWorkItemView>();

        String queryString = "SELECT * FROM StructureData ";

        /* If a where clause was specified attach it to the query */
        if (structureWhereClause != null) {
            queryString += " WHERE " + structureWhereClause;

            log.debug("Query StructureData - SQL Query : " + queryString);
            Query query = em
                    .createNativeQuery(queryString, StructureData.class);
            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }

            List<StructureData> structureDatas = (List<StructureData>) executeMultipleResultQuery(query);
            String taskIds = " AND tiid in (";

            for (int i = 0; i < structureDatas.size(); i++) {
                taskIds = taskIds + structureDatas.get(i).getTask_id();
                if (i < structureDatas.size() - 1) {
                    taskIds = taskIds + ", ";
                }
            }
            taskIds = taskIds + ")";
            whereClause = whereClause + taskIds;
        }

        List<WorkItemView> workItems;

        workItems = HTMTaskClientAccessProvider.getTaskClientInstance().query(
                whereClause, maxResults);

        for (WorkItemView workItem : workItems) {
            TaskStructure strData = new TaskStructure(workItem
                    .getTaskInstance().getId());
            resultList.add(new StructuredWorkItemView(workItem, strData));
        }

        return resultList;
    }

    protected List<?> executeMultipleResultQuery(Query query)
            throws SHTMDataBaseException {
        try {
            // TA is container-provided
            //if (tx.isActive()) {
            return query.getResultList();
            //	} else {
            //		throw new SHTMDataBaseException(NO_ACTIVE_TX_ERROR);
            //	}
        } catch (NoResultException e) {
            /*
                * If there are no queries which meet the query condition return
                * null
                */
            return null;
        } catch (Exception e) {
            throw new SHTMDataBaseException(e);
        }
    }

}
