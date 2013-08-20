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

package com.allweathercomputing.htm.datatypes;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.htm.ejb.ITaskModelStoreBean;
import com.htm.ejb.ITaskParentBean;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.jxpath.JXpathQueryEvaluator;
import com.htm.query.jxpath.XPathQueryImpl;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.TaskModelUtils;
import com.htm.utils.Utilities;

@WebService(name = "TaskClient", targetNamespace = "http://htm.allweathercomputing.com/datatypes", endpointInterface = "com.allweathercomputing.htm.datatypes.TaskClient")
public class TaskClientImpl implements TaskClient {

    public String QUERY_INPUT_DATA = "/inputData/*[1]";
    public String QUERY_TASK_MODEL = "/taskModel/*[1]";

    @EJB
    private ITaskParentBean bean;

    @EJB
    private ITaskModelStoreBean taskModelStoreBean;

    private Logger log;

    public TaskClientImpl() {
        this.log = Utilities.getLogger(this.getClass());
    }

    @Override
    public String createTaskInstance(String modelName, String instanceName,
                                     Object inputData) throws Exception {
        log.debug("Try to create task instance '" + instanceName
                + "' from task model '" + modelName + "'");
        //Document inputDataDocument = transformInputData(inputData);
        try {
            Document inputDataDocument = transformInputData(inputData, QUERY_INPUT_DATA);
            return bean.createTaskInstance("64", null, modelName, instanceName, inputDataDocument, null, null);
        } catch (HumanTaskManagerException e) {
            log.error("Cannot create task instance", e);
            throw new Exception("Cannot create task instance", e);
        } catch (Exception e) {
            log.error("Cannot create task instance", e);
            throw new Exception("Cannot create task instance", e);
        }

        //return null;
    }

    @Override
    public String deployTaskModel(Object taskModel) throws Exception {
        log.debug("Try to deploy WS-HumanTask task model");
        try {
            Document taskModelDoc = transformInputData(taskModel, QUERY_TASK_MODEL);
            ITaskModel model = TaskModelUtils.createTaskModel(taskModelDoc);
            taskModelStoreBean.addTaskModel(model);
            return model.getName();
        } catch (HumanTaskManagerException e) {
            log.error("Cannot deploy task model", e);
            throw new Exception("Cannot deploy task model", e);
        } catch (Exception e) {
            log.error("Cannot deploy task model", e);
            throw new Exception("Cannot deploy task model", e);
        }
        //return null;

    }

    protected Document transformInputData(Object inputData, String xPathQuery) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // TransformerFactory tff = TransformerFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Transformer transformer = tff.newTransformer();
            if (inputData != null) {
                if (inputData instanceof Node) {
                    Node inputDataNode = (Node) inputData;
                    JXpathQueryEvaluator evaluater = new JXpathQueryEvaluator(
                            inputDataNode.getOwnerDocument());
                    XPathQueryImpl query = new XPathQueryImpl(
                            xPathQuery, null);
                    List<?> resultList = evaluater.evaluateQuery(query);
                    if (resultList.size() > 0
                            && resultList.get(0) instanceof Element) {
                        Element elem = (Element) resultList.get(0);
                        Document document = db.newDocument();
                        Node plainInputDataNode = document.importNode(elem,
                                true);
                        document.appendChild(plainInputDataNode);

                        return document;

                    }

                }
                throw new IllegalArgumentException("Invalid input data");

            }
        } catch (Exception e) {
            log.debug("Cannot transform input data to document", e);
            throw new Exception("Cannot transform input data to document", e);
        } catch (IllegalArgumentException e) {
            throw new Exception("Cannot transform input data to document", e);
        }
        return null;
    }

}
