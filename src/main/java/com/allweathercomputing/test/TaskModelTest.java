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
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.IQuery;
import com.htm.query.jxpath.JXpathQueryEvaluator;
import com.htm.query.jxpath.XPathUtils;
import com.htm.taskmodel.ILogicalPeopleGroupArgumentDef;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;
import com.htm.taskmodel.ModelElementFactory;

public class TaskModelTest {

    private static final Logger log = Logger.getLogger(TaskModelTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            log.info("Los...");
            ModelElementFactory factory = ModelElementFactory.newInstance();
            ITaskModel model = factory.createTaskModel();
            log.info(model.getClass());
            model.setName("Model 1");
            Hashtable<String, String> namespaces = new Hashtable<String, String>();
            namespaces
                    .put("ht",
                            "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803");
            IQuery query = factory.createXPathQuery(
                    "/inputData/ht:task/ht:name", namespaces);

            model.setSkipable(query);
            IQuery query2 = model.getSkipable();
            log.info(query2.getClass());

            model.setPriority(query);
            query2 = model.getPriority();
            log.info(query2.getClass());

            model.setStartBy(query);
            query2 = model.getStartBy();
            log.info(query2.getClass());

            ILogicalPeopleGroupDef group = factory.createPeopleGroupDefinition("engineers");
            ILogicalPeopleGroupArgumentDef argument = factory.createPeopleGroupArgumentDef();
            argument.setName("skill");
            group.addArgumentDefinition(argument);

            //factory.create


            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer transformer = factory1.newTransformer();
            StreamSource source = new StreamSource(new File(args[0]));
            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            TaskInstance instance = new TaskInstance(1, 2, result.getNode(),
                    "hallo");

            JXpathQueryEvaluator evaluator = new JXpathQueryEvaluator(instance);
            List<?> result1 = evaluator.evaluateQuery(query);
            for (Object obj : result1) {
                log.info(obj.getClass());
                if (obj instanceof Node) {
                    log.info("Node");

                }
                log.info("Text: " + XPathUtils.getResultByType(Timestamp.class, obj));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }

    }

}
