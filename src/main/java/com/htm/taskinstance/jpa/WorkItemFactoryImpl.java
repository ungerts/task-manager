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

package com.htm.taskinstance.jpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.htm.dm.EHumanRoles;
import com.htm.entities.jpa.Workitem;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.peopleresolution.IPeopleResolutionProvider;
import com.htm.peopleresolution.LPGResolutionProviderFactory;
import com.htm.peopleresolution.PeopleAssignmentPostProcessor;
import com.htm.peopleresolution.PeopleAssignmentResult;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.IWorkItem;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskinstance.WorkItemFactory;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.ITaskModel;
import com.htm.utils.Utilities;

public class WorkItemFactoryImpl extends WorkItemFactory {

    protected Logger log;

    public WorkItemFactoryImpl() {
        this.log = Utilities.getLogger(this.getClass());
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.WorkItemFactory#createNewWorkItems(com.htm.model.taskinstance
      * .ITaskInstance)
      */
    public List<IWorkItem> createWorkItemsForTaskInstance(
            ITaskInstance taskInstance) throws HumanTaskManagerException {
        log.debug("Creating work items for user task instance '" + taskInstance.getId() +
                "' (task name:'" + taskInstance.getName() + "')");

        /* Task model contains the people queries */
        ITaskModel taskModel = taskInstance.getTaskModel();

        /* Created work items for all human roles are added to this list */
        List<IWorkItem> createdWorkItems = new ArrayList<IWorkItem>();

        //TODO call it somewhere else!!!
        PeopleAssignmentPostProcessor postProcessor = PeopleAssignmentPostProcessor.newInstance();

        /*
           * Get the ids of the users which have the role task business
           * administrator by evaluating people the corresponding people query and
           * literals. Then create work items that associate the user with the
           * task instance.
           */
        PeopleAssignmentResult businessAdmins = evaluatePeopleQuery(taskModel
                .getBusinessAdminsQuery(), taskInstance);
        /* Add the business administrator literals defined in the task model
           * (if any) to the people query result */
        businessAdmins.addUserids(getUserIdsFromLiterals(taskModel.getBusinessAdminLiterals()));
        /* Use query result post processor for narrowing or enlarging the result set */
        postProcessor.postProcess(businessAdmins,
                EHumanRoles.BUSINESS_ADMINISTRATOR, taskInstance);
        createdWorkItems.addAll(createWorkItemForRole(businessAdmins,
                EHumanRoles.BUSINESS_ADMINISTRATOR, taskInstance));

        /*
           * Get the user ids of the users which have the role task stakeholders
           * and create work items that associate the user with the task instance
           */
        IPeopleAssignment stakeholderQuery = taskModel.getTaskStakeholdersQuery();
        PeopleAssignmentResult taskStakeHolders = evaluatePeopleQuery(stakeholderQuery, taskInstance);
        /* Add the task holder literals defined in the task model
           * (if any) to the people query result */
        taskStakeHolders.addUserids(getUserIdsFromLiterals(taskModel.getTaskStakeholderLiterals()));
        postProcessor.postProcess(taskStakeHolders,
                EHumanRoles.TASK_STAKEHOLDER, taskInstance);
        createdWorkItems.addAll(createWorkItemForRole(taskStakeHolders,
                EHumanRoles.TASK_STAKEHOLDER, taskInstance));


        /*
           * Get potential owners and excluded owners. Excluded owners cannot
           * become potential users thus they have to be removed from the list of
           * potential owners.
           */
        IPeopleAssignment potentialOwnersQuery = taskModel.getPotentialOwnersQuery();

        PeopleAssignmentResult potentialOwners = evaluatePeopleQuery(potentialOwnersQuery, taskInstance);
        /* Add the potential owner literals defined in the task model
           * (if any) to the people query result */
        potentialOwners.addUserids(getUserIdsFromLiterals(taskModel.getPotentialOwnerLiterals()));
        IPeopleAssignment excludedOwnersQuery = taskModel.getExcludedOwnersQuery();

        PeopleAssignmentResult excludedOwners = evaluatePeopleQuery(excludedOwnersQuery, taskInstance);
        postProcessor.postProcess(potentialOwners,
                EHumanRoles.EXCLUDED_OWNER, taskInstance);
        /* If all users are excluded nobody can become potential owner */
        if (excludedOwners.isAssignedToEverybody()) {
            potentialOwners.setNobodyAssigned();
        } else {
            /* Add the excluded owner literals defined in the task model
                * (if any) to the people query result */
            excludedOwners.addUserids(getUserIdsFromLiterals(taskModel.getExcludedOwnerLiterals()));
            potentialOwners.getUserids().removeAll(excludedOwners.getUserids());
            postProcessor.postProcess(potentialOwners,
                    EHumanRoles.POTENTIAL_OWNER, taskInstance);
        }

        /*
           * Create work items that associate the user (potential owners) with the
           * task instance
           */
        createdWorkItems.addAll(createWorkItemForRole(potentialOwners,
                EHumanRoles.POTENTIAL_OWNER, taskInstance));


        /* Call observer that a work item was created */

        // TODO perform nomination if potential users list is empty

        return createdWorkItems;

    }

