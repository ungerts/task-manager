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

package com.htm.ejb.timer;

import java.io.Serializable;

public class TimerInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 743961857874698899L;

    public static final int TIMER_ESCALATION = 0;

    public static final int TIMER_EXPIRATION = 1;

    public static final int TIMER_SUSPEND_UNTIL = 2;

    public static final int TIMER_COMPLETE_BY = 3;

    public static final int TIMER_START_BY = 4;

    private long tiid;

    private int timerType;

    public TimerInfo(long tiid, int timerType) {
        super();
        this.tiid = tiid;
        this.timerType = timerType;
    }

    public long getTiid() {
        return tiid;
    }

    public void setTiid(long tiid) {
        this.tiid = tiid;
    }

    public int getTimerType() {
        return timerType;
    }

    public void setTimerType(int timerType) {
        this.timerType = timerType;
    }


}