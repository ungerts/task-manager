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

package com.htm.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import com.htm.db.IDataAccessProvider;
import com.htm.events.EventHandler;
import com.htm.events.TaskDeadlineMissedEvent;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.ETaskInstanceState;
import com.htm.taskinstance.ITaskInstance;

public class TaskInstanceTimers {

    private static IDataAccessProvider dap;

    private static Logger log = Utilities.getLogger(TaskInstanceTimers.class);

    private static EventHandler eventHandler = EventHandler.newInstance();

    public static class SuspendUntilTimer extends TimerTask {

        private ITaskInstance taskInstance;

        public SuspendUntilTimer(ITaskInstance taskInstance) {
            this.taskInstance = taskInstance;
        }

        @Override
        public void run() {
            try {
                /* Start transaction */
                dap.beginTx();
                log.debug("Suspend until time expired at '"
                        + Utilities.formatTimestamp(Utilities.getCurrentTime()) + "'. Trying to resume task instance");
                taskInstance.setSuspended(false);
                dap.commitTx();
            } catch (HumanTaskManagerException e) {
                dap.rollbackTx();
                throw new RuntimeException(e);
            } finally {
                dap.close();
            }

        }

    }

    public static class ExpirationTimer extends TimerTask {

        private ITaskInstance taskInstance;

        public ExpirationTimer(ITaskInstance taskInstance) {
            this.taskInstance = taskInstance;
        }

        @Override
        public void run() {
            try {
                dap.beginTx();
                log.debug("Expiration timer fired at '"
                        + Utilities.formatTimestamp(Utilities.getCurrentTime())
                        + "'. Trying to transition task instance to the state : "
                        + ETaskInstanceState.OBSOLETE);
                /* When a task instance expires it goes into the state OBSOLETE */
                taskInstance.setStatus(ETaskInstanceState.OBSOLETE);

                String message = "The task instance '" + taskInstance.getId()
                        + "' was put into the '" + ETaskInstanceState.OBSOLETE
                        + "' state because the task has expired. ";

                notifySubscribersAboutMissedDeadline(taskInstance.getId(), message);

                dap.commitTx();
            } catch (HumanTaskManagerException e) {
                dap.rollbackTx();
                throw new RuntimeException(e);
            } finally {
                dap.close();
            }

        }

    }

    public static class CompleteByTimer extends TimerTask {

        private ITaskInstance taskInstance;

        public CompleteByTimer(ITaskInstance taskInstance) {
            this.taskInstance = taskInstance;
        }

        @Override
        public void run() {
            try {
                dap.beginTx();
                log.debug("CompleteBy timer fired at '"
                        + Utilities.formatTimestamp(Utilities.getCurrentTime())
                        + "'. Trying to transition task instance '" + taskInstance.getId() +
                        "' to the state : "
                        + ETaskInstanceState.FAILED);
                /* When the completion deadline was reached task goes into the state FAILED */
                taskInstance.setStatus(ETaskInstanceState.FAILED);
                String message = "The task instance '" + taskInstance.getId()
                        + "' was put into the '" + ETaskInstanceState.FAILED
                        + "' state because the completeBy deadline was missed. ";

                taskInstance.setFaultName("CompleteByDeadlineMissed");

                notifySubscribersAboutMissedDeadline(taskInstance.getId(), message);

                dap.commitTx();
            } catch (HumanTaskManagerException e) {
                dap.rollbackTx();
                throw new RuntimeException(e);
            } finally {
                dap.close();
            }

        }
    }


    public static void reactivateTimers() {
        log.debug("reactivateTimers - Sorry, timers are currently deactivated.");
//		try {
//			dataAccessProvider.beginTx();
//			/* Get all task instance from the database which are not in a final state yet */
//			List<ITaskInstance> nonFinalizedTaskInstances = 
//				dataAccessProvider.getNonFinalizedTaskInstances();
//			
//			/* Check for all non finalized task instances if they either have an 
//			 * expiration time or a suspend until time set (or both) and instantiate 
//			 * timer threads that either expire (expiration timer) or resume (suspend until timer)
//			 * a task instance if the timer has expired  */
//			Iterator<ITaskInstance> iter = nonFinalizedTaskInstances.iterator();
//			while (iter.hasNext()) {
//				ITaskInstance taskInstance = (ITaskInstance) iter.next();
//				activateExpirationTimer(taskInstance);
//				//activateCompleteByTimer(taskInstance);
//				activateSuspendUntilTimer(taskInstance);
//			}
//			
//			dataAccessProvider.commitTx();
//		} catch (HumanTaskManagerException e) {
//			dataAccessProvider.rollbackTx();
//			throw new RuntimeException(e);
//		} finally {
//			dataAccessProvider.close();
//		}
    }

