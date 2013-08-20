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

package com.htm.query.views;

import java.sql.Timestamp;

import com.htm.query.IQueryContext;
import com.htm.taskinstance.IAssignedUser;
import com.htm.taskinstance.IAttachment;
import com.htm.utils.Utilities;

/**
 * This class represents a view on an attachment of a task.</br>
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 * @see TaskInstanceView
 * @see IAttachment
 */
public class AttachmentView implements IQueryContext {

    private String name;

    private String accessType;

    private String contentType;

    private byte[] content;

    private String attachedAt; //Formatted date

    private String attachedBy; //User id

    /**
     * Creates a new view object.</br>
     * The properties of an attachment where expressions can be
     * evaluated on are set here.
     *
     * @param attachment The attachment where the view has to be created from.
     */
    public AttachmentView(IAttachment attachment) {
        setName(attachment.getName());
        setAccessType(attachment.getAccessType());
        setContentType(attachment.getContentType());
        setContent(attachment.getContent());
        setAttachedAt(attachment.getAttachedAt());
        IAssignedUser assignedUser = attachment.getAttachedBy();

        if (assignedUser != null) {
            setAttachedBy(assignedUser.getUserId());
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getAttachedAt() {
        return attachedAt;
    }

    public void setAttachedAt(Timestamp attachedAt) {
        setAttachedAt(Utilities.formatTimestamp(attachedAt));
    }

    public void setAttachedAt(String attachedAt) {
        this.attachedAt = attachedAt;
    }

    public String getAttachedBy() {
        return attachedBy;
    }

    public void setAttachedBy(String assignedBy) {
        this.attachedBy = assignedBy;
    }

}
