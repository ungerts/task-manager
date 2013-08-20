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
import java.util.Iterator;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.htm.query.jxpath.XPathUtils;

public class JXPathTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        BasicConfigurator.configure();
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer();
            StreamSource source = new StreamSource(new File(args[0]));
            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            TaskInstance instance = new TaskInstance(1, 2, result.getNode(),
                    "hallo");
            JXPathContext context = JXPathContext.newContext(instance);
            context.registerNamespace("htm",
                    "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803");
            Object xpathResult = context
                    .getValue("/inputData/htm:task/htm:interface");

            System.out.println(xpathResult);
            System.out.println(xpathResult.getClass().getName());

            Pointer pointer = context
                    .getPointer("/inputData/htm:task/htm:name[@lang='de']/text()");
            System.out.println(pointer.asPath());
            System.out.println(pointer.getNode().getClass().getName());
            System.out.println(pointer.getClass().getName());

            pointer = context.getPointer("/inputData/htm:task/htm:name/text()");
            System.out.println(pointer.asPath());
            System.out.println(pointer.getNode().getClass().getName());
            System.out.println(pointer.getClass().getName());

            pointer = context.getPointer("/tiid");
            System.out.println(pointer.asPath());
            System.out.println(pointer.getNode().getClass().getName());
            System.out.println(pointer.getClass().getName());

            pointer = context
                    .getPointer("count(/inputData/htm:task/htm:name) > 10");
            System.out.println(pointer.asPath());
            System.out.println(pointer.getNode().getClass().getName());
            System.out.println(pointer.getClass().getName());

            System.out.println("Iteratortest");
            Iterator iterator = context
                    .iteratePointers("/inputData/htm:task/htm:name");

            while (iterator.hasNext()) {
                pointer = (Pointer) iterator.next();
                System.out.println(pointer.asPath());
                System.out.println(pointer.getNode().getClass().getName());
                System.out.println(pointer.getClass().getName());
                System.out.println(Node.class.isAssignableFrom(pointer.getNode().getClass()));
                System.out.println(pointer.getNode().getClass().isAssignableFrom(Node.class));
                System.out.println(XPathUtils.getResultByType(String.class, pointer.getNode()));
            }


            pointer = context
                    .getPointer("'Hallo'");

            System.out.println(pointer.asPath());
            System.out.println(pointer.getNode().getClass().getName());
            System.out.println(pointer.getClass().getName());
            System.out.println(XPathUtils.getResultByType(String.class, pointer.getNode()));


            System.out.println("Java XPath");
            DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(args[0]));

            XPathFactory fac2 = XPathFactory.newInstance();
            XPath xpath = fac2.newXPath();
            MyNameSpaceContext myNamespaces = new MyNameSpaceContext();
            myNamespaces
                    .addNamespace("htm",
                            "http://docs.oasis-open.org/ns/bpel4people/ws-humantask/200803");
            xpath.setNamespaceContext(myNamespaces);
            Object xpathResult1;
            XPathExpression compiled = xpath
                    .compile("/htm:task/htm:name/@lang");
            xpathResult1 = compiled.evaluate(doc, XPathConstants.NODESET);
            System.out.println(xpathResult1);
            System.out.println(xpathResult1.getClass().getName());
            NodeList list = (NodeList) xpathResult1;
            Node node;
            for (int i = 0; i < list.getLength(); i++) {
                node = list.item(i);
                System.out.println(node.getClass().getName());
                System.out.println(node.getTextContent());

            }

            compiled = xpath
                    .compile("'hallo'");
            xpathResult1 = compiled.evaluate(doc, XPathConstants.STRING);
            System.out.println(xpathResult1);
            System.out.println(xpathResult1.getClass().getName());

            DatatypeFactory dtfac = DatatypeFactory.newInstance();


            // System.out.println(Double.parseDouble("2"));
            // //System.out.println(Double.parseDouble("Hallo"));
            // System.out.println(Boolean.parseBoolean("2"));
            // System.out.println(Boolean.parseBoolean("Hallo"));
            // System.out.println(Boolean.parseBoolean("1"));
            // System.out.println(Boolean.parseBoolean("true"));
            // System.out.println(Boolean.parseBoolean("True"));
            // System.out.println(Boolean.parseBoolean("TRUE"));
            // System.out.println(Boolean.parseBoolean("tRuE"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
