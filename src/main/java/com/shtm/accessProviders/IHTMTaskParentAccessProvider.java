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

package com.shtm.accessProviders;

import com.htm.exceptions.HumanTaskManagerException;

public interface IHTMTaskParentAccessProvider {

    public static class Factory {

        private static IHTMTaskParentAccessProvider tpa = null;

        public static IHTMTaskParentAccessProvider newInstance() {

            /*
                * Singleton - Only one instance of the data access provider can be
                * created
                */
            if (tpa == null) {
                tpa = HTMTaskParentAccessProvider.getTaskParentInstance();
            }
            return tpa;
        }
    }

    public String createSubTaskInstance(String taskModelName, String taskInstanceName, Object inputData)
            throws HumanTaskManagerException;

    public void exitTaskInstance(String tiid)
            throws HumanTaskManagerException;

    public String createMergeTaskInstance(String taskModelName, String taskInstanceName, Object inputData)
            throws HumanTaskManagerException;

}
