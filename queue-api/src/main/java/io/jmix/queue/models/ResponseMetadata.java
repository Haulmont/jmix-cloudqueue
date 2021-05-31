package io.jmix.queue.models;

import java.util.Map;

/**
 * Represents additional metadata included with a response from provider.
 */
public class ResponseMetadata {

    protected final Map<String, String> metadata;

    /**
     * Creates a new ResponseMetadata object from a specified map of raw
     * metadata information.
     *
     * @param metadata The raw metadata for the new ResponseMetadata object.
     */
    public ResponseMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Creates a new ResponseMetadata object from an existing ResponseMetadata
     * object.
     *
     * @param originalResponseMetadata The ResponseMetadata object from which to create the new
     *                                 object.
     */
    public ResponseMetadata(ResponseMetadata originalResponseMetadata) {
        this(originalResponseMetadata.metadata);
    }

    @Override
    public String toString() {
        if (metadata == null) return "{}";
        return metadata.toString();
    }

}
