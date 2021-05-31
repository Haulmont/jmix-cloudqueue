package io.jmix.sqs.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * A list of received messages.
 * </p>
 */
public class ReceiveMessageResult implements Serializable, Cloneable {

    /**
     * <p>
     * Specified map of raw metadata information.
     * </p>
     */
    private final Map<String, String> metadata;

    /**
     * <p>
     * A list of messages.
     * </p>
     */
    private List<Message> messages;

    public ReceiveMessageResult(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public java.util.List<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }

    public void setMessages(java.util.List<Message> messages) {
        if (messages == null) {
            this.messages = null;
            return;
        }

        this.messages = new ArrayList<Message>(messages);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("Messages: ").append(getMetadata());
        if (getMessages() != null)
            sb.append("Messages: ").append(getMessages());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof ReceiveMessageResult == false)
            return false;
        ReceiveMessageResult other = (ReceiveMessageResult) obj;
        if (other.getMetadata() == null ^ this.getMetadata() == null)
            return false;
        if (other.getMetadata() != null && other.getMetadata().equals(this.getMetadata()) == false)
            return false;
        if (other.getMessages() == null ^ this.getMessages() == null)
            return false;
        if (other.getMessages() != null && other.getMessages().equals(this.getMessages()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getMetadata() == null) ? 0 : getMetadata().hashCode()) + ((getMetadata() == null) ? 0 : getMessages().hashCode());
        return hashCode;
    }

    @Override
    public ReceiveMessageResult clone() {
        try {
            return (ReceiveMessageResult) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }
}
