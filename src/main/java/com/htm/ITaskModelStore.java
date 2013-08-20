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

import java.util.List;
import java.util.Set;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskmodel.ILogicalPeopleGroupDef;
import com.htm.taskmodel.ITaskModel;


public interface ITaskModelStore {

    public void addLogicalPeopleGroupDef(ILogicalPeopleGroupDef lgpDef) throws HumanTaskManagerException;

    public boolean deleteLogicalPeopleGroup(String lpgDefName) throws HumanTaskManagerException;

    public boolean deleteTaskModel(String modelName) throws HumanTaskManagerException;

    public boolean forceDeleteTaskModel(String modelName) throws HumanTaskManagerException;

    public void addTaskModel(ITaskModel taskModel) throws HumanTaskManagerException;

    public boolean updateTaskModel(String name, ITaskModel newTaskModel) throws HumanTaskManagerException;

    public List<ITaskModel> getTaskModels() throws HumanTaskManagerException;

    public Set<String> getTaskModelNames() throws HumanTaskManagerException;

    public ITaskModel getTaskModel(String name) throws HumanTaskManagerException;

    public ILogicalPeopleGroupDef getLogicalPeopleGroupDef(String name) throws HumanTaskManagerException;

    public List<ILogicalPeopleGroupDef> getLogicalPeopleGroupDefs() throws HumanTaskManagerException;

    //TODO deleteTaskInstance (package festlegen)


}
