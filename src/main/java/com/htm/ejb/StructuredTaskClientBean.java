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

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;
import com.shtm.StructuredTaskClientInterfaceImpl;
import com.shtm.exceptions.SHTMDataBaseException;
import com.shtm.exceptions.SHTMException;
import com.shtm.structureClasses.ResultStructure;
import com.shtm.views.StructuredTaskInstanceView;
import com.shtm.views.StructuredWorkItemView;

//@LocalBean
@Stateless(name = "StructuredTaskClientBean")
@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// TODO Roles(Security)
public class StructuredTaskClientBean implements IStructuredTaskClientBean {

    private Logger log;

    // TODO does this solution cause problems with the entity manager?
    private StructuredTaskClientInterfaceImpl structuredTaskClient;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        this.structuredTaskClient = new StructuredTaskClientInterfaceImpl();
    }

    @Override
    public void claim(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.claim(tiid);

    }

    @Override
    public void start(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.start(tiid);

    }

    @Override
    public void stop(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.stop(tiid);

    }

    @Override
    public void release(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.release(tiid);

    }

    @Override
    public void suspend(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.suspend(tiid);

    }

    @Override
    public void forward(String tiid, String forwardeeId)
            throws HumanTaskManagerException {
        this.structuredTaskClient.forward(tiid, forwardeeId);

    }

    @Override
    public void suspendUntil(String tiid, Timestamp pointOfTime)
            throws HumanTaskManagerException {
        this.structuredTaskClient.suspendUntil(tiid, pointOfTime);

    }

    @Override
    public void resume(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.resume(tiid);

    }

    @Override
    public void complete(String tiid, Object outputData)
            throws IllegalArgumentException, HumanTaskManagerException {
        this.structuredTaskClient.complete(tiid, outputData);

    }

    @Override
    public void fail(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        this.structuredTaskClient.fail(tiid, faultName, faultData);

    }

    @Override
    public void skip(String tiid) throws java.lang.IllegalArgumentException,
            HumanTaskManagerException {
        this.structuredTaskClient.skip(tiid);

    }

    @Override
    public void setPriority(String tiid, int priority)
            throws HumanTaskManagerException {
        this.structuredTaskClient.setPriority(tiid, priority);

    }

    @Override
    public void addAttachment(String tiid, IAttachment attachment)
            throws HumanTaskManagerException {
        this.structuredTaskClient.addAttachment(tiid, attachment);

    }

    @Override
    public List<IAttachment> getAttachments(String tiid, String name)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getAttachments(tiid, name);
    }

    @Override
    public boolean deleteAttachments(String tiid, String name)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.deleteAttachments(tiid, name);
    }

    @Override
    public TaskInstanceView getTaskInfo(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getTaskInfo(tiid);
    }

    @Override
    public String getTaskDescription(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getTaskDescription(tiid);
    }

    @Override
    public void setOutput(String tiid, Object output)
            throws HumanTaskManagerException {
        this.structuredTaskClient.setOutput(tiid, output);

    }

    @Override
    public void deleteOutput(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.deleteOutput(tiid);

    }

    @Override
    public void setFault(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        this.structuredTaskClient.setFault(tiid, faultName, faultData);

    }

    @Override
    public void deleteFault(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.deleteFault(tiid);

    }

    @Override
    public Object getInput(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getFault(tiid);
    }

    @Override
    public Object getOutput(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getOutput(tiid);
    }

    @Override
    public IFault getFault(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getFault(tiid);
    }

    @Override
    public List<WorkItemView> query(String whereClause)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.query(whereClause);
    }

    @Override
    public List<WorkItemView> query(String whereClause, int maxResults)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.query(whereClause, maxResults);
    }

    @Override
    public String addSubTask(String tiid, String taskModelName,
                             String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.addSubTask(tiid, taskModelName, taskInstanceName, inputData);
    }

    @Override
    public void removeSubTask(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.removeSubTask(tiid);

    }

    @Override
    public String mergeTasks(String tiid1, String tiid2, String taskModelName,
                             String taskInstanceName, Object inputData)
            throws HumanTaskManagerException {
        return this.structuredTaskClient.mergeTasks(tiid1, tiid2, taskModelName, taskInstanceName, inputData);
    }

    @Override
    public void unmerge(String tiid) throws HumanTaskManagerException {
        this.structuredTaskClient.unmerge(tiid);

    }

    @Override
    public String getParentTask(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getParentTask(tiid);
    }

    @Override
    public List<String> getSubTasks(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getSubTasks(tiid);
    }

    @Override
    public String getSubTaskByStructureNr(String tiid, int strNr)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getSubTaskByStructureNr(tiid, strNr);
    }

    @Override
    public String getMergeTask(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getMergeTask(tiid);
    }

    @Override
    public List<String> getControlledTasks(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getControlledTasks(tiid);
    }

    @Override
    public ResultStructure<String, Object> getInputStructure(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getInputStructure(tiid);
    }

    @Override
    public ResultStructure<String, Object> getOutputStructure(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getOutputStructure(tiid);
    }

    @Override
    public ResultStructure<String, IFault> getFaultStructure(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getFaultStructure(tiid);
    }

    @Override
    public ResultStructure<String, List<IAttachment>> getAttachmentStructure(
            String tiid, String name) throws HumanTaskManagerException {

        return this.structuredTaskClient.getAttachmentStructure(tiid, name);
    }

    @Override
    public void setStructureNr(String tiid, int strNr)
            throws HumanTaskManagerException {
        this.structuredTaskClient.setStructureNr(tiid, strNr);

    }

    @Override
    public int getStructureNr(String tiid) throws HumanTaskManagerException {

        return this.structuredTaskClient.getStructureNr(tiid);
    }

    @Override
    public StructuredTaskInstanceView getStructuredTaskInfo(String tiid)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.getStructuredTaskInfo(tiid);
    }

    @Override
    public List<StructuredWorkItemView> query(String whereClause,
                                              String structureWhereClause, int max)
            throws HumanTaskManagerException {

        return this.structuredTaskClient.query(whereClause, structureWhereClause, max);
    }

    @Override
    public void printStructureInfos(String tiid, boolean shortForm)
            throws HumanTaskManagerException {
        this.structuredTaskClient.printStructureInfos(tiid, shortForm);

    }

    @Override
    public void printAndDeleteStructureDatas() throws SHTMDataBaseException,
            SHTMException {
        this.structuredTaskClient.printAndDeleteStructureDatas();

    }

}
