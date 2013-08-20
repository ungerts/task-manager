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

package com.htm.taskinstance.jpa;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import com.htm.db.IDataAccessProvider;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Assigneduser;
import com.htm.entities.jpa.Attachment;
import com.htm.entities.jpa.Callbackcorrelationproperty;
import com.htm.entities.jpa.Humantaskinstance;
import com.htm.exceptions.DatabaseException;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.exceptions.IllegalArgumentException;
import com.htm.exceptions.UserException;
import com.htm.query.IQuery;
import com.htm.query.IQueryEvaluator;
import com.htm.query.jxpath.XPathQueryImpl;
import com.htm.query.jxpath.XPathUtils;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.ITaskInstance;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.taskmodel.IPresentationModel;
import com.htm.taskmodel.ITaskModel;
import com.htm.utils.Utilities;

public class TaskInstanceFactoryJPA extends TaskInstanceFactory {

    public static final short PRIORITY_DEFAULT = 0;

    public static final boolean SKIPABLE_DEFAULT = false;

    private IQueryEvaluator queryEval;

    private Logger log = Utilities.getLogger(this.getClass());

    private IDataAccessProvider dap;

    public TaskInstanceFactoryJPA() {
        this.dap = IDataAccessProvider.Factory.newInstance();
    }

    @Override
    public ITaskInstance createTaskInstance(String taskModelName,
                                            String taskInstanceName, Object input, String taskParentId,
                                            Set<ICorrelationProperty> correlationProperties,
                                            Timestamp expirationTime) throws HumanTaskManagerException {

        /*
           * Naturally the underlying task model is required to instantiate the
           * task
           */
        ITaskModel taskModel = dap.getHumanTaskModel(taskModelName);

        if (taskModel == null) {
            String errorMsg = "No task model with the name '" + taskModelName
                    + "' could be found.";
            log.error(errorMsg);
            throw new UserException(errorMsg);
        }

        /* Task instance is automatically set to the state CREATED */
        ITaskInstance taskInstance = new TaskInstanceWrapper(taskInstanceName);

        taskInstance.setTaskModel(taskModel);
        taskInstance.setName(taskInstanceName);

        /* Set the createdOn field with the current time */
        taskInstance.setCreatedOn(new Timestamp(Calendar.getInstance()
                .getTimeInMillis()));

        taskInstance.setExpirationTime(expirationTime);

        /*
           * The order of setting the values is important since some queries base
           * on values of other properties. E.g. priority queries base on input
           * data thus input data have to be set before.
           */

        /* Input data passed by the task parent */
        taskInstance.setInputData(input);

        /* Set properties required for task parent callback */
        taskInstance.setTaskParentId(taskParentId);
        taskInstance.setCorrelationProperties(correlationProperties);

        /* The priority */
        taskInstance.setPriority(evaluatePriorityQuery(taskModel.getPriority(),
                taskInstance));

        /* Evaluate whether the task is skipable or not */
        taskInstance.setSkipable(evaluateSkipableQuery(taskModel.getSkipable(),
                taskInstance));

        evaluatePositionQueries(taskModel, taskInstance);

        evaluateDurationQueries(taskModel, taskInstance);

        /* Set and evaluate deadlines */
        Timestamp startBy = evaluateTimestampQuery(taskModel.getStartBy(),
                taskInstance);
        if (startBy != null) {
            log.debug("Result of StartBy query : " + startBy);
            taskInstance.setStartBy(startBy);
        }
        Timestamp completeBy = evaluateTimestampQuery(
                taskModel.getCompleteBy(), taskInstance);
        // TODO
        if (completeBy != null) {
            log.debug("Result of CompleteBy query : " + completeBy);
            taskInstance.setCompleteBy(completeBy);
        }

        /*
           * Set presentation properties (title, subject, description) TODO
           * Queries in presentation models have to be supported TODO Criteria
           * have to be added to select appropriate presentation model
           */
        IPresentationModel presentModel = taskModel.getPresentationModel();

        if (presentModel != null) {
            taskInstance.setPresentationTitle(presentModel.getTitle());
            taskInstance.setPresentationSubject(presentModel.getSubject());
            taskInstance.setPresentationDescription(presentModel
                    .getDescription());
        }

        return taskInstance;
    }

