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

package com.htm.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


import com.htm.ITaskClientInterface;
import com.htm.TaskClientInterfaceImpl;
import com.htm.events.CreateWorkItemEvent;
import com.htm.events.EventHandler;
import com.htm.events.IEvent;
import com.htm.events.IEventSubscriber;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.query.views.WorkItemView;

public class EventSubscriptionTest extends TaskParentInterfaceTest {

    private EventSubscriber eventSubscriber;

    private EventHandler eventhandler;

    private ITaskClientInterface taskClient;

    @Before
    public void init() throws HumanTaskManagerException, FileNotFoundException, SQLException, IOException {
        /* Create test task models, logical people groups, user etc. */
        super.init();

        eventhandler = EventHandler.newInstance();
        /* Init the event subscriber that is used in the test cases */
        eventSubscriber = new EventSubscriber();
        /* The task client interface is also required in the test cases */
        taskClient = new TaskClientInterfaceImpl();
    }

    @Test
    public void testCreateWorkItemEvent() throws HumanTaskManagerException {

        /* Subscribe on the CreateWorkItemEvent */
        eventhandler.subscribe(CreateWorkItemEvent.class, eventSubscriber);
        try {
            dap.beginTx();

            /* Create the task instance during creation the work items are created as well,
                * thus the CreateWorkItem event is sent to all subscribers */
            String tiid = createTaskInstanceDummy();

            /* A potential owner claims the task instance this modifies the existing
                * work items (they are set to claimed), thus the ModifyWorkItem event is sent
                * to all its subscribers */
            String userId = getExpectedPotentialOwners()[0];
            initSecurityContext(userId, USER_PASSWORD);
            taskClient.claim(tiid);
            List<IEvent> events = eventSubscriber.getReceivedEvents();


            List<WorkItemView> expectedWorkItemViews = taskClient.query("TIID=" + tiid);
            assertEquals(expectedWorkItemViews.size(), events.size());


            dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            dap.rollbackTx();
            throw e;
        } finally {
            dap.closeTestCase();
        }
    }

    private class EventSubscriber implements IEventSubscriber {

        List<IEvent> receivedEvents = new ArrayList<IEvent>();

        public List<IEvent> getReceivedEvents() {
            return receivedEvents;
        }

        public void update(IEvent event) {
            receivedEvents.add(event);
        }

    }


}
