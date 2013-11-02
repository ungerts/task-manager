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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.htm.ITaskClientInterface;
import com.htm.TaskClientInterfaceImpl;
import com.htm.TaskParentInterface;
import com.htm.TaskParentInterfaceImpl;
import com.htm.dm.EHumanRoles;
import com.htm.exceptions.AuthorizationException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.InvalidOperationException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.security.IUserManager;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskparent.TaskParentConnectorDummy;
import com.htm.taskparent.DummyCallbackHandler.TaskParentCallbackResponseContainer;
import com.htm.utils.Utilities;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring-beans.xml")
@Transactional
@Ignore
public class TaskClientInterfaceTest extends TaskParentInterfaceTest {

    protected ITaskClientInterface taskClient = null;

    protected IUserManager um = getUserManager();

    @Before
    public void init() throws HumanTaskManagerException, FileNotFoundException, SQLException, IOException {
        super.init();
        //cleanUp();
        /* Create test task models, logical people groups, user etc. */

        /* Init task client interface */
        taskClient = new TaskClientInterfaceImpl();
    }

    @Test
    public void testClaimAndRelease() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            /* Test claiming a task as potential owners */
            String userId = getExpectedPotentialOwners()[0];
            String tiid = createTaskInstanceDummy();
            testClaimOperation(tiid, userId, USER_PASSWORD);
            /* Test releasing a task (the potential owner which has claimed the task before is now actual owner) */
            testReleaseOperation(tiid, userId, USER_PASSWORD);
            /* Test claiming a task as business administrator */
            tiid = createTaskInstanceDummy();
            userId = getExpectedBusinessAdministrators()[0];
            testClaimOperation(tiid, userId, USER_PASSWORD);
            testReleaseOperation(tiid, userId, USER_PASSWORD);
            /* Test claiming a task as task stake holder */
            tiid = createTaskInstanceDummy();
            userId = getExpectedTaskStakeholders()[0];
            testClaimOperation(tiid, userId, USER_PASSWORD);
            testReleaseOperation(tiid, userId, USER_PASSWORD);


            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = InvalidOperationException.class)
    public void claimTwice() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();

            /* Try to claim the task instance twice -> an exception is expected */
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            testClaimOperation(tiid, userId, USER_PASSWORD);
            userId = getExpectedPotentialOwners()[1];
            testClaimOperation(tiid, userId, USER_PASSWORD);


            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = InvalidOperationException.class)
    public void releaseNonClaimedTaskInstance() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedBusinessAdministrators()[0];
            testReleaseOperation(tiid, userId, USER_PASSWORD);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

