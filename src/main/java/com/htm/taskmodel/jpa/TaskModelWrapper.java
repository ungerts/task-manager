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

package com.htm.taskmodel.jpa;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom2.Document;

import com.htm.dm.EHumanRoles;
import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Humantaskinstance;
import com.htm.entities.jpa.Humantaskmodel;
import com.htm.entities.jpa.Peoplequery;
import com.htm.entities.jpa.Presentationinformation;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.IQuery;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.IPeopleAssignment;
import com.htm.taskmodel.IPresentationModel;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;

/**
 * @author sew71sgp
 */
public class TaskModelWrapper implements ITaskModel {

    protected Humantaskmodel taskModelEntity;
    private Logger log;

    public TaskModelWrapper(Humantaskmodel adaptee) {
        this.taskModelEntity = adaptee;
        this.log = Utilities.getLogger(this.getClass());
    }

    public TaskModelWrapper() {
        this.taskModelEntity = new Humantaskmodel();
        this.log = Utilities.getLogger(this.getClass());
    }


    public void addPresentationModel(IPresentationModel model) {
        Presentationinformation presentationInfoEntity = (Presentationinformation) model.getAdaptee();

        List<Presentationinformation> presentationInfoEntities = taskModelEntity.getPresentationinformations();

        /* The list may be null if this is the first presentation model that is added */
        if (presentationInfoEntities == null) {
            presentationInfoEntities = new ArrayList<Presentationinformation>();
            taskModelEntity.setPresentationinformations(presentationInfoEntities);
        }
        /* In JPA relations between two entities are always bidirectional
          * TODO since currently only one presentation information per task model is supported
          * presentation information inserted before are simply overwritten
          */
        presentationInfoEntities.add(0, (Presentationinformation) model.getAdaptee());
        presentationInfoEntity.setHumantaskmodel(taskModelEntity);
    }

