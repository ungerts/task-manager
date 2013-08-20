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

import com.htm.exceptions.HumanTaskManagerException;

/**
 * This enumeration contains all Structure and State Operations and offers
 * methods to get the pre-state and post-state of an operation
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public enum EOperations {
    ADDSUBTASK, CLAIM, COMPLETE, FAIL, FORWARD, MERGE, RELEASE, UNMERGE, REMOVESUBTASK, RESUMEREADY, RESUMERESERVED, RESUMEINPROGRESS, SKIP, START, STOP, SUSPENDREADY, SUSPENDRESERVED, SUSPENDINPROGRESS, SUSPENDUNTILREADY, SUSPENDUNTILRESERVED, SUSPENDUNTILINPROGRESS, DISCHARGE, EXIT;

    public EStates getPostState() throws HumanTaskManagerException {
        switch (this) {
            case CLAIM:
                return EStates.RESERVED;
            case START:
                return EStates.IN_PROGRESS;
            case STOP:
                return EStates.RESERVED;
            case RELEASE:
                return EStates.READY;
            case SUSPENDREADY:
                return EStates.SUSPENDED_READY;
            case SUSPENDRESERVED:
                return EStates.SUSPENDED_RESERVED;
            case SUSPENDINPROGRESS:
                return EStates.SUSPENDED_IN_PROGRESS;
            case SUSPENDUNTILREADY:
                return EStates.SUSPENDED_READY;
            case SUSPENDUNTILRESERVED:
                return EStates.SUSPENDED_RESERVED;
            case SUSPENDUNTILINPROGRESS:
                return EStates.SUSPENDED_IN_PROGRESS;
            case RESUMEREADY:
                return EStates.READY;
            case RESUMERESERVED:
                return EStates.RESERVED;
            case RESUMEINPROGRESS:
                return EStates.IN_PROGRESS;
            case COMPLETE:
                return EStates.COMPLETED;
            case FAIL:
                return EStates.FAILED;
            case SKIP:
                return EStates.OBSOLETE;
            case FORWARD:
                return EStates.READY;
            case EXIT:
                return EStates.EXITED;
        }
        String excMsg = "Operation is no State Operation";
        throw new HumanTaskManagerException(excMsg);
    }

    public boolean isPreState(EStates state) throws HumanTaskManagerException {
        switch (this) {
            case CLAIM:
                return (state == EStates.READY);
            case START:
                return (state == EStates.RESERVED);
            case STOP:
                return (state == EStates.IN_PROGRESS);
            case RELEASE:
                return (state == EStates.RESERVED);
            case SUSPENDREADY:
                return (state == EStates.READY);
            case SUSPENDRESERVED:
                return (state == EStates.RESERVED);
            case SUSPENDINPROGRESS:
                return (state == EStates.IN_PROGRESS);
            case SUSPENDUNTILREADY:
                return (state == EStates.READY);
            case SUSPENDUNTILRESERVED:
                return (state == EStates.RESERVED);
            case SUSPENDUNTILINPROGRESS:
                return (state == EStates.IN_PROGRESS);
            case RESUMEREADY:
                return (state == EStates.SUSPENDED_READY);
            case RESUMERESERVED:
                return (state == EStates.SUSPENDED_RESERVED);
            case RESUMEINPROGRESS:
                return (state == EStates.SUSPENDED_IN_PROGRESS);
            case COMPLETE:
                return (state == EStates.IN_PROGRESS);
            case FAIL:
                return (state == EStates.IN_PROGRESS);
            case SKIP:
                return (state == EStates.READY || state == EStates.RESERVED)
                        || (state == EStates.IN_PROGRESS);
            case FORWARD:
                return (state == EStates.READY);
        }
        String excMsg = "Operation is no State Operation";
        throw new HumanTaskManagerException(excMsg);
    }
}
