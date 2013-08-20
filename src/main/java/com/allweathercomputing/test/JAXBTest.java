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

import java.io.FileInputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ns.bpel4people.ws_humantask._200803.TTask;

public class JAXBTest {

    private static Logger log;

    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        log = Logger.getLogger(JAXBTest.class);
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("org.oasis_open.docs.ns.bpel4people.ws_humantask._200803");
            Unmarshaller u = jc.createUnmarshaller();
            Object task = u.unmarshal(new FileInputStream(args[0]));
            JAXBElement<TTask> task1 = (JAXBElement<TTask>) task;

            log.info("Object class: " + task.getClass().getName());
            JXPathContext context = JXPathContext.newContext(task);
            Iterator pointerIterator = context.iteratePointers("//*");
            //Object pointerObj;
            Pointer pointer;
            while (pointerIterator.hasNext()) {
                pointer = (Pointer) pointerIterator.next();
                log.info("Object class: " + pointer.getClass().getName());
                log.info("Pointer: " + pointer.asPath());
            }

        } catch (Exception e) {
            log.error(e);
        }

    }

}
