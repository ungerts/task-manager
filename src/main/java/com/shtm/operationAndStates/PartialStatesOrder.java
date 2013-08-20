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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shtm.exceptions.SHTMException;

/**
 * This class represents the partial order between parent states and sub states
 *
 * @author Oliver Eckhardt
 * @author Tobias Unger
 */
public class PartialStatesOrder {

    class DirectDescendants {
        List<DirectDescendants> smallerStates;
        EStates state;

        DirectDescendants(EStates state,
                          List<DirectDescendants> smallerStates) {
            this.smallerStates = smallerStates;
            this.state = state;
        }

        public List<DirectDescendants> getSmallerStates() {
            return smallerStates;
        }

        public EStates getState() {
            return state;
        }

        public boolean isDescendant(EStates state) {
            if (this.getState() == state) {
                return true;
            } else if (this.getState().isFinalState() && state.isFinalState()) {
                // Final States are all equal
                return true;
            } else {
                for (DirectDescendants descendant : this.getSmallerStates()) {
                    if (descendant.isDescendant(state)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    private static final DirectDescendants completed = new PartialStatesOrder().new DirectDescendants(
            EStates.COMPLETED,
            new ArrayList<DirectDescendants>());
    private static final DirectDescendants failed = new PartialStatesOrder().new DirectDescendants(
            EStates.FAILED,
            new ArrayList<DirectDescendants>());
    private static final DirectDescendants error = new PartialStatesOrder().new DirectDescendants(
            EStates.ERROR,
            new ArrayList<DirectDescendants>());
    private static final DirectDescendants exited = new PartialStatesOrder().new DirectDescendants(
            EStates.EXITED,
            new ArrayList<DirectDescendants>());
    private static final DirectDescendants obsolete = new PartialStatesOrder().new DirectDescendants(
            EStates.OBSOLETE,
            new ArrayList<DirectDescendants>());

    private static final DirectDescendants suspendedReserved = new PartialStatesOrder().new DirectDescendants(
            EStates.SUSPENDED_RESERVED,
            new ArrayList<DirectDescendants>(Arrays.asList(completed, failed,
                    error, exited, obsolete)));
    private static final DirectDescendants reserved = new PartialStatesOrder().new DirectDescendants(
            EStates.RESERVED,
            new ArrayList<DirectDescendants>(Arrays.asList(suspendedReserved)));
    private static final DirectDescendants suspendedInProgress = new PartialStatesOrder().new DirectDescendants(
            EStates.SUSPENDED_IN_PROGRESS,
            new ArrayList<DirectDescendants>(Arrays.asList(suspendedReserved)));
    private static final DirectDescendants inProgress = new PartialStatesOrder().new DirectDescendants(
            EStates.IN_PROGRESS,
            new ArrayList<DirectDescendants>(Arrays.asList(suspendedInProgress,
                    reserved)));
    private static final DirectDescendants suspendedReady = new PartialStatesOrder().new DirectDescendants(
            EStates.SUSPENDED_READY,
            new ArrayList<DirectDescendants>(Arrays.asList(completed, failed,
                    error, exited, obsolete)));
    private static final DirectDescendants ready = new PartialStatesOrder().new DirectDescendants(
            EStates.READY,
            new ArrayList<DirectDescendants>(Arrays.asList(suspendedReady)));

    public static DirectDescendants getDirectDescendants(
            EStates state)
            throws SHTMException {
        switch (state) {
            case READY:
                return ready;
            case RESERVED:
                return reserved;
            case IN_PROGRESS:
                return inProgress;
            case SUSPENDED_READY:
                return suspendedReady;
            case SUSPENDED_RESERVED:
                return suspendedReserved;
            case SUSPENDED_IN_PROGRESS:
                return suspendedInProgress;
            case COMPLETED:
                return completed;
            case FAILED:
                return failed;
            case ERROR:
                return error;
            case EXITED:
                return exited;
            case OBSOLETE:
                return obsolete;
            default:
                String excMsg = "Unknown state";
                throw new SHTMException(excMsg);
        }
    }

}
