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

package com.shtm.test;

import java.sql.Timestamp;
import java.util.Map;

import com.htm.db.spring.DataAccessRepositoryCustom;
import junit.framework.Assert;

import com.htm.ITaskClientInterface;
import com.htm.TaskClientInterfaceImpl;
import com.htm.TaskParentInterface;
import com.htm.TaskParentInterfaceImpl;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskparent.TaskParentConnectorDummy;
import com.htm.test.TaskParentInterfaceTest;
import com.shtm.IStructuredTaskClientInterface;
import com.shtm.StructuredTaskClientInterfaceImpl;
import com.shtm.exceptions.SHTMException;
import com.shtm.operationAndStates.EStates;
import com.shtm.views.StructuredTaskInstanceView;
import org.junit.Ignore;

@Ignore
public class StructureDataTestUtilities extends TaskParentInterfaceTest {
    protected ITaskClientInterface taskClient = new TaskClientInterfaceImpl();
    public DataAccessRepositoryCustom dap;

    public StructureDataTestUtilities() {
        dap = super.dataAccessRepository;
    }

    public void createModell() throws HumanTaskManagerException {
        init();
    }

    public void init() throws HumanTaskManagerException {

        cleanUp();

        try {
            /* Create transaction boundaries */
            dap.beginTx();
            /*
                * Create LPG definition dummies and the task model dummy where the
                * task instance is created from.
                */
            createPeopleGroupDefDummies();
            createTaskModelDummyLPG(TASK_MODEL_DUMMY_NAME_1);
            createTaskModelDummyLiterals(TASK_MODEL_DUMMY_NAME_2);

            /* Create dummy users and groups */
            createDummyUsersAndGroups();

            dap.commitTx();

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    //User must be a potential owner and be able to create tasks!
    public void initUserSecurityContext() {
        String userId = getExpectedPotentialOwners()[0];
        initSecurityContext(userId,
                USER_PASSWORD);
    }

    public String createTaskInstanceDummy() throws HumanTaskManagerException {
        log.info("Create NormalTask");
        String tiid = createTaskInstanceDummy(null);
        return tiid;
    }


    protected String createTaskInstanceDummy(Timestamp expirationTime) throws HumanTaskManagerException {
        TaskParentInterface partenInterface = new TaskParentInterfaceImpl();

        return partenInterface.createTaskInstance(TaskParentConnectorDummy.TASK_PARENT_ID,
                getCorrelationPropertyDummies(),
                TASK_MODEL_DUMMY_NAME_1,
                TASK_INSTANCE_DUMMY_NAME1,
                getInputDataDummy(),
                null,
                //getAttachmentDummies(),
                expirationTime);
    }

    public void cleanUp() throws HumanTaskManagerException {

        try {
            IStructuredTaskClientInterface strTaskClient = new StructuredTaskClientInterfaceImpl();
            strTaskClient.printAndDeleteStructureDatas();

            dap.beginTx();
            deleteModelDummies();
            deleteDummyUsersAndGroups();
            deleteInstanceData();
            dap.commitTx();


        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } catch (SHTMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            dap.close();
        }

    }

    public void checkStructureTestState(
            Map<String, TestStructureData> ControllDatas, String tiid) {
        try {
            IStructuredTaskClientInterface strTaskClient = new StructuredTaskClientInterfaceImpl();
            StructuredTaskInstanceView task = strTaskClient.getStructuredTaskInfo(tiid);
            TestStructureData testData = ControllDatas.get(tiid);

            if (testData != null) {
                testData.checkTaskAssertions(task);
            } else {
                log.warn("No test data for Task " + tiid);
            }

            for (String controlledTaskId : task.getControlledTaskIds()) {
                checkStructureTestState(ControllDatas, controlledTaskId);
            }

            if (!task.isControlled()) {
                for (String subTaskId : task.getSubTaskIds()) {
                    checkStructureTestState(ControllDatas, subTaskId);
                }
            }
        } catch (HumanTaskManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void exitTask(String tiid) throws HumanTaskManagerException {
        try {
            /* Create transaction boundaries */
            dap.beginTx();
            TaskParentInterface partenInterface = new TaskParentInterfaceImpl();
            partenInterface.exit(tiid);

            dap.commitTx();

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.close();
        }
    }

    public void updateStructureTestState(
            Map<String, TestStructureData> ControllDatas,
            EStates targetState, String tiid) {
        try {
            IStructuredTaskClientInterface strTaskClient = new StructuredTaskClientInterfaceImpl();
            StructuredTaskInstanceView task = strTaskClient.getStructuredTaskInfo(tiid);
            TestStructureData testData = ControllDatas.get(tiid);

            if (testData == null) {
                ControllDatas.put(tiid, new TestStructureData(tiid, false,
                        false, targetState));
            } else {
                testData.setState(targetState);
            }

            for (String controlledTaskId : task.getControlledTaskIds()) {
                updateStructureTestState(ControllDatas, targetState,
                        controlledTaskId);
            }

            if (!task.isControlled()) {
                for (String subTaskId : task.getSubTaskIds()) {
                    updateStructureTestState(ControllDatas, targetState,
                            subTaskId);
                }
            }
        } catch (HumanTaskManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void controllallTestData(Map<String, TestStructureData> controllDatas) {
        for (String testDataId : controllDatas.keySet()) {
            Assert.assertTrue("Task " + testDataId + " Checked:", controllDatas
                    .get(testDataId).isChecked());
        }
    }
}