    protected void evaluatePositionQueries(ITaskModel taskModel,
                                           ITaskInstance taskInstance) {
        /* The position X */
        try {
            log.debug("Evaluating position X");
            taskInstance.setPostionX(evaluateXPath(Double.class,
                    taskModel.getPositionX(), taskInstance));

            /* The position Y */
            taskInstance.setPostionY(evaluateXPath(Double.class,
                    taskModel.getPositionY(), taskInstance));
        } catch (IllegalArgumentException e) {
            // TODO default value from configuration
            log.error(
                    "Cannot evaluate position queries. Setting default value",
                    e);
            taskInstance.setPostionX(null);
            taskInstance.setPostionY(null);
        }
    }

    protected void evaluateDurationQueries(ITaskModel taskModel,
                                           ITaskInstance taskInstance) {

        log.debug("Evaluating duration queries");

        /* Duration Min */
        Duration min;
        try {
            min = evaluateXPath(Duration.class, taskModel.getDurationMin(),
                    taskInstance);

            if (min != null) {
                taskInstance.setDurationMin(min.getTimeInMillis(Calendar
                        .getInstance()));
            }

        } catch (IllegalArgumentException e) {
            // TODO default value from configuration
            log.error("Cannot evaluate min duration. Setting default value", e);
        }

        /* Duration Avg */
        try {
            Duration avg = evaluateXPath(Duration.class,
                    taskModel.getDurationAvg(), taskInstance);
            if (avg != null) {
                taskInstance.setDurationAvg(avg.getTimeInMillis(Calendar
                        .getInstance()));
            }
        } catch (IllegalArgumentException e) {
            // TODO default value from configuration
            log.error("Cannot evaluate avg duration. Setting default value", e);
        }

        /* Duration Max */
        try {
            Duration max = evaluateXPath(Duration.class,
                    taskModel.getDurationMax(), taskInstance);
            if (max != null) {
                taskInstance.setDurationMax(max.getTimeInMillis(Calendar
                        .getInstance()));
            }
        } catch (IllegalArgumentException e) {
            // TODO default value from configuration
            log.error("Cannot evaluate max duration. Setting default value", e);
        }
    }

    protected int evaluatePriorityQuery(IQuery query, Object context)
            throws com.htm.exceptions.IllegalArgumentException {

        /* Query can be null since the priority query is optional */
        if (query != null) {
            // List<?> priority = evaluateXPath(query, context);
            //
            // /*
            // * Obviously exactly one value is expected for priority and it
            // must
            // * be a number represented as string.
            // */
            // if (priority.size() == 1 && priority.get(0) instanceof String) {
            // String priorityAsString = (String) priority.get(0);
            // log.debug("Evaluating priority query - Determined value is '"
            // + priorityAsString + "'. "
            // + "Try to create integer from this value.");
            // return Integer.valueOf(priorityAsString);
            // } else {
            // log.debug("Evaluating priority query -  No value could be determined during evaluation.");
            // }

            // XPATH Number by default is mapped to Double
            int priority;
            Double tmpPriority;
            try {
                tmpPriority = evaluateXPath(Double.class, query, context);
                if (tmpPriority != null) {
                    priority = tmpPriority.intValue();
                } else {
                    log.debug("priority is null - Default priority value '"
                            + PRIORITY_DEFAULT + "' is used.");
                    priority = PRIORITY_DEFAULT;
                }

                log.debug("Evaluating priority query - Determined value is '"
                        + priority + "'.");
            } catch (com.htm.exceptions.IllegalArgumentException e) {
                log.error(
                        "Cannot evaluate priority query - Default priority value '"
                                + PRIORITY_DEFAULT + "' is used.", e);
                priority = PRIORITY_DEFAULT;
            }
            return priority;
        } else {
            log.debug("Evaluating priority query - No priority query was specified");
        }

        log.debug("Evaluating priority query - Default priority value '"
                + PRIORITY_DEFAULT + "' is used.");
        /*
           * If the query has failed for some reason return the default value for
           * priority
           */
        return PRIORITY_DEFAULT;
    }

