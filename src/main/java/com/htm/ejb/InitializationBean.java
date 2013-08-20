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

package com.htm.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.htm.utils.Utilities;

//@LocalBean
//@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
//@TransactionManagement(TransactionManagementType.CONTAINER)
//@TransactionAttribute(TransactionAttributeType.REQUIRED)
//TODO Roles(Security)
@Singleton(name = "InitializationBean")
@Startup
public class InitializationBean {

    private static final String LOG4J_PROPERTIES = "/META-INF/log4j.properties";

    private Logger log;

    //private static java.util.logging.Logger log1 = java.util.logging.Logger.getLogger("Tut nicht!");

    @PostConstruct
    private void init() {
        //log1.log(Level.WARNING, "Shit");
        Properties log4jConf = new Properties();

        String logFile = System.getProperty("java.io.tmpdir") + "htm" + File.separator + "taskmanager.log";

        //String logFile = System.getProperty("com.sun.aas.instanceRoot")
        //		+ File.separator + "logs" + File.separator + "taskmanager.log";

        //log1.log(Level.WARNING, "Log-File: " + logFile);

        try {
            log4jConf.load(this.getClass()
                    .getResourceAsStream(LOG4J_PROPERTIES));
            log4jConf.setProperty("log4j.appender.FILE.File", logFile);
            PropertyConfigurator.configure(log4jConf);
            //log1.log(Level.WARNING, "ShitShit");
        } catch (IOException e) {
            //log1.log(Level.WARNING, "ShitShitShit", e);
            e.printStackTrace();
            throw new RuntimeException("Log4j config file " + LOG4J_PROPERTIES
                    + " cannot be loaded", e);
        }

        this.log = Utilities.getLogger(this.getClass());
        log.debug("Log4j configured successfully. Log-File: " + logFile);
        log.debug("InstanceRoot: " + System.getProperty("com.sun.aas.instanceRoot"));
        log.debug("TmpDir: " + System.getProperty("java.io.tmpdir"));

    }

}
