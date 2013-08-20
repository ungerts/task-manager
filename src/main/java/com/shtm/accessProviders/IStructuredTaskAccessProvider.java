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

import java.util.List;

import com.htm.exceptions.HumanTaskManagerException;
import com.shtm.exceptions.SHTMDataBaseException;
import com.shtm.structureClasses.StructureData;
import com.shtm.structureClasses.StructuredTask;
import com.shtm.views.StructuredWorkItemView;

public interface IStructuredTaskAccessProvider {

    public final static boolean JUNIT_TEST = false;

    public static class Factory {

        // private static IStructuredTaskAccessProvider sdap = null;

        public static IStructuredTaskAccessProvider newInstance() {

            // /*
            // * Singleton - Only one instance of the data access provider can
            // be
            // * created
            // */
            // if (sdap == null) {
            // if (JUNIT_TEST) {
            // // dap = new StructureDataAccessProviderJpaJUnit();
            // } else {
            // sdap = StructuredTaskAccessProviderJPA.newInstance();
            // }
            //
            // }
            // TODO introduce properties to maintain "lightness"
            return new JEEStructuredAccessProviderJPA();
        }
    }

    public abstract void open();

    public abstract void beginTx();

    public abstract void commitTx() throws SHTMDataBaseException;

    public abstract void rollbackTx();

    public abstract boolean isTxActive();

    public abstract void close();

    public void persistStructureData(StructureData strData)
            throws SHTMDataBaseException;

    public StructureData getStructureDataByTaskId(String tiid)
            throws SHTMDataBaseException;

    public StructureData getStructureDataByStructureId(String tiid)
            throws SHTMDataBaseException;

    public void deleteStructureDatas();

    public List<StructuredTask> getAllStructureData()
            throws SHTMDataBaseException;

    public List<StructuredWorkItemView> query(String whereClause,
                                              String structureWhereClause, int maxResults)
            throws HumanTaskManagerException;

}