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

public enum ETaskClientActions {
    /* Task instance client related actions */
    ADD_ATTACHMENT, CLAIM, START, STOP, RELEASE, SUSPEND, SUSPEND_UNTIL,
    RESUME, COMPLETE, FAIL, SET_PRIORITY, GET_ATTACHMENT_INFOS,
    GET_ATTACHMENTS, DELETE_ATTACHMENTS, SKIP, FORWARD, DELEGATE, GET_TASK_INFO,
    GET_TASK_DESCRIPTION, SET_OUTPUT, DELETE_OUTPUT, SET_FAULT, DELETE_FAULT,
    GET_INPUT, GET_OUTPUT, GET_FAULT, QUERY,


    /**
     * Read a tuple of the work item view.
     */
    READ_WORK_ITEM_VIEW_TUPLE;

}
