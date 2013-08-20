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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.htm.dm.EHumanRoles;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.IQuery;
import com.htm.query.IQueryEvaluator;
import com.htm.query.jxpath.IJXPathQuery;
import com.htm.query.jxpath.JXpathQueryEvaluator;
import com.htm.query.jxpath.NamespaceEvaluator;
import com.htm.query.jxpath.XPathQueryImpl;
import com.htm.query.jxpath.XPathUtils;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskmodel.ILiteral;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;
import com.htm.utils.Utilities;

public class DOMTaskModelParserTest {

    public static final String PREFIX_WSHT = "ht";

    public static final String NAMESPACE_WSHT = "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803";

    public static final String PREFIX_WSHT_TYPES = "htt";

    public static final String NAMESPACE_WSHT_TYPES = "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/types/200803";

    public static final String PREFIX_ATM = "atm";

    public static final String NAMESPACE_ATM = "http://htm.allweathercomputing.com/taskmodel";

    public static final String QUERY_SKIPABLE = "/ht:task/atm:skipable";

    public static final String QUERY_TASK_MODEL_NAME = "/ht:task/@name";

    public static final String QUERY_EXCLUDED_OWNER = "/ht:task/ht:peopleAssignments/ht:excludedOwners";

    public static final String QUERY_BUSINESS_ADMINISTRATOR = "/ht:task/ht:peopleAssignments/ht:businessAdministrators";

    public static final String QUERY_STAKEHOLDER = "/ht:task/ht:peopleAssignments/ht:taskStakeholders";

    public static final String QUERY_POTENTIAL_OWNERS = "/ht:task/ht:peopleAssignments/ht:potentialOwners";

    public static final String QUERY_LITERAL_USER = "//htt:user";

    public static final String QUERY_START_DEADLINE = "/ht:task/ht:deadlines/ht:startDeadline/ht:for | /ht:task/ht:deadlines/ht:startDeadline/ht:until";

    public static final String QUERY_COMPLETION_DEADLINE = "/ht:task/ht:deadlines/ht:completionDeadline/ht:for | /ht:task/ht:deadlines/ht:completionDeadline/ht:until";

    public static final String QUERY_PRIORITY = "/ht:task/ht:priority";

    public static final String QUERY_DURATION_MAX = "/ht:task/atm:durations/atm:max";

    public static final String QUERY_DURATION_AVG = "/ht:task/atm:durations/atm:avg";

    public static final String QUERY_DURATION_MIN = "/ht:task/atm:durations/atm:min";

    public static final String QUERY_ALTITUDE = "/ht:task/atm:position/atm:altitude";

    public static final String QUERY_LATITUDE = "/ht:task/atm:position/atm:latitude";

    public static final String QUERY_LONGITUDE = "/ht:task/atm:position/atm:longitude";

    public static final String QUERY_QUERY_PROPERTY1 = "/ht:task/atm:queryProperties/atm:queryProperty[@queryPropertyNumber = '1']";

    public static final String QUERY_QUERY_PROPERTY2 = "/ht:task/atm:queryProperties/atm:queryProperty[@queryPropertyNumber = '2']";

    public static final String QUERY_QUERY_PROPERTY3 = "/ht:task/atm:queryProperties/atm:queryProperty[@queryPropertyNumber = '3']";

    public static final String QUERY_QUERY_PROPERTY4 = "/ht:task/atm:queryProperties/atm:queryProperty[@queryPropertyNumber = '4']";

    private static Logger log;

    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        log = Utilities.getLogger(DOMTaskModelParserTest.class);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();

            Document document = parser.parse(new File(args[0]));

