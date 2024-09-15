package com.endyary.mobsoftstore.application;

/**
 * Signals that an error while processing Application archive has occurred
 */
public class ArchiveProcessingException extends RuntimeException {
    public ArchiveProcessingException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
