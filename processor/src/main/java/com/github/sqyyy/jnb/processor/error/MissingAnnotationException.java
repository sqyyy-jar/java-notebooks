package com.github.sqyyy.jnb.processor.error;

public class MissingAnnotationException extends RuntimeException {
    public MissingAnnotationException() {
    }

    public MissingAnnotationException(String message) {
        super(message);
    }

    public MissingAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingAnnotationException(Throwable cause) {
        super(cause);
    }

    public MissingAnnotationException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
