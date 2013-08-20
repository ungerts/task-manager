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

package com.htm.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.htm.db.IDataAccessProvider;
import com.htm.dm.EHumanRoles;
import com.htm.exceptions.AuthenticationException;
import com.htm.exceptions.AuthorizationException;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.UserException;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IWorkItem;
import com.htm.utils.SessionUtils;
import com.htm.utils.Utilities;

public class AuthorizationManager implements IAuthorizationManager {

    protected static Map<EActions, EHumanRoles[]> actionRolesMap;

    protected static Logger log;

    static {
        log = Utilities.getLogger(AuthorizationManager.class);

        actionRolesMap = new HashMap<EActions, EHumanRoles[]>();

        actionRolesMap = new HashMap<EActions, EHumanRoles[]>();

        actionRolesMap.put(EActions.CLAIM, new EHumanRoles[]{
                EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.START,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.STOP, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.RELEASE, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.FORWARD, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.SUSPEND, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.SUSPEND_UNTIL, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.RESUME, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.COMPLETE,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.FAIL,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.SET_PRIORITY, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.ADD_ATTACHMENT, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.GET_ATTACHMENT_INFOS, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.GET_ATTACHMENTS, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.POTENTIAL_OWNER,
                EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.DELETE_ATTACHMENTS, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.SKIP, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_INITIATOR, EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.GET_TASK_INFO,
                new EHumanRoles[]{EHumanRoles.ANYBODY});

        actionRolesMap.put(EActions.GET_TASK_DESCRIPTION,
                new EHumanRoles[]{EHumanRoles.ANYBODY});

        actionRolesMap.put(EActions.SET_OUTPUT,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.DELETE_OUTPUT,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.SET_FAULT,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.DELETE_FAULT,
                new EHumanRoles[]{EHumanRoles.ACTUAL_OWNER});

        actionRolesMap.put(EActions.GET_INPUT, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.POTENTIAL_OWNER, EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.GET_OUTPUT, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.GET_FAULT, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.QUERY, new EHumanRoles[]{
                EHumanRoles.ACTUAL_OWNER, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER});

        actionRolesMap.put(EActions.READ_WORK_ITEM_VIEW_TUPLE,
                new EHumanRoles[]{EHumanRoles.ANYBODY});// TODO narrow
        // permissions

        /* Task Parent Actions */
        actionRolesMap.put(EActions.CREATE_TASK_INSTANCE,
                new EHumanRoles[]{EHumanRoles.ANYBODY});

        actionRolesMap.put(EActions.EXIT, new EHumanRoles[]{
                EHumanRoles.TASK_INITIATOR, EHumanRoles.BUSINESS_ADMINISTRATOR,
                EHumanRoles.TASK_STAKEHOLDER, EHumanRoles.ACTUAL_OWNER});

        /* Administrative and User Management Actions */
        actionRolesMap.put(EActions.ADD_TASK_MODEL,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.GET_TASK_MODEL,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.UPDATE_TASK_MODEL,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_TASK_MODEL,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.FORCE_DELETE_TASK_MODEL,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.ADD_LPG_DEFINITION,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.UPDATE_LPG_DEFINITION,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_LPG_DEFINITION,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.GET_LOGICAL_PEOPLE_GROUP_DEF,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_TASK_INSTANCES,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_WORK_ITEM,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.ADD_USER,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_USER,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.GET_USER,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.ADD_GROUP,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.GET_GROUP,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.DELETE_GROUP,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

        actionRolesMap.put(EActions.CHANGE_PASSWORD,
                new EHumanRoles[]{EHumanRoles.ADMINISTRATOR});

    }

    // TODO Check relation between IUser and IAssignedUSer
    // TODO Command pattern

    protected static boolean containsUser(String userid,
                                          Set<IAssignedUser> users) {
        Iterator<IAssignedUser> iter = users.iterator();
        while (iter.hasNext()) {
            if (((IAssignedUser) iter.next()).getUserId().equals(userid))
                return true;
        }

        return false;
    }

    public static void authorizeAdministrativeAction(EActions action)
            throws HumanTaskManagerException {

        /* Get the id of the user that is currently logged in */
        String userId = SessionUtils.getCurrentUser();

        /* Get the roles that are allowed to perform the action */
        EHumanRoles[] roles = actionRolesMap.get(action);
        if (roles == null) {
            throw new IllegalArgumentException("No user roles were defined "
                    + "for administrative action " + action);
        }
        /*
           * If the user has ONE of the required roles he is allowed to perform
           * the action
           */
        for (EHumanRoles role : roles) {
            if (role.equals(getAdministrativeRole(userId))) {
                return;
            }
        }

        throw new AuthorizationException("User " + userId
                + " has not the proper roles to perform the action " + action
                + ".");
    }

    protected static EHumanRoles getAdministrativeRole(String adminUserId) {
        return EHumanRoles.ADMINISTRATOR;
        // TODO Dummy implementation, this method has to return the
        // administrative role (or null) of the user
    }

    public static IAssignedUser authorizeTaskQueryAction(EActions action)
            throws HumanTaskManagerException {
        // TODO Everybody can execute queries, improve that !!!
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        /* Get the id of the user that is currently logged in */
        String userId = SessionUtils.getCurrentUser();
        return dap.getAssignedUser(userId);

    }

    public static void authorizeTaskParentAction(String userId, String tiid,
                                                 EActions action) throws AuthenticationException,
            IllegalArgumentException, DatabaseException, AuthorizationException {

        log.debug("Authorize task client action - Action: '" + action
                + "' User id: '" + userId + "'");
        boolean isAuthorized = false;

        switch (action) {
            case CREATE_TASK_INSTANCE:
                // TODO currently every valid user can create a task instance
                isAuthorized = true;
                break;
            case EXIT:
                isAuthorized = hasRoleForPerformingAction(userId, tiid, action);
                break;
            default:
                break;
        }

        if (!isAuthorized) {
            String errorMsg = "Authorize task parent action - User '" + userId
                    + "' has not the proper roles to perform the action "
                    + action + " for task instance '" + tiid + "'";
            log.error(errorMsg);
            throw new AuthorizationException(errorMsg);
        }
    }

    public static IAssignedUser authorizeTaskClientAction(String tiid,
                                                          EActions action) throws AuthorizationException,
            AuthenticationException, IllegalArgumentException,
            DatabaseException, UserException {
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        /* Get the id of the user that is currently logged in */
        String userId = SessionUtils.getCurrentUser();
        /* Check if the user is authorized to perform the action */
        log.debug("Authorize task client action - Action: '" + action
                + "' User id: '" + userId + "'");
        boolean isAuthorized = hasRoleForPerformingAction(userId, tiid, action);
        log.debug("Authorize task client action - Is user authorized: "
                + isAuthorized);
        if (!isAuthorized) {
            String errorMsg = "Authorize task client action - User '" + userId
                    + "' has not the proper roles to perform the action "
                    + action + " for task instance '" + tiid + "'";
            log.error(errorMsg);
            throw new AuthorizationException(errorMsg);
        }
        return dap.getAssignedUser(userId);
    }

    // TODO is implementation correct?
    // TODO reduce redundancy
    public static IAssignedUser authorizeTaskClientAction2(String tiid,
                                                           String userId, EActions action) throws AuthorizationException,
            AuthenticationException, IllegalArgumentException,
            DatabaseException, UserException {
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        /* Check if the user is authorized to perform the action */
        log.debug("Authorize task client action - Action: '" + action
                + "' User id: '" + userId + "'");
        boolean isAuthorized = hasRoleForPerformingAction(userId, tiid, action);
        log.debug("Authorize task client action - Is user authorized: "
                + isAuthorized);
        if (!isAuthorized) {
            String errorMsg = "Authorize task client action - User '" + userId
                    + "' has not the proper roles to perform the action "
                    + action + " for task instance '" + tiid + "'";
            log.error(errorMsg);
            throw new AuthorizationException(errorMsg);
        }
        return dap.getAssignedUser(userId);
    }

    protected static boolean hasRoleForPerformingAction(String userId,
                                                        String tiid, EActions action) throws IllegalArgumentException,
            DatabaseException {

        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();

        if (dap.getTaskInstance(tiid) == null) {
            throw new IllegalArgumentException("The task instance " + tiid
                    + " can not be found.");
        }

        IAssignedUser assignedUser = dap.getAssignedUser(userId);

        if (assignedUser != null) {
            /* Get the roles that are required to perform the action */
            EHumanRoles[] roles = actionRolesMap.get(action);
            log.debug("Authorize task client action - One of the following roles is required to "
                    + "perform the action: "
                    + Utilities.concateArrayElementsToString(roles));
            if (roles == null) {
                throw new IllegalArgumentException(
                        "No user roles were defined "
                                + "for task client action " + action);
            }
            /*
                * Get all work items that associated the user with the given task
                * instance
                */
            List<IWorkItem> workItemsOfUserForTI = dap.getWorkItems(tiid,
                    assignedUser);
            log.debug("Authorize task client action - Checking if the user possesses the required work items for executing the action.");
            log.debug("Authorize task client action - "
                    + "Number of work items assigned to user "
                    + workItemsOfUserForTI.size());

            /*
                * The algorithm checks for each work item of the user if the user
                * has the proper human role that is required to perform the action.
                * When the first role of the user meets the required role the
                * algorithm terminates. If ANYBODY is allowed to perform the action
                * and the user has at least one work item assigned return true.
                */
            Iterator<IWorkItem> iter = workItemsOfUserForTI.iterator();
            while (iter.hasNext()) {
                IWorkItem workItem = (IWorkItem) iter.next();
                EHumanRoles workItemRole = workItem.getGenericHumanRole();
                log.debug("Authorize task client action - User has role: " + workItem.getGenericHumanRole());
                for (EHumanRoles role : roles) {
                    /*
                          * Either the user has the required role or ANYBODY is
                          * allowed to execute the action
                          */
                    if (workItemRole.equals(role)
                            || role.equals(EHumanRoles.ANYBODY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<EHumanRoles> getRolesOfUser(String tiid, String userId)
            throws HumanTaskManagerException {
        List<EHumanRoles> roles = null;
        log.debug("Get roles of user '" + userId + "' for task '" + tiid + "'");
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        IAssignedUser assignedUser;
        try {
            assignedUser = dap.getAssignedUser(userId);
            if (assignedUser != null) {
                List<IWorkItem> workItemsOfUserForTI = dap.getWorkItems(tiid,
                        assignedUser);
                if (workItemsOfUserForTI.size() > 0) {
                    roles = new ArrayList<EHumanRoles>();
                    for (IWorkItem workItem : workItemsOfUserForTI) {
                        log.debug("User has role: " + workItem.getGenericHumanRole());
                        // TODO anybody
                        roles.add(workItem.getGenericHumanRole());

                    }
                }
            }
        } catch (DatabaseException e) {
            log.error("Cannot load work items of user '" + userId + "' for task '" + tiid + "'", e);
            throw new HumanTaskManagerException("Cannot load work items of user '" + userId + "' for task '" + tiid + "'", e);
        }

        return roles;
    }

}
