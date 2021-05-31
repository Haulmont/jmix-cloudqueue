package io.jmix.sqs.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents message from queue.
 */
public class Message implements Serializable, Cloneable{

    /**
     * <p>
     * A unique identifier for the message.
     * </p>
     */
    private String messageId;

    /**
     * <p>
     * An identifier associated with the act of receiving the message. A new receipt handle is returned every time you
     * receive a message. When deleting a message, you provide the last received receipt handle to delete the message.
     * </p>
     */
    private String receiptHandle;

    /**
     * <p>
     * An MD5 digest of the non-URL-encoded message body string.
     * </p>
     */
    private String mD5OfBody;

    /**
     * <p>
     * The message's contents (not URL-encoded).
     * </p>
     */
    private String body;

    /**
     * <p>
     * A map of the attributes requested.
     * </p>
     */
    private Map<String, String> attributes;

    /**
     * <p>
     * An MD5 digest of the non-URL-encoded message attribute string. You can use this attribute to verify that Amazon
     * SQS received the message correctly.
     * </p>
     */
    private String mD5OfMessageAttributes;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String getMD5OfBody() {
        return mD5OfBody;
    }

    public void setMD5OfBody(String mD5OfBody) {
        this.mD5OfBody = mD5OfBody;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getMD5OfMessageAttributes() {
        return mD5OfMessageAttributes;
    }

    public void setMD5OfMessageAttributes(String mD5OfMessageAttributes) {
        this.mD5OfMessageAttributes = mD5OfMessageAttributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getMessageId() != null)
            sb.append("MessageId: ").append(getMessageId()).append(",");
        if (getReceiptHandle() != null)
            sb.append("ReceiptHandle: ").append(getReceiptHandle()).append(",");
        if (getMD5OfBody() != null)
            sb.append("MD5OfBody: ").append(getMD5OfBody()).append(",");
        if (getBody() != null)
            sb.append("Body: ").append(getBody()).append(",");
        if (getAttributes() != null)
            sb.append("Attributes: ").append(getAttributes()).append(",");
        if (getMD5OfMessageAttributes() != null)
            sb.append("MD5OfMessageAttributes: ").append(getMD5OfMessageAttributes()).append(",");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof Message == false)
            return false;
        Message other = (Message) obj;
        if (other.getMessageId() == null ^ this.getMessageId() == null)
            return false;
        if (other.getMessageId() != null && other.getMessageId().equals(this.getMessageId()) == false)
            return false;
        if (other.getReceiptHandle() == null ^ this.getReceiptHandle() == null)
            return false;
        if (other.getReceiptHandle() != null && other.getReceiptHandle().equals(this.getReceiptHandle()) == false)
            return false;
        if (other.getMD5OfBody() == null ^ this.getMD5OfBody() == null)
            return false;
        if (other.getMD5OfBody() != null && other.getMD5OfBody().equals(this.getMD5OfBody()) == false)
            return false;
        if (other.getBody() == null ^ this.getBody() == null)
            return false;
        if (other.getBody() != null && other.getBody().equals(this.getBody()) == false)
            return false;
        if (other.getAttributes() == null ^ this.getAttributes() == null)
            return false;
        if (other.getAttributes() != null && other.getAttributes().equals(this.getAttributes()) == false)
            return false;
        if (other.getMD5OfMessageAttributes() == null ^ this.getMD5OfMessageAttributes() == null)
            return false;
        if (other.getMD5OfMessageAttributes() != null && other.getMD5OfMessageAttributes().equals(this.getMD5OfMessageAttributes()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getMessageId() == null) ? 0 : getMessageId().hashCode());
        hashCode = prime * hashCode + ((getReceiptHandle() == null) ? 0 : getReceiptHandle().hashCode());
        hashCode = prime * hashCode + ((getMD5OfBody() == null) ? 0 : getMD5OfBody().hashCode());
        hashCode = prime * hashCode + ((getBody() == null) ? 0 : getBody().hashCode());
        hashCode = prime * hashCode + ((getAttributes() == null) ? 0 : getAttributes().hashCode());
        hashCode = prime * hashCode + ((getMD5OfMessageAttributes() == null) ? 0 : getMD5OfMessageAttributes().hashCode());
        return hashCode;
    }

    @Override
    public Message clone() {
        try {
            return (Message) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }
}