            XPathQueryImpl xpathQuery = new XPathQueryImpl();
            xpathQuery.setQuery("//ht:from");
            xpathQuery
                    .addNamespace("ht",
                            "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803");
            JXpathQueryEvaluator evaluator = new JXpathQueryEvaluator(document);
            List<?> result = evaluator.evaluateQuery(xpathQuery);
            log.info("Found objets: " + result.size());
            for (Object obj : result) {
                // log.info("Class: " + obj.getClass().getName());
                if (obj instanceof Element) {
                    Element fromElem = (Element) obj;

                    Attr logicalPeopleGroupAttr = fromElem
                            .getAttributeNode("logicalPeopleGroup");

                    Element literalElement = (Element) fromElem
                            .getElementsByTagNameNS(
                                    "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803",
                                    "literal").item(0);

                    if (logicalPeopleGroupAttr != null) {
                        log.info("LPG: "
                                + logicalPeopleGroupAttr.getTextContent());
                    } else if (literalElement != null) {
                        log.info("Literal: " + literalElement.getTextContent());
                        JXpathQueryEvaluator literalEvaluator = new JXpathQueryEvaluator(
                                literalElement);
                        XPathQueryImpl literalQuery = new XPathQueryImpl();
                        literalQuery.setQuery("//htt:user");
                        literalQuery
                                .addNamespace("htt",
                                        "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/types/200803");
                        literalEvaluator.evaluateQuery(literalQuery);
                    } else {
                        log.info("Expression: " + fromElem.getTextContent());
                    }

                    // log.info("LPG: " +
                    // fromElem.getAttributeNode("logicalPeopleGroup"));
                    //
                    // log.info("Literal: " +
                    // fromElem.getElementsByTagNameNS("http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803",
                    // "literal"));
                    //
                    // log.info("Text content: " + fromElem.getTextContent());

                }
            }
            XPathQueryImpl query = new XPathQueryImpl();
            query.setQuery("//*");
            evaluator.evaluateQuery(query);
            log.info(StringEscapeUtils.escapeXml("$request.amount >= 10000"));