    public static void activateExpirationTimer(ITaskInstance taskInstance) {
        log.debug("activateExpirationTimer - Sorry, timers are currently deactivated.");
//		Timestamp expirationTime = taskInstance.getExpirationTime();
//		//TODO Discuss if that can cause concurrency problems
//		
//		/* Check if an expiration timer was set for the task instance and only activate the timer
//		 * if the task instance is not already in an end state. */
//		if (expirationTime != null && !taskInstance.getStatus().isFinalState()) {
//			Timer timer = new Timer();
//			/* 
//			 * Start timer thread. If the expiration time is in the past the task instance
//			 * will expire instantly
//			 */
//			timer.schedule(new ExpirationTimer(taskInstance), 
//					new Date(expirationTime.getTime()));
//			log.debug("Installing timers - Expiration timer installed for task instance '" 
//					+ taskInstance.getId() 
//					+ "'. Task instance will expire: '" 				
//					+ Utilities.formatTimestamp(expirationTime) + "'.");
//			
//		}
    }

    public static void activateSuspendUntilTimer(ITaskInstance taskInstance) {
        log.debug("activateSuspendUntilTimer - Sorry, timers are currently deactivated.");
//		Timestamp suspendUntilTime = taskInstance.getSuspendUntil();
//		
//		/* Check if a suspendUntil timer was set for the task instance 
//		 * only activate the timer if the task instance is not already 
//		 * in an end state. Moreover the task must be already in the 'suspended'
//		 * state by the suspendUntil method in the task client interface.
//		 */
//		if (suspendUntilTime != null && 
//				taskInstance.isSuspended() && 
//				!taskInstance.getStatus().isFinalState()) {	
//		Timer timer = new Timer();
//		/* 
//		 * Start timer thread. If the suspend until time is in the past the task instance
//		 * is resumed immediately.
//		 */
//		timer.schedule(new SuspendUntilTimer(taskInstance), 
//				new Date(suspendUntilTime.getTime()));
//		log.debug("Installing timers - Suspend until timer installed for task instance '" 
//				+ taskInstance.getId() 
//				+ "'. Task instance is resumed: '" 
//				+ Utilities.formatTimestamp(suspendUntilTime) + "'.");
//		}

    }

    public static void activateCompleteByTimer(ITaskInstance taskInstance) {
        log.debug("activateCompleteByTimer - Sorry, timers are currently deactivated.");
//		Timestamp completeByTime = taskInstance.getCompleteBy();
//		
//		/* Check if a completeBy timer was set for the task instance 
//		 * only activate the timer if the task instance is not already 
//		 * in an end state
//		 */
//		if (completeByTime != null && !taskInstance.getStatus().isFinalState()) {	
//		Timer timer = new Timer();
//		/* 
//		 * Start timer thread. If the suspend until time is in the past the task instance
//		 * is resumed instantly
//		 */
//		timer.schedule(new CompleteByTimer(taskInstance), 
//				new Date(completeByTime.getTime()));
//		log.debug("Installing timers - CompleteBy timer installed for task instance '" 
//				+ taskInstance.getId() 
//				+ "'. Task instance has to be completed until: '" 
//				+ Utilities.formatTimestamp(completeByTime) + "'.");
//		}

    }

    protected static void notifySubscribersAboutMissedDeadline(String tiid, String reason) {
        log.debug("notifySubscribersAboutMissedDeadline - Sorry, timers are currently deactivated.");
//		/* Create the event */
//		TaskDeadlineMissedEvent event = new TaskDeadlineMissedEvent();
//		event.setDescription(reason);
//		event.setTaskInstance(tiid);
//		
//		eventHandler.notifySubscribers(event);
    }

}
