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

import javax.annotation.PostConstruct;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.allweathercomputing.htm.org.IOrganizationManager;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

@Stateless
public class TestBean {

    private Logger log;

    //private static java.util.logging.Logger log1 = java.util.logging.Logger.getLogger("Tut nicht!");

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        log.debug("TestBean initialized");
    }

    public String testOrgManagerBean() throws Exception {

        try {
            IOrganizationManager bean = JEEUtils.getOrganizationManagerBean();
            return bean.getClass().getName();
        } catch (HumanTaskManagerException e) {
            throw new Exception(e);
        }

    }

    public String testSessionContext() throws Exception {

        try {
            SessionContext ctx = JEEUtils.getSessionContext();
            return ctx.getCallerPrincipal().getName();
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

}