    protected boolean evaluateSkipableQuery(IQuery query, Object context)
            throws com.htm.exceptions.IllegalArgumentException {

        /* Query can be null since the skipable query is optional */
        if (query != null) {
            // List<?> skipable = evaluateXPath(query, context);

            // /*
            // * Obviously exactly one value is expected for priority and it
            // must
            // * be a boolean represented as string
            // */
            // if (skipable.size() == 1 && skipable.get(0) instanceof String) {
            // String skipableAsString = (String) skipable.get(0);
            // log.debug("Evaluating skipable query - Determined value is '"
            // + skipableAsString + "'. "
            // + "Try to create boolean from this value.");
            // return Boolean.valueOf(skipableAsString);
            // } else {
            // log.debug("Evaluating skipable query -  No value could be found.");
            // }

            Boolean skipable;
            try {

                skipable = evaluateXPath(Boolean.class, query, context);
                if (skipable == null) {
                    log.debug("skipable is null - Default priority value '"
                            + SKIPABLE_DEFAULT + "' is used.");
                    skipable = SKIPABLE_DEFAULT;
                }
                log.debug("Evaluating skipable query - Determined value is '"
                        + skipable + "'");
            } catch (com.htm.exceptions.IllegalArgumentException e) {
                log.error(
                        "Cannot evaluate skipable query - Default priority value '"
                                + SKIPABLE_DEFAULT + "' is used.", e);
                skipable = SKIPABLE_DEFAULT;
            }
            return skipable;

        } else {
            log.debug("Evaluating skipable query - No skipable query was specified");
        }

        log.debug("Evaluating skipable query - Default skipable value '"
                + SKIPABLE_DEFAULT + "' is used.");
        /*
           * If the query has failed for some reason return the default value for
           * skipable
           */
        return SKIPABLE_DEFAULT;
    }

    protected Timestamp evaluateTimestampQuery(IQuery query, Object context)
            throws com.htm.exceptions.IllegalArgumentException {
        /* Query can be null since all timestamp queries are optional */
        if (query != null) {
            // List<?> timestamp = evaluateXPath(query, context);
            //
            // /*
            // * Obviously exactly one value is expected for a timestamp. It
            // must
            // * be a long integer (the time in millis) which is always
            // * represented by a String
            // */
            // if (timestamp.size() == 1 && timestamp.get(0) instanceof String)
            // {
            // String longIntegerAsString = (String) timestamp.get(0);
            // log.debug("Evaluating timestamp query - Determined value is '"
            // + timestamp.get(0)
            // + "'. Try to create timestamp from this value.");
            //
            // return new Timestamp(new Long(longIntegerAsString));

            // } else {
            // log.debug("Evaluating timestamp query - "
            // + "No value could be found for creating a timestamp.");
            // }
            Timestamp timestamp;
            boolean durationQuery = false;
            XPathQueryImpl xpathQuery;
            try {
                if (query instanceof XPathQueryImpl) {
                    xpathQuery = (XPathQueryImpl) query;
                    durationQuery = xpathQuery.isDurationQuery();
                }
                if (durationQuery) {
                    log.debug("Determing timestamp from duration.");
                    Duration duration = evaluateXPath(Duration.class, query,
                            context);
                    try {
                        timestamp = XPathUtils
                                .getTimestampFromDuration(duration);
                    } catch (Exception e) {
                        log.error(
                                "Cannot evaluate timestamp query - Default priority value '"
                                        + null + "' is used.", e);
                        timestamp = null;
                    }
                } else {
                    timestamp = evaluateXPath(Timestamp.class, query, context);
                }
                log.debug("Evaluating timestamp/duration query - Determined value is '"
                        + timestamp + "'");
            } catch (com.htm.exceptions.IllegalArgumentException e) {
                log.error(
                        "Cannot evaluate timestamp query - Default priority value '"
                                + null + "' is used.", e);
                timestamp = null;
            }
            return timestamp;
        } else {
            log.debug("Evaluating timestamp query - "
                    + "No query was specified for creating a timestamp.");
        }
        return null;
    }

