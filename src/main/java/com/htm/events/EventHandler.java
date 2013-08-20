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

package com.htm.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventHandler {

    private static Map<Class<? extends IEvent>, List<IEventSubscriber>> subscriberByEvent = new HashMap<Class<? extends IEvent>, List<IEventSubscriber>>();

    private static EventHandler eventHandler;

    public static EventHandler newInstance() {

        /* Singleton Pattern */
        if (eventHandler == null) {
            eventHandler = new EventHandler();
        }

        return eventHandler;
    }

    protected EventHandler() {
        /* Hide constructor for to enforce instantiation with newInstance method */
    }

    public void notifySubscribers(IEvent event) {
        /* Get all subscribes of the event type */
        List<IEventSubscriber> eventSubscribers = subscriberByEvent.get(event
                .getClass());

        if (eventSubscribers != null) {
            Iterator<IEventSubscriber> iter = eventSubscribers.iterator();
            /* Notify all subscribers about the event */
            while (iter.hasNext()) {
                IEventSubscriber eventSubscriber = (IEventSubscriber) iter
                        .next();
                eventSubscriber.update(event);
            }
        }
    }

    public void subscribe(Class<? extends IEvent> eventType,
                          IEventSubscriber subscriber) {
        /* Get all subscribes of the event */
        List<IEventSubscriber> eventSubscribers = subscriberByEvent
                .get(eventType);

        /*
           * If this is the first subscriber that is subscribed to the event type,
           * the event type has to be added to the map.
           */
        if (eventSubscribers == null) {
            /* Initialize the list of subscribers */
            eventSubscribers = new ArrayList<IEventSubscriber>();
            subscriberByEvent.put(eventType, eventSubscribers);
        }

        eventSubscribers.add(subscriber);

    }

    public void unsuscribe(Class<? extends IEvent> eventType,
                           IEventSubscriber subscriber) {
        /* Get all subscribes of the event type */
        List<IEventSubscriber> eventSubscribers = subscriberByEvent
                .get(eventType);

        /*
           * If the event type doesn't exist do nothing otherwise remove the
           * subscriber
           */
        if (eventSubscribers != null) {
            eventSubscribers.remove(subscriber);
        }
    }

    public void removeAllSubscribers() {
        subscriberByEvent.clear();
    }

}
