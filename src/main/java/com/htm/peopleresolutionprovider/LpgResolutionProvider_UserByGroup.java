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

package com.htm.peopleresolutionprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


import com.htm.db.IDataAccessProvider;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.peopleresolution.IPeopleResolutionProvider;
import com.htm.peopleresolution.PeopleAssignmentResult;
import com.htm.query.IQuery;
import com.htm.query.IQueryEvaluator;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.IPeopleQueryArgument;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.taskmodel.jpa.LpgGroupArgDefWrapper;
import com.htm.utils.Utilities;

public class LpgResolutionProvider_UserByGroup implements IPeopleResolutionProvider {

    public static ILogicalPeopleGroupDef SUPPORTED_LPG_DEF;

    public static String LPG_DEF_NAME = "userByGroup";

    public static String LPG_DEF_ARGUMENT_NAME = "group";

    private Logger log = Utilities.getLogger(this.getClass());

    static {
        /*
           * Initialize the definition supported by this people resolution provider
           * implementation.
           */
        ModelElementFactory fac = ModelElementFactory.newInstance();
        SUPPORTED_LPG_DEF = fac.createPeopleGroupDefinition(LPG_DEF_NAME);

        ILogicalPeopleGroupArgumentDef lpgArgDef = new LpgGroupArgDefWrapper();
        lpgArgDef.setName(LPG_DEF_ARGUMENT_NAME);
        SUPPORTED_LPG_DEF.addArgumentDefinition(lpgArgDef);

    }


    public PeopleAssignmentResult executePeopleQuery(IPeopleAssignment peopleQuery, Object context) throws HumanTaskManagerException {
        log.debug("People query evaluation - Lpg Resolution Provider '" + this.getClass().getName() + "'.");
        PeopleAssignmentResult resultSet = new PeopleAssignmentResult();

        /*
           * The people query that can be evaluated by this implementation
           * contains only one argument (like indicated by the LPG definition).
           * That's why the iterator or the argument list respectively contains
           * only one element.
           */
        Iterator<IPeopleQueryArgument> iter =
                peopleQuery.getArguments().iterator();
        if (iter.hasNext()) {
            IPeopleQueryArgument argument = iter.next();
            IQuery peopleQueryValue = argument.getValue();

            /* Evaluate the query to get the argument value
                * TODO the evaluation of the XPath expressions should have been already done before this
                * method is called because otherwise every people resolution provider implementation
                * has to deal with that (duplicate code). */
            IQueryEvaluator queryEvaluator = IQueryEvaluator.Factory.newInstance(peopleQueryValue.getQueryLanguage(), context);
            List<?> argumentValue = queryEvaluator.evaluateQuery(peopleQueryValue);

            /*
                * Since a group name is expected the list must
                * contain exactly ONE string.
                */
            if (argumentValue.size() == 1 && argumentValue.get(0) instanceof String) {
                /* Get the user which are member of the group */
                resultSet.setUserids(getUsersByGroupMembership((String) argumentValue.get(0)));
            } else {
                log.debug("People query evaluation - The evalution of people query '" + peopleQueryValue + "' for LPG '" +
                        LPG_DEF_NAME + "' failed. The query must return a group name.");
            }
        }

        /* By convention always an empty list is returned when query has failed */
        return resultSet;
    }


    //actual staff verb
    protected Set<String> getUsersByGroupMembership(String groupName) throws DatabaseException {
        log.debug("People query evaluation - Trying to retrieve users for group '" + groupName + "'.");
        IDataAccessProvider dap = IDataAccessProvider.Factory.newInstance();
        Set<String> users = dap.getUserIdsByGroup(groupName);
        log.debug("People query evaluation - The following users were retrieved '" + users.toArray() + "'.");

        return users;

    }


}
