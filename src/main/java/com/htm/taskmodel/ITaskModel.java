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

package com.htm.taskmodel;


import java.util.Set;

import org.jdom2.Document;

import com.htm.dm.EHumanRoles;
import com.htm.dm.IDataModelElement;
import com.htm.query.IQuery;
import com.htm.taskinstance.ITaskInstance;

public interface ITaskModel extends IDataModelElement {

    public void setName(String name);

    public void setPriority(IQuery priority);

    public void setSkipable(IQuery query);

    public void setStartBy(IQuery query);

    public void setCompleteBy(IQuery query);

    public String getId();

    public String getName();

    public IQuery getPriority();

    public IQuery getSkipable();

    public IQuery getStartBy();

    public IQuery getCompleteBy();

    public IPresentationModel getPresentationModel();

    public void addPresentationModel(IPresentationModel model);

    //TODO Roles should be generic in the future i.e. the role name can be specified and is not hard coded anymore

    public void setBusinessAdminsQuery(IPeopleAssignment query);

    public void setTaskStakeholdersQuery(IPeopleAssignment query);

    public void setPotentialOwnersQuery(IPeopleAssignment query);

    public void setExcludedOwnersQuery(IPeopleAssignment query);

    public IPeopleAssignment getBusinessAdminsQuery();

    /**
     * Returns all literals that are associated to the generic human role
     * {@link EHumanRoles#BUSINESS_ADMINISTRATOR}.
     *
     * @return a set of literals or an an empty
     *         set if no literal exists that corresponds to the role {@link EHumanRoles#BUSINESS_ADMINISTRATOR}.
     */
    public Set<ILiteral> getBusinessAdminLiterals();

    public void setBusinessAdminLiterals(Set<ILiteral> literals);

    public IPeopleAssignment getTaskStakeholdersQuery();

    /**
     * Returns all literals that are associated to the generic human role
     * {@link EHumanRoles#TASK_STAKEHOLDER}.
     *
     * @return a set of literals or an an empty
     *         set if no literal exists that corresponds to the role {@link EHumanRoles#TASK_STAKEHOLDER}.
     */
    public Set<ILiteral> getTaskStakeholderLiterals();

    public void setTaskStakeholderLiterals(Set<ILiteral> literals);

    public IPeopleAssignment getPotentialOwnersQuery();

    /**
     * Returns all literals that are associated to the generic human role
     * {@link EHumanRoles#POTENTIAL_OWNER}.
     *
     * @return a set of literals or an an empty
     *         set if no literal exists that corresponds to the role {@link EHumanRoles#POTENTIAL_OWNER}.
     */
    public Set<ILiteral> getPotentialOwnerLiterals();

    public void setPotentialOwnerLiterals(Set<ILiteral> literals);

    public IPeopleAssignment getExcludedOwnersQuery();

    /**
     * Returns all literals that are associated to the generic human role
     * {@link EHumanRoles#EXCLUDED_OWNER}.
     *
     * @return a set of literals or an an empty
     *         set if no literal exists that corresponds to the role {@link EHumanRoles#EXCLUDED_OWNER}.
     */
    public Set<ILiteral> getExcludedOwnerLiterals();

    public void setExcludedOwnerLiterals(Set<ILiteral> literals);

    public Document getInputSchema();

    public Document getOutputSchema();

    public Document getFaultSchema();

    public void setInputSchema(Document inputSchema);

    public void setOutputSchema(Document outputSchema);

    public void setFaultSchema(Document faultSchema);

    public Set<ITaskInstance> getTaskInstances();

    public boolean isInstantiated();

    public void setPositionX(IQuery query);

    public IQuery getPositionX();

    public void setPositionY(IQuery query);

    public IQuery getPositionY();

    public void setDurationMin(IQuery query);

    public IQuery getDurationMin();

    public void setDurationMax(IQuery query);

    public IQuery getDurationMax();

    public void setDurationAvg(IQuery query);

    public IQuery getDurationAvg();


    /* Query Properties */

    public IQuery getQueryProperty1();

    public void setQueryProperty1(IQuery queryProperty1);

    public String getQueryProperty1Name();

    public void setQueryProperty1Name(String queryProperty1Name);

    public IQuery getQueryProperty2();

    public void setQueryProperty2(IQuery queryProperty2);

    public String getQueryProperty2Name();

    public void setQueryProperty2Name(String queryProperty2Name);

    public IQuery getQueryProperty3();

    public void setQueryProperty3(IQuery queryProperty3);

    public String getQueryProperty3Name();

    public void setQueryProperty3Name(String queryProperty3Name);

    public IQuery getQueryProperty4();

    public void setQueryProperty4(IQuery queryProperty4);

    public String getQueryProperty4Name();

    public void setQueryProperty4Name(String queryProperty4Name);


}
