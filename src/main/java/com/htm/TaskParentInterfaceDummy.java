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

package com.htm;

import java.sql.Timestamp;
import java.util.Set;

import org.jdom2.Document;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.ICorrelationProperty;

public class TaskParentInterfaceDummy implements TaskParentInterface {

    public String createTaskInstance(String taskParentId,
                                     Set<ICorrelationProperty> correlationProperties,
                                     String taskModelName, String taskInstanceName,
                                     Document inputMessage, Set<IAttachment> attachments,
                                     Timestamp expirationTime) throws HumanTaskManagerException {
        return "1";
    }

    public void exit(String tiid) throws HumanTaskManagerException {

    }

    public String createTaskInstance(String taskParentId,
                                     Set<ICorrelationProperty> correlationProperties,
                                     String taskModelName, String taskInstanceName, Object inputData,
                                     Set<IAttachment> attachments, Timestamp expirationTime)
            throws HumanTaskManagerException {
        // TODO Auto-generated method stub
        return null;
    }

}
