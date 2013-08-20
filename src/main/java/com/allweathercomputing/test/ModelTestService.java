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

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.htm.dm.EHumanRoles;
import com.htm.ejb.ITaskModelStoreBean;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.jxpath.XPathQueryImpl;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;

@WebService
public class ModelTestService {

    private static final Logger log = Utilities
            .getLogger(ModelTestService.class);

    @EJB
    private ITaskModelStoreBean taskModelStoreBean;

    public void addLogicalPeopleGroup(String name, String[] arguments)
            throws Exception {
        log.debug("addLogicalPeopleGroup: " + name);
        ModelElementFactory factory = ModelElementFactory.newInstance();
        ILogicalPeopleGroupDef group = factory
                .createPeopleGroupDefinition(name);
        ILogicalPeopleGroupArgumentDef argument;
        for (String argumentName : arguments) {
            argument = factory.createPeopleGroupArgumentDef();
            argument.setName(argumentName);
            group.addArgumentDefinition(argument);

        }

        log.debug("Trying to save LPG");
        try {
            this.taskModelStoreBean.addLogicalPeopleGroupDef(group);
            log.debug("LPG added successfully. Name: " + group.getName());
        } catch (HumanTaskManagerException e) {
            log.error("Could not save LPG Name: " + group.getName());
            throw new Exception("Could not save LPG Name: " + group.getName(),
                    e);
        }

    }

    public void addTaskModel(String name, String[] pOwner,
                             String[] excludedUsers, String[] bAdmins, String[] stakeholder,
                             int prority, boolean isSkipable) throws Exception {
        log.debug("addTaskModel: " + name);
        ModelElementFactory factory = ModelElementFactory.newInstance();
        ITaskModel model = factory.createTaskModel();
        model.setName(name);
        model.setPotentialOwnerLiterals(createLiterals(pOwner,
                EHumanRoles.POTENTIAL_OWNER));
        model.setExcludedOwnerLiterals(createLiterals(excludedUsers,
                EHumanRoles.EXCLUDED_OWNER));
        model.setBusinessAdminLiterals(createLiterals(bAdmins,
                EHumanRoles.BUSINESS_ADMINISTRATOR));
        model.setTaskStakeholderLiterals(createLiterals(stakeholder,
                EHumanRoles.TASK_STAKEHOLDER));
        model.setSkipable(factory.createQuery("'" + isSkipable + "'"));
        model.setPriority(factory.createQuery("'" + prority + "'"));
        model.setPositionX(factory.createQuery("'" + prority + "'"));
        model.setPositionY(factory.createQuery("'" + prority + "'"));
        model.setDurationAvg(factory.createQuery("'PT130S'"));
        model.setDurationMax(factory.createQuery("'PT130S'"));
        model.setDurationMin(factory.createQuery("'PT130S'"));
        XPathQueryImpl query = (XPathQueryImpl) factory.createQuery("/input/ns1:patientData/ns1:patient");
        query.addNamespace("ns1", "http://example.org");
        model.setQueryProperty1(query);
        model.setQueryProperty2(factory.createQuery("'2'"));
        model.setQueryProperty3(factory.createQuery("'3'"));
        model.setQueryProperty4(factory.createQuery("'4'"));
        model.setQueryProperty1Name("patient");
        model.setQueryProperty2Name("Query 2");
        model.setQueryProperty3Name("Query 3");
        model.setQueryProperty4Name("Query 4");


        try {
            taskModelStoreBean.addTaskModel(model);
        } catch (HumanTaskManagerException e) {
            log.error("Could not save taskmodel with name: " + name);
            throw new Exception("Could not save taskmodel with name: " + name,
                    e);
        }
    }

    private Set<ILiteral> createLiterals(String[] userIds, EHumanRoles humanRole) {

        Set<ILiteral> literalModels = new HashSet<ILiteral>();

        for (int i = 0; i < userIds.length; i++) {
            literalModels.add(ModelElementFactory.newInstance().createLiteral(
                    userIds[i], humanRole));
        }

        return literalModels;
    }

}
