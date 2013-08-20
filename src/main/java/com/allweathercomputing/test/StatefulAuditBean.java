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

import java.rmi.RemoteException;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.log4j.Logger;

import com.htm.utils.Utilities;

@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class StatefulAuditBean {

    private Logger log;
    private String message = "Hallo Bean";
    private long id;
    private long timestamp;

    public StatefulAuditBean() {
        Random generator = new Random(System.currentTimeMillis());
        this.id = generator.nextLong();
        this.timestamp = System.currentTimeMillis();
    }

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        log.debug("StatefulAuditBean initialized");
        log.debug("Message: " + message);
    }

    @AfterBegin
    private void afterBegin() throws EJBException, RemoteException {
        log.debug("StatefulAuditBean afterbegin");
        log.debug("Message: " + message);
        log.debug("Bean class: " + this.getClass().getName());
        log.debug("Bean id: " + this.id);
        log.debug("Bean timestamp: " + this.timestamp);
    }

    @AfterCompletion
    private void afterCompletion(boolean committed) throws EJBException,
            RemoteException {
        String message = "Hallo Bean";
        log.debug("StatefulAuditBean aftercompletion. Commited: " + committed);
        log.debug("Reset message to " + message);
        this.message = message;
    }

    @BeforeCompletion
    private void beforeCompletion() throws EJBException, RemoteException {
        log.debug("StatefulAuditBean beforecompletion");
        log.debug("Message: " + message);
    }

    public String getMessage() {
        log.debug("StatefulAuditBean getMessage: " + message);
        log.debug("Bean class: " + this.getClass().getName());
        log.debug("Bean id: " + this.id);
        log.debug("Bean timestamp: " + this.timestamp);
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        log.debug("StatefulAuditBean setMessage: " + message);
        log.debug("Bean class: " + this.getClass().getName());
        log.debug("Bean id: " + this.id);
        log.debug("Bean timestamp: " + this.timestamp);
    }

}
