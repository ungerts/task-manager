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

package com.htm.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.htm.db.IDataAccessProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.htm.ITaskModelStore;
import com.htm.TaskModelStore;
import com.htm.db.DataAccessProviderJpaJUnit;
import com.htm.dm.EHumanRoles;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.IQuery;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.IPeopleQueryArgument;
import com.htm.taskmodel.IPresentationModel;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * A JUnit implementation of the DataAccessProvider has to be used since the tested methods
 * begin and commit transactions.
 *
 * @author sew71sgp
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring-beans.xml")
@Transactional
public class TaskModelRepositoryTest extends TaskModelDummyProvider {


    @Before
    public void before() throws HumanTaskManagerException, FileNotFoundException, SQLException, IOException {
        cleanUp();

        IDataAccessProvider dap = getTestDap();
        try {
            /* Create transaction boundaries */
            dap.beginTx();
            createPeopleGroupDefDummies();
            dap.commitTx();

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    @After
    public void cleanUp() throws HumanTaskManagerException {
        IDataAccessProvider dap = getTestDap();
        try {
            dap.beginTx();
            deleteModelDummies();
            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    /**
     * Tests functionality for adding multiple task models to the database.</br>
     * Method {@link ITaskModelStore#addTaskModel(ITaskModel)} is tested.</br>
     * Remark: The following methods are tested implicitly by the <code>before</code> or <code>cleanUp</code> method:</br>
     * {@link ITaskModelStore#deleteTaskModel(String)}</br>
     * </br>
     * {@link ITaskModelStore#deleteLogicalPeopleGroup(String)}</br>
     *
     * @throws DatabaseException
     * @see com.htm.test.TaskModelRepositoryTest
     */
    @Test
    public void test_addTaskModels() throws HumanTaskManagerException {
        IDataAccessProvider dap = getTestDap();
        try {
            dap.beginTx();

            ITaskModelStore taskModelStore = this.taskModelStore;

            ITaskModel dummyTaskModel1 = createTaskModelDummyLPG(TASK_MODEL_DUMMY_NAME_1);
            ITaskModel dummyTaskModel2 = createTaskModelDummyLiterals(TASK_MODEL_DUMMY_NAME_2);
            /* Persist task models */
            taskModelStore.addTaskModel(dummyTaskModel1);
            taskModelStore.addTaskModel(dummyTaskModel2);

            dap.commitTx();
        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }


    /**
     * Tests functionality for adding and fetching a task model from or to the database
     * respectively. </br>
     * Method <code>TaskModelStore.addTaskModel</code> is tested.</br>
     * Method <code>TaskModelStore.getTaskModel</code> is tested.</br>
     *
     * Remark: Method  <code>TaskModelStore.deleteTaskModel</code> is
     * tested implicitly by the <code>cleanUp</code> method.
     * @throws DatabaseException
     */
    /**
     * @throws DatabaseException
     */
    @Test
    public void test_getTaskModel() throws HumanTaskManagerException {

        IDataAccessProvider dap = getTestDap();
        try {
            dap.beginTx();

            /*
                * Add a dummy task model to the model store and after that fetch it
                * from the model store.
                */
            ITaskModelStore taskModelStore = this.taskModelStore;

            ITaskModel expectedTaskModel = createTaskModelDummyLPG(TASK_MODEL_DUMMY_NAME_1);
            taskModelStore.addTaskModel(expectedTaskModel);
            ITaskModel resultTaskModel = taskModelStore.getTaskModel(TASK_MODEL_DUMMY_NAME_1);

            dap.commitTx();

            /*
                * Check if the task model attributes of the dummy and the result
                * task model that was fetched from the database are the same
                */
            assertNotNull(resultTaskModel);
            assertEquals(expectedTaskModel.getName(), resultTaskModel.getName());
            assertEquals(expectedTaskModel.getPriority().getQuery(),
                    resultTaskModel.getPriority().getQuery());

            assertEquals(Utilities.getStringFromXMLDoc(expectedTaskModel
                    .getInputSchema()), Utilities
                    .getStringFromXMLDoc(resultTaskModel.getInputSchema()));
            assertEquals(Utilities.getStringFromXMLDoc(expectedTaskModel
                    .getOutputSchema()), Utilities
                    .getStringFromXMLDoc(resultTaskModel.getOutputSchema()));
            assertEquals(Utilities.getStringFromXMLDoc(expectedTaskModel.getFaultSchema()),
                    Utilities.getStringFromXMLDoc(resultTaskModel.getFaultSchema()));

            assertEquals(expectedTaskModel.getSkipable().getQuery(),
                    resultTaskModel.getSkipable().getQuery());
            assertEquals(expectedTaskModel.getStartBy().getQuery(),
                    resultTaskModel.getStartBy().getQuery());
            assertEquals(expectedTaskModel.getCompleteBy().getQuery(),
                    resultTaskModel.getCompleteBy().getQuery());

            /* Compare the people queries */
            assertPeopleGroups(expectedTaskModel.getBusinessAdminsQuery(),
                    resultTaskModel.getBusinessAdminsQuery());
            assertPeopleGroups(expectedTaskModel.getTaskStakeholdersQuery(),
                    resultTaskModel.getTaskStakeholdersQuery());
            assertPeopleGroups(expectedTaskModel.getPotentialOwnersQuery(),
                    resultTaskModel.getPotentialOwnersQuery());
            assertPeopleGroups(expectedTaskModel.getExcludedOwnersQuery(),
                    resultTaskModel.getExcludedOwnersQuery());

            /* Compare presentation information */
            IPresentationModel expectedPresentationModel = expectedTaskModel.getPresentationModel();
            IPresentationModel resultPresentationModel = resultTaskModel.getPresentationModel();
            assertNotNull(resultPresentationModel);
            assertEquals(expectedPresentationModel.getTitle(), resultPresentationModel.getTitle());
            assertEquals(expectedPresentationModel.getSubject(), resultPresentationModel.getSubject());
            assertEquals(expectedPresentationModel.getDescription(), resultPresentationModel.getDescription());

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    @Test
    public void test_getTaskModelWithLiterals() throws HumanTaskManagerException {

        IDataAccessProvider dap = getTestDap();
        try {
            dap.beginTx();

            /*
                * Add a dummy task model to the model store and after that fetch it
                * from the model store.
                */
            ITaskModelStore taskModelStore = this.taskModelStore;
            ITaskModel expectedTaskModel = createTaskModelDummyLiterals(TASK_MODEL_DUMMY_NAME_2);
            taskModelStore.addTaskModel(expectedTaskModel);
            ITaskModel resultTaskModel = taskModelStore.getTaskModel(TASK_MODEL_DUMMY_NAME_2);

            dap.commitTx();

            /*
                * Check if the task model attributes of the dummy and the result
                * task model that was fetched from the databse are the same
                */
            assertNotNull(resultTaskModel);
            assertEquals(expectedTaskModel.getName(), resultTaskModel.getName());

            /* Check if the expected and the result literals are correct */
            assertLiterals(expectedTaskModel.getBusinessAdminLiterals(),
                    resultTaskModel.getBusinessAdminLiterals(),
                    EHumanRoles.BUSINESS_ADMINISTRATOR);
            assertLiterals(expectedTaskModel.getTaskStakeholderLiterals(),
                    resultTaskModel.getTaskStakeholderLiterals(),
                    EHumanRoles.TASK_STAKEHOLDER);
            assertLiterals(expectedTaskModel.getPotentialOwnerLiterals(),
                    resultTaskModel.getPotentialOwnerLiterals(),
                    EHumanRoles.POTENTIAL_OWNER);
            assertLiterals(expectedTaskModel.getExcludedOwnerLiterals(),
                    resultTaskModel.getExcludedOwnerLiterals(),
                    EHumanRoles.EXCLUDED_OWNER);

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }

    }

    protected void assertLiterals(Set<ILiteral> expectedLiterals, Set<ILiteral> resultLiterals, EHumanRoles humanRole) {

        Iterator<ILiteral> expectedIter = expectedLiterals.iterator();
        Set<String> expectedOrgEntityIds = new HashSet<String>();

        /* For making it more easy to compare the sets of expected and result entities
           * the organizational entity ids of the expected set of literals with the correct human role are
           * copied to a set expectedOrgEntityIds.
           */
        while (expectedIter.hasNext()) {
            ILiteral expectedLiteral = (ILiteral) expectedIter.next();
            /* Only add the organizational entity ids with the given human role */
            if (expectedLiteral.getGenericHumanRole().equals(humanRole)) {
                expectedOrgEntityIds.add(
                        expectedLiteral.getOrganizationalEntityId());
            }
        }

        /* There must be same amount of expected organizationals entities like actual entities */
        assertEquals(expectedOrgEntityIds.size(), expectedLiterals.size());

        /* Here each organizational entity id is removed from the set expectedOrgEntityIds that
           * that has a corresponding literal in the set resultLiteral.
           * At the end of the loop the set expectedOrgEntityIds
           * should be empty if the expected literals match with the resulting literals.
           */
        Iterator<ILiteral> resultIter = resultLiterals.iterator();
        while (resultIter.hasNext()) {
            ILiteral resultLiteral = (ILiteral) resultIter.next();
            /* If a literal represents the same organizational entity (with the same role)
                * that exist in the set expectedOrgEntityIds it is removed from this set */
            if (resultLiteral.getGenericHumanRole().equals(humanRole)) {
                expectedOrgEntityIds.remove(resultLiteral.getOrganizationalEntityId());
            }
        }

        assertTrue(expectedOrgEntityIds.isEmpty());
    }

    protected void assertPeopleGroups(IPeopleAssignment expected, IPeopleAssignment result) {
        /* The people group definitions must be the same */
        assertEquals(expected.getBoundPeopleGroup().getName(), result.getBoundPeopleGroup().getName());

        Set<String> expectedArgumentNames = expected.getArgumentNames();
        Set<String> resultArgumentNames = result.getArgumentNames();

        /* The expected and result argument set must have the same size */
        assertEquals(expectedArgumentNames.size(), resultArgumentNames.size());

        Iterator<String> iter = expectedArgumentNames.iterator();

        while (iter.hasNext()) {
            String expectedArgumentName = iter.next();
            IPeopleQueryArgument expectedArgument = expected.getArgument(expectedArgumentName);
            IPeopleQueryArgument resultArgument = result.getArgument(expectedArgumentName);
            /* The result people query must contain the expected argument */
            assertNotNull(resultArgument);
            /* Compare the queries */
            assertEquals(expectedArgument.getName(), resultArgument.getName());
            assertEquals(expectedArgument.getValue().getQuery(), resultArgument.getValue().getQuery());
            assertEquals(expectedArgument.getValue().getQueryLanguage(), resultArgument.getValue().getQueryLanguage());
        }

    }


    public void updateTaskModel() {
        //TODO
    }

    @Override
    protected IQuery getCompleteByQuery() {
        return ModelElementFactory.newInstance().createQuery("completeByQuery");
    }

    @Override
    protected IQuery getPriorityQuery() {
        return ModelElementFactory.newInstance().createQuery("priorityQuery");
    }

    @Override
    protected IQuery getSkipableQuery() {
        return ModelElementFactory.newInstance().createQuery("skipableQuery");
    }

    @Override
    protected IQuery getStartByQuery() {
        return ModelElementFactory.newInstance().createQuery("startByQuery");
    }

    @Override
    protected String getBusinessAdminQuery() {
        return "businessAdminQuery";
    }

    @Override
    protected String getExcludedOwnersQuery() {
        return "excludedOwnersQuery";
    }

    @Override
    protected String getPotentialOwnersQuery() {
        return "potentialOwnersQuery";
    }

    @Override
    protected String getTaskStakeholderQuery() {
        return "taskStakeholderQuery";
    }


}
