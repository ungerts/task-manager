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

import java.util.Hashtable;

import com.htm.dm.EHumanRoles;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Humantaskmodel;
import com.htm.entities.jpa.Literal;
import com.htm.entities.jpa.Logicalpeoplegroupdef;
import com.htm.entities.jpa.LpgArgumentdef;
import com.htm.entities.jpa.Peoplequery;
import com.htm.entities.jpa.Peoplequeryargument;
import com.htm.entities.jpa.Presentationinformation;
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
import com.htm.utils.Utilities;

public class ModelElementFactoryJPA extends ModelElementFactory {

    public ModelElementFactoryJPA() {

    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.ModelElementFactory#createPeopleGroupDefinition(java.lang.String)
      */
    @Override
    public ILogicalPeopleGroupDef createPeopleGroupDefinition(
            WrappableEntity lpgDefinitionObject) {

        if (lpgDefinitionObject == null) {
            return null;
        }

        /* Check if it is a JPA object that represents the LPG definition entity */
        Utilities
                .isValidClass(lpgDefinitionObject, Logicalpeoplegroupdef.class);

        /* Create logical people group definition wrapper */
        return new PeopleGroupDefJpaWrapper(
                (Logicalpeoplegroupdef) lpgDefinitionObject);
    }

    /*
      * (non-Javadoc)
      *
      * @see com.htm.ModelElementFactory#createTaskModel(java.lang.String)
      */
    @Override
    public ITaskModel createTaskModel(WrappableEntity taskModelObject) {

        if (taskModelObject == null) {
            return null;
        }

        /* Check if it is a JPA object that represents the task model entity */
        Utilities.isValidClass(taskModelObject, Humantaskmodel.class);

        return new TaskModelWrapper((Humantaskmodel) taskModelObject);

    }

    @Override
    public ILogicalPeopleGroupDef createPeopleGroupDefinition(String name) {
        return new PeopleGroupDefJpaWrapper(name);
    }

    @Override
    public IPeopleAssignment createPeopleQuery(WrappableEntity peopleQueryObject) {

        if (peopleQueryObject == null) {
            return null;
        }

        /*
           * Check if it is a JPA object that represents the people query entity
           */
        Utilities.isValidClass(peopleQueryObject, Peoplequery.class);

        return new PeopleQueryWrapper((Peoplequery) peopleQueryObject);
    }

    @Override
    public IPeopleQueryArgument createPeopleGroupArgument(
            WrappableEntity lpgArgumentObject) {

        if (lpgArgumentObject == null) {
            return null;
        }

        /*
           * Check if it is a JPA object that represents the people query argument
           * entity
           */
        Utilities.isValidClass(lpgArgumentObject, Peoplequeryargument.class);

        return new PeopleQueryArgumentWrapper(
                (Peoplequeryargument) lpgArgumentObject);
    }

    @Override
    public ILogicalPeopleGroupArgumentDef createPeopleGroupArgumentDef(
            WrappableEntity lpgArgumentDefObject) {

        if (lpgArgumentDefObject == null) {
            return null;
        }

        /*
           * Check if it is a JPA object that represents the LPG argument
           * definition entity
           */
        Utilities.isValidClass(lpgArgumentDefObject, LpgArgumentdef.class);

        return new LpgGroupArgDefWrapper((LpgArgumentdef) lpgArgumentDefObject);
    }

    @Override
    public IPresentationModel createPresentationModel(
            WrappableEntity presentationObject) {

        if (presentationObject == null) {
            return null;
        }

        /*
           * Check if it is a JPA object that represents the presentation entity
           */
        Utilities.isValidClass(presentationObject,
                Presentationinformation.class);

        return new PresentationModelWrapper(
                (Presentationinformation) presentationObject);
    }

    @Override
    public IQuery createQuery(String queryValue) {

        if (queryValue == null) {
            return null;
        }

        /*
           * Currently query objects are simply created from strings which
           * represent the query expression i.e. there is no seperate JPA entity
           * for queries.
           */
        IQuery query = new XPathQueryImpl();
        query.setQuery(queryValue);

        return query;
    }

    @Override
    public IPeopleQueryArgument createPeopleGroupArgument(
            ILogicalPeopleGroupArgumentDef argumentDef) {
        return new PeopleQueryArgumentWrapper(argumentDef);
    }

    @Override
    public ILogicalPeopleGroupArgumentDef createPeopleGroupArgumentDef() {
        return new LpgGroupArgDefWrapper();
    }

    @Override
    public IPeopleAssignment createPeopleQuery(
            ILogicalPeopleGroupDef lpgDefinition) {
        return new PeopleQueryWrapper(lpgDefinition);
    }

    @Override
    public IPresentationModel createPresentationModel() {
        return new PresentationModelWrapper();
    }

    @Override
    public IQuery createQuery() {
        return new XPathQueryImpl();
    }

    @Override
    public ITaskModel createTaskModel() {
        return new TaskModelWrapper();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.dm.taskmodel.ModelElementFactory#createLiteral(com.htm.dm.entities
      * .WrappableEntity)
      */
    @Override
    public ILiteral createLiteral(WrappableEntity literalObject) {
        if (literalObject == null) {
            return null;
        }

        /*
           * Check if it is a literal object that represents the literal JPA
           * entity
           */
        Utilities.isValidClass(literalObject, Literal.class);

        return new LiteralWrapper((Literal) literalObject);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.htm.dm.taskmodel.ModelElementFactory#createLiteral(java.lang.String,
      * com.htm.dm.EHumanRoles)
      */
    @Override
    public ILiteral createLiteral(String orgEntityId, EHumanRoles role) {
        ILiteral literalModel = new LiteralWrapper();
        literalModel.setOrganizationalEntityId(orgEntityId);
        literalModel.setGenericHumanRole(role);

        return literalModel;
    }

    @Override
    public IQuery createXPathQuery(String queryValue,
                                   Hashtable<String, String> namespaces) {
        if (queryValue == null) {
            return null;
        }

        XPathQueryImpl query = new XPathQueryImpl();
        query.setQuery(queryValue);
        query.setNamespaces(namespaces);

        return query;
    }

}
