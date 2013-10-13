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

package com.shtm.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.IFault;
import com.htm.taskinstance.TaskInstanceFactory;
import com.htm.utils.Utilities;
import com.shtm.IStructuredTaskClientInterface;
import com.shtm.StructuredTaskClientInterfaceImpl;
import com.shtm.exceptions.SHTMException;
import com.shtm.operationAndStates.EStates;
import com.shtm.structureClasses.ResultStructure;

@Ignore
public class StructureTestClass {
    String credentials;
    IStructuredTaskClientInterface strTaskClient;
    StructureDataTestUtilities testUt;

    @Before
    public void init() {
        testUt = new StructureDataTestUtilities();

        try {

            testUt.createModell();

            testUt.dap.beginTx();

            testUt.initUserSecurityContext();

            strTaskClient = new StructuredTaskClientInterfaceImpl();

            testUt.dap.commitTestCase();

        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }

    }

    @Test
    public void addAndRemoveSubTasksOnType1() {
        try {
            testUt.dap.beginTx();
            /**
             * Type1: single Task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);

            System.out.println(strTaskClient.getInputStructure(tiid1));
            System.out.println(strTaskClient.getInputStructure(tiid11));
            System.out.println(strTaskClient.getInputStructure(tiid12));

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask st1 = new StructureTestTask();
            t1.addSubTask(st1);
            StructureTestTask st2 = new StructureTestTask();
            t1.addSubTask(st2);

            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));

            checkLifeCycle(tiid1);

            // remove Sub Task1
            strTaskClient.removeSubTask(tiid11);
            t1.removeSubTask(st1);
            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));

            // remove Sub Task2
            strTaskClient.removeSubTask(tiid12);
            t1.removeSubTask(st2);
            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));
            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void addAndRemoveSubTasksOnType2() {
        try {
            testUt.dap.beginTx();
            /**
             * Type2: Merge tasks of two single tasks
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();

            String mtid1 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            String mtid11 = strTaskClient.addSubTask(mtid1, null, null, null);
            String mtid12 = strTaskClient.addSubTask(mtid1, null, null, null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            t1.addControlledTask(new StructureTestTask());
            t1.addControlledTask(new StructureTestTask());
            StructureTestTask st1 = new StructureTestTask();
            st1.addControlledTask(new StructureTestTask());
            st1.addControlledTask(new StructureTestTask());
            t1.addSubTask(st1);
            StructureTestTask st2 = new StructureTestTask();
            st2.addControlledTask(new StructureTestTask());
            st2.addControlledTask(new StructureTestTask());
            t1.addSubTask(st2);

            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            checkLifeCycle(mtid1);

            // remove Sub Task1
            strTaskClient.removeSubTask(mtid11);
            t1.removeSubTask(st1);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            // remove Sub Task2
            strTaskClient.removeSubTask(mtid12);
            t1.removeSubTask(st2);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void addAndRemoveSubTasksOnType3() {
        try {
            testUt.dap.beginTx();
            /**
             * Type2: Merge tasks of one single task and one merge task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();

            String mtid2 = strTaskClient.mergeTasks(tiid2, tiid3, null, null,
                    null);

            String mtid1 = strTaskClient.mergeTasks(tiid1, mtid2, null, null,
                    null);

            String mtid11 = strTaskClient.addSubTask(mtid1, null, null, null);
            String mtid12 = strTaskClient.addSubTask(mtid1, null, null, null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask mt2 = new StructureTestTask();
            mt2.addControlledTask(new StructureTestTask());
            mt2.addControlledTask(new StructureTestTask());
            t1.addControlledTask(new StructureTestTask());
            t1.addControlledTask(mt2);

            StructureTestTask st1 = new StructureTestTask();
            StructureTestTask smt1 = new StructureTestTask();
            smt1.addControlledTask(new StructureTestTask());
            smt1.addControlledTask(new StructureTestTask());
            st1.addControlledTask(new StructureTestTask());
            st1.addControlledTask(smt1);
            t1.addSubTask(st1);

            StructureTestTask st2 = new StructureTestTask();
            StructureTestTask smt2 = new StructureTestTask();
            smt2.addControlledTask(new StructureTestTask());
            smt2.addControlledTask(new StructureTestTask());
            st2.addControlledTask(new StructureTestTask());
            st2.addControlledTask(smt2);
            t1.addSubTask(st2);

            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            checkLifeCycle(mtid1);

            // remove Sub Task1
            strTaskClient.removeSubTask(mtid11);
            t1.removeSubTask(st1);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            // remove Sub Task2
            strTaskClient.removeSubTask(mtid12);
            t1.removeSubTask(st2);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void addAndRemoveSubTasksOnType4() {
        try {
            testUt.dap.beginTx();
            /**
             * Type2: Merge two tasks of two merge tasks each
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();

            String mtid3 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String mtid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            String mtid1 = strTaskClient.mergeTasks(mtid2, mtid3, null, null,
                    null);

            String mtid11 = strTaskClient.addSubTask(mtid1, null, null, null);
            String mtid12 = strTaskClient.addSubTask(mtid1, null, null, null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask mt2 = new StructureTestTask();
            mt2.addControlledTask(new StructureTestTask());
            mt2.addControlledTask(new StructureTestTask());
            StructureTestTask mt3 = new StructureTestTask();
            mt3.addControlledTask(new StructureTestTask());
            mt3.addControlledTask(new StructureTestTask());
            t1.addControlledTask(mt2);
            t1.addControlledTask(mt3);

            StructureTestTask st1 = new StructureTestTask();
            StructureTestTask smt2 = new StructureTestTask();
            smt2.addControlledTask(new StructureTestTask());
            smt2.addControlledTask(new StructureTestTask());
            StructureTestTask smt3 = new StructureTestTask();
            smt3.addControlledTask(new StructureTestTask());
            smt3.addControlledTask(new StructureTestTask());
            st1.addControlledTask(smt2);
            st1.addControlledTask(smt3);
            t1.addSubTask(st1);

            StructureTestTask st2 = new StructureTestTask();
            StructureTestTask smt4 = new StructureTestTask();
            smt4.addControlledTask(new StructureTestTask());
            smt4.addControlledTask(new StructureTestTask());
            StructureTestTask smt5 = new StructureTestTask();
            smt5.addControlledTask(new StructureTestTask());
            smt5.addControlledTask(new StructureTestTask());
            st2.addControlledTask(smt4);
            st2.addControlledTask(smt5);
            t1.addSubTask(st2);

            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            checkLifeCycle(mtid1);

            // remove Sub Task1
            strTaskClient.removeSubTask(mtid11);
            t1.removeSubTask(st1);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            // remove Sub Task2
            strTaskClient.removeSubTask(mtid12);
            t1.removeSubTask(st2);
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(t1.verifyStructure(mtid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    /**
     * removeSubTask is only on sub tasks valid. The root of a merge task
     * structure is not a subtask, even if its controlled tasks are sub tasks.
     * So the parent of sub task must have the same composition as the subtask.
     * Its sibling could have a different composition, then their parents would
     * be locked
     */
    @Test
    public void removeSubTasksOfType1() {
        try {
            testUt.dap.beginTx();
            /**
             * Type1: Single Sub Task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask t11 = new StructureTestTask();
            t11.addSubTask(new StructureTestTask());
            t11.addSubTask(new StructureTestTask());
            t1.addSubTask(t11);
            t1.addSubTask(new StructureTestTask());

            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));

            // remove Sub Task1
            strTaskClient.claim(tiid1);
            strTaskClient.removeSubTask(tiid11);
            t1.removeSubTask(t11);
            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void removeSubTasksOfType2() {
        try {
            testUt.dap.beginTx();
            /**
             * Type1: Single Sub Task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid21 = strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);

            String miid1 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            t1.addControlledTask(new StructureTestTask());
            t1.addControlledTask(new StructureTestTask());
            StructureTestTask t11 = new StructureTestTask();
            t11.addControlledTask(new StructureTestTask());
            t11.addControlledTask(new StructureTestTask());
            StructureTestTask t12 = new StructureTestTask();
            t12.addControlledTask(new StructureTestTask());
            t12.addControlledTask(new StructureTestTask());
            StructureTestTask t111 = new StructureTestTask();
            t111.addControlledTask(new StructureTestTask());
            t111.addControlledTask(new StructureTestTask());
            StructureTestTask t112 = new StructureTestTask();
            t112.addControlledTask(new StructureTestTask());
            t112.addControlledTask(new StructureTestTask());
            t1.addSubTask(t11);
            t1.addSubTask(t12);
            t11.addSubTask(t111);
            t11.addSubTask(t112);

            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            // remove Sub Task1
            strTaskClient.claim(miid1);
            String miid11 = strTaskClient.getSubTaskByStructureNr(miid1, 1);

            strTaskClient.removeSubTask(miid11);
            t1.removeSubTask(t11);
            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void removeSubTasksOfType3() {
        try {
            testUt.dap.beginTx();
            /**
             * Type3: Merge task of one single sub task and one merge task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid21 = strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);

            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid31 = strTaskClient.addSubTask(tiid3, null, null, null);
            strTaskClient.addSubTask(tiid3, null, null, null);
            strTaskClient.addSubTask(tiid31, null, null, null);
            strTaskClient.addSubTask(tiid31, null, null, null);

            String miid2 = strTaskClient.mergeTasks(tiid3, tiid2, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(tiid1, miid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask c11 = new StructureTestTask();
            c11.addControlledTask(new StructureTestTask());
            c11.addControlledTask(new StructureTestTask());
            t1.addControlledTask(new StructureTestTask());
            t1.addControlledTask(c11);
            StructureTestTask t11 = t1.getMergeTaskCopy();
            StructureTestTask t12 = t1.getMergeTaskCopy();
            StructureTestTask t111 = t1.getMergeTaskCopy();
            StructureTestTask t112 = t1.getMergeTaskCopy();

            t1.addSubTask(t11);
            t1.addSubTask(t12);
            t11.addSubTask(t111);
            t11.addSubTask(t112);

            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            // remove Sub Task1
            strTaskClient.claim(miid1);
            String miid11 = strTaskClient.getSubTaskByStructureNr(miid1, 1);

            strTaskClient.removeSubTask(miid11);
            t1.removeSubTask(t11);
            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void removeSubTasksOfType4() {
        try {
            testUt.dap.beginTx();
            /**
             * Type3: Merge task of one single sub task and one merge task
             */
            // addSubTasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);
            strTaskClient.addSubTask(tiid11, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid21 = strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);
            strTaskClient.addSubTask(tiid21, null, null, null);

            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid31 = strTaskClient.addSubTask(tiid3, null, null, null);
            strTaskClient.addSubTask(tiid3, null, null, null);
            strTaskClient.addSubTask(tiid31, null, null, null);
            strTaskClient.addSubTask(tiid31, null, null, null);

            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid41 = strTaskClient.addSubTask(tiid4, null, null, null);
            strTaskClient.addSubTask(tiid4, null, null, null);
            strTaskClient.addSubTask(tiid41, null, null, null);
            strTaskClient.addSubTask(tiid41, null, null, null);

            String miid3 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            StructureTestTask c11 = new StructureTestTask();
            StructureTestTask c12 = new StructureTestTask();
            c11.addControlledTask(new StructureTestTask());
            c11.addControlledTask(new StructureTestTask());
            c12.addControlledTask(new StructureTestTask());
            c12.addControlledTask(new StructureTestTask());
            t1.addControlledTask(c11);
            t1.addControlledTask(c12);
            StructureTestTask t11 = t1.getMergeTaskCopy();

            StructureTestTask t12 = t1.getMergeTaskCopy();

            StructureTestTask t111 = t1.getMergeTaskCopy();

            StructureTestTask t112 = t1.getMergeTaskCopy();

            t1.addSubTask(t11);
            t1.addSubTask(t12);
            t11.addSubTask(t111);
            t11.addSubTask(t112);

            strTaskClient.printStructureInfos(tiid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            // remove Sub Task1
            strTaskClient.claim(miid1);
            String miid11 = strTaskClient.getSubTaskByStructureNr(miid1, 1);

            strTaskClient.removeSubTask(miid11);
            t1.removeSubTask(t11);
            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T1T1() {
        try {
            testUt.dap.beginTx();
            /**
             * M1T1T1: Merge two single tasks
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();

            String miid1 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask t1 = new StructureTestTask();
            t1.addControlledTask(new StructureTestTask());
            t1.addControlledTask(new StructureTestTask());

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(t1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            t1 = new StructureTestTask();
            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));
            assertTrue(t1.verifyStructure(tiid2, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T1T2() {
        try {
            testUt.dap.beginTx();
            /**
             * M1T1T1: Merge one single task and one merge task of a single task
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();

            String miid2 = strTaskClient.mergeTasks(tiid2, tiid3, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(tiid1, miid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            StructureTestTask t1 = new StructureTestTask();
            m2 = new StructureTestTask();
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(miid2, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m2.verifyStructure(miid2, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T1T3() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();

            String miid3 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid2, miid3, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(tiid1, miid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            StructureTestTask m3 = new StructureTestTask();
            m3.addControlledTask(new StructureTestTask());
            m3.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(m3);
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            StructureTestTask t1 = new StructureTestTask();
            m1.removeControlledTask(m2);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(miid2, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m2.verifyStructure(miid2, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T1T4() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();

            String miid4 = strTaskClient.mergeTasks(tiid4, tiid5, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(tiid2, tiid3, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(miid3, miid4, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(tiid1, miid2, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            StructureTestTask m3 = new StructureTestTask();
            m3.addControlledTask(new StructureTestTask());
            m3.addControlledTask(new StructureTestTask());
            m2.addControlledTask(m3);
            m2.addControlledTask(m3.getMergeTaskCopy());
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            StructureTestTask t1 = new StructureTestTask();
            m1.removeControlledTask(m2);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(miid2, true);
            assertTrue(t1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m2.verifyStructure(miid2, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T2T2() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();

            String miid3 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());
            StructureTestTask m3 = m2.getMergeTaskCopy();
            m1.addControlledTask(m3);
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T2T3() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();

            String miid4 = strTaskClient.mergeTasks(tiid4, tiid5, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(tiid3, miid4, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());
            StructureTestTask m3 = new StructureTestTask();
            m3.addControlledTask(m2.getMergeTaskCopy());
            m3.addControlledTask(new StructureTestTask());
            m1.addControlledTask(m3);
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T2T4() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();
            String tiid6 = testUt.createTaskInstanceDummy();

            String miid5 = strTaskClient.mergeTasks(tiid5, tiid6, null, null,
                    null);
            String miid4 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(miid4, miid5, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            m2.addControlledTask(new StructureTestTask());
            m2.addControlledTask(new StructureTestTask());
            StructureTestTask m3 = new StructureTestTask();
            m3.addControlledTask(m2.getMergeTaskCopy());
            m3.addControlledTask(m2.getMergeTaskCopy());
            m1.addControlledTask(m3);
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T3T3() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();
            String tiid6 = testUt.createTaskInstanceDummy();

            String miid5 = strTaskClient.mergeTasks(tiid5, tiid6, null, null,
                    null);
            String miid4 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(tiid2, miid5, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, miid4, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();

            StructureTestTask m3 = new StructureTestTask();
            StructureTestTask m4 = new StructureTestTask();
            m4.addControlledTask(new StructureTestTask());
            m4.addControlledTask(new StructureTestTask());
            m3.addControlledTask(m4);
            m3.addControlledTask(new StructureTestTask());
            StructureTestTask m2 = m3.getMergeTaskCopy();
            m1.addControlledTask(m2);
            m1.addControlledTask(m3);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T3T4() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();
            String tiid6 = testUt.createTaskInstanceDummy();
            String tiid7 = testUt.createTaskInstanceDummy();

            String miid6 = strTaskClient.mergeTasks(tiid6, tiid7, null, null,
                    null);
            String miid5 = strTaskClient.mergeTasks(tiid4, tiid5, null, null,
                    null);
            String miid4 = strTaskClient.mergeTasks(tiid2, tiid3, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(miid5, miid6, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(tiid1, miid4, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m2 = new StructureTestTask();
            StructureTestTask m3 = new StructureTestTask();
            StructureTestTask m4 = new StructureTestTask();
            m4.addControlledTask(new StructureTestTask());
            m4.addControlledTask(new StructureTestTask());
            m3.addControlledTask(m4);
            m3.addControlledTask(m4.getMergeTaskCopy());
            m2.addControlledTask(m4.getMergeTaskCopy());
            m2.addControlledTask(new StructureTestTask());
            m1.addControlledTask(m3);
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            checkLifeCycle(miid1);

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksM1T4T4() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String tiid3 = testUt.createTaskInstanceDummy();
            String tiid4 = testUt.createTaskInstanceDummy();
            String tiid5 = testUt.createTaskInstanceDummy();
            String tiid6 = testUt.createTaskInstanceDummy();
            String tiid7 = testUt.createTaskInstanceDummy();
            String tiid8 = testUt.createTaskInstanceDummy();

            String miid7 = strTaskClient.mergeTasks(tiid7, tiid8, null, null,
                    null);
            String miid6 = strTaskClient.mergeTasks(tiid5, tiid6, null, null,
                    null);
            String miid5 = strTaskClient.mergeTasks(tiid3, tiid4, null, null,
                    null);
            String miid4 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);
            String miid3 = strTaskClient.mergeTasks(miid6, miid7, null, null,
                    null);
            String miid2 = strTaskClient.mergeTasks(miid4, miid5, null, null,
                    null);
            String miid1 = strTaskClient.mergeTasks(miid2, miid3, null, null,
                    null);

            checkLifeCycle(miid1);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            StructureTestTask m3 = new StructureTestTask();
            StructureTestTask m4 = new StructureTestTask();
            m4.addControlledTask(new StructureTestTask());
            m4.addControlledTask(new StructureTestTask());
            m3.addControlledTask(m4);
            m3.addControlledTask(m4.getMergeTaskCopy());
            StructureTestTask m2 = m3.getMergeTaskCopy();
            m1.addControlledTask(m3);
            m1.addControlledTask(m2);

            strTaskClient.printStructureInfos(miid1, true);
            assertTrue(m1.verifyStructure(miid1, strTaskClient));

            // unmerge Task
            strTaskClient.unmerge(miid1);
            m1.removeControlledTask(m2);
            m1.removeControlledTask(m3);

            strTaskClient.printStructureInfos(miid2, true);
            strTaskClient.printStructureInfos(miid3, true);
            assertTrue(m2.verifyStructure(miid2, strTaskClient));
            assertTrue(m3.verifyStructure(miid3, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeKreisStruktur() {
        try {
            testUt.dap.beginTx();
            /**
             *
             */
            // merge tasks
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();

            strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid13 = strTaskClient.addSubTask(tiid1, null, null, null);

            strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid22 = strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid23 = strTaskClient.addSubTask(tiid2, null, null, null);

            strTaskClient.mergeTasks(tiid13, tiid23, null, null, null);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            strTaskClient.mergeTasks(tiid12, tiid22, null, null, null);
            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            // Controll Structure
            StructureTestTask m1 = new StructureTestTask();
            m1.setLock(true);
            StructureTestTask m11 = new StructureTestTask();
            StructureTestTask m12 = new StructureTestTask();
            StructureTestTask m13 = new StructureTestTask();

            StructureTestTask m2 = new StructureTestTask();
            m2.setLock(true);
            StructureTestTask m21 = new StructureTestTask();
            StructureTestTask m22 = new StructureTestTask();
            StructureTestTask m23 = new StructureTestTask();

            m1.addSubTask(m11);
            m1.addSubTask(m12);
            m1.addSubTask(m13);

            m2.addSubTask(m21);
            m2.addSubTask(m22);
            m2.addSubTask(m23);

            m12.addControlledTask(new StructureTestTask());
            m12.addControlledTask(new StructureTestTask());

            m13.addControlledTask(new StructureTestTask());
            m13.addControlledTask(new StructureTestTask());

            m22.addControlledTask(new StructureTestTask());
            m22.addControlledTask(new StructureTestTask());

            m23.addControlledTask(new StructureTestTask());
            m23.addControlledTask(new StructureTestTask());

            assertTrue(m1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m2.verifyStructure(tiid2, strTaskClient));

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void FailTest() {
        try {
            testUt.dap.beginTx();

            String tiid1 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid22 = strTaskClient.addSubTask(tiid2, null, null, null);

            String tiid3 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid3, null, null, null);
            String tiid32 = strTaskClient.addSubTask(tiid3, null, null, null);

            strTaskClient.claim(tiid1);
            strTaskClient.claim(tiid2);
            strTaskClient.claim(tiid3);

            strTaskClient.start(tiid1);
            strTaskClient.start(tiid2);
            strTaskClient.start(tiid3);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            strTaskClient.printStructureInfos(tiid3, true);

            String mtid1 = strTaskClient.mergeTasks(tiid12, tiid22, null, null,
                    null);
            strTaskClient.addSubTask(mtid1, null, null, null);
            String miid12 = strTaskClient.addSubTask(mtid1, null, null, null);

            String mtid2 = strTaskClient.mergeTasks(miid12, tiid32, null, null,
                    null);
            strTaskClient.addSubTask(mtid2, null, null, null);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            strTaskClient.printStructureInfos(tiid3, true);

            strTaskClient
                    .fail(mtid2, "Fehler",
                            "<testFault>This is a test fault message of a human task instance</testFault>");

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            strTaskClient.printStructureInfos(tiid3, true);

            strTaskClient.unmerge(mtid2);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            strTaskClient.printStructureInfos(tiid3, true);

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void FailTest2() {
        try {
            testUt.dap.beginTx();

            String tiid1 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid22 = strTaskClient.addSubTask(tiid2, null, null, null);

            strTaskClient.claim(tiid1);
            strTaskClient.claim(tiid2);

            strTaskClient.start(tiid1);
            strTaskClient.start(tiid2);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            String mtid1 = strTaskClient.mergeTasks(tiid12, tiid22, null, null,
                    null);
            strTaskClient.addSubTask(mtid1, null, null, null);
            strTaskClient.addSubTask(mtid1, null, null, null);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            strTaskClient
                    .fail(mtid1, "Fehler",
                            "<testFault>This is a test fault message of a human task instance</testFault>");

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            System.out.println(strTaskClient.getFault(mtid1).getName());
            System.out.println(strTaskClient.getFault(mtid1).getData());

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void FailTest3() {
        try {
            testUt.dap.beginTx();

            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);

            String tiid2 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid22 = strTaskClient.addSubTask(tiid2, null, null, null);

            strTaskClient.claim(tiid1);
            strTaskClient.start(tiid1);

            strTaskClient.claim(tiid2);
            strTaskClient.start(tiid2);

            String mtid1 = strTaskClient.mergeTasks(tiid12, tiid22, null, null,
                    null);
            strTaskClient.printStructureInfos(tiid1, true);

            strTaskClient.fail(mtid1, "POutput", "<asdfasfa>");
            strTaskClient.fail(tiid11, "POutput", "<asdfasfa>");

            strTaskClient.printStructureInfos(tiid1, true);

            strTaskClient.printStructureInfos(tiid2, true);

            strTaskClient.unmerge(mtid1);

            strTaskClient.printStructureInfos(tiid1, true);

            strTaskClient.printStructureInfos(tiid2, true);

            System.out.println(strTaskClient.getFault(tiid1).getName());
            System.out.println(strTaskClient.getFault(tiid1).getData());

            testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTwoSubStuctures() {
        try {
            // testUt.dap.beginTx();
            Map<String, TestStructureData> ControllDatas = new HashMap<String, TestStructureData>();

            String tiid1 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid1, null, null, null);
            String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid12, null, null, null);
            strTaskClient.addSubTask(tiid12, null, null, null);
            strTaskClient.printStructureInfos(tiid1, true);

            String tiid2 = testUt.createTaskInstanceDummy();
            strTaskClient.addSubTask(tiid2, null, null, null);
            String tiid22 = strTaskClient.addSubTask(tiid2, null, null, null);
            strTaskClient.addSubTask(tiid22, null, null, null);
            strTaskClient.addSubTask(tiid22, null, null, null);
            strTaskClient.printStructureInfos(tiid2, true);

            String mtid1 = strTaskClient.mergeTasks(tiid12, tiid22, null, null,
                    null);

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);

            strTaskClient.unmerge(mtid1);
            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            strTaskClient.printStructureInfos(mtid1, true);

            // performOperationsOnStructure(mtid1, tiid1, null, ControllDatas);
            testUt.controllallTestData(ControllDatas);

            // testUt.dap.commitTestCase();
        } catch (HumanTaskManagerException e) {
            testUt.dap.rollbackTx();
            e.printStackTrace();
        } finally {
            testUt.dap.closeTestCase();
        }
    }

    @Test
    public void mergeTasksAndAddSubTasks() {
        try {
            /**
             * create two tasks and merge them
             */
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            String mtid1 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            StructureTestTask m1 = new StructureTestTask();
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(new StructureTestTask());

            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(m1.verifyStructure(mtid1, strTaskClient));

            /**
             * add another subtask to the merge structure
             */
            strTaskClient.addSubTask(mtid1, null, null, null);

            StructureTestTask mt11 = new StructureTestTask();
            m1.addSubTask(mt11);
            mt11.addControlledTask(new StructureTestTask());
            mt11.addControlledTask(new StructureTestTask());
            strTaskClient.printStructureInfos(mtid1, true);
            assertTrue(m1.verifyStructure(mtid1, strTaskClient));

            /**
             * unmerge the structure
             */
            strTaskClient.unmerge(mtid1);

            m1 = new StructureTestTask();
            m1.addSubTask(new StructureTestTask());

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            assertTrue(m1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m1.verifyStructure(tiid2, strTaskClient));

            /**
             * merge them again
             */
            String mtid2 = strTaskClient.mergeTasks(tiid1, tiid2, null, null,
                    null);

            m1 = new StructureTestTask();
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(new StructureTestTask());
            mt11 = new StructureTestTask();
            mt11.addControlledTask(new StructureTestTask());
            mt11.addControlledTask(new StructureTestTask());
            m1.addSubTask(mt11);

            strTaskClient.printStructureInfos(mtid2, true);
            assertTrue(m1.verifyStructure(mtid2, strTaskClient));

            /**
             * check lifeCycle
             */

            checkLifeCycle(mtid2);

            /**
             * remove a subtask from Merge Structure
             */
            String msid21 = strTaskClient.getSubTasks(mtid2).get(0);
            strTaskClient.removeSubTask(msid21);

            m1 = new StructureTestTask();
            m1.addControlledTask(new StructureTestTask());
            m1.addControlledTask(new StructureTestTask());

            strTaskClient.printStructureInfos(mtid2, true);
            assertTrue(m1.verifyStructure(mtid2, strTaskClient));

            /**
             * unmerge the structure
             */
            strTaskClient.unmerge(mtid2);

            m1 = new StructureTestTask();

            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(tiid2, true);
            assertTrue(m1.verifyStructure(tiid1, strTaskClient));
            assertTrue(m1.verifyStructure(tiid2, strTaskClient));
        } catch (HumanTaskManagerException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void CheckMergeParents() {
        try {
            /**
             * create two tasks and merge them
             */
            String tiid1 = testUt.createTaskInstanceDummy();
            String tiid2 = testUt.createTaskInstanceDummy();
            testUt.createTaskInstanceDummy();
            testUt.createTaskInstanceDummy();

            strTaskClient.claim(tiid1);
            strTaskClient.start(tiid1);
            String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.addSubTask(tiid1, null, null, null);
            strTaskClient.printStructureInfos(tiid1, true);

            strTaskClient.claim(tiid2);
            strTaskClient.start(tiid2);

            String mtid1 = strTaskClient.mergeTasks(tiid11, tiid2, null, null,
                    null);

            strTaskClient.stop(mtid1);
            strTaskClient.printStructureInfos(tiid1, true);
            strTaskClient.printStructureInfos(mtid1, true);

        } catch (HumanTaskManagerException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void checkPartialOrder() {
        for (EStates parentState : EStates.values()) {
            for (EStates state : EStates.values()) {
                try {
                    System.out.println(parentState + " - " + state + ": "
                            + parentState.allowsChildState(state));
                } catch (SHTMException e) {
                    System.out.println(parentState + " - " + state + ": ERROR");
                }
            }
        }

    }

    private void checkLifeCycle(String tiid1) throws HumanTaskManagerException {

        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.suspend(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.resume(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.claim(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.release(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.claim(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.suspend(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.resume(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.start(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.stop(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.start(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.suspend(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.resume(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.fail(tiid1, "asdf", "sdoipwer");
        strTaskClient.printStructureInfos(tiid1, true);

    }

    @Test
    public void checkCompletion() throws HumanTaskManagerException {
        String tiid1 = testUt.createTaskInstanceDummy();
        String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
        String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);
        String tiid13 = strTaskClient.addSubTask(tiid1, null, null, null);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.claim(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.start(tiid1);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.skip(tiid11);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.skip(tiid12);
        strTaskClient.printStructureInfos(tiid1, true);

        strTaskClient.complete(tiid13, "asdf");
        strTaskClient.printStructureInfos(tiid1, true);

    }

    @Test(expected = HumanTaskManagerException.class)
    public void exitTest() throws HumanTaskManagerException {
        String tiid1 = testUt.createTaskInstanceDummy();
        String tiid2 = testUt.createTaskInstanceDummy();

        String miid1 = strTaskClient.mergeTasks(tiid1, tiid2, null, null, null);
        strTaskClient.printStructureInfos(miid1, true);

        strTaskClient.addSubTask(miid1, null, null, null);

        strTaskClient.printStructureInfos(miid1, true);

        testUt.exitTask(tiid1);

        strTaskClient.printStructureInfos(miid1, true);

        strTaskClient.claim(miid1);
    }

    @Test
    public void checkInformationOperations() throws HumanTaskManagerException {
        String tiid1 = testUt.createTaskInstanceDummy();
        String tiid2 = testUt.createTaskInstanceDummy();
        String tiid3 = testUt.createTaskInstanceDummy();

        String miid2 = strTaskClient.mergeTasks(tiid2, tiid3, null, null, null);
        String miid1 = strTaskClient.mergeTasks(tiid1, miid2, null, null, null);
        strTaskClient.claim(miid1);
        strTaskClient.printStructureInfos(miid1, true);

        strTaskClient.setFault(miid1, "asd", "asfasfasdf!");
        System.out.println(strTaskClient.getFault(tiid1).getData());
        System.out.println(strTaskClient.getFault(tiid2).getData());
        System.out.println(strTaskClient.getFault(tiid3).getData());
        strTaskClient.deleteFault(miid1);
        System.out.println(strTaskClient.getFault(tiid1));
        System.out.println(strTaskClient.getFault(tiid2));
        System.out.println(strTaskClient.getFault(tiid3));

        strTaskClient.setOutput(miid1, "output");
        System.out.println(strTaskClient.getOutput(tiid1));
        System.out.println(strTaskClient.getOutput(tiid2));
        System.out.println(strTaskClient.getOutput(tiid3));
        strTaskClient.deleteOutput(miid1);
        System.out.println(strTaskClient.getOutput(tiid1));
        System.out.println(strTaskClient.getOutput(tiid2));
        System.out.println(strTaskClient.getOutput(tiid3));

        String attachment1Name = "myAttachment1";
        String attachment1AccessType = IAttachment.ACCESS_TYPE_INLINE;
        String attachment1ContentType = "String";
        byte[] attachment1Content = Utilities
                .getBLOBFromString("Test content for junit attachment1");
        IAttachment attachment1 = TaskInstanceFactory.newInstance()
                .createAttachment(attachment1Name);
        attachment1.setAccessType(attachment1AccessType);
        attachment1.setContentType(attachment1ContentType);
        attachment1.setContent(attachment1Content);

        strTaskClient.addAttachment(miid1, attachment1);
        System.out.println(strTaskClient.getAttachments(tiid1, attachment1Name)
                .get(0).getName());
        System.out.println(strTaskClient.getAttachments(tiid2, attachment1Name)
                .get(0).getName());
        System.out.println(strTaskClient.getAttachments(tiid3, attachment1Name)
                .get(0).getName());

        String attachment2Name = "myAttachment2";
        IAttachment attachment2 = TaskInstanceFactory.newInstance()
                .createAttachment(attachment2Name);
        attachment2.setAccessType(attachment1AccessType);
        attachment2.setContentType(attachment1ContentType);
        attachment2.setContent(attachment1Content);

        String tiid4 = testUt.createTaskInstanceDummy();
        strTaskClient.claim(tiid4);
        strTaskClient.addAttachment(tiid4, attachment1);
        String tiid5 = testUt.createTaskInstanceDummy();
        strTaskClient.claim(tiid5);
        strTaskClient.addAttachment(tiid5, attachment2);

        String miid3 = strTaskClient.mergeTasks(tiid4, tiid5, null, null, null);
        strTaskClient.printStructureInfos(miid3, true);
        System.out.println(strTaskClient.getAttachments(miid3, attachment1Name)
                .get(0).getName());
        System.out.println(strTaskClient.getAttachments(miid3, attachment2Name)
                .get(0).getName());

        strTaskClient.deleteAttachments(miid3, attachment1Name);
        System.out.println(strTaskClient.getAttachments(tiid5, attachment2Name)
                .get(0).getName());
        System.out.println(strTaskClient.getAttachments(tiid4, null).size());
        System.out.println(strTaskClient.getAttachments(miid3, null).size());

        strTaskClient.printStructureInfos(miid1, true);
    }

    @Test
    public void checkInformationStructureOperations()
            throws HumanTaskManagerException {
        String tiid1 = testUt.createTaskInstanceDummy();
        strTaskClient.claim(tiid1);

        strTaskClient.setOutput(tiid1, "out1");
        strTaskClient.setFault(tiid1, "Fault1", "fault");
        String tiid11 = strTaskClient.addSubTask(tiid1, null, null, null);
        strTaskClient.setOutput(tiid11, "out11");
        strTaskClient.setFault(tiid11, "Fault11", "fault");
        String tiid12 = strTaskClient.addSubTask(tiid1, null, null, null);
        strTaskClient.setOutput(tiid12, "out12");
        strTaskClient.setFault(tiid12, "Fault12", "fault");
        String tiid111 = strTaskClient.addSubTask(tiid11, null, null, null);
        strTaskClient.setOutput(tiid111, "out111");
        strTaskClient.setFault(tiid111, "Fault111", "fault");
        String tiid112 = strTaskClient.addSubTask(tiid11, null, null, null);
        strTaskClient.setOutput(tiid112, "out112");
        strTaskClient.setFault(tiid112, "Fault112", "fault");

        String attachment1Name = "myAttachment1";
        String attachment1AccessType = IAttachment.ACCESS_TYPE_INLINE;
        String attachment1ContentType = "String";
        byte[] attachment1Content = Utilities
                .getBLOBFromString("Test content for junit attachment1");
        IAttachment attachment1 = TaskInstanceFactory.newInstance()
                .createAttachment(attachment1Name);
        attachment1.setAccessType(attachment1AccessType);
        attachment1.setContentType(attachment1ContentType);
        attachment1.setContent(attachment1Content);
        strTaskClient.addAttachment(tiid112, attachment1);

        strTaskClient.printStructureInfos(tiid1, true);

        ResultStructure<String, Object> result1 = strTaskClient
                .getInputStructure(tiid11);
        ResultStructure<String, Object> result2 = strTaskClient
                .getOutputStructure(tiid11);
        ResultStructure<String, IFault> result3 = strTaskClient
                .getFaultStructure(tiid11);
        ResultStructure<String, List<IAttachment>> result4 = strTaskClient
                .getAttachmentStructure(tiid11, null);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);
        strTaskClient.printStructureInfos(tiid1, true);

    }

    @After
    public void cleanUp() {
        try {
            testUt.cleanUp();
        } catch (HumanTaskManagerException e) {
            // TODO Auto-generated catch block
            System.out.println("Final Cleanup failed");
            e.printStackTrace();
        }
    }
}
