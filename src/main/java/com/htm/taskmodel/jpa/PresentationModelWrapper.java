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

package com.htm.taskmodel.jpa;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.jpa.Presentationinformation;
import com.htm.taskmodel.IPresentationModel;
import com.htm.utils.Utilities;

public class PresentationModelWrapper implements IPresentationModel {

    protected Presentationinformation presentationEntity;


    public PresentationModelWrapper(Presentationinformation adaptee) {
        this.presentationEntity = adaptee;
    }

    public PresentationModelWrapper() {
        this.presentationEntity = new Presentationinformation();
    }

    public String getDescription() {
        return Utilities.getStringFromBLOB(presentationEntity.getDescription());
    }

    public String getSubject() {
        return presentationEntity.getSubject();
    }

    public String getTitle() {
        return presentationEntity.getTitle();
    }

    public void setDescription(String description) {
        presentationEntity.setDescription(Utilities.getBLOBFromString(description));

    }

    public void setSubject(String subject) {
        presentationEntity.setSubject(subject);

    }

    public void setTitle(String title) {
        presentationEntity.setTitle(title);

    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public String getId() {
        return Integer.toString(presentationEntity.getId());
    }

    public Presentationinformation getAdaptee() {
        return presentationEntity;
    }

}
