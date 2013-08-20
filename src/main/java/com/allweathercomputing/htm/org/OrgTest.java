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

package com.allweathercomputing.htm.org;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.codec.digest.DigestUtils;

public class OrgTest {


    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence
                .createEntityManagerFactory("OrgDirectory");
        EntityManager entityManager = entityManagerFactory
                .createEntityManager();

        try {


            System.out.println("Hash: " + DigestUtils.sha256Hex("tobi"));

//			Role role = new Role();
//			role.setRoleId("administrators");
//			role.setRoleName("administrators");
//			role.setRoleType(Role.ROLE_TYPE_SECURITY);
//			
//			Organization organization = new Organization();
//			organization.setOrganizationId("iaas");

            //Person person = new Person();
            //person.setUserId("tobi");
            //person.setPasswd(DigestUtils.sha256Hex("tobi"));

//			person.getRoles().add(role);
//			
//			role.setCoordinator(person);
//			
//			organization.setManager(person);

            entityManager.getTransaction().begin();
            //entityManager.persist(person);
//			entityManager.persist(role);
//			entityManager.persist(organization);

            Person p = entityManager.find(Person.class, "tobi");
            Role r = entityManager.find(Role.class, "administrators");
            p.getRoles().add(r);
            entityManager.persist(p);
            entityManager.getTransaction().commit();
            //entityManager.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