            XPathQueryImpl query1 = new XPathQueryImpl();
            query1.setQuery("/ht:task/atm:skipable");
            query1.addNamespace("ht",
                    "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803");
            query1.addNamespace("atm",
                    "http://htm.allweathercomputing.com/taskmodel");
            List<?> skipList = evaluator.evaluateQuery(query1);
            log.info("skipList.size: " + skipList.size());
            if (skipList.size() > 0) {
                Element skipNode = (Element) skipList.get(0);
                createModelIQuery(skipNode);
            }
            createTaskModel(document);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }

    }

    public static ITaskModel createTaskModel(Document taskModelDoc) {
        log.debug("Create task model");
        ModelElementFactory factory = ModelElementFactory.newInstance();
        ITaskModel model = factory.createTaskModel();
        try {
            addModelName(model, taskModelDoc);
            addStaffQueries(model, taskModelDoc);
            addDeadlineQueries(model, taskModelDoc);
            addPriorityQuery(model, taskModelDoc);
            addSkipableQuery(model, taskModelDoc);
            addPositionQueries(model, taskModelDoc);

            addDurationQueries(model, taskModelDoc);
            addQueryPropertyQueries(model, taskModelDoc);
        } catch (HumanTaskManagerException e) {

        }
        return model;
    }

    private static void addPriorityQuery(ITaskModel model, Document taskModelDoc)
            throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // priority
            IJXPathQuery priorityQuery = getWSHTQuery(QUERY_PRIORITY);
            Element priorityElement = evaluateXPath(Element.class,
                    priorityQuery, evaluator);
            if (priorityElement != null) {

                IJXPathQuery priorityModelQuery = createModelIQuery(priorityElement);
                model.setPriority(priorityModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create priority query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addSkipableQuery(ITaskModel model, Document taskModelDoc)
            throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // priority
            IJXPathQuery skipableQuery = getWSHTQuery(QUERY_SKIPABLE);
            Element skipableElement = evaluateXPath(Element.class,
                    skipableQuery, evaluator);
            if (skipableElement != null) {

                IJXPathQuery skipableModelQuery = createModelIQuery(skipableElement);
                model.setSkipable(skipableModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create skipable query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addDurationQueries(ITaskModel model,
                                           Document taskModelDoc) throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // duration min
            IJXPathQuery minQuery = getWSHTQuery(QUERY_DURATION_MIN);
            Element minElement = evaluateXPath(Element.class, minQuery,
                    evaluator);
            if (minElement != null) {

                IJXPathQuery minModelQuery = createModelIQuery(minElement);
                model.setDurationMin(minModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create duration min query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // duration avg
            IJXPathQuery avgQuery = getWSHTQuery(QUERY_DURATION_AVG);
            Element avgElement = evaluateXPath(Element.class, avgQuery,
                    evaluator);
            if (avgElement != null) {

                IJXPathQuery avgModelQuery = createModelIQuery(avgElement);
                model.setDurationAvg(avgModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create duration avg query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // duration max
            IJXPathQuery maxQuery = getWSHTQuery(QUERY_DURATION_MAX);
            Element maxElement = evaluateXPath(Element.class, maxQuery,
                    evaluator);
            if (maxElement != null) {

                IJXPathQuery maxModelQuery = createModelIQuery(maxElement);
                model.setDurationMax(maxModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create duration max query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addQueryPropertyQueries(ITaskModel model,
                                                Document taskModelDoc) throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // queryProperty1
            IJXPathQuery queryProperty1Query = getWSHTQuery(QUERY_QUERY_PROPERTY1);
            Element queryProperty1Element = evaluateXPath(Element.class,
                    queryProperty1Query, evaluator);
            if (queryProperty1Element != null) {

                IJXPathQuery queryProperty1ModelQuery = createModelIQuery(queryProperty1Element);
                model.setQueryProperty1(queryProperty1ModelQuery);
                // log.info(queryProperty1Element.getAttribute("queryPropertyName"));
                model.setQueryProperty1Name(queryProperty1Element
                        .getAttribute("queryPropertyName"));
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create queryProperty1 query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // queryProperty2
            IJXPathQuery queryProperty2Query = getWSHTQuery(QUERY_QUERY_PROPERTY2);
            Element queryProperty2Element = evaluateXPath(Element.class,
                    queryProperty2Query, evaluator);
            if (queryProperty2Element != null) {

                IJXPathQuery queryProperty2ModelQuery = createModelIQuery(queryProperty2Element);
                model.setQueryProperty2(queryProperty2ModelQuery);
                log.info(queryProperty2Element
                        .getAttribute("queryPropertyName"));
                model.setQueryProperty2Name(queryProperty2Element
                        .getAttribute("queryPropertyName"));
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create queryProperty2 query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // queryProperty3
            IJXPathQuery queryProperty3Query = getWSHTQuery(QUERY_QUERY_PROPERTY3);
            Element queryProperty3Element = evaluateXPath(Element.class,
                    queryProperty3Query, evaluator);
            if (queryProperty3Element != null) {

                IJXPathQuery queryProperty3ModelQuery = createModelIQuery(queryProperty3Element);
                model.setQueryProperty3(queryProperty3ModelQuery);
                log.info(queryProperty3Element
                        .getAttribute("queryPropertyName"));
                model.setQueryProperty3Name(queryProperty3Element
                        .getAttribute("queryPropertyName"));
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create queryProperty3 query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // queryProperty4
            IJXPathQuery queryProperty4Query = getWSHTQuery(QUERY_QUERY_PROPERTY4);
            Element queryProperty4Element = evaluateXPath(Element.class,
                    queryProperty4Query, evaluator);
            if (queryProperty4Element != null) {

                IJXPathQuery queryProperty4ModelQuery = createModelIQuery(queryProperty4Element);
                model.setQueryProperty4(queryProperty4ModelQuery);
                log.info(queryProperty4Element
                        .getAttribute("queryPropertyName"));
                model.setQueryProperty4Name(queryProperty4Element
                        .getAttribute("queryPropertyName"));
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create queryProperty4 query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addPositionQueries(ITaskModel model,
                                           Document taskModelDoc) throws HumanTaskManagerException {
        // TODO altitude
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // longitude
            IJXPathQuery longitudeQuery = getWSHTQuery(QUERY_LONGITUDE);
            Element longitudeElement = evaluateXPath(Element.class,
                    longitudeQuery, evaluator);
            if (longitudeElement != null) {

                IJXPathQuery longitudeModelQuery = createModelIQuery(longitudeElement);
                model.setPositionX(longitudeModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create longitude query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // latitude
            IJXPathQuery latitudeQuery = getWSHTQuery(QUERY_LATITUDE);
            Element latitudeElement = evaluateXPath(Element.class,
                    latitudeQuery, evaluator);
            if (latitudeElement != null) {

                IJXPathQuery latitudeModelQuery = createModelIQuery(latitudeElement);
                model.setPositionY(latitudeModelQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create latitude query for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addDeadlineQueries(ITaskModel model,
                                           Document taskModelDoc) throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        try {
            // start
            IJXPathQuery startQuery = getWSHTQuery(QUERY_START_DEADLINE);
            Element startElement = evaluateXPath(Element.class, startQuery,
                    evaluator);
            if (startElement != null) {
                boolean duration = startElement.getLocalName()
                        .equalsIgnoreCase("for");
                log.debug("Duration query found: " + duration);

                IJXPathQuery startDeadlineQuery = createModelIQuery(startElement);
                startDeadlineQuery.setDurationQuery(duration);
                model.setStartBy(startDeadlineQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create start deadline for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // completion
            IJXPathQuery completionQuery = getWSHTQuery(QUERY_COMPLETION_DEADLINE);
            Element completionElement = evaluateXPath(Element.class,
                    completionQuery, evaluator);
            if (completionElement != null) {
                boolean duration = completionElement.getLocalName()
                        .equalsIgnoreCase("for");
                log.debug("Duration query found: " + duration);

                IJXPathQuery completionDeadlineQuery = createModelIQuery(completionElement);
                completionDeadlineQuery.setDurationQuery(duration);
                model.setCompleteBy(completionDeadlineQuery);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create completion deadline for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void addStaffQueries(ITaskModel model, Document taskModelDoc)
            throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);

        try {
            // pOwner
            IQuery pOwnerQuery = getWSHTQuery(QUERY_POTENTIAL_OWNERS);
            Element pOwnerElement = evaluateXPath(Element.class, pOwnerQuery,
                    evaluator);
            if (pOwnerElement != null) {
                createStaffQuery(model, pOwnerElement,
                        EHumanRoles.POTENTIAL_OWNER);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create potential owner staff queries for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // excludedUser
            IQuery eOwnerQuery = getWSHTQuery(QUERY_EXCLUDED_OWNER);
            Element eOwnerElement = evaluateXPath(Element.class, eOwnerQuery,
                    evaluator);
            if (eOwnerElement != null) {
                createStaffQuery(model, eOwnerElement,
                        EHumanRoles.EXCLUDED_OWNER);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create excluded owner staff queries for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // stakeholder
            IQuery stakeholderQuery = getWSHTQuery(QUERY_STAKEHOLDER);
            Element stakeholderElement = evaluateXPath(Element.class,
                    stakeholderQuery, evaluator);
            if (stakeholderElement != null) {
                createStaffQuery(model, stakeholderElement,
                        EHumanRoles.TASK_STAKEHOLDER);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create stakeholder staff queries for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

        try {
            // business administrators
            IQuery baQuery = getWSHTQuery(QUERY_STAKEHOLDER);
            Element baElement = evaluateXPath(Element.class, baQuery, evaluator);
            if (baElement != null) {
                createStaffQuery(model, baElement,
                        EHumanRoles.BUSINESS_ADMINISTRATOR);
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create business administrator staff queries for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static void createStaffQuery(ITaskModel model, Element rootElement,
                                         EHumanRoles humanRole) {
        Attr logicalPeopleGroupAttr = rootElement
                .getAttributeNode("logicalPeopleGroup");

        Element literalElement = (Element) rootElement.getElementsByTagNameNS(
                NAMESPACE_WSHT, "literal").item(0);

        if (logicalPeopleGroupAttr != null) {
            log.error("LPG assignment currently not supported and ignored");
            // throw new
            // IllegalArgumentException("Logical People Groups currently not supported");
        } else if (literalElement != null) {
            log.info("Literal assignment");
            try {
                createLiteral(model, rootElement, humanRole);
            } catch (IllegalArgumentException e) {
                log.error("Ignoring literal query due to evaluation fault");
            }
            // JXpathQueryEvaluator literalEvaluator = new JXpathQueryEvaluator(
            // literalElement);
            // XPathQueryImpl literalQuery = new XPathQueryImpl();
            // literalQuery.setQuery("//htt:user");
            // literalQuery
            // .addNamespace("htt",
            // "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/types/200803");
            // literalEvaluator.evaluateQuery(literalQuery);
        } else {
            log.error("LPG assignment currently not supported and ignored");
        }

    }

    private static void createLiteral(ITaskModel model, Element rootElement,
                                      EHumanRoles humanRole) throws IllegalArgumentException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", rootElement);
        IQuery literalQuery = getWSHTQuery(QUERY_LITERAL_USER);
        try {
            List<?> users = evaluateXPath(literalQuery, evaluator);
            if (users.size() > 0) {
                Set<ILiteral> literalModels = new HashSet<ILiteral>();

                for (Object obj : users) {
                    literalModels.add(ModelElementFactory.newInstance()
                            .createLiteral(
                                    XPathUtils.getResultByType(String.class,
                                            obj), humanRole));
                }
                if (humanRole.equals(EHumanRoles.POTENTIAL_OWNER)) {
                    model.setPotentialOwnerLiterals(literalModels);
                } else if (humanRole.equals(EHumanRoles.TASK_STAKEHOLDER)) {
                    model.setTaskStakeholderLiterals(literalModels);
                } else if (humanRole.equals(EHumanRoles.EXCLUDED_OWNER)) {
                    model.setExcludedOwnerLiterals(literalModels);
                } else if (humanRole.equals(EHumanRoles.BUSINESS_ADMINISTRATOR)) {
                    model.setBusinessAdminLiterals(literalModels);
                }
            }
        } catch (IllegalArgumentException e) {
            String message = "Cannot create staff queries for model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new IllegalArgumentException(message, e);
        } catch (Exception e) {
            String message = "Cannot create literal model for task model '"
                    + model.getName() + "'";
            log.error(message, e);
            throw new IllegalArgumentException(message, e);
        }
    }

    private static void addModelName(ITaskModel model, Document taskModelDoc)
            throws HumanTaskManagerException {
        IQueryEvaluator evaluator = IQueryEvaluator.Factory.newInstance(
                "XPATH", taskModelDoc);
        IQuery query = getWSHTQuery(QUERY_TASK_MODEL_NAME);
        try {
            String taskModelName = evaluateXPath(String.class, query, evaluator);
            if (taskModelName == null) {
                String message = "Task model name must not be null";
                log.error(message);
                throw new HumanTaskManagerException(message);
            }
            log.debug("addModelName - Name of task model is '" + taskModelName
                    + "'");
            model.setName(taskModelName);
        } catch (IllegalArgumentException e) {
            String message = "Cannot evaluate task model name.";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static IJXPathQuery getWSHTQuery(String query) {
        IJXPathQuery iQuery = new XPathQueryImpl(query);
        iQuery.addNamespace(PREFIX_ATM, NAMESPACE_ATM);
        iQuery.addNamespace(PREFIX_WSHT, NAMESPACE_WSHT);
        iQuery.addNamespace(PREFIX_WSHT_TYPES, NAMESPACE_WSHT_TYPES);

        return iQuery;
    }

    private static IJXPathQuery createModelIQuery(Element queryNode)
            throws HumanTaskManagerException {
        log.debug("create Query for Element: " + queryNode.getTextContent());
        XPathQueryImpl iQuery;
        String query = queryNode.getTextContent();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NamespaceEvaluator namespaceEvaluator = new NamespaceEvaluator(
                queryNode);
        xpath.setNamespaceContext(namespaceEvaluator);
        try {
            xpath.compile(query);
            iQuery = new XPathQueryImpl(query,
                    namespaceEvaluator.getNamespaces());
            return iQuery;
        } catch (XPathExpressionException e) {
            String message = "Cannot compile query '" + query + "'";
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }

    }

    private static List<?> evaluateXPath(IQuery query, IQueryEvaluator queryEval)
            throws com.htm.exceptions.IllegalArgumentException {

        /* Only instantiate the query evaluator once */
        if (queryEval == null) {
            throw new IllegalArgumentException(
                    "Query evaluator must not be null");
        }

        return queryEval.evaluateQuery(query);

    }

    private static <T> T evaluateXPath(Class<T> resultClass, IQuery query,
                                       IQueryEvaluator queryEval)
            throws com.htm.exceptions.IllegalArgumentException {
        log.debug("evaluateXPath with defined ResultClass (" + resultClass
                + ")");

        List<?> resultList = evaluateXPath(query, queryEval);
        if (resultList.size() > 0) {
            try {
                return XPathUtils.getResultByType(resultClass,
                        resultList.get(0));
            } catch (Exception e) {
                throw new com.htm.exceptions.IllegalArgumentException(e);
            }
        }
        return null;
    }

}
