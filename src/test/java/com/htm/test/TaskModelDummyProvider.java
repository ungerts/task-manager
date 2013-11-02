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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.htm.ITaskModelStore;
import com.htm.TaskModelStore;
import com.htm.db.DataAccessProviderJpaJUnit;
import com.htm.db.IDataAccessProvider;
import com.htm.db.spring.DataAccessRepositoryCustom;
import com.htm.db.spring.DataAccessRepositoryImpl;
import com.htm.dm.EHumanRoles;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.peopleresolutionprovider.LpgResolutionProvider_UserByGroup;
import com.htm.query.IQuery;
import com.htm.query.jxpath.XPathQueryImpl;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.IPeopleQueryArgument;
import com.htm.taskmodel.IPresentationModel;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.taskmodel.jpa.PeopleQueryArgumentWrapper;
import com.htm.taskmodel.jpa.PresentationModelWrapper;
import com.htm.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class TaskModelDummyProvider extends UserManagerDummy {

    protected static final String LPG_DEF_NAME_DUMMY1 = "Test_LPG_Def1";

    protected static final String LPG_DEF_NAME_DUMMY2 = "Test_LPG_Def2";

    protected static final String LPG_DEF_USER_BY_GROUP = LpgResolutionProvider_UserByGroup.LPG_DEF_NAME;

    protected static final String TASK_MODEL_DUMMY_NAME_1 = "testTaskModel1";

    protected static final String TASK_MODEL_DUMMY_NAME_2 = "testTaskModel2";

    protected static final String PRESENTATION_DESC = "test description";

    protected static final String PRESENTATION_TITLE = "test title";

    protected static final String PRESENTATION_SUBJECT = "test subject";

    protected static final String MODEL_REPO_ADMIN_ID = "modelRepoAdmin";

    protected static final String MODEL_REPO_ADMIN_PASSWORD = "modelRepoAdminPwd";

    @Autowired
    protected DataAccessRepositoryCustom dataAccessRepository;

    @Autowired
    ITaskModelStore taskModelStore;


    protected void createPeopleGroupDefDummies() throws HumanTaskManagerException {

        ITaskModelStore taskModelStore = this.taskModelStore;

        /* People query with multiple arguments. The map contains
               * as key the argument name and as value the argument value */
        Map<String, String> peopleQueryArgs1 = preparePeopleQueryArgs1();
        /* People query with no argument */
        Map<String, String> peopleQueryArgs2 = preparePeopleQueryArgs2();
        /* People query with one argument based on LPG resolution provider "user by group" */
        Set<String> userByGroupLPGArgumentNames = new HashSet<String>();
        userByGroupLPGArgumentNames.add(LpgResolutionProvider_UserByGroup.LPG_DEF_ARGUMENT_NAME);

        loginInAsModelRepoAdmin();

        /*
               * Create Dummy LPG definitions based on the people
               * query arguments defined before and add them to the model store.
               */
        ILogicalPeopleGroupDef dummyLPG1 = createLPGDefDummy(
                LPG_DEF_NAME_DUMMY1, peopleQueryArgs1.keySet());
        taskModelStore.addLogicalPeopleGroupDef(dummyLPG1);
        ILogicalPeopleGroupDef dummyLPG2 = createLPGDefDummy(
                LPG_DEF_NAME_DUMMY2, peopleQueryArgs2.keySet());
        taskModelStore.addLogicalPeopleGroupDef(dummyLPG2);
        ILogicalPeopleGroupDef userByGroupLPGDef = createLPGDefDummy(
                LPG_DEF_USER_BY_GROUP, userByGroupLPGArgumentNames);
        taskModelStore.addLogicalPeopleGroupDef(userByGroupLPGDef);

    }

    protected void deleteModelDummies() throws HumanTaskManagerException {
        deleteTaskModelDummies();
        deleteLpgDummies();
    }

    protected void deleteTaskModelDummies() throws HumanTaskManagerException {
        loginInAsModelRepoAdmin();
        ITaskModelStore taskModelStore = this.taskModelStore;
        log.debug("Delete task model " + TASK_MODEL_DUMMY_NAME_1 + ":"
                + taskModelStore.forceDeleteTaskModel(TASK_MODEL_DUMMY_NAME_1));
        log.debug("Delete task model " + TASK_MODEL_DUMMY_NAME_2 + ":"
                + taskModelStore.forceDeleteTaskModel(TASK_MODEL_DUMMY_NAME_2));
    }

    protected void deleteLpgDummies() throws HumanTaskManagerException {
        loginInAsModelRepoAdmin();
        ITaskModelStore taskModelStore = this.taskModelStore;
        taskModelStore.deleteLogicalPeopleGroup(LPG_DEF_NAME_DUMMY1);
        taskModelStore.deleteLogicalPeopleGroup(LPG_DEF_NAME_DUMMY2);
        taskModelStore.deleteLogicalPeopleGroup(LPG_DEF_USER_BY_GROUP);
    }


    protected IDataAccessProvider getTestDap() {
        //return (DataAccessProviderJpaJUnit) IDataAccessProvider.Factory
        //        .newInstance();
        return this.dataAccessRepository;
    }


    protected Map<String, String> preparePeopleQueryArgs1() {
        Map<String, String> peopleQueryArgs1 = new HashMap<String, String>();

        peopleQueryArgs1.put("arg1", "query1");
        peopleQueryArgs1.put("arg2", "query2");
        peopleQueryArgs1.put("arg3", "query3");

        return peopleQueryArgs1;
    }

    protected Map<String, String> preparePeopleQueryArgs2() {
        Map<String, String> peopleQueryArgs2 = new HashMap<String, String>();

        return peopleQueryArgs2;
    }

    protected ITaskModel createTaskModelDummyLPG(String modelName)
            throws HumanTaskManagerException {

        ITaskModel taskModel = ModelElementFactory.newInstance()
                .createTaskModel();
        taskModel.setName(modelName);
        taskModel.setPriority(getPriorityQuery());
        taskModel.setSkipable(getSkipableQuery());
        taskModel.setStartBy(getStartByQuery());
        taskModel.setCompleteBy(getCompleteByQuery());
        taskModel.setInputSchema(Utilities
                .getXMLFromString("<testInputSchema></testInputSchema>"));
        taskModel.setOutputSchema(Utilities
                .getXMLFromString("<testOutputSchema></testOutputSchema>"));
        taskModel.setFaultSchema(Utilities
                .getXMLFromString("<testFaultSchema></testFaultSchema>"));

        /*
           * Create people queries, i.e. the logical people group definition is as
           */
        ILogicalPeopleGroupDef userByGroupLPGDef = getLogicalPeopleGroupDefFromDB(LPG_DEF_USER_BY_GROUP);
        /* Contains the people query argument name as key and as value the argument query */
        Map<String, String> peopleQueryArguments = new HashMap<String, String>();

        /* Business Admins */
        peopleQueryArguments.put(LpgResolutionProvider_UserByGroup.LPG_DEF_ARGUMENT_NAME, getBusinessAdminQuery());
        taskModel.setBusinessAdminsQuery(createPeopleQueryDummy(peopleQueryArguments, userByGroupLPGDef));
        /*
           * Task stakeholders (the key value pair of the map used before
           * is overwritten since the LPG argument name is the same)
           */
        peopleQueryArguments.put(LpgResolutionProvider_UserByGroup.LPG_DEF_ARGUMENT_NAME, getTaskStakeholderQuery());
        taskModel.setTaskStakeholdersQuery(createPeopleQueryDummy(peopleQueryArguments, userByGroupLPGDef));
        /* Potential owners */
        peopleQueryArguments.put(LpgResolutionProvider_UserByGroup.LPG_DEF_ARGUMENT_NAME, getPotentialOwnersQuery());
        taskModel.setPotentialOwnersQuery(createPeopleQueryDummy(peopleQueryArguments, userByGroupLPGDef));
        /* Excluded owners */
        peopleQueryArguments.put(LpgResolutionProvider_UserByGroup.LPG_DEF_ARGUMENT_NAME, getExcludedOwnersQuery());
        taskModel.setExcludedOwnersQuery(createPeopleQueryDummy(peopleQueryArguments, userByGroupLPGDef));

        /* Set presentation information */
        IPresentationModel presentationModel = new PresentationModelWrapper();
        presentationModel.setTitle(PRESENTATION_TITLE);
        presentationModel.setSubject(PRESENTATION_SUBJECT);
        presentationModel.setDescription(PRESENTATION_DESC);
        taskModel.addPresentationModel(presentationModel);

        /* Add model to the task model store */
        ITaskModelStore taskModelStore = this.taskModelStore;
        taskModelStore.addTaskModel(taskModel);

        return taskModel;
    }


    /**
     * Creates a task model for tests.</br>
     * The task model is using literal for defining business administrators, potential owners etc.</br>
     * Queries for the attributes skipable, priority or deadlines (like activation time, complete by etc.) are not set either,
     * i.e. default values are used.
     *
     * @param modelName The name of the task model.
     * @return The task model as object.
     * @throws HumanTaskManagerException
     */
    protected ITaskModel createTaskModelDummyLiterals(String modelName)
            throws HumanTaskManagerException {

        ITaskModel taskModel = ModelElementFactory.newInstance()
                .createTaskModel();

        taskModel.setName(modelName);

        taskModel.setInputSchema(Utilities
                .getXMLFromString("<testInputSchema></testInputSchema>"));
        taskModel.setOutputSchema(Utilities
                .getXMLFromString("<testOutputSchema></testOutputSchema>"));
        taskModel.setFaultSchema(Utilities
                .getXMLFromString("<testFaultSchema></testFaultSchema>"));

        /*
           * Set Literals.
           */

        /* Business Admins */
        taskModel.setBusinessAdminLiterals(
                createLiteralDummies(getBusinessAdminUserIds(), EHumanRoles.BUSINESS_ADMINISTRATOR));

        /* Task stakeholders */
        taskModel.setTaskStakeholderLiterals(
                createLiteralDummies(getTaskStakeholderUserIds(), EHumanRoles.TASK_STAKEHOLDER));

        /* Potential owners */
        taskModel.setPotentialOwnerLiterals(
                createLiteralDummies(getPotentialOwnerUserIds(), EHumanRoles.POTENTIAL_OWNER));

        /* Excluded owners */
        taskModel.setExcludedOwnerLiterals(
                createLiteralDummies(getExcludedOwnersUserIds(), EHumanRoles.EXCLUDED_OWNER));

        /* Add model to the task model store */
        ITaskModelStore taskModelStore = this.taskModelStore;
        taskModelStore.addTaskModel(taskModel);

        return taskModel;
    }

    protected IQuery createTestQuery(String queryValue) {
        IQuery query = new XPathQueryImpl();
        query.setQuery(queryValue);

        return query;
    }

    protected IPeopleAssignment createPeopleQueryDummy(
            Map<String, String> arguments, ILogicalPeopleGroupDef lpgDef) {

        /* Create people query based on the LPG definition */
        IPeopleAssignment peopleQuery = ModelElementFactory.newInstance()
                .createPeopleQuery(lpgDef);

        /*
           * The Map contains the argument name and the value that shall be
           * assigned to the argument. Before the argument can be created the
           * definition of the argument has to be fetched. After the argument was
           * created it is added to the people query.
           */
        Iterator<String> iter = arguments.keySet().iterator();
        while (iter.hasNext()) {
            String argumentName = (String) iter.next();
            /*
                * Get the argument definition for the argument. An argument without
                * a definition can not be created.
                */
            ILogicalPeopleGroupArgumentDef argumentDef = lpgDef
                    .getArgumentDef(argumentName);

            if (argumentDef == null) {
                throw new RuntimeException("An argument definiton with name "
                        + argumentName
                        + " does not exist within LPG definition "
                        + lpgDef.getName());

            }

            IPeopleQueryArgument peopleQueryArgument = new PeopleQueryArgumentWrapper(
                    argumentDef);
            peopleQueryArgument.setValue(createTestQuery(arguments
                    .get(argumentName)));
            peopleQuery.addArgument(peopleQueryArgument);
        }

        return peopleQuery;

    }

    private ILogicalPeopleGroupDef createLPGDefDummy(String lpgDefName, Set<String> dummyArgumentNames) {
        ILogicalPeopleGroupDef lpgDef = ModelElementFactory.newInstance().createPeopleGroupDefinition(lpgDefName);


        /* Generate dummy LPG argument definitions and add them to the LPG definition */
        Iterator<String> iter = dummyArgumentNames.iterator();
        while (iter.hasNext()) {
            String dummyArgumentName = (String) iter.next();
            ILogicalPeopleGroupArgumentDef dummyArgument = ModelElementFactory.newInstance().createPeopleGroupArgumentDef();
            dummyArgument.setName(dummyArgumentName);
            lpgDef.addArgumentDefinition(dummyArgument);

        }

        return lpgDef;
    }

    protected ILogicalPeopleGroupDef getLogicalPeopleGroupDefFromDB(
            String lpgDefName) throws DatabaseException {
        return getTestDap().getLogicalPeopleGroupDef(lpgDefName);
    }


    protected Set<ILiteral> createLiteralDummies(String[] userIds, EHumanRoles humanRole) {

        Set<ILiteral> literalModels = new HashSet<ILiteral>();

        for (int i = 0; i < userIds.length; i++) {
            literalModels.add(
                    ModelElementFactory.newInstance().createLiteral(
                            userIds[i], humanRole));
        }

        return literalModels;
    }


    protected String[] getBusinessAdminUserIds() {
        return new String[]{"friedrth"};
    }

    protected String[] getTaskStakeholderUserIds() {
        return new String[]{"friedrth"};
    }

    protected String[] getPotentialOwnerUserIds() {
        return new String[]{"edelmaal", "khalilno", "lokanava",
                "sundarvi", "friedrth", "gallemel", "schmitcl", "wagnerse", "wodarsan"};
    }

    protected String[] getExcludedOwnersUserIds() {
        return new String[]{"gallemel", "schmitcl", "wagnerse", "wodarsan"};
    }

    protected void loginInAsModelRepoAdmin() {
        initSecurityContext(MODEL_REPO_ADMIN_ID, MODEL_REPO_ADMIN_PASSWORD);
    }


    protected abstract IQuery getPriorityQuery();

    protected abstract IQuery getStartByQuery();

    protected abstract IQuery getCompleteByQuery();

    protected abstract IQuery getSkipableQuery();

    protected abstract String getBusinessAdminQuery();

    protected abstract String getPotentialOwnersQuery();

    protected abstract String getExcludedOwnersQuery();

    protected abstract String getTaskStakeholderQuery();
}
