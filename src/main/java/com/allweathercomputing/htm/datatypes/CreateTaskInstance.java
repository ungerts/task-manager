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

package com.allweathercomputing.htm.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="instanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inputData" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "modelName",
        "instanceName",
        "inputData"
})
@XmlRootElement(name = "createTaskInstance")
public class CreateTaskInstance {

    @XmlElement(required = true, nillable = true)
    protected String modelName;
    @XmlElement(required = true, nillable = true)
    protected String instanceName;
    @XmlElement(required = true, nillable = true)
    protected Object inputData;

    /**
     * Gets the value of the modelName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * Gets the value of the instanceName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Sets the value of the instanceName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInstanceName(String value) {
        this.instanceName = value;
    }

    /**
     * Gets the value of the inputData property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getInputData() {
        return inputData;
    }

    /**
     * Sets the value of the inputData property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setInputData(Object value) {
        this.inputData = value;
    }

}
