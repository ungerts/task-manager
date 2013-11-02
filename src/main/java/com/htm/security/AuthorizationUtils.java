package com.htm.security;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.AuthorizationException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAssignedUser;
import com.htm.utils.SessionUtils;
import com.htm.utils.Utilities;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ungerts
 * Date: 02.11.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationUtils {

    public static Map<EActions, EHumanRoles[]> actionRolesMap;

    protected static Logger log;

    static {
        log = Utilities.getLogger(AuthorizationUtils.class);

        actionRolesMap = new HashMap<EActions, EHumanRoles[]>();

        //actionRolesMap = new HashMap<EActions, EHumanRoles[]>();

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


}
