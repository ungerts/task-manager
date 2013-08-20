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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.log4j.Logger;

import com.htm.utils.Utilities;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class StatelessAuditBean {

    @EJB
    private StatefulAuditBean bean;

    private Logger log;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        log.debug("StatelessAuditBean initialized");
    }

    public String getMessage(String inputMessage) throws Exception {
        String output;
        output = bean.getMessage();
        bean.setMessage(inputMessage);
        output = output + " " + bean.getMessage();
        AuditPOJO pojo = new AuditPOJO();
        pojo.duplicateMessage();
        return output + ";" + bean.getMessage();
    }

}
