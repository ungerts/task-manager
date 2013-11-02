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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.htm.query.jxpath.XPathUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.htm.ITaskClientInterface;
import com.htm.TaskClientInterfaceImpl;
import com.htm.TaskParentInterface;
import com.htm.TaskParentInterfaceImpl;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskparent.TaskParentConnectorDummy;
import com.htm.taskparent.TaskParentContextDummy;
import com.htm.utils.Utilities;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring-beans.xml")
@Transactional
public class TaskParentInterfaceTest extends TaskInstanceDummyProvider {


    /**
     * Prepares the execution of a test case, i.e. the database is created
     * and test data like user, logical people groups and task models are added.
     *
     * @throws HumanTaskManagerException
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    @Before
    public void init() throws HumanTaskManagerException, FileNotFoundException, SQLException, IOException {

        try {
            cleanUp();
            /* Create transaction boundaries */
            dataAccessRepository.beginTx();
            /*
                * Create LPG definition dummies and the task model dummy where the
                * task instance is created from.
                */
            createPeopleGroupDefDummies();
            createTaskModelDummyLPG(TASK_MODEL_DUMMY_NAME_1);
            createTaskModelDummyLiterals(TASK_MODEL_DUMMY_NAME_2);
            /* Create dummy users and groups */
            createDummyUsersAndGroups();
            dataAccessRepository.commitTx();

        } catch (DatabaseException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }


    /**
     * Actually this method is not needed when using an in-memory database
     * since the database is recreated from the scratch for each test case.</br>
     * Nevertheless this method tests if the deletion of task models, user data and
     * logical people groups works. Furthermore when migrating to another database
     * this method might be required again.
     *
     * @throws HumanTaskManagerException
     */
    @After
    public void cleanUp() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            deleteModelDummies();
            deleteDummyUsersAndGroups();
            deleteInstanceData();
            dataAccessRepository.commitTx();
        } catch (DatabaseException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    //TODO test exit

    /**
     * Test the creation of a task instance.</br>
     * In the corresponding task model all properties are set (priority, skipable etc.).
     * For assigning people to the task instance only logical people groups are used.
     *
     * @throws HumanTaskManagerException
     */
    @Test
    public void createTaskInstance() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();

            /* Set the the user name and password in the security context of spring */
            initSecurityContext(TASK_INITIATOR_USER_ID, TASK_INITIATOR_PASSWORD);

            /* Time before creation required for assertions */
            long timeBeforeTiCreation = Calendar.getInstance()
                    .getTimeInMillis();

            Timestamp expirationTime = new Timestamp(Calendar.getInstance()
                    .getTimeInMillis() + 5000);
            TaskParentInterface partenInterface = this.taskParentInterface;
            /* Create task instance */
            String tiid = partenInterface.createTaskInstance(TaskParentConnectorDummy.TASK_PARENT_ID, getCorrelationPropertyDummies(),
                    TASK_MODEL_DUMMY_NAME_1, TASK_INSTANCE_DUMMY_NAME1,
                    getInputDataDummy(), getAttachmentDummies(), expirationTime);
            /* Time after creation required for assertions */
            long timeAfterTiCreation = Calendar.getInstance().getTimeInMillis();

            ITaskModel taskModel = dataAccessRepository.getHumanTaskModel(TASK_MODEL_DUMMY_NAME_1);
            ITaskInstance resultTaskInstance = dataAccessRepository.getTaskInstance(tiid);

            /*
                * Assertions
                */
            assertEquals(TASK_INSTANCE_DUMMY_NAME1, resultTaskInstance
                    .getName());
            assertEquals(ETaskInstanceState.READY, resultTaskInstance.getStatus());
            /*
                * Since we don't know exact time when the task instance was
                * transitioned to the READY state (i.e. activated) we have can only
                * specify an interval (it's the same with createdOn).
                */
            long activationTime = resultTaskInstance.getActivationTime()
                    .getTime();

            assertTrue(timeBeforeTiCreation <= activationTime
                    && activationTime <= timeAfterTiCreation);
            long createdOnTime = resultTaskInstance.getCreatedOn().getTime();
            assertTrue(timeBeforeTiCreation <= createdOnTime
                    && createdOnTime <= timeAfterTiCreation);
            assertEquals(expirationTime.getTime(), resultTaskInstance
                    .getExpirationTime().getTime());

            assertEquals(TaskParentConnectorDummy.TASK_PARENT_ID, resultTaskInstance.getTaskParentId());

            /*
                * Priority, startBy, completeBy and skipable is determined while
                * task instance creation using xpath expressions. xpath expressions
                * are defined in the methods getPriorityQuery, getSkipableQuery
                * etc. in the class TaskInstanceDummyProvider. The evaluation
                * context is defined in the class TaskParentContextDummy
                */
            assertEquals("Compare priority.", Integer.valueOf(TaskParentContextDummy.PRIORITY)
                    .intValue(), resultTaskInstance.getPriority());
            try {
                assertEquals(XPathUtils.getTimestampFromString(TaskParentContextDummy.STARTBY), resultTaskInstance
                        .getStartBy());
            } catch (Exception e) {
               fail(e.getMessage());
            }
            try {
                assertEquals(XPathUtils.getTimestampFromString(TaskParentContextDummy.COMPLETEBY), resultTaskInstance
                        .getCompleteBy());
            } catch (Exception e) {
                fail(e.getMessage());
            }
            assertEquals(new Boolean(TaskParentContextDummy.SKIPABLE),
                    resultTaskInstance.isSkipable());

            assertTrue(resultTaskInstance.getInput() instanceof Map<?, ?>);
            assertInputDummies(getInputDataDummy(), (Map<?, ?>) resultTaskInstance.getInput());

            /* The values for the presentation data that were set in the task model should be set here */
            assertEquals(taskModel.getPresentationModel().getTitle(),
                    resultTaskInstance.getPresentationName());
            assertEquals(taskModel.getPresentationModel().getSubject(),
                    resultTaskInstance.getPresentationSubject());
            assertEquals(taskModel.getPresentationModel().getDescription(),
                    resultTaskInstance.getPresentationDescription());
            /*
                * The users are determined while task instance creation using xpath
                * expressions. The expressions are defined in the methods
                * getBusinessAdminQuery, getExcludedOwnerQuery etc. of this class.
                * The evaluation context is defined in getInputDataDummy
                */
            assertEquals(TASK_INITIATOR_USER_ID, resultTaskInstance.getTaskInitiator());
            assertUsers(getExpectedBusinessAdministrators(), resultTaskInstance
                    .getBusinessAdministrators());
            assertUsers(getExpectedTaskStakeholders(), resultTaskInstance
                    .getTaskStakeholders());
            /* potential owners are employees without interns */
            assertUsers(getExpectedPotentialOwners(), resultTaskInstance
                    .getPotentialOwners());

            assertAttachments(TASK_INITIATOR_USER_ID, getAttachmentDummies(), resultTaskInstance
                    .getAttachments());
            assertCorrelationProperties(getCorrelationPropertyDummies(),
                    resultTaskInstance.getCorrelationProperties());

            assertEquals(ETaskInstanceState.READY, resultTaskInstance.getStatus());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }


    /**
     * Tests the creation of a task instance.</br>
     * Only literals are used to define business administrators, potential owners etc.</br>
     * Only required attributes were set in the task model thus default values or null values are used in the
     * task instance.
     *
     * @throws HumanTaskManagerException
     */
    @Test
    public void createTaskInstance1() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();

            TaskParentInterface partenInterface = this.taskParentInterface;

            /* Set the the user name and password in the security context */
            initSecurityContext(TASK_INITIATOR_USER_ID, TASK_INITIATOR_PASSWORD);

            /* Create task instance without input data, attachment, correlation properties */
            String tiid = partenInterface.createTaskInstance(TaskParentConnectorDummy.TASK_PARENT_ID, null,
                    TASK_MODEL_DUMMY_NAME_2, TASK_INSTANCE_DUMMY_NAME1,
                    null, null, null);

            dataAccessRepository.getHumanTaskModel(TASK_MODEL_DUMMY_NAME_1);
            ITaskInstance resultTaskInstance = dataAccessRepository.getTaskInstance(tiid);

            /*
                * Assertions
                */
            assertEquals(TASK_INSTANCE_DUMMY_NAME1, resultTaskInstance
                    .getName());
            assertEquals(ETaskInstanceState.READY, resultTaskInstance.getStatus());
            /* No timer were set in the task model thus the activation, expiration time attribute should be null.	*/
            //assertNull(resultTaskInstance.getActivationTime());TODO activation timer
            assertNull(resultTaskInstance.getExpirationTime());
            assertNull(resultTaskInstance.getStartBy());
            assertNull(resultTaskInstance.getCompleteBy());

            assertEquals(0, Integer.valueOf(resultTaskInstance.getPriority()).intValue());
            /* Since skipable attribute was not define it should be false by default */
            assertFalse(resultTaskInstance.isSkipable());

            /* No input and output message was set thus it should be null */
            assertNull(resultTaskInstance.getInput());
            assertNull(resultTaskInstance.getOutput());

            /* The values for the presentation data were not set as well */
            assertNull(resultTaskInstance.getPresentationName());
            assertNull(resultTaskInstance.getPresentationSubject());
            assertNull(resultTaskInstance.getPresentationDescription());
            /*
                * The users are determined while task instance creation using xpath
                * expressions. The expressions are defined in the methods
                * getBusinessAdminQuery, getExcludedOwnerQuery etc. of this class.
                * The evaluation context is defined in getInputDataDummy
                */
            assertEquals(TASK_INITIATOR_USER_ID, resultTaskInstance.getTaskInitiator());
            assertUsers(getExpectedBusinessAdministrators(), resultTaskInstance
                    .getBusinessAdministrators());
            assertUsers(getExpectedTaskStakeholders(), resultTaskInstance
                    .getTaskStakeholders());
            /* potential owners are employees without interns */
            assertUsers(getExpectedPotentialOwners(), resultTaskInstance
                    .getPotentialOwners());

            /* No attachments were set thus list should be empty */
            assertTrue(resultTaskInstance.getAttachments().isEmpty());
            /* No correlations properties were set thus it should be null */
            assertTrue(resultTaskInstance.getCorrelationProperties().isEmpty());

            assertEquals(ETaskInstanceState.READY, resultTaskInstance.getStatus());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }


    @Test
    public void exitOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();

            TaskParentInterface partenInterface = this.taskParentInterface;
            partenInterface.exit(tiid);

            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            assertEquals(ETaskInstanceState.EXITED, taskInstance.getStatus());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    /**
     * Tests if an exception is thrown when
     * the task parent tries to exit a task that is already in the state 'obsolete'.
     *
     * @throws HumanTaskManagerException
     */
    @Test(expected = IllegalArgumentException.class)
    public void exitObsoleteTaskInstance() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();

            TaskParentInterface partenInterface = this.taskParentInterface;

            /* The task instance has to be skipped to go into the obsolete state */
            ITaskClientInterface taskClient = this.taskClientInterface;
            this.taskClientInterface.skip(tiid);

            /* Then try to exit the skipped task instance, an exception is expected */
            partenInterface.exit(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }


    protected void deleteInstanceData() throws DatabaseException {
        dataAccessRepository.deleteAllWorkItems();
        dataAccessRepository.deleteAllTaskInstances();
    }

    protected void assertUsers(String[] expectedUserIds, Set<String> resultUserIds) {

        assertEquals(expectedUserIds.length, resultUserIds.size());

        /* Check for each user id in the expected array if it also exists in the result array */
        for (int i = 0; i < expectedUserIds.length; i++) {
            assertTrue("Result user id array does not equals expected user id array.", resultUserIds.contains(expectedUserIds[i]));
            resultUserIds.remove(expectedUserIds[i]);
        }
        /* If each expected user was mapped to a result user the list is empty */
        assertTrue(resultUserIds.isEmpty());

    }

    protected void assertCorrelationProperties(Set<ICorrelationProperty> expectedCorrelProperties, Set<ICorrelationProperty> resultCorelProperties) {
        assertNotNull(expectedCorrelProperties);
        assertNotNull(resultCorelProperties);
        assertEquals(expectedCorrelProperties.size(), resultCorelProperties.size());

        /* Put result correlation properties in a map where the key is the name and the value the correlation property model */
        Map<String, ICorrelationProperty> resultCorrelPropertiesMap = new HashMap<String, ICorrelationProperty>();
        Iterator<ICorrelationProperty> resultIter = resultCorelProperties.iterator();
        while (resultIter.hasNext()) {
            ICorrelationProperty resultCorrelProperty = (ICorrelationProperty) resultIter.next();
            resultCorrelPropertiesMap.put(resultCorrelProperty.getName(), resultCorrelProperty);

        }

        /* Check if the map of result correlation properties contains the expected correlation properties */
        Iterator<ICorrelationProperty> expectedIter = expectedCorrelProperties.iterator();
        while (expectedIter.hasNext()) {
            ICorrelationProperty expectedCorrelProperty = (ICorrelationProperty) expectedIter.next();
            ICorrelationProperty resultCorrelProperty = resultCorrelPropertiesMap.get(expectedCorrelProperty.getName());
            /* Check if correlation proprety exists */
            assertNotNull(resultCorrelProperty);
            /* Assert attributes of the correlation property */
            assertEquals(expectedCorrelProperty.getName(), resultCorrelProperty.getName());
            assertEquals(Utilities.getStringFromXMLDoc(expectedCorrelProperty.getValue()), Utilities.getStringFromXMLDoc(resultCorrelProperty.getValue()));

        }

    }

    protected void assertAttachments(String attachedBy, Set<IAttachment> expectedAttachments, Set<IAttachment> resultAttachments) {
        assertNotNull(expectedAttachments);
        assertNotNull(resultAttachments);
        assertEquals(expectedAttachments.size(), resultAttachments.size());

        /* Put result attachments in a map where the key is the name and the value the attachment model */
        Map<String, IAttachment> resultAttachmentMap = new HashMap<String, IAttachment>();
        Iterator<IAttachment> resultIter = resultAttachments.iterator();
        while (resultIter.hasNext()) {
            IAttachment resultAttachment = (IAttachment) resultIter.next();
            resultAttachmentMap.put(resultAttachment.getName(), resultAttachment);

        }

        /* Check if the map of result attachments contains the expected atttachment */
        Iterator<IAttachment> expectedIter = expectedAttachments.iterator();
        while (expectedIter.hasNext()) {
            IAttachment expectedAttachment = (IAttachment) expectedIter.next();
            IAttachment resultAttachment = resultAttachmentMap.get(expectedAttachment.getName());
            /* Check if attachment exists */
            assertNotNull(resultAttachment);
            assertEquals(expectedAttachment.getAccessType(), resultAttachment.getAccessType());
            assertEquals(expectedAttachment.getAttachedAt().getTime(), resultAttachment.getAttachedAt().getTime());
            assertEquals(attachedBy, resultAttachment.getAttachedBy().getUserId());
            assertArrayEquals(expectedAttachment.getContent(), resultAttachment.getContent());
            assertEquals(expectedAttachment.getContentType(), resultAttachment.getContentType());
        }

    }

    protected void assertInputDummies(Map<?, ?> expected, Map<?, ?> result) {

        /* If the reference dummy and this dummy contain a different amount of roles
           * they are unequal.
           */
        assertEquals(expected.size(), result.size());

        Set<?> resultKeys = result.keySet();
        Iterator<?> iter = resultKeys.iterator();

        /*
           * Check if the keys and the values of the result
           * and the expected map are the same.
           */
        while (iter.hasNext()) {

            Object resultKey = iter.next();
            Object resultValue = result.get(resultKey);
            /* Get the value form the expected map it should be the same as in the result map */
            Object expectedValue = expected.get(resultKey);

            assertNotNull(expectedValue);
            assertEquals(resultValue, resultValue);

        }
    }


}
