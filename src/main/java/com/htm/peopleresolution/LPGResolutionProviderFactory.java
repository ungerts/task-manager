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

package com.htm.peopleresolution;

import com.htm.peopleresolutionprovider.LpgResolutionProvider_UserByGroup;
import com.htm.taskmodel.ILogicalPeopleGroupDef;

public class LPGResolutionProviderFactory {

    private static LpgResolutionProvider_UserByGroup userByGroup;

    public static IPeopleResolutionProvider createPeopleResolutionProvider(
            ILogicalPeopleGroupDef lpgDefinition) {

        /* Here all logical people group resolution provider must be registered. */
        if (lpgDefinition
                .equals(LpgResolutionProvider_UserByGroup.SUPPORTED_LPG_DEF)) {
            /* Avoid multiple instantiation */
            if (userByGroup == null) {
                return new LpgResolutionProvider_UserByGroup();
            }
            return userByGroup;
        } else {
            throw new RuntimeException(
                    "No logical people goup resolution provider can be found for logical "
                            + "people group definition "
                            + lpgDefinition.getName() + ".");// TODO Exception handling
        }

    }

}
