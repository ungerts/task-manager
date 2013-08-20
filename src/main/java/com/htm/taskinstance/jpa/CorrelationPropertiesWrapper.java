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

package com.htm.taskinstance.jpa;

import java.io.Serializable;

import org.jdom2.Document;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Callbackcorrelationproperty;
import com.htm.taskinstance.ICorrelationProperty;
import com.htm.utils.Utilities;

public class CorrelationPropertiesWrapper implements ICorrelationProperty, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3233580863092896576L;
    private Callbackcorrelationproperty correlationPropery;

    public CorrelationPropertiesWrapper(Callbackcorrelationproperty adaptee) {
        this.correlationPropery = adaptee;
    }

    public CorrelationPropertiesWrapper(String name) {
        this.correlationPropery = new Callbackcorrelationproperty();
        this.correlationPropery.setName(name);
    }

    public Document getValue() {
        return Utilities.getXMLFromString(Utilities.getStringFromBLOB(correlationPropery.getValue()));
    }

    public String getName() {
        return correlationPropery.getName();
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public String getId() {
        return Integer.toString(correlationPropery.getId());
    }

    public void setName(String name) {
        correlationPropery.setName(name);

    }

    public void setValue(Document value) {
        correlationPropery.setValue(Utilities.getBLOBFromString(Utilities.getStringFromXMLDoc(value)));
    }


    public WrappableEntity getAdaptee() {
        return correlationPropery;
    }


}