//	@Test  
//	public void startOperation4Ever() throws HumanTaskManagerException {
//		for (int i = 0; i < 1000; i++) {
//			System.out.println(i);
//			startOperation();
//		}
//	}

    @Test
    public void startOperation() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* Before starting the execution of a task instance it has to be claimed */
            taskClient.claim(tiid);
            taskClient.start(tiid);

            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            assertEquals(ETaskInstanceState.IN_PROGRESS, taskInstance.getStatus());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = AuthorizationException.class)
    public void startUnclaimedOperation() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* AuthorizationException expected */
            taskClient.start(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test
    public void stopOperation() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* Before starting the execution of a task instance it has to be claimed and started */
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.stop(tiid);

            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            assertEquals(ETaskInstanceState.RESERVED, taskInstance.getStatus());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void stopNotStartedTaskInstance() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* Before starting the execution of a task instance it has to be claimed and started */
            taskClient.claim(tiid);
            /* IllegalArgumentException expected */
            taskClient.stop(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test
    public void completeOperation() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* Before completing a task instance it has to be claimed and started */
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.complete(tiid, getOutputDataDummy());

            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            /* Check if the expected attributes in the task instance were set */
            assertEquals(ETaskInstanceState.COMPLETED, taskInstance.getStatus());
            assertEquals(getOutputDataDummy(), (String) taskInstance.getOutput());

            /*
                * The task parent is always called when a task instance was completed.
                * Check if the values that are passed to the task parent are valid.
                */
            assertEquals(ETaskInstanceState.COMPLETED, TaskParentCallbackResponseContainer.getState());
            assertCorrelationProperties(taskInstance.getCorrelationProperties(),
                    TaskParentCallbackResponseContainer.getCorrelationProperties());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void completeWithoutOutput() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* Before completing a task instance it has to be claimed and started */
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.complete(tiid, null);
            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = InvalidOperationException.class)
    public void completeNonStartedOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            /* Before completing a task instance it has to be claimed and started */
            taskClient.claim(tiid);

            /* InvalidArgumentException expected since task instance is not in state IN_PROGRESS */
            taskClient.complete(tiid, getOutputDataDummy());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void failOperation() throws HumanTaskManagerException {

        try {
            dataAccessRepository.beginTx();
            String expectedFaultName = "MyFault";
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            /* A task instance must be in the state in progress to fail thus
                * it has to be claimed and started before*/
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.fail(tiid, expectedFaultName, getFaultDataDummy());

            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            /* Check if the expected attributes in the task instance were set */
            assertEquals(ETaskInstanceState.FAILED, taskInstance.getStatus());
            assertEquals(expectedFaultName, taskInstance.getFault().getName());
            /* The fault data can be represented by an arbitrary object, here it is a string */
            assertEquals(getFaultDataDummy(), (String) taskInstance.getFault().getData());

            /*
                * The task parent is always called when a task instance failed.
                * Check if the state and the correlation properties that are passed to the task parent
                * are valid.
                */
            assertEquals(ETaskInstanceState.FAILED, TaskParentCallbackResponseContainer.getState());
            assertCorrelationProperties(taskInstance.getCorrelationProperties(),
                    TaskParentCallbackResponseContainer.getCorrelationProperties());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }

    }

    @Test(expected = InvalidOperationException.class)
    public void failNonStartedOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            String expectedFaultName = "MyFault";
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);

            /* An exception is expected if a fault is raised if the
                * task isn't in state 'In Progress' */
            taskClient.fail(tiid, expectedFaultName, getFaultDataDummy());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOperationWithoutFaultMsg() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            String expectedFaultName = "MyFault";
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);
            taskClient.start(tiid);

            /* InvalidArgumentException expected since fault message is null */
            taskClient.fail(tiid, expectedFaultName, null);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOperationWithoutFaultName() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);

            /* InvalidArgumentException expected since fault name is null */
            taskClient.fail(tiid, null, getFaultDataDummy());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void suspendAndResumeOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /*
                * 1. Suspend a task instance which
                * was created i.e. in state READY
                */
            testSuspendOperation(tiid, ETaskInstanceState.SUSPENDED_READY);
            /* Resume task instance */
            testResumeOperation(tiid, ETaskInstanceState.READY);

            /*
                * 2. Suspend a task instance which
                * was claimed i.e. in state RESERVED
                */
            taskClient.claim(tiid);
            testSuspendOperation(tiid, ETaskInstanceState.SUSPENDED_RESERVED);
            /* Resume task instance */
            testResumeOperation(tiid, ETaskInstanceState.RESERVED);

            /*
                * 3. Suspend a task instance which
                * was started i.e. in state IN_PROGRESS
                */
            taskClient.start(tiid);
            testSuspendOperation(tiid, ETaskInstanceState.SUSPENDED_IN_PROGRESS);
            /* Resume task instance */
            testResumeOperation(tiid, ETaskInstanceState.IN_PROGRESS);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    //TODO resume task instance that is suspended until a certain point of time
    //TODO illegal argument exception if a task is finished without output
    //TODO illegal argument exception if a task is fails without fault
    //TODO set priority

    @Test(expected = InvalidOperationException.class)
    public void suspendNotSuspendableTask() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.complete(tiid, getOutputDataDummy());
            /* Completed task instance can't  be suspended -> Exception expected */
            testSuspendOperation(tiid, ETaskInstanceState.SUSPENDED_IN_PROGRESS);
            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = InvalidOperationException.class)
    public void resumeNotSuspendedTask() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            /* Resume task instance that was never suspended -> Exception expected */
            testResumeOperation(tiid, ETaskInstanceState.READY);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void skipOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            /* Test skip operation as task initiator */
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedTaskInitiator();
            initSecurityContext(userId, USER_PASSWORD);
            testSkipOperation(tiid);

            /* Test skip operation as business administrator */
            tiid = createTaskInstanceDummy();
            userId = getExpectedBusinessAdministrators()[0];
            initSecurityContext(userId, USER_PASSWORD);
            testSkipOperation(tiid);

            /* Test skip operation as business administrator */
            tiid = createTaskInstanceDummy();
            userId = getExpectedTaskStakeholders()[0];
            initSecurityContext(userId, USER_PASSWORD);
            testSkipOperation(tiid);

            /* Test skip operation as actual owner */
            tiid = createTaskInstanceDummy();
            userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);
            testSkipOperation(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = InvalidOperationException.class)
    public void skipCompletedOperation() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];

            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);
            taskClient.start(tiid);
            taskClient.complete(tiid, getOutputDataDummy());
            /* Exception is expected since task client is already completed */
            testSkipOperation(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = InvalidOperationException.class)
    public void skipNonSkipableTask() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            /* Task instance must not be skip */
            taskInstance.setSkipable(false);
            String userId = getExpectedBusinessAdministrators()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* Exception is expected since a potential owner tries to skip
                * the task instance */
            testSkipOperation(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = AuthorizationException.class)
    public void skipOperationInvalidUser() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* Exception is expected since a potential owner tries to skip
                * the task instance */
            testSkipOperation(tiid);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void suspendUntilOperation() throws HumanTaskManagerException, InterruptedException {

        try {
            dataAccessRepository.beginTx();
            /* A completed operation can not be skipped */
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* The task is suspended for 5 seconds */
            long duration = (long) 5000;
            Timestamp suspendUntilTime = new Timestamp(Utilities.getCurrentTime().getTime() + duration);
            taskClient.suspendUntil(tiid, suspendUntilTime);

            /* Check if the task instance is suspended. */
            TaskInstanceView taskInstance = taskClient.getTaskInfo(tiid);
            assertEquals(ETaskInstanceState.SUSPENDED_READY.toString(),
                    taskInstance.getStatus());
            assertTrue(taskInstance.isSuspended());
            /* Wait until the set 'suspendUntil time' has expired and check
                * if the task instance is not suspended anymore */
            Thread.sleep(duration + 3000);
            /* The task instance view is detached i.e. means that it doesn't get
                * refreshed automatically. I new instance has to be fetched. */
            taskInstance = taskClient.getTaskInfo(tiid);
            assertEquals(ETaskInstanceState.READY.toString(), taskInstance.getStatus());
            assertTrue(!taskInstance.isSuspended());

            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = InvalidOperationException.class)
    public void suspendUntilWithASuspendedTask() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);


            long duration = (long) 50000;
            Timestamp suspendUntilTime = new Timestamp(Utilities.getCurrentTime().getTime() + duration);
            /* Suspend the task instance and then try to suspend it again. An
                * exception is expected */
            taskClient.suspendUntil(tiid, suspendUntilTime);
            /* Suspend again */
            taskClient.suspendUntil(tiid, suspendUntilTime);

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void addAndGetAttachment() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            String attachment1Name = "myAttachment1";

            String attachment1AccessType = IAttachment.ACCESS_TYPE_INLINE;
            String attachment1ContentType = "String";
            byte[] attachment1Content = Utilities.getBLOBFromString("Test content for junit attachment1");

            String attachment2Name = "myAttachment2";
            String attachment2AccessType = IAttachment.ACCESS_TYPE_REFERENCE;
            String attachment2ContentType = "URL";
            byte[] attachment2Content = Utilities.getBLOBFromString("http://www.htm.junit.attachment2.com");
            
            IAttachment attachment1 =
                    this.taskInstanceFactory.createAttachment(attachment1Name);
            attachment1.setAccessType(attachment1AccessType);
            attachment1.setContentType(attachment1ContentType);
            attachment1.setContent(attachment1Content);

            IAttachment attachment2 =
                    this.taskInstanceFactory.createAttachment(attachment2Name);
            attachment2.setAccessType(attachment2AccessType);
            attachment2.setContentType(attachment2ContentType);
            attachment2.setContent(attachment2Content);

            long timeBeforeAddingAttachments = Utilities.getCurrentTime().getTime();
            /* Add the attachments. Task instance has to be claimed before */
            taskClient.claim(tiid);
            taskClient.addAttachment(tiid, attachment1);
            taskClient.addAttachment(tiid, attachment2);

            long timeAfterAddingAttachments = Utilities.getCurrentTime().getTime();

            /* Get the attachment 1 */
            List<IAttachment> attachments = taskClient.getAttachments(tiid, attachment1Name);

            /* Only one attachment with that name expected */
            assertEquals(1, attachments.size());

            IAttachment resultAttachment1 = attachments.get(0);

            assertEquals(attachment1AccessType, resultAttachment1.getAccessType());
            assertEquals(attachment1ContentType, resultAttachment1.getContentType());
            assertEquals(Utilities.getStringFromBLOB(attachment1Content),
                    Utilities.getStringFromBLOB(resultAttachment1.getContent()));
            /*
                * Since we don't know the exact time when the attachment was added
                * we can only check if this point of time is within the interval.
                */
            assertTrue(timeBeforeAddingAttachments <= attachment1.getAttachedAt().getTime() &&
                    timeAfterAddingAttachments >= attachment1.getAttachedAt().getTime());
            assertEquals(userId, attachment1.getAttachedBy().getUserId());

            /* Get the attachment 2 */
            attachments = taskClient.getAttachments(tiid, attachment2Name);

            /* Only one attachment with that name expected */
            assertEquals(1, attachments.size());

            IAttachment resultAttachment2 = attachments.get(0);

            assertEquals(attachment2AccessType, resultAttachment2.getAccessType());
            assertEquals(attachment2ContentType, resultAttachment2.getContentType());
            assertEquals(Utilities.getStringFromBLOB(attachment2Content),
                    Utilities.getStringFromBLOB(resultAttachment2.getContent()));
            /*
                * Since we don't know the exact time when the attachment was added
                * we can only check if this point of time is within the interval.
                */
            assertTrue(timeBeforeAddingAttachments <= resultAttachment2.getAttachedAt().getTime() &&
                    timeAfterAddingAttachments >= resultAttachment2.getAttachedAt().getTime());
            assertEquals(userId, resultAttachment2.getAttachedBy().getUserId());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }
