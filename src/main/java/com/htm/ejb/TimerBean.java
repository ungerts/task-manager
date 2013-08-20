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

import java.sql.Timestamp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.htm.TaskClientInterfaceImpl;
import com.htm.ejb.timer.TimerInfo;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

//@LocalBean
@Stateless(name = "TimerBean")
@PersistenceContext(name = JEEUtils.PERSISTENCE_MANAGER_HTM)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
// TODO Roles(Security)
public class TimerBean {

    @Resource
    private TimerService timerService;

    private TaskClientInterfaceImpl taskClient;
    private Logger log;

    @PostConstruct
    private void init() {
        this.log = Utilities.getLogger(this.getClass());
        this.taskClient = new TaskClientInterfaceImpl();
    }

    @Timeout
    private void onTimeout(Timer timer) {
        //timer.getInfo()

    }

    public void createTimer(Timestamp timestamp, TimerInfo info) {
        log.debug("Create timer on " + timestamp + "for task " + info.getTiid());
        this.timerService.createTimer(timestamp, info);
    }

}