    protected List<?> evaluateXPath(IQuery query, Object context)
            throws com.htm.exceptions.IllegalArgumentException {

        /* Only instantiate the query evaluator once */
        if (this.queryEval == null) {
            this.queryEval = IQueryEvaluator.Factory.newInstance(
                    query.getQueryLanguage(), (ITaskInstance) context);
        }

        return queryEval.evaluateQuery(query);

    }

    protected <T> T evaluateXPath(Class<T> resultClass, IQuery query,
                                  Object context) throws com.htm.exceptions.IllegalArgumentException {
        log.debug("evaluateXPath with defined ResultClass (" + resultClass
                + ")");
        if (query == null) {
            log.debug("Evaluating query - No query was specified");
            return null;
        }
        List<?> resultList = evaluateXPath(query, context);
        if (resultList.size() > 0) {
            try {
                return XPathUtils.getResultByType(resultClass,
                        resultList.get(0));
            } catch (Exception e) {
                throw new com.htm.exceptions.IllegalArgumentException(e);
            }
        }
        return null;
    }

    @Override
    public ITaskInstance createTaskInstanceFromEntity(
            WrappableEntity taskInstanceObject) {

        Humantaskinstance taskInstanceEntity = null;

        if (taskInstanceObject instanceof Humantaskinstance) {
            taskInstanceEntity = (Humantaskinstance) taskInstanceObject;
            return new TaskInstanceWrapper(taskInstanceEntity);

        } else {
            throw new RuntimeException("Invalid class error. "
                    + "Task instance entity object must be of type "
                    + Humantaskinstance.class);
        }

    }

    @Override
    public IAssignedUser createAssignedUserFromEntity(
            WrappableEntity assignedUserObject) {
        if (assignedUserObject == null) {
            return null;
        }

        /*
           * Check if it is a JPA object that represents the assigned user entity
           */
        Utilities.isValidClass(assignedUserObject, Assigneduser.class);
        return new AssignedUserWrapper((Assigneduser) assignedUserObject);
    }

    @Override
    public IAttachment createAttachmentFromEntity(
            WrappableEntity attachmentObject) {
        Attachment attachmentEntity = null;

        if (attachmentObject instanceof Attachment) {
            attachmentEntity = (Attachment) attachmentObject;
            return new AttachmentWrapper(attachmentEntity);

        } else {
            throw new RuntimeException("Invalid class error. "
                    + "Attachment entity object must be of type "
                    + Attachment.class);
        }
    }

    @Override
    public ICorrelationProperty createCorrelationPropertyFromEntity(
            WrappableEntity correlationPropsObject) {
        Callbackcorrelationproperty attachmentEntity = null;

        if (correlationPropsObject instanceof Callbackcorrelationproperty) {
            attachmentEntity = (Callbackcorrelationproperty) correlationPropsObject;
            return new CorrelationPropertiesWrapper(attachmentEntity);

        } else {
            throw new RuntimeException("Invalid class error. "
                    + "Correlation property entity object must be of type "
                    + Attachment.class);
        }
    }

    @Override
    public IAttachment createAttachment(String name) {
        return new AttachmentWrapper(name);
    }

    @Override
    public IAssignedUser createAssignedUser(String userId)
            throws DatabaseException {
        /*
           * Since assigned users must be unique within the database it is checked
           * if the assigned user already exists within the database
           */
        IAssignedUser assignedUser = dap.getAssignedUser(userId);
        if (assignedUser != null) {
            return assignedUser;
        }
        /* Assigned user does not exist and is created */
        return new AssignedUserWrapper(userId);

    }

