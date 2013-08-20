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

import com.htm.dm.EHumanRoles;
import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.taskmodel.ILiteral;


public class LiteralWrapper implements ILiteral {

    private com.htm.entities.jpa.Literal literalEntity;

    public LiteralWrapper() {
        this.literalEntity = new com.htm.entities.jpa.Literal();
    }

    public LiteralWrapper(com.htm.entities.jpa.Literal literalEntity) {
        this.literalEntity = literalEntity;
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ILiteral#getGenericHumanRole()
      */
    public EHumanRoles getGenericHumanRole() {
        return EHumanRoles.valueOf(literalEntity.getHumanrole());
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ILiteral#getOrganizationalEntityId()
      */
    public String getOrganizationalEntityId() {
        return literalEntity.getEntityidentifier();
    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ILiteral#setGenericHumanRole(com.htm.dm.EHumanRoles)
      */
    public void setGenericHumanRole(EHumanRoles humanRole) {
        literalEntity.setHumanrole(humanRole.name());

    }

    /* (non-Javadoc)
      * @see com.htm.dm.taskmodel.ILiteral#setOrganizationalEntityId(java.lang.String)
      */
    public void setOrganizationalEntityId(String orgEntityId) {
        literalEntity.setEntityidentifier(orgEntityId);

    }

    /* (non-Javadoc)
      * @see com.htm.dm.IDataModelElement#accept(com.htm.dm.IPersistenceVisitor)
      */
    public void accept(IPersistenceVisitor visitor) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
      * @see com.htm.dm.IDataModelElement#getAdaptee()
      */
    public WrappableEntity getAdaptee() {
        return literalEntity;
    }

    /* (non-Javadoc)
      * @see com.htm.dm.IDataModelElement#getId()
      */
    public String getId() {
        return Integer.toString(literalEntity.getId());
    }

}
