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

package com.htm.entities.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.htm.entities.WrappableEntity;


/**
 * The persistent class for the USERS database table.
 */
@Entity
@Table(name = "USERS")
public class User implements Serializable, WrappableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String firstname;

    private String lastname;

    private String userid;


    //bi-directional many-to-one association to Usergroupdirectory
    @OneToMany(mappedBy = "user")
    private List<UsersGroups> usergroupdirectories;

    //bi-directional many-to-many association to Group
    @ManyToMany
    @JoinTable(
            name = "UsersGroups"
            , joinColumns = {
            @JoinColumn(name = "GROUP_ID")
    }
            , inverseJoinColumns = {
            @JoinColumn(name = "USER_ID")
    }
    )
    private List<Group> groups;

    public User() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public List<UsersGroups> getUsergroupdirectories() {
        return this.usergroupdirectories;
    }

    public void setUsergroupdirectories(List<UsersGroups> usergroupdirectories) {
        this.usergroupdirectories = usergroupdirectories;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}