    @Override
    public ICorrelationProperty createCorrelationProperty(String name) {
        return new CorrelationPropertiesWrapper(name);
    }

    @Override
    public IFault createFault(String name, Object faultData) {
        return new FaultImpl(name, faultData);
    }

    @Override
    public void evaluateQueryProperties(String tiid, String taskModelName) {
        log.debug("Evaluating query properties - tiid '" + tiid + "'");
        try {
            ITaskModel model = dap.getHumanTaskModel(taskModelName);
            ITaskInstance instance = dap.getTaskInstance(tiid);
            IQuery queryProperty1 = model.getQueryProperty1();
            if (queryProperty1 != null) {
                log.debug("Query Property 1: " + queryProperty1.getQuery());
                String queryProperty1String;
                try {
                    queryProperty1String = evaluateXPath(String.class,
                            queryProperty1, instance);
                    log.debug("Evaluating query property 1 - Determined value is '"
                            + queryProperty1String + "'.");
                    instance.setQueryProperty1(queryProperty1String);
                    instance.setQueryProperty1Name(model
                            .getQueryProperty1Name());
                } catch (com.htm.exceptions.IllegalArgumentException e) {
                    log.error(
                            "Cannot evaluate query property 1 - Default value '"
                                    + null + "' is used.", e);

                }
            } else {
                log.debug("Evaluating query property 1 - No query was specified");
            }

            IQuery queryProperty2 = model.getQueryProperty2();
            if (queryProperty2 != null) {
                log.debug("Query Property 2: " + queryProperty2.getQuery());
                String queryProperty2String;
                try {
                    queryProperty2String = evaluateXPath(String.class,
                            queryProperty2, instance);
                    log.debug("Evaluating query property 2 - Determined value is '"
                            + queryProperty2String + "'.");
                    instance.setQueryProperty2(queryProperty2String);
                    instance.setQueryProperty2Name(model
                            .getQueryProperty2Name());
                } catch (com.htm.exceptions.IllegalArgumentException e) {
                    log.error(
                            "Cannot evaluate query property 2 - Default value '"
                                    + null + "' is used.", e);

                }

            } else {
                log.debug("Evaluating query property 3 - No query was specified");
            }

            IQuery queryProperty3 = model.getQueryProperty3();
            if (queryProperty3 != null) {
                log.debug("Query Property 3: " + queryProperty3.getQuery());
                Double queryProperty3Double;
                try {
                    queryProperty3Double = evaluateXPath(Double.class,
                            queryProperty3, instance);
                    log.debug("Evaluating query property 3 - Determined value is '"
                            + queryProperty3Double + "'.");
                    instance.setQueryProperty3(queryProperty3Double);
                    instance.setQueryProperty3Name(model
                            .getQueryProperty3Name());
                } catch (com.htm.exceptions.IllegalArgumentException e) {
                    log.error(
                            "Cannot evaluate query property 3 - Default value '"
                                    + null + "' is used.", e);

                }

            } else {
                log.debug("Evaluating query property 3 - No query was specified");
            }

            IQuery queryProperty4 = model.getQueryProperty4();
            if (queryProperty4 != null) {
                log.debug("Query Property 4: " + queryProperty4.getQuery());
                Double queryProperty4Double;
                try {
                    queryProperty4Double = evaluateXPath(Double.class,
                            queryProperty4, instance);
                    log.debug("Evaluating query property 4 - Determined value is '"
                            + queryProperty4Double + "'.");
                    instance.setQueryProperty4(queryProperty4Double);
                    instance.setQueryProperty4Name(model
                            .getQueryProperty4Name());
                } catch (com.htm.exceptions.IllegalArgumentException e) {
                    log.error(
                            "Cannot evaluate query property 4 - Default value '"
                                    + null + "' is used.", e);

                }

            } else {
                log.debug("Evaluating query property 4 - No query was specified");
            }

        } catch (DatabaseException e) {
            log.error("Cannot fetch task model or instance (tiid='" + tiid
                    + "')", e);
        }

    }

}
