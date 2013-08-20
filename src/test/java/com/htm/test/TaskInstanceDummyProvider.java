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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.htm.TaskParentInterface;
import com.htm.TaskParentInterfaceImpl;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.IQuery;
import com.htm.security.UserManagerBasicImpl;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.taskparent.TaskParentConnectorDummy;
import com.htm.utils.Utilities;

public abstract class TaskInstanceDummyProvider extends TaskModelDummyProvider {

    public static final String TASK_INSTANCE_DUMMY_NAME1 = "taskInstance1";

    public static final String TASK_INITIATOR_USER_ID = UserManagerBasicImpl.DUMMY_ADMIN_USERNAME;

    public static final String TASK_INITIATOR_PASSWORD = UserManagerBasicImpl.DUMMY_ADMIN_PASSWORD;


    public void deleteTaskInstanceDummy(String name) throws DatabaseException {
        try {
            /* Create transaction boundaries */
            dap.beginTx();
            dap.deleteHumanTaskInstance(TASK_INSTANCE_DUMMY_NAME1);
            dap.commitTestCase();

        } catch (DatabaseException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.closeTestCase();
        }

    }

    protected String createTaskInstanceDummy() throws HumanTaskManagerException {
        return createTaskInstanceDummy(null);
    }


    protected String createTaskInstanceDummy(Timestamp expirationTime) throws HumanTaskManagerException {
        initSecurityContext(TASK_INITIATOR_USER_ID, TASK_INITIATOR_PASSWORD);

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

    protected Map<String, String> getInputDataDummy() {

        Map<String, String> groupsByRole = new HashMap<String, String>();
        groupsByRole.put("PotentialOwners", "employees");
        groupsByRole.put("ExcludedOwners", "interns");
        groupsByRole.put("BusinessAdmins", "managers");
        groupsByRole.put("TaskStakeHolders", "managers");

        return groupsByRole;

    }

    protected String getInputDataDummy2() {
        return "<testInput2>This is another test input message of a " +
                "human task instance</testInput2>";
    }

    protected String getOutputDataDummy() {
        return "<testOutput>This is a test output message of a human task instance</testOutput>";

    }

    protected String getOutputDataDummy2() {
        return "<testOutput2>This is another test output message of a human task instance</testOutput2>";

    }

    protected String getFaultDataDummy() {
        return "<testFault>This is a test fault message of a human task instance</testFault>";
    }

    protected Set<IAttachment> getAttachmentDummies() throws DatabaseException {
        Set<IAttachment> attachments = new HashSet<IAttachment>();

        IAttachment attachment = TaskInstanceFactory.newInstance().createAttachment("Attachment1");
        /* Add first attachment. It contains a simple String*/
        attachment.setAccessType(IAttachment.ACCESS_TYPE_INLINE);
        attachment.setContentType("Good old String");
        attachment.setContent(Utilities.getBLOBFromString("Test content for " + attachment.getName()));
        attachment.setAttachedAt(new Timestamp(Long.valueOf("1250045365812")));// Arbitrary time
        //	attachment.setAttachedBy(TaskInstanceFactory.newInstance().createAssignedUser(TASK_INITIATOR));

        attachments.add(attachment);

        /* Add second attachment. It accesses an imaginary resource via an URL (i.e. by reference)  */
        attachment = TaskInstanceFactory.newInstance().createAttachment("Attachment2");
        //	attachment.setAttachedBy(TaskInstanceFactory.newInstance().createAssignedUser(TASK_INITIATOR));
        attachment.setAccessType(IAttachment.ACCESS_TYPE_REFERENCE);
        attachment.setContentType("URL");
        attachment.setContent(Utilities.getBLOBFromString("http://htm." + attachment.getName() + ".com"));
        attachment.setAttachedAt(new Timestamp(Long.valueOf("1250045363814")));// Arbitrary time
        //attachment.setAttachedBy(TaskInstanceFactory.newInstance().createAssignedUser(TASK_INITIATOR));

        attachments.add(attachment);

        return attachments;
    }

    protected Set<ICorrelationProperty> getCorrelationPropertyDummies() {
        Set<ICorrelationProperty> corProperties = new HashSet<ICorrelationProperty>();

        /* Create first correlation property */
        ICorrelationProperty corProperty = TaskInstanceFactory.newInstance()
                .createCorrelationProperty("CorrelationProperty1");
        corProperty.setValue(Utilities.getXMLFromString("<value>"
                + corProperty.getName() + "</value>"));
        corProperties.add(corProperty);

        /* Create second correlation property */
        corProperty = TaskInstanceFactory.newInstance()
                .createCorrelationProperty("CorrelationProperty2");
        corProperty.setValue(Utilities.getXMLFromString("<value>"
                + corProperty.getName() + "</value>"));
        corProperties.add(corProperty);

        return corProperties;
    }


    @Override
    protected IQuery getCompleteByQuery() {
        return ModelElementFactory.newInstance().createQuery("/taskParentContext/properties/humanTask/infos/completeBy");
    }

    @Override
    protected IQuery getPriorityQuery() {
        return ModelElementFactory.newInstance().createQuery("/taskParentContext/properties/humanTask/infos/priority");
    }

    @Override
    protected IQuery getSkipableQuery() {
        return ModelElementFactory.newInstance().createQuery("/taskParentContext/properties/humanTask/infos/skipable");
    }

    @Override
    protected IQuery getStartByQuery() {
        return ModelElementFactory.newInstance().createQuery("/taskParentContext/properties/humanTask/infos/startBy");
    }


    @Override
    protected String getBusinessAdminQuery() {
        return "/input/BusinessAdmins";
    }

    @Override
    protected String getExcludedOwnersQuery() {
        return "/input/ExcludedOwners";
    }

    @Override
    protected String getPotentialOwnersQuery() {
        return "/input/PotentialOwners";
    }

    @Override
    protected String getTaskStakeholderQuery() {
        return "/input/TaskStakeHolders";
    }

    protected String[] getExpectedBusinessAdministrators() {
        return new String[]{"friedrth"};
    }

    protected String[] getExpectedTaskStakeholders() {
        return new String[]{"friedrth"};
    }

    protected String[] getExpectedPotentialOwners() {
        return new String[]{"edelmaal", "khalilno", "lokanava",
                "sundarvi", "friedrth"};
    }

    protected int getExpectedWorkItems(String tiid) {
        return getExpectedBusinessAdministrators().length +
                getExpectedTaskStakeholders().length +
                getExpectedPotentialOwners().length;
    }

    protected String getExpectedTaskInitiator() {
        return TASK_INITIATOR_USER_ID;
    }


}