    // is created within db
    public IWorkItem createNewWorkItem(String userid, EHumanRoles humanRole,
                                       ITaskInstance taskInstance) throws DatabaseException {

        IWorkItem workItem = new WorkItemWrapper();
        workItem.setAssignedToEverbody(false);
        log.debug("Work Item Creation - Work item assignee '" + userid + "'.");

        IAssignedUser assignedUser = TaskInstanceFactory.newInstance().createAssignedUser(userid);


        workItem.setAssignee(assignedUser);
        /* Just creating the work item thus it can't be claimed yet */
        workItem.setClaimed(false);
        workItem.setGenericHumanRole(humanRole);
        log.debug("Work Item Creation - Work item human role '" + humanRole + "'.");
        workItem.setTaskInstance(taskInstance);
        log.debug("Work Item Creation - Work item associated to task instance id '" + taskInstance.getId() + "'.");
        workItem.setCreationTime(new Timestamp(Calendar.getInstance()
                .getTimeInMillis()));

        return workItem;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.WorkItemFactory#createWorkItemFromEntity(java.lang.Object)
      */
    public IWorkItem createWorkItemFromEntity(Object workItemObject) {

        Utilities.isValidClass(workItemObject, Workitem.class);

        return new WorkItemWrapper((Workitem) workItemObject);
    }

    /**
     * Evaluates a people query and returns the result of the people query.
     *
     * @param peopleQuery  The people query that has to be evaluated. If the people query is <code>null</code> a people
     *                     query result is returned that contains no organizational entity, i.e. it is empty.
     * @param taskInstance The task instance that is the context of the people query.
     * @return A {@link PeopleAssignmentResult} that contains the organizational entities that were determined by
     *         the people query.
     * @throws HumanTaskManagerException
     */
    protected PeopleAssignmentResult evaluatePeopleQuery(IPeopleAssignment peopleQuery,
                                                         ITaskInstance taskInstance) throws HumanTaskManagerException {

        if (peopleQuery != null) {
            log.debug("People query evaluation - Logical people group name:" + peopleQuery.getBoundPeopleGroup().getName());
            IPeopleResolutionProvider peopleReolutionProvider =
                    LPGResolutionProviderFactory.createPeopleResolutionProvider(peopleQuery.getBoundPeopleGroup());
            PeopleAssignmentResult resultSet = peopleReolutionProvider.executePeopleQuery(peopleQuery, taskInstance);


            return resultSet;

        } else {
            return new PeopleAssignmentResult();
        }
    }

    protected List<IWorkItem> createWorkItemForRole(
            PeopleAssignmentResult peopleQueryResult, EHumanRoles humanRole,
            ITaskInstance taskInstance) throws DatabaseException {
        log.debug("Work Item Creation - Trying to create work items for role " + humanRole + ".");
        /* Created work items are added to this list */
        List<IWorkItem> createdWorkItems = new ArrayList<IWorkItem>();

        if (peopleQueryResult.isAssignedToEverybody()) {
            log.debug("Work Item Creation - Work item is assigned to everybody.");
            /*
                * Only one work item is created when a task instance is assigned to
                * everybody
                */
            IWorkItem workItem = new WorkItemWrapper(null);
            workItem.setAssignedToEverbody(true);
            /*
                * No user id is set since the work item is assigned to everybody
                */
            workItem.setAssignee(null);
            /* Just creating work item thus it can't be in the state claimed yet */
            workItem.setClaimed(false);
            workItem.setGenericHumanRole(humanRole);
            workItem.setTaskInstance(taskInstance);
            workItem.setCreationTime(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));

            createdWorkItems.add(workItem);
        } else {

            Iterator<String> iter = peopleQueryResult.getUserids().iterator();
            /* For each user in the people query result a work item is created */
            while (iter.hasNext()) {
                IWorkItem workItem = createNewWorkItem((String) iter.next(),
                        humanRole, taskInstance);
                log.debug("Work Item Creation - Work item for user '" + workItem.getAssignee().getUserId() + "' created.");
                createdWorkItems.add(workItem);
            }
        }
        return createdWorkItems;
    }

    protected Set<String> getUserIdsFromLiterals(Set<ILiteral> literals) {
        Set<String> userIds = new HashSet<String>();

        /* Copy the organizational entity ids of the literals to a list of strings */
        Iterator<ILiteral> iter = literals.iterator();
        while (iter.hasNext()) {
            userIds.add(iter.next().getOrganizationalEntityId());
        }

        return userIds;
    }


}