    public IQuery getCompleteBy() {
        //return ModelElementFactory.newInstance().createQuery(Utilities.getStringFromBLOB(taskModelEntity.getCompleteby()));
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getCompleteby());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }


    public String getId() {
        return Integer.toString(taskModelEntity.getId());
    }

    public String getName() {
        return taskModelEntity.getName();
    }

    public IPresentationModel getPresentationModel() {
        List<Presentationinformation> presentationInfoEntities = taskModelEntity.getPresentationinformations();
//		Set<IPresentationModel> presentationModels = new HashSet<IPresentationModel>(); 
        /* It's possible that The presentationInfoEntities object is null if the */
        if (presentationInfoEntities != null) {
            Iterator<Presentationinformation> iter = presentationInfoEntities.iterator();

            if (iter.hasNext()) {
                return ModelElementFactory.newInstance().createPresentationModel((Presentationinformation) iter.next());
            }
//TODO Add support for multiple presentation information			
//			while (iter.hasNext()) {
//				/* Create the presentation model from the presentation entity and add it to the list
//				 * of presentation models */
//				presentationModels.add(ModelElementFactory.newInstance().createPresentationModel(
//						(Presentationinformation) iter.next()));
//	
//			}		ho
        }
        return null;

    }

    public IQuery getPriority() {
        //return ModelElementFactory.newInstance().createQuery(
        //		Utilities.getStringFromBLOB(taskModelEntity.getPriority()));
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getPriority());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    public IQuery getSkipable() {
        //return ModelElementFactory.newInstance().createQuery(
        //		Utilities.getStringFromBLOB(taskModelEntity.getSkipable()));
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getSkipable());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    public IQuery getStartBy() {
        //return ModelElementFactory.newInstance().createQuery(
        //		Utilities.getStringFromBLOB(taskModelEntity.getStartby()));
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getStartby());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    public void setCompleteBy(IQuery query) {
        /* Query is currently simply stored as string i.e.
           * information about query language etc. is missing */
        try {
            taskModelEntity.setCompleteby(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }
    }


    public void setName(String name) {
        taskModelEntity.setName(name);
    }

    public void setPriority(IQuery priority) {
        //taskModelEntity.setPriority(Utilities.getBLOBFromString(priority.getQuery()));
        try {
            taskModelEntity.setPriority(Utilities.getBlobFromObject(priority));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }
    }

    public void setSkipable(IQuery skipable) {
        //taskModelEntity.setSkipable(Utilities.getBLOBFromString(skipable.getQuery()));
        try {
            taskModelEntity.setSkipable(Utilities.getBlobFromObject(skipable));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    public void setStartBy(IQuery startBy) {
        //taskModelEntity.setStartby(Utilities.getBLOBFromString(startBy.getQuery()));
        try {
            taskModelEntity.setStartby(Utilities.getBlobFromObject(startBy));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }
    }

    public Document getInputSchema() {
        return Utilities.getXMLFromString(Utilities.getStringFromBLOB(taskModelEntity.getInputschema()));
    }

    public Document getOutputSchema() {
        return Utilities.getXMLFromString(Utilities.getStringFromBLOB(taskModelEntity.getOutputschema()));
    }


    public Document getFaultSchema() {
        return Utilities.getXMLFromString(Utilities.getStringFromBLOB(taskModelEntity.getFaultschema()));
    }

    public void setInputSchema(Document inputSchema) {
        taskModelEntity.setInputschema(Utilities.getBLOBFromString(Utilities.getStringFromXMLDoc(inputSchema)));

    }

    public void setOutputSchema(Document outputSchema) {
        taskModelEntity.setOutputschema(Utilities.getBLOBFromString(Utilities.getStringFromXMLDoc(outputSchema)));
    }


    public void setFaultSchema(Document faultSchema) {
        taskModelEntity.setFaultschema(Utilities.getBLOBFromString(Utilities.getStringFromXMLDoc(faultSchema)));

    }

    public void setBusinessAdminsQuery(IPeopleAssignment query) {
        bindPeopleQuery(query, EHumanRoles.BUSINESS_ADMINISTRATOR);
    }

    public void setTaskStakeholdersQuery(IPeopleAssignment query) {
        bindPeopleQuery(query, EHumanRoles.TASK_STAKEHOLDER);
    }


    public void setPotentialOwnersQuery(IPeopleAssignment query) {
        bindPeopleQuery(query, EHumanRoles.POTENTIAL_OWNER);
    }

    public void setExcludedOwnersQuery(IPeopleAssignment query) {
        bindPeopleQuery(query, EHumanRoles.EXCLUDED_OWNER);
    }

    protected void bindPeopleQuery(IPeopleAssignment query, EHumanRoles humanRole) {
        Peoplequery peopleQueryEntity = (Peoplequery) query.getAdaptee();
        peopleQueryEntity.setHumanrole(humanRole.toString());

        /*
           * Bind the people query to this task model.
           * The JPA relation is bidirectional thus the people query has to be added to
           * the task model and the task model has to be added to the people query
           */
        List<Peoplequery> peopleQueriesOfTaskModel = taskModelEntity.getPeoplequeries();
        /*
           * If this is the first people query that is to be added to
           * the task model the list of people queries is null.
           */
        if (peopleQueriesOfTaskModel == null) {
            peopleQueriesOfTaskModel = new ArrayList<Peoplequery>();
            taskModelEntity.setPeoplequeries(peopleQueriesOfTaskModel);
        }
        peopleQueriesOfTaskModel.add(peopleQueryEntity);
        peopleQueryEntity.setHumantaskmodel(taskModelEntity);


    }

    public IPeopleAssignment getBusinessAdminsQuery() {
        Set<IPeopleAssignment> businessAdmins = getPeopleQuery(EHumanRoles.BUSINESS_ADMINISTRATOR);
        //TODO add support for multiple queries
        if (!businessAdmins.isEmpty()) {
            return businessAdmins.iterator().next();
        }

        return null;
    }


    public IPeopleAssignment getTaskStakeholdersQuery() {
        Set<IPeopleAssignment> taskStakeholders = getPeopleQuery(EHumanRoles.TASK_STAKEHOLDER);
        //TODO add support for multiple queries
        if (!taskStakeholders.isEmpty()) {
            return taskStakeholders.iterator().next();
        }

        return null;
    }

    public IPeopleAssignment getPotentialOwnersQuery() {
        Set<IPeopleAssignment> potentialOwners = getPeopleQuery(EHumanRoles.POTENTIAL_OWNER);
        //TODO add support for multiple queries
        if (!potentialOwners.isEmpty()) {
            return potentialOwners.iterator().next();
        }

        return null;
    }

    public IPeopleAssignment getExcludedOwnersQuery() {

        Set<IPeopleAssignment> excludedOwners = getPeopleQuery(EHumanRoles.EXCLUDED_OWNER);
        //TODO add support for multiple queries
        if (!excludedOwners.isEmpty()) {
            return excludedOwners.iterator().next();
        }

        return null;
    }

    protected Set<IPeopleAssignment> getPeopleQuery(EHumanRoles humanRole) {
        List<Peoplequery> peopleQueryEntities = taskModelEntity.getPeoplequeries();

        Set<IPeopleAssignment> peopleQueries = new HashSet<IPeopleAssignment>();

        if (peopleQueryEntities != null) {
            Iterator<Peoplequery> iter = peopleQueryEntities.iterator();
            /* Iterate over all people queries bound to this task model and
                * add those which have the desired human role.
                */
            while (iter.hasNext()) {
                Peoplequery peopleQueryEntity = (Peoplequery) iter.next();
                /* If the people query entity represents the desired human role
                     * the people query model is created and added to the set.
                     */
                if (peopleQueryEntity.getHumanrole().equals(humanRole.toString())) {
                    peopleQueries.add(ModelElementFactory.newInstance().createPeopleQuery(
                            peopleQueryEntity));
                }

            }
        }
        return peopleQueries;
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }


    /* (non-Javadoc)
      * @see com.htm.model.IPersistableElement#getAdaptee()
      */
    public WrappableEntity getAdaptee() {
        return taskModelEntity;
    }

    public Set<ITaskInstance> getTaskInstances() {
        List<Humantaskinstance> htInstanceEntities =
                taskModelEntity.getHumantaskinstances();

        Set<ITaskInstance> taskInstances = new HashSet<ITaskInstance>();
        /* If there are no human task instances the list may be empty */
        if (htInstanceEntities != null) {
            Iterator<Humantaskinstance> iter = htInstanceEntities.iterator();
            while (iter.hasNext()) {
                /* Create the model of the human task instance and add it to the list of
                     * instance models */
                taskInstances.add(Utilities.createTaskInstanceFromEntity(
                        iter.next()));
            }

        }
        return taskInstances;
    }

    public boolean isInstantiated() {
        List<Humantaskinstance> htInstanceEntities =
                taskModelEntity.getHumantaskinstances();
        /* True if the list of human task instances exist
           * and if it contains at least one element */
        return htInstanceEntities != null && !htInstanceEntities.isEmpty();
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ITaskModel#getBusinessAdminLiterals()
      */
    public Set<ILiteral> getBusinessAdminLiterals() {
        return getLiteralsByRole(EHumanRoles.BUSINESS_ADMINISTRATOR);
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ITaskModel#getExcludedOwnerLiterals()
      */
    public Set<ILiteral> getExcludedOwnerLiterals() {
        return getLiteralsByRole(EHumanRoles.EXCLUDED_OWNER);
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ITaskModel#getPotentialOwnerLiterals()
      */
    public Set<ILiteral> getPotentialOwnerLiterals() {
        return getLiteralsByRole(EHumanRoles.POTENTIAL_OWNER);
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ITaskModel#getTaskStakeholderLiterals()
      */
    public Set<ILiteral> getTaskStakeholderLiterals() {
        return getLiteralsByRole(EHumanRoles.TASK_STAKEHOLDER);
    }

    public void setBusinessAdminLiterals(Set<ILiteral> literals) {
        setLiterals(literals);
    }

    public void setExcludedOwnerLiterals(Set<ILiteral> literals) {
        setLiterals(literals);
    }

    public void setPotentialOwnerLiterals(Set<ILiteral> literals) {
        setLiterals(literals);
    }

    public void setTaskStakeholderLiterals(Set<ILiteral> literals) {
        setLiterals(literals);

    }


    /**
     * Returns all literals that are associated to the generic human role.
     *
     * @param expectedRole The genric human role.
     * @return a set of literals or an an empty
     *         set if no literal exist that corresponds to the given role.
     */
    protected Set<ILiteral> getLiteralsByRole(EHumanRoles expectedRole) {

        ModelElementFactory modelElementFac = ModelElementFactory.newInstance();
        Set<ILiteral> literalModelsByRole = new HashSet<ILiteral>();

        List<com.htm.entities.jpa.Literal> literalEntities = taskModelEntity.getLiterals();
        Iterator<com.htm.entities.jpa.Literal> iter = literalEntities.iterator();

        /* Get all literal entities and add these to the set of
           * literals that have the human role specified in the parameter expected role
           */
        while (iter.hasNext()) {
            /* Create literal model by using the model factory */
            ILiteral literalModel = modelElementFac.createLiteral((com.htm.entities.jpa.Literal) iter.next());
            if (literalModel.getGenericHumanRole().equals(expectedRole)) {
                literalModelsByRole.add(literalModel);
            }
        }

        return literalModelsByRole;
    }

    protected void setLiterals(Set<ILiteral> literalModels) {

        List<com.htm.entities.jpa.Literal> literalEntities = taskModelEntity.getLiterals();

        /*
           * If this is the first literal that is to be added to
           * the task model entity the list of literals is null.
           */
        if (literalEntities == null) {
            literalEntities = new ArrayList<com.htm.entities.jpa.Literal>();
            taskModelEntity.setLiterals(literalEntities);
        }

        Iterator<ILiteral> iter = literalModels.iterator();

        /* Convert all literal models to literal entities and add each to the task model entity */
        while (iter.hasNext()) {
            /* Get the wrapped literal entity object that has to be added to the
                * task model entity to get persisted.  */
            com.htm.entities.jpa.Literal literalEntity =
                    (com.htm.entities.jpa.Literal) ((ILiteral) iter.next()).getAdaptee();
            /*
                * The JPA relation is bidirectional thus the literal entity has to be added to
                * the task model entity and vice versa.
                */
            literalEntities.add(literalEntity);
            literalEntity.setHumantaskmodel(taskModelEntity);
        }

    }

    @Override
    public void setPositionX(IQuery query) {
        try {
            taskModelEntity.setPositionX(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public IQuery getPositionX() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getPositionX());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setPositionY(IQuery query) {
        try {
            taskModelEntity.setPositionY(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public IQuery getPositionY() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getPositionY());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setDurationMin(IQuery query) {
        try {
            taskModelEntity.setDurationMin(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public IQuery getDurationMin() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getDurationMin());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setDurationMax(IQuery query) {
        try {
            taskModelEntity.setDurationMax(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public IQuery getDurationMax() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getDurationMax());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setDurationAvg(IQuery query) {
        try {
            taskModelEntity.setDurationAvg(Utilities.getBlobFromObject(query));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public IQuery getDurationAvg() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getDurationAvg());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }


    /* Task Query Properties */

    @Override
    public IQuery getQueryProperty1() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getQueryProperty1());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setQueryProperty1(IQuery queryProperty1) {
        try {
            taskModelEntity.setQueryProperty1(Utilities.getBlobFromObject(queryProperty1));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public String getQueryProperty1Name() {
        return taskModelEntity.getQueryProperty1Name();
    }

    @Override
    public void setQueryProperty1Name(String queryProperty1Name) {
        taskModelEntity.setQueryProperty1Name(queryProperty1Name);

    }

    @Override
    public IQuery getQueryProperty2() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getQueryProperty2());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setQueryProperty2(IQuery queryProperty2) {
        try {
            taskModelEntity.setQueryProperty2(Utilities.getBlobFromObject(queryProperty2));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public String getQueryProperty2Name() {
        return taskModelEntity.getQueryProperty2Name();
    }

    @Override
    public void setQueryProperty2Name(String queryProperty2Name) {
        taskModelEntity.setQueryProperty2Name(queryProperty2Name);

    }

    @Override
    public IQuery getQueryProperty3() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getQueryProperty3());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }

    @Override
    public void setQueryProperty3(IQuery queryProperty3) {
        try {
            taskModelEntity.setQueryProperty3(Utilities.getBlobFromObject(queryProperty3));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public String getQueryProperty3Name() {
        return taskModelEntity.getQueryProperty3Name();
    }

    @Override
    public void setQueryProperty3Name(String queryProperty3Name) {
        taskModelEntity.setQueryProperty3Name(queryProperty3Name);

    }

    @Override
    public IQuery getQueryProperty4() {
        try {
            return (IQuery) Utilities.getObjectFromBlob(taskModelEntity.getQueryProperty4());
        } catch (HumanTaskManagerException e) {
            this.log.error("Cannot retrieve IQuery", e);
        }
        return null;
    }


    @Override
    public void setQueryProperty4(IQuery queryProperty4) {
        try {
            taskModelEntity.setQueryProperty4(Utilities.getBlobFromObject(queryProperty4));
        } catch (HumanTaskManagerException e) {
            log.error("Cannot write query into BLOB", e);
        }

    }

    @Override
    public String getQueryProperty4Name() {
        return taskModelEntity.getQueryProperty4Name();
    }

    @Override
    public void setQueryProperty4Name(String queryProperty4Name) {
        taskModelEntity.setQueryProperty4Name(queryProperty4Name);

    }


}
