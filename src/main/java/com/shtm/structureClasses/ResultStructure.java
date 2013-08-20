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

package com.shtm.structureClasses;

import java.util.ArrayList;
import java.util.List;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;

public class ResultStructure<I, R> {

    List<ResultStructure<I, R>> children = new ArrayList<ResultStructure<I, R>>();
    ResultStructure<I, R> parent = null;
    R result;
    I identifier;

    private ResultStructure(I indentifier, R result) {
        this.result = result;
        this.identifier = indentifier;
    }

    // this constructor is necessary to create the XOperation-Classes
    public ResultStructure() {
    }

    public ResultStructure(TaskStructure taskStructure,
                           IResultOperation<I, R> operation) throws HumanTaskManagerException {
        this.result = operation.getOperationResult(taskStructure);
        this.identifier = operation.getIdentifier(taskStructure);

        if (taskStructure.isSelectedTaskSubTask()) {
            ResultStructure<I, R> parentResult = getRootPathStructure(
                    taskStructure.getSelectedTaskParentTask(), operation);
            parentResult.addChild(this);
        }

        for (TaskStructure subTaskStructure : taskStructure.getSelectedTaskSubTasks()) {
            this.addChild(getDecendantStructure(subTaskStructure, operation));
        }

    }

    public List<ResultStructure<I, R>> getChildren() {
        return children;
    }

    public void addChild(ResultStructure<I, R> child) {
        this.children.add(child);
        child.setParent(this);
    }

    public ResultStructure<I, R> getParent() {
        return parent;
    }

    private void setParent(ResultStructure<I, R> parent) {
        this.parent = parent;
    }

    public R getResult() {
        return result;
    }

    public I getIdentifier() {
        return this.identifier;
    }

    public static interface IResultOperation<I, R> {
        public R getOperationResult(TaskStructure task)
                throws HumanTaskManagerException;

        public I getIdentifier(TaskStructure task)
                throws HumanTaskManagerException;
    }

    class AttachmentOperation implements
            IResultOperation<String, List<IAttachment>> {
        private String parameter;

        public AttachmentOperation(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public List<IAttachment> getOperationResult(TaskStructure task)
                throws HumanTaskManagerException {
            return task.getSelectedTaskAttachment(parameter);
        }

        @Override
        public String getIdentifier(TaskStructure task)
                throws HumanTaskManagerException {
            return task.getSelectedTask_Id();
        }
    }

    class InputOperation implements IResultOperation<String, Object> {

        @Override
        public Object getOperationResult(TaskStructure task)
                throws HumanTaskManagerException {
            return task.getSelectedTaskInput();
        }

        public String getIdentifier(TaskStructure task) {
            return task.getSelectedTask_Id();
        }
    }

    class OutputOperation implements IResultOperation<String, Object> {

        @Override
        public Object getOperationResult(TaskStructure task)
                throws HumanTaskManagerException {
            return task.getSelectedTaskOutput();
        }

        public String getIdentifier(TaskStructure task) {
            return task.getSelectedTask_Id();
        }
    }

    class FaultOperation implements IResultOperation<String, IFault> {

        @Override
        public IFault getOperationResult(TaskStructure task)
                throws HumanTaskManagerException {
            return task.getSelectedTaskFault();
        }

        public String getIdentifier(TaskStructure task) {
            return task.getSelectedTask_Id();
        }
    }

    private ResultStructure<I, R> getRootPathStructure(TaskStructure task,
                                                       IResultOperation<I, R> operation) throws HumanTaskManagerException {
        if (!task.isSelectedTaskSubTask()) {
            R operationResult = operation.getOperationResult(task);
            I identifier = operation.getIdentifier(task);
            return new ResultStructure<I, R>(identifier, operationResult);
        } else {
            ResultStructure<I, R> parentResult = getRootPathStructure(task
                    .getSelectedTaskParentTask(), operation);
            R operationResult = operation.getOperationResult(task);
            ResultStructure<I, R> result = new ResultStructure<I, R>(
                    identifier, operationResult);
            parentResult.addChild(result);
            return result;
        }
    }

    private ResultStructure<I, R> getDecendantStructure(
            TaskStructure taskStructure, IResultOperation<I, R> operation)
            throws HumanTaskManagerException {
        R operationResult = operation
                .getOperationResult(taskStructure);
        I identifier = operation.getIdentifier(taskStructure);
        ResultStructure<I, R> result = new ResultStructure<I, R>(identifier,
                operationResult);
        for (TaskStructure subTaskStructure : taskStructure.getSelectedTaskSubTasks()) {
            result.addChild(getDecendantStructure(subTaskStructure, operation));
        }
        return result;
    }
}
