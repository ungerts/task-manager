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

package com.htm.security;

import com.htm.ITaskClientInterface;
import com.htm.ITaskModelStore;
import com.htm.TaskParentInterface;

/**
 * This enum contains all actions that can be performed by a user via the external
 * interfaces of the HTM (task model store, task parent interface, task client interface).
 * Action is simply another term for the operations that are provided by the task
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see AuthorizationManager
 * @see ITaskClientInterface
 * @see TaskParentInterface
 * @see ITaskModelStore
 */
public enum EActions {

    /*
      * Task instance client related actions
      */
    ADD_ATTACHMENT, CLAIM, START, STOP, RELEASE, SUSPEND, SUSPEND_UNTIL,
    RESUME, COMPLETE, FAIL, SET_PRIORITY, GET_ATTACHMENT_INFOS,
    GET_ATTACHMENTS, DELETE_ATTACHMENTS, SKIP, FORWARD, DELEGATE, GET_TASK_INFO,
    GET_TASK_DESCRIPTION, SET_OUTPUT, DELETE_OUTPUT, SET_FAULT, DELETE_FAULT,
    GET_INPUT, GET_OUTPUT, GET_FAULT, QUERY,
    /**
     * Read a tuple of the work item view.
     */
    READ_WORK_ITEM_VIEW_TUPLE,

    /*
      * Task parent related actions
      */
    CREATE_TASK_INSTANCE, EXIT,

    /*
      * Administrative actions
      */
    ADD_TASK_MODEL, DELETE_TASK_MODEL, FORCE_DELETE_TASK_MODEL, UPDATE_TASK_MODEL, GET_TASK_MODELS, GET_TASK_MODEL, GET_TASK_MODEL_NAMES,
    ADD_LPG_DEFINITION, DELETE_LPG_DEFINITION, UPDATE_LPG_DEFINITION, GET_LOGICAL_PEOPLE_GROUP_DEF,
    DELETE_TASK_INSTANCES, DELETE_WORK_ITEM,

    /*
      * User Management
      */
    ADD_USER, DELETE_USER, GET_USER, ADD_GROUP, DELETE_GROUP, GET_GROUP, CHANGE_PASSWORD, LOGIN

}