//	@Test
//	public void addAndDeleteAttachmentWrapper() throws HumanTaskManagerException {
//		int i = 0;
//		while (i < 10000) {
//			addAndDeleteAttachment();
//			System.out.println(i);
//			i++;
//		}
//	}

    @Test
    public void addAndDeleteAttachment() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedBusinessAdministrators()[0];
            initSecurityContext(userId, USER_PASSWORD);

            String attachmentName = "myAttachment1";

            String attachmentAccessType = IAttachment.ACCESS_TYPE_INLINE;
            String attachmentContentType = "String";
            byte[] attachmentContent = Utilities.getBLOBFromString("Test content for junit test: add and delete attachment");
            IAttachment attachment =
                    this.taskInstanceFactory.createAttachment(attachmentName);
            attachment.setAccessType(attachmentAccessType);
            attachment.setContentType(attachmentContentType);
            attachment.setContent(attachmentContent);

            /* Add attachment. */
            taskClient.addAttachment(tiid, attachment);
            /* Delete these attachment */
            taskClient.deleteAttachments(tiid, attachmentName);

            /* Check if they were really deleted */
            List<IAttachment> attachments = taskClient.getAttachments(tiid, attachmentName);
            assertEquals(0, attachments.size());

            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void setAndGetFault() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            IFault expectedFault = this.taskInstanceFactory.createFault("myFault", getFaultDataDummy());
            /* Task has to be claimed because only actual owners can set fault data */
            taskClient.claim(tiid);
            taskClient.setFault(tiid,
                    expectedFault.getName(), expectedFault.getData());

            /* Get fault from database and check if values are correct
                * Get it as business administrator */
            userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            IFault resultFault = taskClient.getFault(tiid);
            assertEquals(expectedFault.getName(), resultFault.getName());
            assertEquals((String) expectedFault.getData(), (String) resultFault.getData());
            dataAccessRepository.commitTx();
        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void setAndDeleteFault() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            IFault expectedFault = this.taskInstanceFactory.createFault("myFault", getFaultDataDummy());
            /* Task has to be claimed because only actual owners can set fault data */
            taskClient.claim(tiid);
            taskClient.setFault(tiid,
                    expectedFault.getName(), expectedFault.getData());

            /* Check if the fault was successfully stored */
            IFault resultFault = taskClient.getFault(tiid);
            assertNotNull(resultFault);

            /* Delete the fault */
            taskClient.deleteFault(tiid);


            resultFault = taskClient.getFault(tiid);
            assertNull(resultFault);
            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void setGetAndDeleteOutput() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* Task has to be claimed because only actual owners can set output data */
            taskClient.claim(tiid);
            taskClient.setOutput(tiid, getOutputDataDummy2());

            /* Check if the output was successfully stored */
            assertEquals(getOutputDataDummy2(),
                    taskClient.getOutput(tiid));

            /* Delete the output thus the output should be null */
            taskClient.deleteOutput(tiid);
            assertNull(taskClient.getOutput(tiid));

            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getInput() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedBusinessAdministrators()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* Check if returned input is correct, i.e. it has to be checked
                * if the input dummies are equal */
            assertTrue(getInputDataDummy().equals((Map<String, String>) taskClient.getInput(tiid)));

            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void getTaskDescription() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            String tiid = createTaskInstanceDummy();
            String userId = getExpectedBusinessAdministrators()[0];
            initSecurityContext(userId, USER_PASSWORD);

            /* Check if returned input is correct */
            String description = taskClient.getTaskDescription(tiid);
            assertEquals(PRESENTATION_DESC, description);

            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test
    public void query() throws HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            /*
                * Create three different task instances. Two of the are claimed by the same
                * user.
                */
            String tiid1 = createTaskInstanceDummy();
            String userId1 = getExpectedPotentialOwners()[0];
            initSecurityContext(userId1, USER_PASSWORD);
            taskClient.claim(tiid1);

            String tiid2 = createTaskInstanceDummy();
            String userId2 = getExpectedPotentialOwners()[0];
            initSecurityContext(userId2, USER_PASSWORD);
            taskClient.claim(tiid2);

            String tiid3 = createTaskInstanceDummy();
            String userId3 = getExpectedPotentialOwners()[1];
            initSecurityContext(userId3, USER_PASSWORD);
            taskClient.claim(tiid3);

            /* Get all work items that are claimed by a certain user */
            List<WorkItemView> workItemViews = taskClient.query(
                    "GENERICHUMANROLE='" + EHumanRoles.POTENTIAL_OWNER.toString() + "' AND ASSIGNEE='"
                            + userId1 + "' AND ISCLAIMED=1");

            /* Two work items expected that fit the search condition */
            assertEquals(2, workItemViews.size());

            assertEquals(userId1, workItemViews.get(0).getAssignee().getUserId());
            /* Actual owner is expected because the work item is claimed */
            assertEquals(EHumanRoles.ACTUAL_OWNER.toString(), workItemViews.get(0).getGenericHumanRole());
            assertTrue(workItemViews.get(0).isClaimed());

            assertEquals(userId1, workItemViews.get(1).getAssignee().getUserId());
            /* Actual owner is expected because the work item is claimed */
            assertEquals(EHumanRoles.ACTUAL_OWNER.toString(), workItemViews.get(1).getGenericHumanRole());
            assertTrue(workItemViews.get(1).isClaimed());


            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            e.printStackTrace();
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }
    }

    @Test(expected = InvalidOperationException.class)
    public void claimExpiredTask() throws InterruptedException, HumanTaskManagerException {
        try {
            dataAccessRepository.beginTx();
            /* The task expires within 5 seconds */
            long duration = (long) 5000;
            Timestamp expirationTime = new Timestamp(Utilities.getCurrentTime().getTime() + duration);
            log.debug(Utilities.formatTimestamp(Utilities.getCurrentTime()));
            String tiid = createTaskInstanceDummy(expirationTime);
            ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
            String userId = getExpectedBusinessAdministrators()[0];
            /* Task instance should not be expired yet */
            assertFalse(taskInstance.isExpired());
            /* Wait until the task instance has expired and check
                * if it is marked as expired */
            Thread.sleep(duration + 5000);
            assertTrue(taskInstance.isExpired());

            initSecurityContext(userId, USER_PASSWORD);
            /* Try to claim a task instance that is expired */
            taskClient.claim(tiid);

            dataAccessRepository.commitTx();

        } catch (HumanTaskManagerException e) {
            dataAccessRepository.rollbackTx();
            throw e;
        } finally {
            dataAccessRepository.close();
        }


    }

    protected String createTaskInstanceDummy() throws HumanTaskManagerException {
        return createTaskInstanceDummy(null);
    }


    protected String createTaskInstanceDummy(Timestamp expirationTime) throws HumanTaskManagerException {
        initSecurityContext(TASK_INITIATOR_USER_ID, TASK_INITIATOR_PASSWORD);

        TaskParentInterface partenInterface = this.taskParentInterface;

        return partenInterface.createTaskInstance(TaskParentConnectorDummy.TASK_PARENT_ID, getCorrelationPropertyDummies(),
                TASK_MODEL_DUMMY_NAME_1, TASK_INSTANCE_DUMMY_NAME1,
                getInputDataDummy(), getAttachmentDummies(),
                expirationTime);
    }


    protected void testSuspendOperation(String tiid, ETaskInstanceState suspendState) throws HumanTaskManagerException {
        taskClient.suspend(tiid);
        ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);

        assertEquals(suspendState, taskInstance.getStatus());
        assertTrue(taskInstance.isSuspended());
    }

    protected void testSkipOperation(String tiid) throws HumanTaskManagerException {
        taskClient.skip(tiid);
        ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);

        assertEquals(ETaskInstanceState.OBSOLETE, taskInstance.getStatus());
    }

    protected void testResumeOperation(String tiid, ETaskInstanceState nonSuspendedState) throws HumanTaskManagerException {
        taskClient.resume(tiid);
        ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);

        assertEquals(nonSuspendedState, taskInstance.getStatus());
        assertFalse(taskInstance.isSuspended());
    }

    protected void testClaimOperation(String tiid, String userId, String password) throws HumanTaskManagerException {

        initSecurityContext(userId, password);
        taskClient.claim(tiid);

        ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
        assertEquals(ETaskInstanceState.RESERVED.toString(), taskInstance.getStatus().toString());
        /* Owner must now be actual owner */
        assertEquals(userId, taskInstance.getActualOwner());
        assertTrue(taskInstance.isClaimed());

    }


    protected void testReleaseOperation(String tiid, String userId, String password) throws HumanTaskManagerException {
        initSecurityContext(userId, password);

        taskClient.release(tiid);

        ITaskInstance taskInstance = dataAccessRepository.getTaskInstance(tiid);
        assertEquals(ETaskInstanceState.READY.toString(), taskInstance.getStatus().toString());
        /* No actual owner must be set */

        assertTrue(taskInstance.getActualOwner() == null);

        /*
           * Check if the assigned users and their roles are correct.
           */

        /*
           * The users are determined while task instance creation using xpath
           * expressions. The expressions are defined in the methods
           * getBusinessAdminQuery, getExcludedOwnerQuery etc. of this class.
           * The evaluation context is defined in getInputDataDummy
           */
        assertEquals(TASK_INITIATOR_USER_ID, taskInstance.getTaskInitiator());
        assertUsers(getExpectedBusinessAdministrators(), taskInstance
                .getBusinessAdministrators());
        assertUsers(getExpectedTaskStakeholders(), taskInstance
                .getTaskStakeholders());
        /* potential owners are employees without interns */
        assertUsers(getExpectedPotentialOwners(), taskInstance
                .getPotentialOwners());

    }

}
