package com.smattme.requestvalidator.validators;

public class ValidationResult {

    private boolean valid;
    private String error;

    public ValidationResult() {
    }

    public ValidationResult(boolean valid, String error) {
        this.valid = valid;
        this.error = error;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "");
    }

    public static ValidationResult failed(String error) {
        return new ValidationResult(false, error);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
