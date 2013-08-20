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
import java.sql.Timestamp;

import com.htm.dm.IPersistenceVisitor;
import com.htm.entities.WrappableEntity;
import com.htm.entities.jpa.Assigneduser;
import com.htm.entities.jpa.Attachment;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.taskinstance.TaskInstanceFactory;

public class AttachmentWrapper implements IAttachment, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1547978081956590517L;
    protected Attachment attachmentEntity;

    public AttachmentWrapper(Attachment adaptee) {
        this.attachmentEntity = adaptee;
    }

    public AttachmentWrapper(String name) {
        this.attachmentEntity = new Attachment();
        this.attachmentEntity.setName(name);
    }


    public byte[] getContent() {
        return attachmentEntity.getValue();
    }

    public void setContent(byte[] content) {
        attachmentEntity.setValue(content);

    }

    public String getAccessType() {
        return attachmentEntity.getAccesstype();
    }

    public String getContentType() {
        return attachmentEntity.getContenttype();
    }

    public String getName() {
        return attachmentEntity.getName();
    }

    public String getId() {
        return Integer.toString(attachmentEntity.getId());
    }


    public void setAccessType(String accessType) {

        /* Check if a valid access type is specified in parameter accessType */
        for (int i = 0; i < ACCESS_TYPES.length; i++) {
            if (ACCESS_TYPES[i].equals(accessType)) {
                attachmentEntity.setAccesstype(accessType);
                return;
            }
        }
        //TODO  no runtime exception
        throw new RuntimeException("Invalid access type for attachment. " +
                "Following Access types are valid: " + ACCESS_TYPES.toString());


    }

    public void setName(String name) {
        attachmentEntity.setName(name);
    }

    public void setContentType(String contentType) {
        attachmentEntity.setContenttype(contentType);
    }

    public Timestamp getAttachedAt() {
        return attachmentEntity.getAttachedat();
    }

    public IAssignedUser getAttachedBy() {
        return TaskInstanceFactory.newInstance().createAssignedUserFromEntity(attachmentEntity.getAssigneduser());
    }

    public void setAttachedAt(Timestamp attachedAt) {
        attachmentEntity.setAttachedat(attachedAt);
    }

    public void setAttachedBy(IAssignedUser assignedUser) {

        attachmentEntity.setAssigneduser((Assigneduser) assignedUser.getAdaptee());
    }

    public void accept(IPersistenceVisitor visitor) {
        visitor.visit(this);

    }

    public WrappableEntity getAdaptee() {
        return attachmentEntity;
    }


}
