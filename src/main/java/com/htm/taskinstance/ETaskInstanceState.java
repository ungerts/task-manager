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

package com.htm.taskinstance;


public enum ETaskInstanceState {
    CREATED,
    READY,
    RESERVED,
    IN_PROGRESS,
    SUSPENDED_READY,
    SUSPENDED_RESERVED,
    SUSPENDED_IN_PROGRESS,
    COMPLETED,
    FAILED,
    ERROR,
    EXITED,
    OBSOLETE;

    public boolean isFinalState() {

        boolean isClosable = true;

        /*
               * If one of the states in the case statement is reached
               * the task can be closed.
               */
        switch (this) {
            case COMPLETED:
                break;
            case FAILED:
                break;
            case ERROR:
                break;
            case EXITED:
                break;
            case OBSOLETE:
                break;
            default:
                isClosable = false;
                break;
        }

        return isClosable;
    }

    public boolean canBeChangedTo(ETaskInstanceState newState) {

        switch (newState) {
            case CREATED:
                /* CREATED is the initial state of a task instance
                     * you can never transition back to it*/
                return false;
            case READY:
                if (this.equals(ETaskInstanceState.CREATED) ||
                        this.equals(ETaskInstanceState.RESERVED) ||
                        this.equals(ETaskInstanceState.SUSPENDED_READY)) {
                    return true;
                } else {
                    return false;
                }
            case SUSPENDED_READY:
                if (this.equals(ETaskInstanceState.READY)) {
                    return true;
                } else {
                    return false;
                }
            case RESERVED:
                if (this.equals(ETaskInstanceState.READY) ||
                        this.equals(ETaskInstanceState.IN_PROGRESS) ||
                        this.equals(ETaskInstanceState.SUSPENDED_RESERVED)) {
                    return true;
                } else {
                    return false;
                }
            case SUSPENDED_RESERVED:
                if (this.equals(ETaskInstanceState.RESERVED)) {
                    return true;
                } else {
                    return false;
                }
            case IN_PROGRESS:
                if (this.equals(ETaskInstanceState.RESERVED) ||
                        this.equals(ETaskInstanceState.READY) ||
                        this.equals(ETaskInstanceState.SUSPENDED_IN_PROGRESS)) {
                    return true;
                } else {
                    return false;
                }
            case SUSPENDED_IN_PROGRESS:
                if (this.equals(ETaskInstanceState.IN_PROGRESS)) {
                    return true;
                } else {
                    return false;
                }
            case COMPLETED:
                if (this.equals(ETaskInstanceState.IN_PROGRESS)) {
                    return true;
                } else {
                    return false;
                }
            case FAILED:
                if (this.equals(ETaskInstanceState.IN_PROGRESS)) {
                    return true;
                } else {
                    return false;
                }
            case ERROR:
                /* From each state except a 'final state' (e.g. COMPLETE, EXIT etc.)
                     * ERROR can be reached */
                if (!this.isFinalState()) {
                    return true;
                } else {
                    return false;
                }
            case EXITED:
                /* From each state except a 'final state' (e.g. COMPLETE, ERROR etc.)
                     * EXITED can be reached */
                if (!this.isFinalState()) {
                    return true;
                } else {
                    return false;
                }
            case OBSOLETE:
                /* From each state except a 'final state' (e.g. COMPLETE, ERROR etc.)
                     * OBSOLETE can be reached */
                if (!this.isFinalState()) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    public boolean isSuspended() {
        return this.equals(SUSPENDED_READY)
                || this.equals(SUSPENDED_IN_PROGRESS)
                || this.equals(SUSPENDED_RESERVED);
    }


}
