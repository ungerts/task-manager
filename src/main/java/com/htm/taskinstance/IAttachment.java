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

package com.htm.taskinstance;

import java.sql.Timestamp;

import com.htm.ITaskClientInterface;
import com.htm.dm.IDataModelElement;

/**
 * This interface contains the methods that have to be implemented by
 * a valid attachment model.</br>
 * Attachments contain additional data that are assigned to task instances
 * by a particular user.
 *
 * @author Sebastian Wagner
 * @author Tobias Unger
 */
public interface IAttachment extends IDataModelElement {

    /**
     * This attachment type indicates that the content is held within the
     * attachment.</br> The concrete type of the attachment content can be
     * obtained by the method {@link IAttachment#getContentType()}.
     *
     * @see IAttachment#ACCESS_TYPE_REFERENCE Attachment content as reference
     */
    public static final String ACCESS_TYPE_INLINE = "inline";

    /**
     * This attachment type indicates that the content of the attachment is an
     * url the points on to an external location where the attachment can be
     * obtained from.
     *
     * @see IAttachment#ACCESS_TYPE_INLINE Inline attachment content
     */
    public static final String ACCESS_TYPE_REFERENCE = "reference";

    /**
     * Contains the valid access types.
     */
    public static final String[] ACCESS_TYPES = new String[]{
            ACCESS_TYPE_INLINE, ACCESS_TYPE_REFERENCE};

    /**
     * @return The name of the attachment.
     */
    public String getName();

    /**
     * @return The access type of the attachment.
     * @see IAttachment#ACCESS_TYPES
     */
    public String getAccessType();

    /**
     * @return The data type of the content simply identified by a string.
     */
    public String getContentType();

    /**
     * @return The actual attachment content as byte array.
     */
    public byte[] getContent();

    /**
     * @return The time that indicates when the attachment was added to a task
     *         instance.
     */
    public Timestamp getAttachedAt();

    /**
     * @return The user that has assigned the attachment to the task instance.
     *         </br> Note: There is no equivalent setter method.</br> The
     *         association of the user with the attachment is implicitly done by
     *         the
     *         {@link ITaskClientInterface#addAttachment(String, String, IAttachment)}
     */
    public IAssignedUser getAttachedBy();

    /**
     * Sets the time when the attachment was assigned to the task instance.
     *
     * @param attachedAt
     */
    public void setAttachedAt(Timestamp attachedAt);


    /**
     * Sets the attachment content.
     *
     * @param content
     */
    public void setContent(byte[] content);

    /**
     * Sets the name of the attachment
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Sets the access type of the attachment.
     *
     * @param accessType
     * @see IAttachment#ACCESS_TYPES
     */
    public void setAccessType(String accessType);

    /**
     * Sets the identifier for the data type of the attachment content.
     *
     * @param contentType
     */
    public void setContentType(String contentType);

}
