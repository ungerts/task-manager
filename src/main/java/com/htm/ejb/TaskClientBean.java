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

import com.htm.TaskClientInterfaceImpl;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.views.TaskInstanceView;
import com.htm.query.views.WorkItemView;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
@Stateless(name = "TaskClientBean")
@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// TODO Roles(Security)
public class TaskClientBean implements ITaskClientBean {

    private TaskClientInterfaceImpl taskClient;
    private Logger log;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        this.taskClient = new TaskClientInterfaceImpl();
    }

    @Override
    public void claim(String tiid) throws HumanTaskManagerException {
        this.taskClient.claim(tiid);

    }

    @Override
    public void start(String tiid) throws HumanTaskManagerException {
        this.taskClient.start(tiid);

    }

    @Override
    public void stop(String tiid) throws HumanTaskManagerException {
        this.taskClient.stop(tiid);

    }

    @Override
    public void release(String tiid) throws HumanTaskManagerException {
        this.taskClient.release(tiid);

    }

    @Override
    public void suspend(String tiid) throws HumanTaskManagerException {
        this.taskClient.suspend(tiid);

    }

    @Override
    public void forward(String tiid, String forwardeeId)
            throws HumanTaskManagerException {
        this.taskClient.forward(tiid, forwardeeId);

    }

    @Override
    public void suspendUntil(String tiid, Timestamp pointOfTime)
            throws HumanTaskManagerException {
        this.taskClient.suspendUntil(tiid, pointOfTime);

    }

    @Override
    public void resume(String tiid) throws HumanTaskManagerException {
        this.taskClient.resume(tiid);

    }

    @Override
    public void complete(String tiid, Object outputData)
            throws IllegalArgumentException, HumanTaskManagerException {
        this.taskClient.complete(tiid, outputData);

    }

    @Override
    public void fail(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        this.taskClient.fail(tiid, faultName, faultData);

    }

    @Override
    public void skip(String tiid) throws java.lang.IllegalArgumentException,
            HumanTaskManagerException {
        this.taskClient.skip(tiid);

    }

    @Override
    public void setPriority(String tiid, int priority)
            throws HumanTaskManagerException {
        this.taskClient.setPriority(tiid, priority);

    }

    @Override
    public void addAttachment(String tiid, IAttachment attachment)
            throws HumanTaskManagerException {
        this.taskClient.addAttachment(tiid, attachment);

    }

    @Override
    public List<IAttachment> getAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        return this.taskClient.getAttachments(tiid, name);
    }

    @Override
    public boolean deleteAttachments(String tiid, String name)
            throws HumanTaskManagerException {
        return this.taskClient.deleteAttachments(tiid, name);
    }

    @Override
    public TaskInstanceView getTaskInfo(String tiid)
            throws HumanTaskManagerException {
        return this.taskClient.getTaskInfo(tiid);
    }

    @Override
    public String getTaskDescription(String tiid)
            throws HumanTaskManagerException {

        return this.taskClient.getTaskDescription(tiid);
    }

    @Override
    public void setOutput(String tiid, Object output)
            throws HumanTaskManagerException {
        this.taskClient.setOutput(tiid, output);

    }

    @Override
    public void deleteOutput(String tiid) throws HumanTaskManagerException {
        this.taskClient.deleteOutput(tiid);

    }

    @Override
    public void setFault(String tiid, String faultName, Object faultData)
            throws HumanTaskManagerException {
        this.taskClient.setFault(tiid, faultName, faultData);

    }

    @Override
    public void deleteFault(String tiid) throws HumanTaskManagerException {
        this.taskClient.deleteFault(tiid);

    }

    @Override
    public Object getInput(String tiid) throws HumanTaskManagerException {

        return this.taskClient.getInput(tiid);
    }

    @Override
    public Object getOutput(String tiid) throws HumanTaskManagerException {

        return this.taskClient.getOutput(tiid);
    }

    @Override
    public IFault getFault(String tiid) throws HumanTaskManagerException {

        return this.taskClient.getFault(tiid);
    }

    @Override
    public List<WorkItemView> query(String whereClause)
            throws HumanTaskManagerException {

        return this.taskClient.query(whereClause);
    }

    @Override
    public List<WorkItemView> query(String whereClause, int maxResults)
            throws HumanTaskManagerException {

        return this.taskClient.query(whereClause, maxResults);

    }
}
