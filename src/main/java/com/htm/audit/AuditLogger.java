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

package com.htm.audit;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.TaskInstanceView;
import com.htm.security.AuthorizationManager;
//import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;
import com.shtm.StructuredTaskClientInterfaceImpl;
import com.shtm.views.StructuredTaskInstanceView;

public class AuditLogger implements IAuditLogger {

    private Logger log;
    private EntityManager em;
    private StructuredTaskClientInterfaceImpl stc;

    public AuditLogger() {
        this.log = Utilities.getLogger(this.getClass());
        //this.em = JEEUtils.getEntityManager(JEEUtils.PERSISTENCE_MANAGER_HTM);
        //this.stc = new StructuredTaskClientInterfaceImpl();
    }

    @Override
    public void logAction(AuditAction action) throws HumanTaskManagerException {
          log.debug("Auditing disabled");
//        log.debug("Logging audit action '" + action.getAction()
//                + "' for tiid '" + action.getTaskInstanceView().getId() + "'");
//        try {
//            AuditEntry entry = new AuditEntry();
//            TaskInstanceView taskInstance = action.getTaskInstanceView();
//            entry.seteAction(action.getAction());
//            entry.setOldState(action.getOldState());
//            entry.setState(action.getState());
//            entry.setOriginator(action.getOriginator());
//
//            entry.setEventTimeStamp(new Timestamp(Calendar.getInstance()
//                    .getTimeInMillis()));
//
//            entry.setTiid(Long.parseLong(taskInstance.getId()));
//
//            // TODO tmid;
//
//            entry.setTaskModelName(taskInstance.getTaskModelName());
//
//            // TODO taskInstanceName;
//
//            List<EHumanRoles> roles = AuthorizationManager.getRolesOfUser(
//                    taskInstance.getId(), action.getOriginator());
//
//            if (roles == null) {
//                entry.setOrginatorIsBusinessAdministrator(false);
//                entry.setOriginatorIsPOwner(false);
//                entry.setOriginatorIsStakeholder(false);
//            } else {
//                entry.setOrginatorIsBusinessAdministrator(roles
//                        .contains(EHumanRoles.BUSINESS_ADMINISTRATOR));
//                entry.setOriginatorIsPOwner(roles
//                        .contains(EHumanRoles.POTENTIAL_OWNER));
//                entry.setOriginatorIsStakeholder(roles
//                        .contains(EHumanRoles.TASK_STAKEHOLDER));
//            }
//
//            entry.setTaskInitiator(taskInstance.getTaskInitiator());
//
//            entry.setActualOwner(taskInstance.getActualOwner());
//
//            StructuredTaskInstanceView structuredTaskView = stc
//                    .getStructuredTaskInfo(taskInstance.getId());
//
//            if (structuredTaskView != null) {
//                entry.setHasSubTasks(structuredTaskView.isHasSubTasks());
//                entry.setHasControlledTasks(structuredTaskView
//                        .isHasControlledTasks());
//            } else {
//                entry.setHasSubTasks(false);
//                entry.setHasControlledTasks(false);
//
//            }
//
//            // private Boolean hasSubTasks;
//            //
//            // private Boolean hasControlledTasks;
//            this.em.persist(entry);
//        } catch (HumanTaskManagerException e) {
//            String message = "Audit action '" + action.getAction()
//                    + "' for tiid '" + action.getTaskInstanceView().getId()
//                    + "' cannot be logged";
//            log.error(message, e);
//            throw new HumanTaskManagerException(message, e);
//        }

    }

}
