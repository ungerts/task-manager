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

package com.allweathercomputing.test;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.htm.ejb.IStructuredTaskClientBean;
import com.htm.ejb.ITaskClientBean;
import com.htm.ejb.ITaskParentBean;
import com.htm.query.views.WorkItemView;
import com.htm.utils.Utilities;
import com.shtm.views.StructuredWorkItemView;

@WebService
public class InstanceTestService {

    private static final Logger log = Utilities
            .getLogger(ModelTestService.class);

    @EJB
    private ITaskParentBean taskParentBean;

    @EJB
    private ITaskClientBean taskClientBean;

    @EJB
    private IStructuredTaskClientBean structuredTaskClientBean;

    public String createInstance(String taskModelName, String taskInstanceName)
            throws Exception {
        log.debug("Create instance (Name: " + taskInstanceName
                + ") from model " + taskModelName);
        try {
            return this.taskParentBean.createTaskInstance("64", null,
                    taskModelName, taskInstanceName, null, null, null);
        } catch (Throwable e) {
            String message = "Cannot create instance (Name: "
                    + taskInstanceName + ") from model " + taskModelName;
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void claim(String tiid) throws Exception {
        log.debug("Claim: Trying to claim task with tiid '" + tiid + "'");
        try {
            taskClientBean.claim(tiid);
        } catch (Throwable e) {
            String message = "Claim: Cannot claim task with tiid '" + tiid
                    + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureClaim(String tiid) throws Exception {
        log.debug("structureClaim: Trying to claim task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.claim(tiid);
        } catch (Throwable e) {
            String message = "structureClaim: Cannot claim task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureStart(String tiid) throws Exception {
        log.debug("structureStart: Trying to start task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.start(tiid);
        } catch (Throwable e) {
            String message = "structureStart: Cannot start task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureComplete(String tiid) throws Exception {
        log.debug("structureComplete: Trying to complete task with tiid '" + tiid + "'");
        try {
            structuredTaskClientBean.complete(tiid, "");
        } catch (Throwable e) {
            String message = "structureComplete: Cannot complete task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureStop(String tiid) throws Exception {
        log.debug("structureStop: Trying to stop task with tiid '" + tiid + "'");
        try {
            structuredTaskClientBean.stop(tiid);
        } catch (Throwable e) {
            String message = "structureStop: Cannot stop task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureSkip(String tiid) throws Exception {
        log.debug("structureExit: Trying to skip task with tiid '" + tiid + "'");
        try {
            structuredTaskClientBean.skip(tiid);
        } catch (Throwable e) {
            String message = "structureClaim: Cannot skip task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureExit(String tiid) throws Exception {
        log.debug("structureExit: Trying to exit task with tiid '" + tiid + "'");
        try {
            taskParentBean.exit(tiid);
        } catch (Throwable e) {
            String message = "structureExit: Cannot exit task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureRelease(String tiid) throws Exception {
        log.debug("structureRelease: Trying to exit task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.release(tiid);
        } catch (Throwable e) {
            String message = "structureExit: Cannot exit task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureSuspend(String tiid) throws Exception {
        log.debug("structureSuspend: Trying to suspend task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.suspend(tiid);

        } catch (Throwable e) {
            String message = "structureSuspend: Cannot suspend task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureResume(String tiid) throws Exception {
        log.debug("structureResume: Trying to resume task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.resume(tiid);

        } catch (Throwable e) {
            String message = "structureResume: Cannot resume task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void structureForward(String tiid, String userid) throws Exception {
        log.debug("structureForward: Trying to forward task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.forward(tiid, userid);

        } catch (Throwable e) {
            String message = "structureForward: Cannot foreward task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void removeSubTask(String tiid) throws Exception {
        log.debug("removeSubTask: Trying to remove sub task with tiid '" + tiid
                + "'");
        try {
            structuredTaskClientBean.removeSubTask(tiid);

        } catch (Throwable e) {
            String message = "removeSubTask: Cannot remove sub task with tiid '"
                    + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public String addSubTask(String parentTiid, String taskModelName,
                             String taskInstanceName) throws Exception {
        log.debug("addSubTask: Trying to add subtask to task with tiid '"
                + parentTiid + "'");
        try {
            return structuredTaskClientBean.addSubTask(parentTiid,
                    taskModelName, taskInstanceName, null);

        } catch (Throwable e) {
            String message = "addSubTask: Cannot add subtask to task with tiid '"
                    + parentTiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
        // return null;
    }

    public String mergeTasks(String tiid1, String tiid2, String taskModelName,
                             String taskInstanceName) throws Exception {
        log.debug("mergeTasks: Trying to add merge task '" + tiid1
                + "' and task '" + tiid1 + "'");
        try {
            return this.structuredTaskClientBean.mergeTasks(tiid1, tiid2,
                    taskModelName, taskInstanceName, null);

        } catch (Throwable e) {
            String message = "mergeTasks: Trying to add merge task '" + tiid1
                    + "' and task '" + tiid1 + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
        // return null;
    }

    public void unmerge(String tiid) throws Exception {
        log.debug("unmerge: Trying to unmerge task with tiid '" + tiid + "'");
        try {
            this.structuredTaskClientBean.unmerge(tiid);

        } catch (Throwable e) {
            String message = "Cannot unmerge task with tiid '" + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void forward(String tiid, String userid) throws Exception {
        log.debug("forward: Trying to forward task with tiid '" + tiid + "'");
        try {
            this.taskClientBean.forward(tiid, userid);

        } catch (Throwable e) {
            String message = "Cannot foreward task with tiid '" + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void release(String tiid) throws Exception {
        log.debug("release: Trying to release task with tiid '" + tiid + "'");
        try {
            this.taskClientBean.release(tiid);

        } catch (Throwable e) {
            String message = "Cannot release task with tiid '" + tiid + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public String query(String queryString) throws Exception {
        log.debug("query: Trying evaluate query '" + queryString + "'");
        try {
            List<WorkItemView> result = this.taskClientBean.query(queryString);
            return "" + result.size();

        } catch (Throwable e) {
            String message = "Cannot evaluate query '" + queryString + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public List<String> structuredQuery(String queryString, String structureQuery) throws Exception {
        log.debug("query: Trying evaluate structuredQuery '" + queryString + "'");
        try {

            List<StructuredWorkItemView> result = this.structuredTaskClientBean.query(queryString, structureQuery, -1);

            List<String> taskIds = new ArrayList<String>(25);

            if (result == null) {
                return taskIds;
            }


            for (StructuredWorkItemView view : result) {
                taskIds.add(view.getWorkItem().getTaskInstance().getId());
            }

            return taskIds;

        } catch (Throwable e) {
            String message = "Cannot evaluate structuredQuery '" + queryString + "'";
            log.error(message, e);
            throw new Exception(message, e);
        }
    }
}
