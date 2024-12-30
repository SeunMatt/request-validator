package com.smattme.requestvalidator.exceptions;

import java.util.Collections;
import java.util.List;

public class RequestValidatorException extends RuntimeException {

    private List<String> errors;

    public RequestValidatorException(String message) {
        this(message, Collections.emptyList());
    }

    public RequestValidatorException(Throwable throwable) {
        super(throwable);
        errors = Collections.emptyList();
    }

    public RequestValidatorException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
