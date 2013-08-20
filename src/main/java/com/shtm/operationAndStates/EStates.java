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

package com.shtm.operationAndStates;

import com.shtm.exceptions.SHTMException;

/**
 * This enumeration contains all states of a task
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public enum EStates {
    CREATED, READY, RESERVED, IN_PROGRESS, SUSPENDED_READY, SUSPENDED_RESERVED, SUSPENDED_IN_PROGRESS, COMPLETED, FAILED, ERROR, EXITED, OBSOLETE;

    /**
     * Return true if this state can be a parent state for the specified state
     *
     * @param otherState
     * @return
     * @throws SHTMException
     */
    public boolean allowsChildState(EStates otherState)
            throws SHTMException {
        PartialStatesOrder.DirectDescendants parent = PartialStatesOrder
                .getDirectDescendants(this);
        return parent.isDescendant(otherState);
    }

    public static EStates convertToStructuredInstanceState(
            String state) throws SHTMException {
        if (state.equals("CREATED")) {
            return EStates.CREATED;
        } else if (state.equals("READY")) {
            return EStates.READY;
        } else if (state.equals("RESERVED")) {
            return EStates.RESERVED;
        } else if (state.equals("IN_PROGRESS")) {
            return EStates.IN_PROGRESS;
        } else if (state.equals("SUSPENDED_READY")) {
            return EStates.SUSPENDED_READY;
        } else if (state.equals("SUSPENDED_RESERVED")) {
            return EStates.SUSPENDED_RESERVED;
        } else if (state.equals("SUSPENDED_IN_PROGRESS")) {
            return EStates.SUSPENDED_IN_PROGRESS;
        } else if (state.equals("COMPLETED")) {
            return EStates.COMPLETED;
        } else if (state.equals("FAILED")) {
            return EStates.FAILED;
        } else if (state.equals("ERROR")) {
            return EStates.ERROR;
        } else if (state.equals("EXITED")) {
            return EStates.EXITED;
        } else if (state.equals("OBSOLETE")) {
            return EStates.OBSOLETE;
        } else {
            String excMsg = "Unknown state from HumanTaskMananger";
            throw new SHTMException(excMsg);
        }

    }

    public EStates getSuspendedState() {
        switch (this) {
            case READY:
                return EStates.SUSPENDED_READY;
            case RESERVED:
                return EStates.SUSPENDED_RESERVED;
            case IN_PROGRESS:
                return EStates.SUSPENDED_IN_PROGRESS;
            default:
                return null;
        }
    }

    public EStates getResumedState() {
        switch (this) {
            case SUSPENDED_READY:
                return EStates.READY;
            case SUSPENDED_RESERVED:
                return EStates.RESERVED;
            case SUSPENDED_IN_PROGRESS:
                return EStates.IN_PROGRESS;
            default:
                return null;
        }
    }

    public boolean isSuspendedState() {
        boolean isSuspended = true;
        switch (this) {
            case SUSPENDED_READY:
                break;
            case SUSPENDED_RESERVED:
                break;
            case SUSPENDED_IN_PROGRESS:
                break;
            default:
                isSuspended = false;
                break;
        }
        return isSuspended;
    }

    public boolean isFinalState() {
        boolean isFinal = true;
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
                isFinal = false;
                break;
        }
        return isFinal;
    }

}
